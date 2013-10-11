package geogebra.common.euclidian3D.input3D;


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
	 * 
	 * @return 3D mouse position
	 */
	public double[] getMouse3DPosition();
	
	/**
	 * 
	 * @return 3D mouse orientation (as quaternion)
	 */
	public double[] getMouse3DOrientation();

	/**
	 * 
	 * @return true if right button is pressed
	 */
	public boolean isRightPressed();
	
}
