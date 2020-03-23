package org.geogebra.common.euclidian.draw;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Interface for drawables resizeable by bounding box.
 */
public interface DrawWidget {
	/**
	 * @param newWidth
	 *            pixel width at current zoom
	 */
	public void setWidth(int newWidth);

	/**
	 * @param newHeight
	 *            pixel height at current zoom
	 */
	public void setHeight(int newHeight);

	/**
	 * @return left corner x-coord in EV
	 */
	public int getLeft();

	/**
	 * @return top corner y-coord in EV
	 */
	public int getTop();

	/**
	 * @param x
	 *            left corner x-coord in EV
	 * @param y
	 *            top corner y-coord in EV
	 */
	public void setAbsoluteScreenLoc(int x, int y);

	/**
	 * @return aspect ratio at start of resize (NaN if last drag changed it)
	 */
	public double getOriginalRatio();

	/**
	 * @return width on screen at current zoom
	 */
	public int getWidth();

	/**
	 * @return height on screen at current zoom
	 */
	public int getHeight();

	/**
	 * Reset aspect ratio.
	 */
	public void resetRatio();

	/**
	 * Update drawable.
	 */
	public void update();

	/**
	 * @return the geo linked to this
	 */
	public GeoElement getGeoElement();

	/**
	 * @return whether aspect ratio is fixed for this widget
	 */
	public boolean isFixedRatio();

	/**
	 * @return embed ID
	 */
	int getEmbedID();

	boolean isBackground();

	void setBackground(boolean b);
}
