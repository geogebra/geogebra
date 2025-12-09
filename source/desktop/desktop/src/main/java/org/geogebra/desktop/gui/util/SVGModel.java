/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
