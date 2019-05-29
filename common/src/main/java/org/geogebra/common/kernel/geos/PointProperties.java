package org.geogebra.common.kernel.geos;

/**
 * Element with point style & point size (point or list)
 */
public interface PointProperties {
	/**
	 * Sets the point size
	 * 
	 * @param size
	 *            new point size
	 */
	void setPointSize(int size);

	/**
	 * Returns the point size
	 * 
	 * @return point size
	 */
	int getPointSize();

	/**
	 * Sets the point style
	 * 
	 * @param type
	 *            point style
	 */
	void setPointStyle(int type);

	/**
	 * Returns the point style
	 * 
	 * @return point style
	 */
	int getPointStyle();

	/**
	 * Updates and repaints the object
	 */
	void updateRepaint();

	/**
	 * @return whether point settings should be shown
	 */
	boolean showPointProperties();
}