package geogebra.common.euclidian3D;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;



/**
 * interface for specific 3D inputs
 * @author mathieu
 *
 */
public interface Input3D {

	public enum DeviceType{HAND, PEN};
	
	/**
	 * 
	 * @return device type
	 */
	public DeviceType getDeviceType();
	
	
	/**
	 * update values
	 * @return true if the update worked
	 */
	public boolean update();
	
	/**
	 *  Center is center of the screen, unit is pixels
	 * @return 3D mouse position
	 */
	public double[] getMouse3DPosition();
	
//	/**
//	 * 
//	 * @return 2D mouse x position
//	 */
//	public float getMouse2DX();
//	
//	/**
//	 * 
//	 * @return 2D mouse y position
//	 */
//	public float getMouse2DY();
//	
//	/**
//	 * 
//	 * @return 2D mouse factor
//	 */
//	public float getMouse2DFactor();
	
	/**
	 *
	 * @return 3D mouse orientation (as quaternion)
	 */
	public double[] getMouse3DOrientation();
	
	
	/**
	 *  Center is center of the screen, unit is pixels
	 * @return glasses position (two eyes center)
	 */
	public double[] getGlassesPosition();
	
	
	/**
	 * 
	 * @return eye separation
	 */
	public double getEyeSeparation();


	/**
	 * 
	 * @return true if right button is pressed
	 */
	public boolean isRightPressed();
	
	/**
	 * 
	 * @return true if left button is pressed
	 */
	public boolean isLeftPressed();

	/**
	 * 
	 * @return true if the input use depth for hitting
	 */
	public boolean useInputDepthForHitting();

	/**
	 * 
	 * @return true if the input use a robot to controll 2D mouse
	 */
	public boolean useMouseRobot();


	/**
	 * 
	 * @param view3D 3D view
	 * @return true if input3D has mouse on 3D view
	 */
	public boolean hasMouse(EuclidianView3D view3D);


	/**
	 * 
	 * @return true if 3D input is currently (possibly) using 2D mouse
	 */
	public boolean currentlyUseMouse2D();


	/**
	 * set left button is pressed
	 * @param flag flag
	 */
	public void setLeftButtonPressed(boolean flag);
	
	/**
	 * 
	 * @return left button status
	 */
	public boolean getLeftButton();



	
}
