/** 
 * VerletSurface subclass for creating 
 * rectanglular surfaces with corner anchors.
 * By: Ira Greenberg 
 * December 2010
 */


import processing.core.*;

public class VerletSurfaceRect extends VerletSurface {

	public int rows, cols;

	VerletSurfaceRect() {
	}

	public VerletSurfaceRect(PApplet p, PVector loc, PVector rot, Dimension3 dim, String textureURL, int rows, int cols) {
		super(p, loc, rot, dim, textureURL);
		this.rows = rows;
		this.cols = cols;
		isFixedIndex = new boolean[4];
		init();
	}

	public VerletSurfaceRect(PApplet p, PVector loc, PVector rot, Dimension3 dim, String textureURL, int rows, int cols, boolean[] isFixedIndex, float tension) {
		super(p, loc, rot, dim, textureURL);
		this.rows = rows;
		this.cols = cols;
		this.isFixedIndex = isFixedIndex;
		this.tension = tension;
		init();
	}

	protected void init() {
		vBalls = new VerletBall[rows*cols];
		vBallInitPos = new PVector[rows*cols];
		vBalls2D = new VerletBall[rows][cols];
		uvs = new PVector[rows][cols];

		int r = rows-1;
		int c = cols-1;
		int stickCount = cols*r + rows*c + r*c;
		vSticks = new VerletStick[stickCount];
		tris = new Face3[r*c*2];

		//sNorms = new PVector[tris.length];
		vNorms = new PVector[rows*cols]; 

		anchors = new PVector[4];

		createVBalls();
		createVSticks();
		createTris();
		createAnchors();
		setAnchors();
	}

