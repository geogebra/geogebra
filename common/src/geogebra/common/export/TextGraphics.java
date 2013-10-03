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
public class TextGraphics extends geogebra.common.awt.GGraphics2D{

	@Override
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		//intentionaly left blank
		
	}

	@Override
	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		//intentionaly left blank
		
	}

	@Override
	public void draw(GShape s) {
		//intentionaly left blank
		
	}

	@Override
	public boolean drawImage(GImage img, GAffineTransform xform,
			GImageObserver obs) {
		//intentionaly left blank
		return false;
	}

	@Override
	public void drawImage(GBufferedImage img, GBufferedImageOp op, int x, int y) {
		//intentionaly left blank
		
	}

	@Override
	public void drawImage(GBufferedImage img, int x, int y) {
		//intentionaly left blank
		
	}

	@Override
	public void drawRenderedImage(GRenderedImage img, GAffineTransform xform) {
		//intentionaly left blank
		
	}

	@Override
	public void drawRenderableImage(GRenderableImage img, GAffineTransform xform) {
		//intentionaly left blank
		
	}

	@Override
	public void drawString(String str, int x, int y) {
		//intentionaly left blank
		
	}

	@Override
	public void drawString(String str, float x, float y) {
		//intentionaly left blank
		
	}

	@Override
	public void drawString(GAttributedCharacterIterator iterator, int x, int y) {
		//intentionaly left blank
		
	}

	@Override
	public void drawString(GAttributedCharacterIterator iterator, float x,
			float y) {
		//intentionaly left blank
		
	}

	@Override
	public void drawGlyphVector(GGlyphVector g, float x, float y) {
		//intentionaly left blank
		
	}

	@Override
	public void fill(GShape s) {
		//intentionaly left blank
		
	}

	@Override
	public GGraphicsConfiguration getDeviceConfiguration() {
		//intentionaly left blank
		return null;
	}

	@Override
	public void setComposite(GComposite comp) {
		//intentionaly left blank
		
	}

	@Override
	public void setPaint(GPaint paint) {
		//intentionaly left blank
		
	}

	@Override
	public void setStroke(GBasicStroke s) {
		//intentionaly left blank
		
	}

	@Override
	public void setRenderingHint(GKey hintKey, Object hintValue) {
		//intentionaly left blank
		
	}

	@Override
	public Object getRenderingHint(GKey hintKey) {
		//intentionaly left blank
		return null;
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		//intentionaly left blank
		
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		//intentionaly left blank
		
	}

	@Override
	public GRenderingHints getRenderingHints() {
		//intentionaly left blank
		return null;
	}

	@Override
	public void translate(int x, int y) {
		//intentionaly left blank
		
	}

	@Override
	public void translate(double tx, double ty) {
		//intentionaly left blank
		
	}

	@Override
	public void rotate(double theta) {
		//intentionaly left blank
		
	}

	@Override
	public void rotate(double theta, double x, double y) {
		//intentionaly left blank
		
	}

	@Override
	public void scale(double sx, double sy) {
		//intentionaly left blank
		
	}

	@Override
	public void shear(double shx, double shy) {
		//intentionaly left blank
		
	}

	@Override
	public void transform(GAffineTransform Tx) {
		//intentionaly left blank
		
	}

	@Override
	public void setTransform(GAffineTransform Tx) {
		//intentionaly left blank
		
	}

	@Override
	public GAffineTransform getTransform() {
		//intentionaly left blank
		return null;
	}

	@Override
	public GPaint getPaint() {
		//intentionaly left blank
		return null;
	}

	@Override
	public GComposite getComposite() {
		//intentionaly left blank
		return null;
	}

	@Override
	public void setBackground(GColor color) {
		//intentionaly left blank
		
	}

	@Override
	public GColor getBackground() {
		//intentionaly left blank
		return null;
	}

	@Override
	public GBasicStroke getStroke() {
		//intentionaly left blank
		return null;
	}

	@Override
	public void clip(GShape s) {
		//intentionaly left blank
		
	}

	@Override
	public GFontRenderContext getFontRenderContext() {
		//intentionaly left blank
		return null;
	}

	@Override
	public GColor getColor() {
		//intentionaly left blank
		return null;
	}
	private GFont font = AwtFactory.prototype.newFont("SansSerif", GFont.PLAIN, 12);
	@Override
	public GFont getFont() {
		return font;
	}

	@Override
	public void setFont(GFont font) {
		this.font = font;		
	}

	@Override
	public void setColor(GColor selColor) {
		//intentionaly left blank
		
	}

	@Override
	public void fillRect(int i, int j, int k, int l) {
		//intentionaly left blank
		
	}

	@Override
	public void clearRect(int i, int j, int k, int l) {
		//intentionaly left blank
		
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		//intentionaly left blank
		
	}

	@Override
	public void setClip(GShape shape) {
		//intentionaly left blank
		
	}

	@Override
	public GShape getClip() {
		//intentionaly left blank
		return null;
	}

	@Override
	public void drawRect(int i, int j, int k, int l) {
		//intentionaly left blank
		
	}

	@Override
	public void setClip(int xAxisStart, int i, int width, int yAxisEnd) {
		//intentionaly left blank
		
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		//intentionaly left blank
		
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		//intentionaly left blank
		
	}

	@Override
	public void drawImage(GImage img, int x, int y) {
		//intentionaly left blank
		
	}

	@Override
	public void setAntialiasing() {
		//intentionaly left blank
		
	}

	@Override
	public void setTransparent() {
		//intentionaly left blank
		
	}

}
