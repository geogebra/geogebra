package org.geogebra.common.export;

import java.util.Map;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GBufferedImageOp;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GComposite;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GImage;
import org.geogebra.common.awt.GKey;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.factories.AwtFactory;

/**
 * Dummy implementation that can be extended for text format exports (PSTRICKS,
 * PGF, ASY, EPS, SVG) in the future.
 *
 */
public class TextGraphics implements org.geogebra.common.awt.GGraphics2D {

	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		// intentionaly left blank

	}

	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		// intentionaly left blank

	}

	public void draw(GShape s) {
		// intentionaly left blank

	}

	public void drawImage(GBufferedImage img, GBufferedImageOp op, int x, int y) {
		// intentionaly left blank

	}

	public void drawImage(GBufferedImage img, int x, int y) {
		// intentionaly left blank

	}

	public void drawImage(MyImage img, GBufferedImageOp op, int x, int y) {
		// intentionally left blank

	}

	public void drawImage(MyImage img, int x, int y) {
		// intentionally left blank

	}

	public void drawString(String str, int x, int y) {
		// intentionaly left blank

	}

	public void drawString(String str, float x, float y) {
		// intentionaly left blank

	}

	public void fill(GShape s) {
		// intentionaly left blank

	}

	public void setComposite(GComposite comp) {
		// intentionaly left blank

	}

	public void setPaint(GPaint paint) {
		// intentionaly left blank

	}

	public void setStroke(GBasicStroke s) {
		// intentionaly left blank

	}

	public void setRenderingHint(GKey hintKey, Object hintValue) {
		// intentionaly left blank

	}

	public Object getRenderingHint(GKey hintKey) {
		// intentionaly left blank
		return null;
	}

	public void setRenderingHints(Map<?, ?> hints) {
		// intentionaly left blank

	}

	public void translate(double tx, double ty) {
		// intentionaly left blank

	}

	public void scale(double sx, double sy) {
		// intentionaly left blank

	}

	public void transform(GAffineTransform Tx) {
		// intentionaly left blank

	}

	public void setTransform(GAffineTransform Tx) {
		// intentionaly left blank

	}

	public GAffineTransform getTransform() {
		// intentionaly left blank
		return null;
	}

	public GComposite getComposite() {
		// intentionaly left blank
		return null;
	}

	public GColor getBackground() {
		// intentionaly left blank
		return null;
	}

	public GBasicStroke getStroke() {
		// intentionaly left blank
		return null;
	}

	public void clip(GShape s) {
		// intentionaly left blank

	}

	public GFontRenderContext getFontRenderContext() {
		// intentionaly left blank
		return null;
	}

	public GColor getColor() {
		// intentionaly left blank
		return null;
	}

	private GFont font = AwtFactory.prototype.newFont("SansSerif", GFont.PLAIN,
			12);

	public GFont getFont() {
		return font;
	}

	public void setFont(GFont font) {
		this.font = font;
	}

	public void setColor(GColor selColor) {
		// intentionaly left blank

	}

	public void fillRect(int i, int j, int k, int l) {
		// intentionaly left blank

	}

	public void clearRect(int i, int j, int k, int l) {
		// intentionaly left blank

	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		// intentionaly left blank

	}

	public void setClip(GShape shape) {
		// intentionaly left blank

	}

	public GShape getClip() {
		// intentionaly left blank
		return null;
	}

	public void drawRect(int i, int j, int k, int l) {
		// intentionaly left blank

	}

	public void setClip(int xAxisStart, int i, int width, int yAxisEnd) {
		// intentionaly left blank

	}

	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		// intentionaly left blank

	}

	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		// intentionaly left blank

	}

	public void drawImage(GImage img, int x, int y) {
		// intentionaly left blank

	}

	public void setAntialiasing() {
		// intentionaly left blank

	}

	public void setTransparent() {
		// intentionaly left blank

	}

	public void drawWithValueStrokePure(GShape shape) {
		draw(shape);
	}

	public void fillWithValueStrokePure(GShape shape) {
		fill(shape);
	}

	public Object setInterpolationHint(boolean needsInterpolationRenderingHint) {
		return null;
	}

	@Override
	public void updateCanvasColor() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetInterpolationHint(Object oldInterpolationHint) {
		// TODO Auto-generated method stub

	}

	private GLine2D line = AwtFactory.prototype.newLine2D();

	@Override
	public void drawStraightLine(double x1, double y1, double x2, double y2) {
		line.setLine(x1, y1, x2, y2);
		this.draw(line);
	}

}
