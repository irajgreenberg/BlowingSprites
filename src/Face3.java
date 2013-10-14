/** 
 * Simple triangle face class
 * encapsulating three PVectors.
 * Includes own getNormal method.
 * By: Ira Greenberg 
 * December 2010
 */

import processing.core.*;

public class Face3 {
	public PVector v0, v1, v2;
	public PVector[] vs;
	public PVector n;

	public Face3() {
	}

	public Face3(PVector v0, PVector v1, PVector v2) {
		this.v0 = v0;
		this.v1 = v1;
		this.v2 = v2;
		vs = new PVector[] {
				v0, v1, v2
		};
	}

	public PVector getNormal() {
		n = new PVector();
		n = PVector.cross(PVector.sub(v1, v2), PVector.sub(v0, v2), null);
		n.normalize();
		return(n);
	}

//	public void render() {
//	fill(127);
//	//stroke(0);
//	beginShape(TRIANGLES);
//	for (int i=0;i<3; i++) {
//	vertex(vs[i].x, vs[i].y, vs[i].z);
//	}
//	endShape();
//	}

//	public void renderNorm(float len) {
//	stroke(200, 100, 0);
//	noFill();
//	PVector n = getNormal();
//	n.mult(len);
//	PVector o = getOrigin();
//	beginShape();
//	vertex(o.x, o.y, o.z);
//	vertex(o.x-n.x, o.y-n.y, o.z-n.z);
//	endShape();
//	}

	public PVector getCentroid() {
		PVector centroid = new PVector();
		centroid.set(v0);
		centroid.add(v1);
		centroid.add(v2);
		centroid.div(3);
		return centroid;
	}
}
