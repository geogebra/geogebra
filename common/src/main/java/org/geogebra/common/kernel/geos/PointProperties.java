package org.geogebra.common.kernel.geos;

/**
 * Element with point style & point size (point or list)
 */
public interface PointProperties {
	/**
	 * Sets the point size
	 * @param size new point size
	 */
	public void setPointSize(int size);
	/**
	 * Returns the point size
	 * @return point size
	 */
	public int getPointSize();
	/**
	 * Sets the point style
	 * @param type point style
	 */
	public void setPointStyle(int type);
	/**
	 * Returns the point style
	 * @return point style
	 */
	public int getPointStyle();
	/**
	 * Updates and repaints the object
	 */
	public void updateRepaint();
}