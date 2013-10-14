/** 
 * Abstract class for building
 * Verlet surfaces.
 * By: Ira Greenberg 
 * December 2010
 */

import java.awt.Color;

import processing.core.*;

abstract class VerletSurface {

	public PApplet p;
	public PVector loc = new PVector();
	public PVector rot = new PVector();
	public PVector locSpd = new PVector();
	public PVector rotSpd = new PVector();
	public Dimension3 dim;
	public String textureURL;

	public PImage img;
	// for tethering surface
	public PVector[] anchors;
	public boolean[] isFixedIndex;

	// for verlet sticks
	public float tension = .04f;

	public VerletBall[] vBalls;
	public VerletBall[][] vBalls2D; // convenience datatype
	public PVector[] vBallInitPos;
	public VerletStick[] vSticks; 
	public PVector[][] uvs; // UV Texturing

	// Used to calculate vertex normals for smooth shading
	public Face3[] tris;
	//PVector[] sNorms;
	public PVector[] vNorms;

	// Used for applied force to surface
	public float theta = 0.0f;

	// for interactive effects
	public boolean isBallVisible = true;
	public boolean isStickVisible = true;
	public boolean isSurfaceVisible = true;

	//balls
	public Color ballColor;
	public float ballColorAlpha = 0.0f;
	public float dynamicBallColorAlpha = 0.0f; // not used at present
	public float ballColorFadeRate = .05f;
	// sticks
	public Color stickColor;
	public float stickColorAlpha = .2f;
	public float dynamicStickColorAlpha = .2f;
	public float stickColorFadeRate = .04f;
	// surface
	public float surfaceImageAlpha = .5f;
	public float dynamicSurfaceImageAlpha = .5f;
	public float surfaceImageAlphaFadeRate = .25f;




	public VerletSurface() {
	}

	public VerletSurface(PApplet p, PVector loc, PVector rot, Dimension3 dim, String textureURL) {
		this.p = p;
		this.loc = loc;
		this.rot = rot;
		this.dim = dim;
		this.textureURL = textureURL;
		img = p.loadImage(textureURL);
		ballColor = new Color(1.0f, .5f, 0.0f, ballColorAlpha);
		stickColor = new Color(.85f, 0.0f, .9f, stickColorAlpha);
	}

	// implement these in subclass
	abstract void init();
	abstract void createVBalls();
	abstract void createVSticks();
	abstract void createTris();
	abstract void createAnchors();
	abstract void setAnchors();
	abstract void render();

	public void createVertexNormals() {
		for (int i=0; i<vBalls.length; i++) {
			PVector vn = new PVector();
			int triCntr = 0;
			for (int j=0; j<tris.length; j++) {
				if (vBalls[i].pos == tris[j].v0 || vBalls[i].pos == tris[j].v1 || vBalls[i].pos == tris[j].v2) {
					triCntr++;
					vn.add(tris[j].getNormal());
				}
			}
			vn.div(triCntr);
			vNorms[i] = vn;
		}
	}

	public void applyHitForce(int verletBall, float amp) {
		vBalls[verletBall].pos.y+=amp;
	}

	public void setDynamics(PVector locSpd, PVector rotSpd){
		this.locSpd = locSpd;
		this.rotSpd = rotSpd;
	}

	// Starts Verlet and constraints, including anchors
	public  void start() {
		// start verlet
		for (int i=0; i<vBalls.length; i++) {
			vBalls[i].verlet();
			setAnchors();
		}

		// Start constrain
		for (int i=0; i<vSticks.length; i++) {
			vSticks[i].constrainLen();
		}

		// start dynamics
		loc.add(locSpd);
		rot.add(rotSpd);
	}

	public Face3[] getTris() {
		if (tris != null) {
			return tris;
		}
		return null;
	}

	public VerletBall[] getVBalls() {
		return vBalls;
	}

	public PVector[] getVNorms() {
		return vNorms;
	}

	public void imgReset(int id){};

	// ball color/alpha
	public void setBallColorAlpha(float ballColorAlpha){
		this.ballColorAlpha = ballColorAlpha;

	}
	public void setBallColor(Color ballColor){
		this.ballColor = ballColor;
	}

	public void setBallColorFadeRate(float ballColorFadeRate){
		this.ballColorFadeRate = ballColorFadeRate;
	}

	// stick color/alpha
	public void setStickColorAlpha(float stickColorAlpha){
		this.stickColorAlpha = stickColorAlpha;

	}
	public void setStickColor(Color stickColor){
		this.stickColor = stickColor;
	}

	public void setStickColorFadeRate(float stickColorFadeRate){
		this.stickColorFadeRate = stickColorFadeRate;
	}

	// dynamically change surface  image
	public void setSurfaceImage(String imgName){
		img = p.loadImage(imgName);
	}

	public void setSurfaceImageAlpha(float surfaceImageAlpha){
		this.surfaceImageAlpha = surfaceImageAlpha;
	}

	public void resetSurfaceImageAlpha(){
		dynamicSurfaceImageAlpha = 0.0f;
	}

	public void setIsBallVisible(boolean isBallVisible){
		this.isBallVisible = isBallVisible;
	}
	public void setIsStickVisible(boolean isStickVisible){
		this.isStickVisible = isStickVisible;
	}
	public void setIsSurfaceVisible(boolean isSurfaceVisible){
		this.isSurfaceVisible = isSurfaceVisible;
	}

	public void setBallsGlow(){
		isBallVisible = true;
		dynamicBallColorAlpha = 1.0f;
	}

	public void setSticksGlow(){
		isStickVisible = true; // needed at present since always visible
		dynamicStickColorAlpha = 1.0f;
	}

}
