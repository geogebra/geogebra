package org.geogebra.web.html5.euclidian;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GComposite;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.web.html5.awt.GFontW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;

public class GGraphics2DE implements GGraphics2DWI {

	private Element element;

	public GGraphics2DE() {
		element = DOM.createElement("canvas");
	}

	@Override
	public void draw(GShape s) {
		// not needed
	}

	@Override
	public void drawImage(GBufferedImage img, int x, int y) {
		// not needed
	}

	@Override
	public void drawImage(MyImage img, int x, int y) {
		// not needed
	}

	@Override
	public void drawString(String str, int x, int y) {
		// not needed
	}

	@Override
	public void drawString(String str, double x, double y) {
		// not needed
	}

	@Override
	public void fill(GShape s) {
		// not needed
	}

	@Override
	public void setComposite(GComposite comp) {
		// not needed
	}

	@Override
	public void setPaint(GPaint paint) {
		// not needed
	}

	@Override
	public void setStroke(GBasicStroke s) {
		// not needed
	}

	@Override
	public void setRenderingHint(int hintKey, int hintValue) {
		// not needed
	}

	@Override
	public void translate(double tx, double ty) {
		// not needed
	}

	@Override
	public void scale(double sx, double sy) {
		// not needed
	}

	@Override
	public void transform(GAffineTransform Tx) {
		// not needed
	}

	@Override
	public GComposite getComposite() {
		return null;
	}

	@Override
	public GColor getBackground() {
		return null;
	}

	@Override
	public GBasicStroke getStroke() {
		return null;
	}

	@Override
	public GFontRenderContext getFontRenderContext() {
		return null;
	}

	@Override
	public GColor getColor() {
		return null;
	}

	@Override
	public GFontW getFont() {
		return new GFontW("serif");
	}

	@Override
	public void setFont(GFont font) {
		// not needed
	}

	@Override
	public void setColor(GColor selColor) {
		// not needed
	}

	@Override
	public void fillRect(int x, int y, int w, int h) {
		// not needed
	}

	@Override
	public void clearRect(int x, int y, int w, int h) {
		// not needed
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		// not needed
	}

	@Override
	public void setClip(GShape shape) {
		// not needed
	}

	@Override
	public void setClip(GShape shape, boolean restoreSaveContext) {
		// not needed
	}

	@Override
	public void resetClip() {
		// not needed
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		// not needed
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		// not needed
	}

	@Override
	public void setClip(int x, int y, int width, int height,
			boolean restoreSaveContext) {
		// not needed
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth,
			int arcHeight) {
		// not needed
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth,
			int arcHeight) {
		// not needed
	}

	@Override
	public void setAntialiasing() {
		// not needed
	}

	@Override
	public void setTransparent() {
		// not needed
	}

	@Override
	public Object setInterpolationHint(
			boolean needsInterpolationRenderingHint) {
		return null;
	}

	@Override
	public void resetInterpolationHint(Object oldInterpolationHint) {
		// not needed
	}

	@Override
	public void updateCanvasColor() {
		// not needed
	}

	@Override
	public void drawStraightLine(double xCrossPix, double d, double xCrossPix2,
			double i) {
		// not needed
	}

	@Override
	public void saveTransform() {
		// not needed
	}

	@Override
	public void restoreTransform() {
		// not needed
	}

	@Override
	public void startGeneralPath() {
		// not needed
	}

	@Override
	public void addStraightLineToGeneralPath(double x1, double y1, double x2,
			double y2) {
		// not needed
	}

	@Override
	public void endAndDrawGeneralPath() {
		// not needed
	}

	@Override
	public void drawImage(MyImage img, int sx, int sy, int sw, int sh, int dx,
			int dy, int dw, int dh) {
		// not needed
	}

	@Override
	public void drawImage(MyImage img, int dx, int dy, int dw, int dh) {
		// not needed
	}

	@Override
	public int getOffsetWidth() {
		return 0;
	}

	@Override
	public int getOffsetHeight() {
		return 0;
	}

	@Override
	public int getCoordinateSpaceWidth() {
		return 0;
	}

	@Override
	public Canvas getCanvas() {
		return null;
	}

	@Override
	public void setDevicePixelRatio(double pixelRatio) {
		// not needed
	}

	@Override
	public void setCoordinateSpaceSize(int realWidth, int realHeight) {
		// not needed
	}

	@Override
	public double getDevicePixelRatio() {
		return 1;
	}

	@Override
	public boolean setAltText(String altStr) {
		return false;
	}

	@Override
	public int getAbsoluteTop() {
		return 0;
	}

	@Override
	public int getAbsoluteLeft() {
		return 0;
	}

	@Override
	public void startDebug() {
		// not needed
	}

	@Override
	public void setPreferredSize(GDimension preferredSize) {
		// not needed
	}

	@Override
	public int getCoordinateSpaceHeight() {
		return 600;
	}

	@Override
	public void forceResize() {
		// not needed
	}

	@Override
	public JLMContext2d getContext() {
		return null;
	}

	@Override
	public void setCoordinateSpaceSizeNoTransformNoColor(int width,
			int height) {
		// not needed
	}

	@Override
	public void clearAll() {
		// not needed
	}

	@Override
	public void fillWith(GColor backgroundCommon) {
		// not needed
	}

	@Override
	public Element getElement() {
		return element;
	}

	@Override
	public boolean isAttached() {
		return false;
	}

	@Override
	public int embed() {
		return 0;
	}

	@Override
	public void resetLayer() {
		// no layers
	}

	@Override
	public void setPreviewLayer() {
		// no layers
	}

}
