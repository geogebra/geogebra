package geogebra.common.euclidianForPlane;

import geogebra.common.kernel.kernelND.ViewCreator;

/**
 * 
 * @author mathieu
 * Interface for EuclidianForPlane
 */
public interface EuclidianViewForPlaneInterface {

	/**
	 * @return creator of the view
	 * 
	 */
	public ViewCreator getPlane();
	
	/**
	 * set the transform matrix regarding view direction
	 */
	public void setTransformRegardingView();

	/**
	 * remove the view (when creator is removed)
	 */
	public void doRemove();
	
	/**
	 * 
	 * @return the id of the view
	 */
	public int getId();
	
	/**
	 * update the matrix transformation
	 */
	public void updateMatrix();
	
	/**
	 * @param repaint
	 *            true to repaint
	 */
	public void updateAllDrawables(boolean repaint);
	
	/**
	 * only used in EuclidianViewForPlane
	 */
	public void updateForPlane();
	
	
	
}
