package geogebra.common.euclidian3D;


/**
 * interface for specific 3D inputs
 * @author mathieu
 *
 */
public interface Input3D {

	
	
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
	
}
