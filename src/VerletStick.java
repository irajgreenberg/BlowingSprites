/** 
 * Verlet sticks constrain
 * position of Verlet balls.
 * By: Ira Greenberg 
 * December 2010
 */

import processing.core.*;
 
 public class VerletStick {
  VerletBall b1, b2;
  VerletBall[] bs;
  PVector vecOrig;
  float len;
  float tension = .35f;
  Tuple2 constrainVal = new Tuple2(.5f, .5f);
  public PApplet p;

  public VerletStick() {
  }

  public VerletStick(PApplet p, VerletBall b1, VerletBall b2) {
    this.p = p;
    this.b1 = b1;
    this.b2 = b2;
    bs = new VerletBall[] {
      b1, b2
    };
    vecOrig  = new PVector(b2.pos.x-b1.pos.x, b2.pos.y-b1.pos.y, b2.pos.z-b1.pos.z);
   // len = dist(b1.pos.x, b1.pos.y, b1.pos.z, b2.pos.x, b2.pos.y, b2.pos.z);
     len = vecOrig.mag();
  }

  public VerletStick(PApplet p, VerletBall b1, VerletBall b2, float tension) {
    this.p = p;
    this.b1 = b1;
    this.b2 = b2;
    bs = new VerletBall[] {
      b1, b2
    };
    this.tension = tension;
    vecOrig  = new PVector(b2.pos.x-b1.pos.x, b2.pos.y-b1.pos.y, b2.pos.z-b1.pos.z);
    //len = dist(b1.pos.x, b1.pos.y, b1.pos.z, b2.pos.x, b2.pos.y, b2.pos.z);
    len = vecOrig.mag();
  }

  public VerletStick(PApplet p, VerletBall b1, VerletBall b2, float tension, Tuple2 constrainVal) {
	this.p = p;
    this.b1 = b1;
    this.b2 = b2;
    bs = new VerletBall[] {
      b1, b2
    };
    this.tension = tension;
    this.constrainVal = constrainVal;
    vecOrig  = new PVector(b2.pos.x-b1.pos.x, b2.pos.y-b1.pos.y, b2.pos.z-b1.pos.z);
    //len = dist(b1.pos.x, b1.pos.y, b1.pos.z, b2.pos.x, b2.pos.y, b2.pos.z);
    len = vecOrig.mag();
  }

  public void constrainLen() {
    for (int i=0; i<30; i++) {
      PVector delta = new PVector(b2.pos.x-b1.pos.x, b2.pos.y-b1.pos.y, b2.pos.z-b1.pos.z);
      float deltaLength = delta.mag();
      float difference = ((deltaLength - len) / deltaLength);
      b1.pos.x += delta.x * (constrainVal.elem0 * tension * difference);
      b1.pos.y += delta.y * (constrainVal.elem0 * tension * difference);
      b1.pos.z += delta.z * (constrainVal.elem0 * tension * difference);
      b2.pos.x -= delta.x * (constrainVal.elem1 * tension * difference);
      b2.pos.y -= delta.y * (constrainVal.elem1 * tension * difference);
      b2.pos.z -= delta.z * (constrainVal.elem1 * tension * difference);
    }
  }

  public void render() {
    p.beginShape();
    p.vertex(bs[0].pos.x, bs[0].pos.y, bs[0].pos.z);
    p.vertex(bs[1].pos.x, bs[1].pos.y, bs[1].pos.z);
    p.endShape();
  }
}
