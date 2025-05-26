package org.geogebra.web.html5.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.web.html5.awt.GFontW;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;

import com.himamis.retex.renderer.web.graphics.JLMContext2d;

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
	JLMContext2d getContext();

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