	// Instantiate Vertex balls
	protected void createVBalls() {
		float xShift = dim.w/(cols-1);
		float zShift = dim.d/(rows-1);

		// UV Texturing
		float uShift = 1.0f/(cols-1);
		float vShift = 1.0f/(rows-1);

		float x = -dim.w/2;
		float z = -dim.d/2;
		//int center = int((rows)/2)*(cols) + int((cols)/2);
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cols; j++) {
				float bx = x + xShift*j;
				float bz = z + zShift*i;
				//println("bx = " + bx + ", bz = " + bz);
				PVector pos = new PVector(bx, 0, bz); 
				vBallInitPos[i*cols+j] = new PVector(bx, 0, bz);
				vBalls2D[i][j] = new VerletBall(p, pos, 1.25f);
				// set 1D arr version to point to same mem addresses
				vBalls[i*cols+j] = vBalls2D[i][j];

				// new UV code
				float bu = uShift*j;
				float bv = vShift*i;
				uvs[i][j] = new PVector(bu, bv);
			}
		}
	}

	// Instantiate Vertex sticks.
	protected void createVSticks() {
		int stickCounter = 0;
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cols; j++) {

				if (i<rows-1) {
					// anchor top left corner:  | stick
					if (i==0 && j==0 && isFixedIndex[0]) {
						vSticks[stickCounter++] = new VerletStick(p, vBalls2D[i][j], vBalls2D[i+1][j], tension, new Tuple2(0.0f, 1.0f));
					} 
					// anchor top right corner:  | stick
					else if (i==0 && j==cols-1 && isFixedIndex[1]) {
						vSticks[stickCounter++] = new VerletStick(p, vBalls2D[i][j], vBalls2D[i+1][j], tension, new Tuple2(0.0f, 1.0f));
					} 
					// anchor bottom right corner:  | stick
					else if (i==rows-2 && j==cols-1 && isFixedIndex[2]) {
						vSticks[stickCounter++] = new VerletStick(p, vBalls2D[i][j], vBalls2D[i+1][j], tension, new Tuple2(1.0f, 0.0f));
					} 
					// anchor bottom left corner:  | stick
					else if (i==rows-2 && j==0 && isFixedIndex[3]) {
						vSticks[stickCounter++] = new VerletStick(p, vBalls2D[i][j], vBalls2D[i+1][j], tension, new Tuple2(1.0f, 0.0f));
					} 
					// no anchors
					else {
						vSticks[stickCounter++] = new VerletStick(p, vBalls2D[i][j], vBalls2D[i+1][j], tension);
					}
				} 

				if (j<cols-1) {
					// anchor top left corner:  -- stick
					if (i==0 && j==0 && isFixedIndex[0]) {
						vSticks[stickCounter++] = new VerletStick(p, vBalls2D[i][j], vBalls2D[i][j+1], tension, new Tuple2(0.0f, 1.0f));
					}  // anchor top right corner:  -- stick
					else if (i==0 && j==cols-2 && isFixedIndex[1]) {
						vSticks[stickCounter++] = new VerletStick(p, vBalls2D[i][j], vBalls2D[i][j+1], tension, new Tuple2(1.0f, 0.0f));
					}  // anchor bottom right corner:  -- stick
					else if (i==rows-1 && j==cols-2 && isFixedIndex[2]) {
						vSticks[stickCounter++] = new VerletStick(p, vBalls2D[i][j], vBalls2D[i][j+1], tension, new Tuple2(1.0f, 0.0f));
					}  // anchor bottom left corner:  -- stick
					else if (i==rows-1 && j==0 && isFixedIndex[3]) {
						vSticks[stickCounter++] = new VerletStick(p, vBalls2D[i][j], vBalls2D[i][j+1], tension, new Tuple2(0.0f, 1.0f));
					} 
					// no anchors
					else {
						vSticks[stickCounter++] = new VerletStick(p, vBalls2D[i][j], vBalls2D[i][j+1], tension);
					}
				}
			}
		}
		// diagonal sticks
		for (int i=0; i<rows-1; i++) {
			for (int j=0; j<cols-1; j++) {
				vSticks[stickCounter++] = new VerletStick(p, vBalls2D[i][j], vBalls2D[i+1][j+1], tension);
			}
		}
	}

	// Creates triangle faces
	protected void createTris() {
		int triCounter = 0;
		for (int i=0; i<vBalls2D.length-1; i++) {
			for (int j=0; j<vBalls2D[i].length-1; j++) {
				tris[triCounter] = new Face3(vBalls2D[i][j].pos, vBalls2D[i][j+1].pos, vBalls2D[i+1][j+1].pos);
				//sNorms[triCounter] = tris[triCounter].getNormal();
				triCounter++;
				tris[triCounter] = new Face3(vBalls2D[i][j].pos, vBalls2D[i+1][j+1].pos, vBalls2D[i+1][j].pos);
				//sNorms[triCounter] = tris[triCounter].getNormal();
				triCounter++;
			}
		}
	}

	// Grab surface corner postions for anchors
	protected void createAnchors() {
		// LT anchor
		anchors[0] = new PVector(vBalls[0].pos.x, vBalls[0].pos.y, vBalls[0].pos.z);
		// RT anchor
		anchors[1] = new PVector(vBalls[cols-1].pos.x, vBalls[cols-1].pos.y, vBalls[cols-1].pos.z);
		// RB anchor
		anchors[2] = new PVector(vBalls[vBalls.length-1].pos.x, vBalls[vBalls.length-1].pos.y, vBalls[vBalls.length-1].pos.z);
		// LB anchor
		int index = vBalls.length-1-(cols-1);
		anchors[3] = new PVector(vBalls[index].pos.x, vBalls[index].pos.y, vBalls[index].pos.z);
	}

	// Apply anchors to surface corners, based on flags
	protected void setAnchors() {

		for (int i=0; i<vBalls.length; i++) {
			// attach corners to anchors - based on flag in isFixedIndex array
			if (i==0 && isFixedIndex[0]) {
				vBalls[i].pos.x = anchors[0].x;
				vBalls[i].pos.y = anchors[0].y;
				vBalls[i].pos.z = anchors[0].z;
			} 
			else if (i==cols-1 && isFixedIndex[1]) {
				vBalls[i].pos.x = anchors[1].x;
				vBalls[i].pos.y = anchors[1].y;
				vBalls[i].pos.z = anchors[1].z;
			} 
			else if (i==vBalls.length-1 && isFixedIndex[2]) {
				vBalls[i].pos.x = anchors[2].x;
				vBalls[i].pos.y = anchors[2].y;
				vBalls[i].pos.z = anchors[2].z;
			} 
			else if (i==vBalls.length-1 - (cols-1) && isFixedIndex[3]) {
				vBalls[i].pos.x = anchors[3].x;
				vBalls[i].pos.y = anchors[3].y;
				vBalls[i].pos.z = anchors[3].z;
			}
		}
	}
	

	// Selectively render point cloud, wireframe and/or surface
	public void render() {
		createVertexNormals();

		p.pushMatrix();
		p.translate(loc.x, loc.y, loc.z);
		p.rotateX(rot.x);
		p.rotateY(rot.y);
		p.rotateZ(rot.z);
		int ctr = 0;

		// if surface rendering flag true
		if (isSurfaceVisible) {
			if(dynamicSurfaceImageAlpha < surfaceImageAlpha){
				dynamicSurfaceImageAlpha += .04f;
			}
			p.noStroke();
			p.tint(1.0f, dynamicSurfaceImageAlpha);
			p.textureMode(p.NORMAL);
			p.beginShape(p.TRIANGLES);
			p.texture(img);
			for (int i=0; i<rows-1; i++) {
				for (int j=0; j<cols-1; j++) {
					p.normal(vNorms[i*cols + j].x, vNorms[i*cols + j].y, vNorms[i*cols + j].z);
					p.vertex(vBalls2D[i][j].pos.x, vBalls2D[i][j].pos.y, vBalls2D[i][j].pos.z, uvs[i][j].x, uvs[i][j].y);
					p.normal(vNorms[i*cols + j+1].x, vNorms[i*cols + j+1].y, vNorms[i*cols + j+1].z);
					p.vertex(vBalls2D[i][j+1].pos.x, vBalls2D[i][j+1].pos.y, vBalls2D[i][j+1].pos.z, uvs[i][j+1].x, uvs[i][j+1].y);
					p.normal(vNorms[(i+1)*cols + j+1].x, vNorms[(i+1)*cols + j+1].y, vNorms[(i+1)*cols + j+1].z);
					p.vertex(vBalls2D[i+1][j+1].pos.x, vBalls2D[i+1][j+1].pos.y, vBalls2D[i+1][j+1].pos.z, uvs[i+1][j+1].x, uvs[i+1][j+1].y);
					p.normal(vNorms[i*cols + j].x, vNorms[i*cols + j].y, vNorms[i*cols + j].z);
					p.vertex(vBalls2D[i][j].pos.x, vBalls2D[i][j].pos.y, vBalls2D[i][j].pos.z, uvs[i][j].x, uvs[i][j].y);
					p.normal(vNorms[(i+1)*cols + j+1].x, vNorms[(i+1)*cols + j+1].y, vNorms[(i+1)*cols + j+1].z);
					p.vertex(vBalls2D[i+1][j+1].pos.x, vBalls2D[i+1][j+1].pos.y, vBalls2D[i+1][j+1].pos.z, uvs[i+1][j+1].x, uvs[i+1][j+1].y);
					p.normal(vNorms[(i+1)*cols + j].x, vNorms[(i+1)*cols + j].y, vNorms[(i+1)*cols + j].z);
					p.vertex(vBalls2D[i+1][j].pos.x, vBalls2D[i+1][j].pos.y, vBalls2D[i+1][j].pos.z, uvs[i+1][j].x, uvs[i+1][j].y);
				}
			}
			p.endShape();
		}
		// balls
		if (isBallVisible) {
			if(dynamicBallColorAlpha > ballColorAlpha){
				dynamicBallColorAlpha -= ballColorFadeRate;
			} else {
				isBallVisible = false;
			}
			p.fill(ballColor.getRed(), ballColor.getGreen(), ballColor.getBlue(), dynamicBallColorAlpha);
			for (int i=0; i<vBalls.length; i++) {
				vBalls[i].render();
			}
		}
		// stick
		if (isStickVisible) {
			if(dynamicStickColorAlpha > stickColorAlpha){
				dynamicStickColorAlpha -= stickColorFadeRate;
			} else {
				//isStickVisible = false; // disabled to keep some of the mesh visible
			}
			p.strokeWeight(.4f);
			p.stroke(stickColor.getRed(), stickColor.getGreen(), stickColor.getBlue(), dynamicStickColorAlpha);
			for (int i=0; i<vSticks.length; i++) {
				vSticks[i].render();
			}
		}
		p.popMatrix();
	}
}
