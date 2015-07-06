package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EnvironmentStyle;

public class EnvironmentStyleW extends EnvironmentStyle {

	private float scaleX;
	private float scaleY;
	private int xOffset;
	private int yOffset;
	private int scrollLeft;
	private int scrollTop;

	public EnvironmentStyleW() {
	}

	/**
	 * @return the scaleX
	 */
	public float getScaleX() {
		return scaleX;
	}

	/**
	 * @param scaleX
	 *            the scaleX to set
	 */
	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	/**
	 * @return the scaleY
	 */
	public float getScaleY() {
		return scaleY;
	}

	/**
	 * @param scaleY
	 *            the scaleY to set
	 */
	public void setScaleY(float scaleY) {
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
	public float getScaleXMultiplier() {
		return (1 / getScaleX());
	}

	/**
	 * @return the multiplier that must be used on native event coordinates
	 */
	public float getScaleYMultiplier() {
		return (1 / getScaleY());
	}

}