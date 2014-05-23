package geogebra.common.euclidianForPlane;



/**
 * 
 * @author mathieu
 * Interface for EuclidianForPlane
 */
public interface EuclidianViewForPlaneInterface {


	/**
	 * remove the view (when creator is removed)
	 */
	public void doRemove();
	
	
	
	/**
	 * @param repaint
	 *            true to repaint
	 */
	public void updateAllDrawables(boolean repaint);
	

	
	
	
	
	
	
	
	
	
}
