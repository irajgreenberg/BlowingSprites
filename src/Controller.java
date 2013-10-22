import java.awt.Color;

import processing.core.*;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;

import javax.media.opengl.GL2;
import java.awt.*;
import javax.swing.JFrame;

import oscP5.*;
import netP5.*;


public class Controller extends PApplet {


	String[] imgNames = {
			"antique_doll.jpg",
			"aztec.jpg",
			"aztec_mask.jpg",
			"clock_turn.jpg", 
			"colored_skull.jpg", 
			"couple.jpg",
			"cyborg.jpg", 
			"dino.jpg", 
			"ears.jpg",
			"exploded_skull.jpg",
			"fish1.jpg", 
			"fish2.jpg",
			"flower_01.jpg", 
			"fossil01.jpg", 
			"fossil02.jpg", 
			"gas_mask.jpg",
			"heart.jpg",
			"kollewitz.jpg",
			"l_elliot.jpg",
			"lopez.jpg",
			"maleDoll.jpg",
			"man.jpg",
			"male_xray.jpg",
			"metal_head.jpg",
			"scared.jpg",
			"skull.jpg", 
			"skull_masks.jpg", 
			"skull_punk.jpg", 
			"skull_side.jpg",
			"snake.jpg",
			"soldier2.jpg",
			"tree_man.jpg",
			"wBritSoldier.jpg",
			"woman.jpg"
			};

	int surfCount = 110;
	VerletSurface[] surfs = new VerletSurface[surfCount];
	int[] hitTargets = new int[surfCount];
	float[] wafeFreqs = new float[surfCount];
	float[] waveAmps = new float[surfCount];


	//	float xShift = 0, yShift = 0, zShift = -100; 
	//	float xShiftSpd, yShiftSpd, zShiftSpd;
	//	float rotX, rotY, rotZ, rotXTheta, rotYTheta, rotZTheta;

	int counter = 0;

	// osc stuff
	OscP5 oscP5;
	NetAddress myRemoteLocation;
	String[] maxVarNames = {
			"noteID", "noteVel, noteFreq"
	};
	int noteID = 0;
	float noteVel, noteFreq;

	int surfRows = 13;
	int surfCols = 13;
	int noteCount = 225;
	float nodeMappingRatio = surfRows*surfCols / noteCount;

	public Controller() {
	}

	public void setup(){
		size((int)(displayWidth/1.5f), displayHeight, P3D);
		
		// freak'n nasty
		(((JFrame) frame).getContentPane()).setBackground(new Color(0,0,0));
		colorMode(RGB, 1.0f);

		// crank up anti-aliasing
		smooth(8);

		//		doesn't due ooh-gatz
		//		GL2 gl;
		//		PGraphicsOpenGL pgl = (PGraphicsOpenGL) g;
		//		gl = pgl.beginPGL().gl.getGL2();
		//		gl.glFrontFace(gl.GL_CCW);
		//		gl.glCullFace(gl.GL_BACK);
		//		gl.glEnable(gl.GL_CULL_FACE);

		// ensure objects fly completely by camera before being clipped
		frustum(-2, 2, -2*(float)displayHeight/(float)displayWidth, 2*(float)displayHeight/(float)displayWidth, 1, 20000);

		// specify if corners are anchored or free - true is anchored
		boolean[] edgeFlags = {
				true, true, true, true
		};

		for(int i=0; i<surfs.length; ++i){
			surfs[i] = new VerletSurfaceRect(this, new PVector(random(-200, 200), random(-200, 200), random(-2200, -500)), 
					new PVector(-PI/4, 0, 0), new Dimension3(random(40, 250), 0, random(40, 250)), 
					"data/"+imgNames[(int)(random(imgNames.length))], surfRows, surfCols, edgeFlags, .5f);
			setDynamics(i);
			surfs[i].setSurfaceImageAlpha(.8f);

		}

//		 //start oscP5, et's hope that it works!
		  //oscP5 = new OscP5(this, 12001);
//		  //myRemoteLocation = new NetAddress("127.0.0.1", 12000);
		repaint();
	}

