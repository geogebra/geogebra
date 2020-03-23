package org.geogebra.web.html5.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.web.html5.awt.GFontW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;

public interface GGraphics2DWI extends GGraphics2D {

	int getOffsetWidth();

	int getOffsetHeight();

	int getCoordinateSpaceWidth();

	Canvas getCanvas();

	void setDevicePixelRatio(double pixelRatio);

	void setCoordinateSpaceSize(int realWidth, int realHeight);

	double getDevicePixelRatio();

	boolean setAltText(String altStr);

	int getAbsoluteTop();

	int getAbsoluteLeft();

	void startDebug();

	void setPreferredSize(GDimension preferredSize);

	int getCoordinateSpaceHeight();

	@Override
	GFontW getFont();

	void forceResize();

	JLMContext2d getContext();

	void setCoordinateSpaceSizeNoTransformNoColor(int width, int height);

	void clearAll();

	void fillWith(GColor backgroundCommon);

	/**
	 * @return canvas element
	 */
	Element getElement();

	/**
	 * @return whether the graphics is attached to DOM
	 */
	boolean isAttached();

	int embed();

	void resetLayer();

	void setPreviewLayer();
}
