package org.geogebra.common.euclidianForPlane;

/**
 * interface for EuclidianViewForPlaneCompanion
 * 
 */
public interface EuclidianViewForPlaneCompanionInterface {

	/**
	 * 
	 * @return view id
	 */
	public int getId();

	/**
	 * set transformation regarding 3D view
	 */
	public void setTransformRegardingView();

	/**
	 * remove the view when the creator doesn't exist anymore
	 */
	public void doRemove();

	/**
	 * update the view
	 */
	public void updateForPlane();

	/**
	 * update the matrix
	 */
	public void updateMatrix();

	/**
	 * update all drawables
	 * 
	 * @param repaint
	 *            says if repaint is needed
	 */
	public void updateAllDrawables(boolean repaint);

}
