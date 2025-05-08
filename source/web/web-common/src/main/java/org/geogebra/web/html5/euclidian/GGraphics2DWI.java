package org.geogebra.web.html5.euclidian;

import org.geogebra.common.annotation.MissingDoc;
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

	@MissingDoc
	int getOffsetWidth();

	@MissingDoc
	int getOffsetHeight();

	@MissingDoc
	int getCoordinateSpaceWidth();

	@MissingDoc
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

	@MissingDoc
	double getDevicePixelRatio();

	@MissingDoc
	int getAbsoluteTop();

	@MissingDoc
	int getAbsoluteLeft();

	@MissingDoc
	void startDebug();

	/**
	 * Set both physical (CSS) and logical (canvas) size of the graphics.
	 * @param preferredSize preferred dimensions
	 */
	void setPreferredSize(GDimension preferredSize);

	@MissingDoc
	int getCoordinateSpaceHeight();

	@Override
	GFontW getFont();

	@MissingDoc
	void forceResize();

	@MissingDoc
	JLMContext2d getContext();

	/**
	 * Set the size of the graphics without resetting color or transform.
	 * @param width width
	 * @param height height
	 */
	void setCoordinateSpaceSizeNoTransformNoColor(int width, int height);

	@MissingDoc
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

	@MissingDoc
	int embed();

	@MissingDoc
	void resetLayer();
}
