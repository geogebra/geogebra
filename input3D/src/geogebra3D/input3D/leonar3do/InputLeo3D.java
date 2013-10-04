package geogebra3D.input3D.leonar3do;

import geogebra.common.euclidian3D.input3D.Input3D;

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
	
	private double screenHalfWidth;// screenHeight;
	
	/**
	 * constructor
	 */
	public InputLeo3D() {
		
		// 3D mouse position
		mousePosition = new double[3];
		
		
		// screen dimensions
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenHalfWidth = gd.getDisplayMode().getWidth()/2;
		//screenHeight = gd.getDisplayMode().getHeight();		
		//App.debug("screen:"+screenWidth+"x"+screenHeight);
		
		
		leoSocket = new LeoSocket();
	}
	
	
	public boolean update(){
	
		boolean updateOccured = false;
		
		// update from last message
		if (leoSocket.gotMessage){
			mousePosition[0] = leoSocket.birdX * screenHalfWidth;
			mousePosition[1] = leoSocket.birdY * screenHalfWidth;
			mousePosition[2] = leoSocket.birdZ * screenHalfWidth;
			updateOccured = true;
		}
		
		// request next message
		leoSocket.getLeoData();		
		
		return updateOccured;
		
	}
	
	public double[] getMouse3DPosition(){
		return mousePosition;
	}
}
