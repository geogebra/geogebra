package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GPaint;

public class GPaintSVG implements GPaint {

	private String path, style, fill;
	private double width, height, angle;

	public GPaintSVG(String path0, String style0, double width0,
			double height0, double angle0, String fill0) {
		this.path = path0;
		this.style = style0;
		this.width = width0;
		this.height = height0;
		this.angle = angle0;
		this.fill = fill0;
	}

	public String getStyle() {
		return style;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public String getPath() {
		return path;
	}

	public double getAngle() {
		return angle;
	}

	public String getFill() {
		return fill;
	}

}
