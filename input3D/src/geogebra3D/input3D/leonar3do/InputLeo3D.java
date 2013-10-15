package geogebra3D.input3D.leonar3do;

import geogebra.common.euclidian3D.input3D.Input3D;
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
	
	private boolean isRightPressed;
	
	private double screenHalfWidth;
	
	
	private double[] leftEyePosition;
	
	/**
	 * constructor
	 */
	public InputLeo3D() {
		
		// 3D mouse position
		mousePosition = new double[3];
		
		// 3D mouse orientation
		mouseOrientation = new double[4];
		
		// eyes position
		leftEyePosition = new double[3];
		
		
		// screen dimensions
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenHalfWidth = gd.getDisplayMode().getWidth()/2;
		//screenHalfHeight = gd.getDisplayMode().getHeight()/2;		
		//App.debug("screen:"+screenWidth+"x"+screenHeight);
		
		
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
			isRightPressed = (leoSocket.smallButton > 0.5);
			
			
			
			
			// left eye position
			leftEyePosition[0] = leoSocket.leftEyeX * screenHalfWidth;
			leftEyePosition[1] = leoSocket.leftEyeY * screenHalfWidth;
			leftEyePosition[2] = leoSocket.leftEyeZ * screenHalfWidth;
			
			/*
			App.debug("\nleft eye"
					+"\nx="+leftEyePosition[0]
					+"\ny="+leftEyePosition[1]
					+"\nz="+leftEyePosition[2]);
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
}
