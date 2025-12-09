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

package org.geogebra.web.awt;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GGraphics2D;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;

/**
 * 2D graphics with Web-specific methods.
 */
public interface GGraphics2DWI extends GGraphics2D {

	/**
	 * @return offset width
	 */
	int getOffsetWidth();

	/**
	 * @return offset height
	 */
	int getOffsetHeight();

	/**
	 * @return width in logical pixels
	 */
	int getCoordinateSpaceWidth();

	/**
	 * @return the canvas
	 */
	Canvas getCanvas();

	/**
	 * Set device pixel ratio.
	 * @param pixelRatio pixel ratio
	 */
	void setDevicePixelRatio(double pixelRatio);

	/**
	 * Set the logical (Canvas) and physical (CSS) size of the graphics,
	 * make sure color and transform are reset.
	 * @param realWidth width
	 * @param realHeight height
	 */
	void setCoordinateSpaceSize(int realWidth, int realHeight);

	/**
	 * @return stored device pixel ratio
	 */
	double getDevicePixelRatio();

	/**
	 * @return absolute vertical offset within the window
	 */
	int getAbsoluteTop();

	/**
	 * @return absolute horizontal offset within the window
	 */
	int getAbsoluteLeft();

	/**
	 * Turn on debug mode (to show line-to, move-to, curve-to points)
	 */
	void startDebug();

	/**
	 * Set both physical (CSS) and logical (canvas) size of the graphics.
	 * @param preferredSize preferred dimensions
	 */
	void setPreferredSize(GDimension preferredSize);

	/**
	 * @return height in logical pixels
	 */
	int getCoordinateSpaceHeight();

	/**
	 * @return current font
	 */
	@Override
	GFontW getFont();

	/**
	 * Resize twice to force context reset.
	 */
	void forceResize();

	/**
	 * @return rendering context
	 */
	JLMContext2D getContext();

	/**
	 * Set the size of the graphics without resetting color or transform.
	 * @param width width
	 * @param height height
	 */
	void setCoordinateSpaceSizeNoTransformNoColor(int width, int height);

	/**
	 * Clears the whole graphics.
	 */
	void clearAll();

	/**
	 * Fill the graphics with a single color
	 * @param color fill color
	 */
	void fillWith(GColor color);

	/**
	 * @return canvas element
	 */
	Element getElement();

	/**
	 * @return whether the graphics is attached to DOM
	 */
	boolean isAttached();

	/**
	 * Add a layer for an embedded element
	 * @return new layer
	 */
	int embed();

	/**
	 * Remove all layers other than the base one (reset layer counter to 0).
	 */
	void resetLayer();
}
