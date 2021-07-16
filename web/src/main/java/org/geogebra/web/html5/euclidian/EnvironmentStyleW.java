package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EnvironmentStyle;

/**
 * Properties of EuclidianView that affect coordinate transformations
 */
public class EnvironmentStyleW extends EnvironmentStyle {

	private double scaleX;
	private double scaleY;
	private int xOffset;
	private int yOffset;
	private int zoomXOffset = 0;
	private int zoomYOffset = 0;
	private int scrollLeft;
	private int scrollTop;

	/**
	 * @return the scaleX
	 */
	@Override
	public double getScaleX() {
		return scaleX;
	}

	/**
	 * @param scaleX
	 *            the scaleX to set
	 */
	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}

	/**
	 * @return the scaleY
	 */
	@Override
	public double getScaleY() {
		return scaleY;
	}

	/**
	 * @param scaleY
	 *            the scaleY to set
	 */
	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}

	/**
	 * @return the xOffset
	 */
	public int getxOffset() {
		return xOffset;
	}

	/**
	 * @param xOffset
	 *            the xOffset to set
	 */
	public void setxOffset(int xOffset) {
		this.xOffset = xOffset;
	}

	/**
	 * @return the yOffset
	 */
	public int getyOffset() {
		return yOffset;
	}

	/**
	 * @param yOffset
	 *            the yOffset to set
	 */
	public void setyOffset(int yOffset) {
		this.yOffset = yOffset;
	}

	/**
	 * @return the scrollLeft
	 */
	public int getScrollLeft() {
		return scrollLeft;
	}

	/**
	 * @param scrollLeft
	 *            the scrollLeft to set
	 */
	public void setScrollLeft(int scrollLeft) {
		this.scrollLeft = scrollLeft;
	}

	/**
	 * @return the scrollTop
	 */
	public int getScrollTop() {
		return scrollTop;
	}

	/**
	 * @param scrollTop
	 *            the scrollTop to set
	 */
	public void setScrollTop(int scrollTop) {
		this.scrollTop = scrollTop;
	}

	@Override
	public String toString() {
		return " xo: " + this.xOffset + ", yo: " + this.yOffset + ", sx: "
		        + this.scaleX + ", sy: " + this.scaleY + ", scrollL: "
		        + this.scrollLeft + ", scrollt: " + this.scrollTop;
	}

	/**
	 * @return the multiplier that must be used on native event coordinates
	 */
	public double getScaleXMultiplier() {
		return (1 / getScaleX());
	}

	/**
	 * @return the multiplier that must be used on native event coordinates
	 */
	public double getScaleYMultiplier() {
		return (1 / getScaleY());
	}

	/**
	 *
	 * @return x offset of css zoom
	 */
	public int getZoomXOffset() {
		return zoomXOffset;
	}

	/**
	 * Sets x offset of css zoom.
	 *
	 * @param x to set.
	 */
	public void setZoomXOffset(int x) {
		this.zoomXOffset = x;
	}

	/**
	 *
	 * @return y offset of css zoom
	 */
	public int getZoomYOffset() {
		return zoomYOffset;
	}

	/**
	 * Sets y offset of css zoom.
	 *
	 * @param zoomYOffset
	 *            zooming y-offset
	 */
	public void setZoomYOffset(int zoomYOffset) {
		this.zoomYOffset = zoomYOffset;
	}

}