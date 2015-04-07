package org.geogebra.common.euclidian3D;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * 
 * @author mathieu
 *
 *         Interface for 3D view
 */
public interface EuclidianView3DInterface extends EuclidianViewInterfaceCommon {

	/**
	 * start a rotation animation to be in the vector direction, shortest way
	 * 
	 * @param v
	 *            vector direction
	 */
	public void setClosestRotAnimation(Coords v);

	/**
	 * @return Returns the zmin.
	 */
	public double getZmin();

	/**
	 * @return Returns the zmax.
	 */
	public double getZmax();

	/**
	 * sets the use of the clipping cube
	 * 
	 * @param flag
	 *            flag
	 */
	public void setUseClippingCube(boolean flag);

	/**
	 * sets if the clipping cube is shown
	 * 
	 * @param flag
	 *            flag
	 */
	public void setShowClippingCube(boolean flag);

	/**
	 * sets the reduction of the clipping box
	 * 
	 * @param value
	 *            reduction
	 */
	public void setClippingReduction(int value);

	/**
	 * 
	 * @param projection
	 *            projection type
	 */
	public void setProjection(int projection);

	/**
	 * sets the visibility of xOy plane grid
	 * 
	 * @param flag
	 *            flag
	 * @return
	 */
	public boolean setShowGrid(boolean flag);

	/**
	 * sets the visibility of xOy plane
	 * 
	 * @param flag
	 *            flag
	 */
	public void setShowPlane(boolean flag);

	/**
	 * sets the visibility of xOy plane plate
	 * 
	 * @param flag
	 *            flag
	 */
	public void setShowPlate(boolean flag);

	/**
	 * set the all-axis scale
	 * 
	 * @param val
	 *            scale value
	 */
	public void setScale(double val);

	/**
	 * sets the rotation matrix
	 * 
	 * @param a
	 * @param b
	 */
	public void setRotXYinDegrees(double a, double b);

	/**
	 * sets the origin
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 */
	public void setZeroFromXML(double x, double y, double z);

	/**
	 * set Matrix for view3D
	 */
	public void updateMatrix();

	/**
	 * tells the view it has changed
	 */
	public void setViewChanged();

	/**
	 * tell the view that it has to be updated
	 * 
	 */
	public void setWaitForUpdate();

	/**
	 * set if y axis is up (and not z axis)
	 * 
	 * @param flag
	 *            flag
	 */
	public void setYAxisVertical(boolean flag);

	/**
	 * @return screen z-coord of origin
	 */
	public double getZZero();

	/**
	 * update all drawables
	 */
	public void updateAllDrawables();
	
	/**
	 * 
	 * @return eye position
	 */
	public Coords getEyePosition();

}
