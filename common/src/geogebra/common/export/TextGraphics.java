package geogebra.common.export;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GAttributedCharacterIterator;
import geogebra.common.awt.GBasicStroke;
import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GBufferedImageOp;
import geogebra.common.awt.GColor;
import geogebra.common.awt.GComposite;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GFontRenderContext;
import geogebra.common.awt.GGlyphVector;
import geogebra.common.awt.GGraphicsConfiguration;
import geogebra.common.awt.GImage;
import geogebra.common.awt.GImageObserver;
import geogebra.common.awt.GKey;
import geogebra.common.awt.GPaint;
import geogebra.common.awt.GRenderableImage;
import geogebra.common.awt.GRenderedImage;
import geogebra.common.awt.GRenderingHints;
import geogebra.common.awt.GShape;
import geogebra.common.factories.AwtFactory;

import java.util.Map;

/**
 * Dummy implementation that can be extended for text format exports (PSTRICKS, PGF, ASY, EPS, SVG)
 * in the future.
 *
 */
public class TextGraphics implements geogebra.common.awt.GGraphics2D{


	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		//intentionaly left blank
		
	}


	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		//intentionaly left blank
		
	}


	public void draw(GShape s) {
		//intentionaly left blank
		
	}


	public boolean drawImage(GImage img, GAffineTransform xform,
			GImageObserver obs) {
		//intentionaly left blank
		return false;
	}


	public void drawImage(GBufferedImage img, GBufferedImageOp op, int x, int y) {
		//intentionaly left blank
		
	}


	public void drawImage(GBufferedImage img, int x, int y) {
		//intentionaly left blank
		
	}


	public void drawRenderedImage(GRenderedImage img, GAffineTransform xform) {
		//intentionaly left blank
		
	}


	public void drawRenderableImage(GRenderableImage img, GAffineTransform xform) {
		//intentionaly left blank
		
	}


	public void drawString(String str, int x, int y) {
		//intentionaly left blank
		
	}


	public void drawString(String str, float x, float y) {
		//intentionaly left blank
		
	}


	public void drawString(GAttributedCharacterIterator iterator, int x, int y) {
		//intentionaly left blank
		
	}


	public void drawString(GAttributedCharacterIterator iterator, float x,
			float y) {
		//intentionaly left blank
		
	}


	public void drawGlyphVector(GGlyphVector g, float x, float y) {
		//intentionaly left blank
		
	}


	public void fill(GShape s) {
		//intentionaly left blank
		
	}


	public GGraphicsConfiguration getDeviceConfiguration() {
		//intentionaly left blank
		return null;
	}


	public void setComposite(GComposite comp) {
		//intentionaly left blank
		
	}


	public void setPaint(GPaint paint) {
		//intentionaly left blank
		
	}


	public void setStroke(GBasicStroke s) {
		//intentionaly left blank
		
	}


	public void setRenderingHint(GKey hintKey, Object hintValue) {
		//intentionaly left blank
		
	}


	public Object getRenderingHint(GKey hintKey) {
		//intentionaly left blank
		return null;
	}


	public void setRenderingHints(Map<?, ?> hints) {
		//intentionaly left blank
		
	}


	public void addRenderingHints(Map<?, ?> hints) {
		//intentionaly left blank
		
	}


	public GRenderingHints getRenderingHints() {
		//intentionaly left blank
		return null;
	}


	public void translate(int x, int y) {
		//intentionaly left blank
		
	}


	public void translate(double tx, double ty) {
		//intentionaly left blank
		
	}


	public void rotate(double theta) {
		//intentionaly left blank
		
	}


	public void rotate(double theta, double x, double y) {
		//intentionaly left blank
		
	}


	public void scale(double sx, double sy) {
		//intentionaly left blank
		
	}


	public void shear(double shx, double shy) {
		//intentionaly left blank
		
	}


	public void transform(GAffineTransform Tx) {
		//intentionaly left blank
		
	}


	public void setTransform(GAffineTransform Tx) {
		//intentionaly left blank
		
	}


	public GAffineTransform getTransform() {
		//intentionaly left blank
		return null;
	}


	public GPaint getPaint() {
		//intentionaly left blank
		return null;
	}


	public GComposite getComposite() {
		//intentionaly left blank
		return null;
	}


	public void setBackground(GColor color) {
		//intentionaly left blank
		
	}


	public GColor getBackground() {
		//intentionaly left blank
		return null;
	}


	public GBasicStroke getStroke() {
		//intentionaly left blank
		return null;
	}


	public void clip(GShape s) {
		//intentionaly left blank
		
	}


	public GFontRenderContext getFontRenderContext() {
		//intentionaly left blank
		return null;
	}


	public GColor getColor() {
		//intentionaly left blank
		return null;
	}
	private GFont font = AwtFactory.prototype.newFont("SansSerif", GFont.PLAIN, 12);

	public GFont getFont() {
		return font;
	}


	public void setFont(GFont font) {
		this.font = font;		
	}


	public void setColor(GColor selColor) {
		//intentionaly left blank
		
	}


	public void fillRect(int i, int j, int k, int l) {
		//intentionaly left blank
		
	}


	public void clearRect(int i, int j, int k, int l) {
		//intentionaly left blank
		
	}


	public void drawLine(int x1, int y1, int x2, int y2) {
		//intentionaly left blank
		
	}


	public void setClip(GShape shape) {
		//intentionaly left blank
		
	}


	public GShape getClip() {
		//intentionaly left blank
		return null;
	}


	public void drawRect(int i, int j, int k, int l) {
		//intentionaly left blank
		
	}


	public void setClip(int xAxisStart, int i, int width, int yAxisEnd) {
		//intentionaly left blank
		
	}


	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		//intentionaly left blank
		
	}


	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		//intentionaly left blank
		
	}


	public void drawImage(GImage img, int x, int y) {
		//intentionaly left blank
		
	}


	public void setAntialiasing() {
		//intentionaly left blank
		
	}


	public void setTransparent() {
		//intentionaly left blank
		
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


	public void resetInterpolationHint(Object oldInterpolationHint) {
		
	}

}