	public void draw(){
		background(0.0f);
		translate(width/2, height/2, 400);
		lights();
		for(int i=0; i<surfs.length; ++i){		
			surfs[i].applyHitForce(hitTargets[i], sin(frameCount*PI/wafeFreqs[i])*waveAmps[i]);
			surfs[i].render();
			surfs[i].start();

			// loop objs
			if (surfs[i].loc.z>1000){
				surfs[i].loc.x = random(-100.0f, 100.0f);
				surfs[i].loc.y = random(-100.0f, 100.0f);
				surfs[i].loc.z = -1000;
				surfs[i].resetSurfaceImageAlpha();
				setDynamics(i);
			}
		}
	}

	public void setDynamics(int i){
		surfs[i].setSurfaceImage("data/"+imgNames[(int)(random(imgNames.length))]);
		surfs[i].setDynamics(new PVector(random(-.5f, .5f), random(-.5f, .5f), random(.5f, 5.5f)), 
				new PVector(random(-PI/90, PI/90), random(-PI/90, PI/90), random(-PI/90, PI/90)));
		hitTargets[i] = (int)(random(5, (surfRows-1)*(surfCols-1)));
		wafeFreqs[i] = random(PI/25.0f, PI/180.0f);
		waveAmps[i] = random(5.0f, 15.0f);
	}


	public void keyPressed() {
		if (key == CODED) {
			if (keyCode == LEFT) {
				for(int i=0; i<surfs.length; ++i){
					surfs[i].setBallsGlow();
				}

			} else  if (keyCode == RIGHT){
				for(int i=0; i<surfs.length; ++i){
					surfs[i].setSticksGlow();
				}

			} else  if (keyCode == UP){
				for(int i=0; i<surfs.length; ++i){
					for(int j=0; j<surfs[i].vBalls.length; ++j){

						surfs[i].vBalls[j].pos.x = random(-10.0f, 10.0f);
						surfs[i].vBalls[j].pos.y = random(-10.0f, 10.0f);
						surfs[i].vBalls[j].pos.z = random(-10.0f, 10.0f);
					}
				}
				
				for(int i=0; i<surfs.length; ++i){
					for(int j=0; j<surfs[i].vSticks.length; ++j){
						//surfs[i].vSticks[j].tension = random(.0001f, .003f);
//						if (surfs[i].vSticks[j].tension > .1f) {
//						surfs[i].vSticks[j].tension -=.1f;
//						}
					}
				}

			} else  if (keyCode == DOWN) {
				for(int i=0; i<surfs.length; ++i){
					for(int j=0; j<surfs[i].vSticks.length; ++j){
						if (surfs[i].vSticks[j].tension < .999f) {
							surfs[i].vSticks[j].tension +=.1f;
						}
					}
				}

			}

		} 

	}

	//	public void oscEvent(OscMessage theOscMessage) {
	//		if (theOscMessage.checkAddrPattern("/jamPactHiRes")==true) {
	//			if (counter++%223 ==0) {
	//
	//				noteID = theOscMessage.get(1).intValue(); // 0-224
	//				noteFreq = theOscMessage.get(2).floatValue(); // 0-1 
	//				noteVel = theOscMessage.get(3).floatValue(); // 0-1
	//				println("noteID = " + noteID);
	//				println("noteFreq = "  + noteFreq);
	//				println("noteVel = " + noteVel);
	//			}
	//		} 
	//
	//
	//		//println("noteID = " + noteID);
	//		if (noteVel != 0) {
	//			//surfRows, surfCols
	//			int mappedNode = int(noteID*nodeMappingRatio);
	//			//println("mappedNode = " + mappedNode);
	//			float x = mappedNode/surfRows;
	//			float y = mappedNode/surfCols;
	//			// println("x = " + x);
	//			//println("y = " + y);
	//			float xCoord = width/surfCols * x;
	//			float yCoord = height/surfRows * y;
	//
	//			/* println("noteID = " + noteID);
	//		     println("noteVel = " + noteVel);
	//		     println("noteFreq = " + noteFreq);
	//		     println("xCoord = " + xCoord);
	//		     println("yCoord = " + yCoord);*/
	//
	//			surf.applyForce(int(xCoord), int(yCoord), noteVel*1500, noteFreq);
	//
	//			/*isNoteOn[noteID] = true;
	//		     keys[noteID].damping = 1.0;
	//		     keys[noteID].freq = noteFreq;
	//		     keys[noteID].vel = noteVel;*/
	//		} 
	//		else {
	//			//isNoteOn[noteID] = false;
	//			// keys[noteID].damping = .85;
	//		}
	//	}



	public static void main(String args[]) {
		PApplet.main(new String[] {"--present", "Controller" });
	}

}
