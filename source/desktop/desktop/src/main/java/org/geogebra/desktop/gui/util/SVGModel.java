package org.geogebra.desktop.gui.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.geogebra.common.awt.GColor;

public interface SVGModel {
	/**
	 * @return image width
	 */
	int getWidth();

	/**
	 * @return image height
	 */
	int getHeight();

	/**
	 * @param color fill color
	 */
	void setFill(GColor color);

	/**
	 * Paint the image onto a graphics.
	 * @param g graphics
	 */
	void paint(Graphics2D g);

	/**
	 * @return whether the SVG document is invalid
	 */
	boolean isInvalid();

	/**
	 * @param transform affine transform
	 */
	void setTransform(AffineTransform transform);

	/**
	 * @return SVG content
	 */
	String getContent();
}
