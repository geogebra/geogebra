package geogebra3D.input3D.leonar3do;

import geogebra.common.euclidian3D.Input3D;
import geogebra.common.main.App;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;


/**
 * controller with specific methods from leonar3do input system
 * @author mathieu
 *
 */
public class InputLeo3D implements Input3D {

	
	private LeoSocket leoSocket;
	

	private double[] mousePosition;
	
	private double[] mouseOrientation;
	
	private boolean isRightPressed, isLeftPressed;
	
	private double screenHalfWidth;
	
	
	private double[] glassesPosition;
	
	private double eyeSeparation;
	
	/**
	 * constructor
	 */
	public InputLeo3D() {
		
		// 3D mouse position
		mousePosition = new double[3];
		
		// 3D mouse orientation
		mouseOrientation = new double[4];
		
		// glasses position
		glassesPosition = new double[3];
		
		
		// screen dimensions
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenHalfWidth = gd.getDisplayMode().getWidth()/2;
		//screenHalfHeight = gd.getDisplayMode().getHeight()/2;		
		//App.debug("screen:"+screenWidth+"x"+screenHeight);
		
		//App.error("height/2="+gd.getDisplayMode().getHeight()/2);
		
		
		leoSocket = new LeoSocket();
	}
	
	
	public boolean update(){
	
		boolean updateOccured = false;
		
		// update from last message
		if (leoSocket.gotMessage){
			
			// mouse position
			mousePosition[0] = leoSocket.birdX * screenHalfWidth;
			mousePosition[1] = leoSocket.birdY * screenHalfWidth;
			mousePosition[2] = leoSocket.birdZ * screenHalfWidth;
			
			/*
			App.debug("\norientation"
			+"\nx="+leoSocket.birdOrientationX
			+"\ny="+leoSocket.birdOrientationY
			+"\nz="+leoSocket.birdOrientationZ
			+"\nw="+leoSocket.birdOrientationW
			+"\nagnle="+(2*Math.acos(leoSocket.birdOrientationW)*180/Math.PI)+"°");
			*/
			
			// mouse position
			mouseOrientation[0] = leoSocket.birdOrientationX;
			mouseOrientation[1] = leoSocket.birdOrientationY;
			mouseOrientation[2] = leoSocket.birdOrientationZ;
			mouseOrientation[3] = leoSocket.birdOrientationW;

			
			// right button
			isRightPressed = (leoSocket.bigButton > 0.5);
			
			// left button
			isLeftPressed = (leoSocket.smallButton > 0.5);
			
		
			
			
			// glasses position
			glassesPosition[0] = leoSocket.leftEyeX * screenHalfWidth;
			glassesPosition[1] = leoSocket.leftEyeY * screenHalfWidth;
			glassesPosition[2] = leoSocket.leftEyeZ * screenHalfWidth;
			
			// eye separation
			eyeSeparation = (leoSocket.leftEyeX - leoSocket.rightEyeX) * screenHalfWidth;
			
			/*
			App.debug("\nleft eye"
					+"\nx="+leftEyePosition[0]
					+"\ny="+leftEyePosition[1]
					+"\nz="+leftEyePosition[2]
				    +
					"\nright eye"
					+"\nx="+rightEyePosition[0]
					+"\ny="+rightEyePosition[1]
					+"\nz="+rightEyePosition[2]);
					
			App.debug("\nleft-right="+(rightEyePosition[0]-leftEyePosition[0])+"\nheight="+GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight());
			*/
			
			/*
			App.debug("\nbuttons"
					+"\nbig = "+leoSocket.bigButton
					+"\nright = "+isRightPressed
					);
					*/
			
			updateOccured = true;
		}
		
		// request next message
		leoSocket.getLeoData();		
		
		return updateOccured;
		
	}
	
	public double[] getMouse3DPosition(){
		return mousePosition;
	}
	
	public double[] getMouse3DOrientation(){
		return mouseOrientation;
	}
	
	public boolean isRightPressed(){
		return isRightPressed;
	}
	
	public boolean isLeftPressed(){
		return isLeftPressed;
	}

	public double[] getGlassesPosition(){
		return glassesPosition;
	}
	
	public double getEyeSeparation(){
		return eyeSeparation;
	}
}
