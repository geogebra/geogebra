package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GPaint;

/**
 * Pattern for SVG painting
 *
 */
public class GPaintSVG implements GPaint {

	private String path;
	private String style;
	private String fill;
	private double width;
	private double height;
	private double angle;

	/**
	 * @param path0
	 *            fill path
	 * @param style0
	 *            style
	 * @param width0
	 *            width
	 * @param height0
	 *            height
	 * @param angle0
	 *            hatching angle
	 * @param fill0
	 *            color #code
	 */
	public GPaintSVG(String path0, String style0, double width0,
			double height0, double angle0, String fill0) {
		this.path = path0;
		this.style = style0;
		this.width = width0;
		this.height = height0;
		this.angle = angle0;
		this.fill = fill0;
	}

	/**
	 * @return style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * @return width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @return height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * @return hatching path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return hatching angle
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * @return fill color #code
	 */
	public String getFill() {
		return fill;
	}

}
