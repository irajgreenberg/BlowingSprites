/** 
 * Point masses at Verlet surface
 * vertices. Verlet sticks each include 
 * a reference to two Verlet balls.
 * By: Ira Greenberg 
 * December 2010
 */

import processing.core.*;

public class VerletBall {

	public PVector pos, posOld;
	public float radius = .125f;
	public PApplet p;

	public VerletBall() {
	}

	public VerletBall(PApplet p, PVector pos) {
		this.p = p;
		this.pos = pos;
		posOld = new PVector(pos.x, pos.y, pos.z);
	}

	public VerletBall(PApplet p, PVector pos, float radius) {
		this.p = p;
		this.pos = pos;
		this.radius = radius;
		posOld = new PVector(pos.x, pos.y, pos.z);
	}

	public void verlet() {
		PVector posTemp = new PVector(pos.x, pos.y, pos.z);
		pos.x += (pos.x-posOld.x);
		pos.y += (pos.y-posOld.y);
		pos.z += (pos.z-posOld.z);
		posOld.set(posTemp);
	}

	public void render() {
		p.pushMatrix();
		p.translate(pos.x, pos.y, pos.z);
		p.box(radius*2, radius*2, radius*2);
		p.popMatrix();
	}
}
