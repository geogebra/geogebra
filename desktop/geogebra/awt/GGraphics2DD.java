package geogebra.awt;

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
import geogebra.common.euclidian.GeneralPathClipped;

import java.util.Map;

/**
 * Desktop implementation of Graphics2D; wraps the java.awt.Graphics2D class
 * @author kondr
 *
 */
public class GGraphics2DD extends geogebra.common.awt.GGraphics2D{
	private java.awt.Graphics2D impl;

	public GGraphics2DD(java.awt.Graphics2D g2Dtemp) {
		impl = g2Dtemp;
	}

	@Override
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		impl.draw3DRect(x, y, width, height, raised);
		
	}

	@Override
	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		impl.fill3DRect(x, y, width, height, raised);
		
	}

	@Override
	public boolean drawImage(GImage img, GAffineTransform xform, GImageObserver obs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawRenderedImage(GRenderedImage img, GAffineTransform xform) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawRenderableImage(GRenderableImage img, GAffineTransform xform) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawString(String str, int x, int y) {
		impl.drawString(str, x, y);
		
	}

	@Override
	public void drawString(String str, float x, float y) {
		impl.drawString(str, x, y);
		
	}

	@Override
	public void drawString(GAttributedCharacterIterator iterator, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawString(GAttributedCharacterIterator iterator, float x,
			float y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawGlyphVector(GGlyphVector g, float x, float y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGraphicsConfiguration getDeviceConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setComposite(GComposite comp) {
		impl.setComposite(geogebra.awt.GCompositeD.getAwtComposite(comp));
	}

	@Override
	public void setPaint(GPaint paint) {
		if(paint instanceof geogebra.awt.GGradientPaintD){
			impl.setPaint(((geogebra.awt.GGradientPaintD)paint).getPaint());
			return;
		}else if(paint instanceof GColor){
			impl.setPaint(geogebra.awt.GColorD.getAwtColor((GColor)paint));
		}
		
	}

	@Override
	public void setRenderingHint(GKey hintKey, Object hintValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getRenderingHint(GKey hintKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GRenderingHints getRenderingHints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void translate(int x, int y) {
		impl.translate(x, y);
		
	}

	@Override
	public void translate(double tx, double ty) {
		impl.translate(tx, ty);
		
	}

	@Override
	public void rotate(double theta) {
		impl.rotate(theta);
		
	}

	@Override
	public void rotate(double theta, double x, double y) {
		impl.rotate(theta, x, y);
		
	}

	@Override
	public void scale(double sx, double sy) {
		impl.scale(sx, sy);
		
	}

	@Override
	public void shear(double shx, double shy) {
		impl.shear(shx, shy);
		
	}

	@Override
	public void transform(GAffineTransform Tx) {
		impl.transform(geogebra.awt.GAffineTransformD.getAwtAffineTransform(Tx));
		
	}

	@Override
	public void setTransform(GAffineTransform Tx) {
		impl.setTransform(geogebra.awt.GAffineTransformD.getAwtAffineTransform(Tx));
		
	}

	@Override
	public GAffineTransform getTransform() {
		return new geogebra.awt.GAffineTransformD(impl.getTransform());
	}

	@Override
	public GPaint getPaint() {
		java.awt.Paint paint = impl.getPaint();
		if (paint instanceof java.awt.Color)
			return new geogebra.awt.GColorD((java.awt.Color)paint);
		else if (paint instanceof java.awt.GradientPaint)
			return new geogebra.awt.GGradientPaintD((java.awt.GradientPaint)paint);

		// other types of paint are currently not used in setPaint
		return null;
	}

	@Override
	public GComposite getComposite() {
		return new geogebra.awt.GCompositeD(impl.getComposite());
	}

	@Override
	public void setBackground(GColor color) {
		impl.setBackground(geogebra.awt.GColorD.getAwtColor(color));
	}

	@Override
	public GColor getBackground() {
		return new geogebra.awt.GColorD(impl.getBackground());
	}

	@Override
	public GFontRenderContext getFontRenderContext() {
		return new geogebra.awt.GFontRenderContextD(impl.getFontRenderContext());
	}

	@Override
	public GColor getColor() {
		return new geogebra.awt.GColorD(impl.getColor());
	}

	@Override
	public GFont getFont() {
		return new geogebra.awt.GFontD(impl.getFont());
	}

	

	public static java.awt.Graphics2D getAwtGraphics(geogebra.common.awt.GGraphics2D g2) {
		return ((GGraphics2DD)g2).impl;
	}

	@Override
	public void setFont(GFont font) {
		impl.setFont(geogebra.awt.GFontD.getAwtFont(font));
		
	}

	@Override
	public void setStroke(GBasicStroke s) {
		impl.setStroke(geogebra.awt.GBasicStrokeD.getAwtStroke(s));
		
	}

	@Override
	public void setColor(GColor selColor) {
		impl.setColor(geogebra.awt.GColorD.getAwtColor(selColor));
		
	}

	@Override
	public GBasicStroke getStroke() {
		return (geogebra.awt.GBasicStrokeD) impl.getStroke();
	}

	@Override
	public void clip(geogebra.common.awt.GShape shape) {
		impl.clip(((geogebra.awt.GShapeD)shape).getAwtShape());
	}

	@Override
	public void drawImage(GBufferedImage img, GBufferedImageOp op, int x,
			int y) {
		impl.drawImage(geogebra.awt.GBufferedImageD.getAwtBufferedImage(img), (geogebra.awt.GBufferedImageOpD) op, x, y);
	}
	
	@Override
	public void drawImage(GBufferedImage img, int x, int y){
		impl.drawImage(GBufferedImageD.getAwtBufferedImage(img), x, y, null);
	}
	
	@Override
	public void drawImage(geogebra.common.awt.GImage img, int x, int y) {
		impl.drawImage(GGenericImageD.getAwtImage(img), x, y, null);
		
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		impl.fillRect(x, y, width, height);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		impl.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void setClip(geogebra.common.awt.GShape shape) {
		if (shape == null){
			impl.setClip(null);
		} else if (shape instanceof geogebra.awt.GShapeD){
			impl.setClip(geogebra.awt.GGenericShapeD.getAwtShape(shape));
		}
	}

	@Override
	public void draw(geogebra.common.awt.GShape s) {
		if (s instanceof geogebra.awt.GShapeD)
			impl.draw(((geogebra.awt.GShapeD)s).getAwtShape());
		if(s instanceof GeneralPathClipped)
			impl.draw(geogebra.awt.GGeneralPathD.getAwtGeneralPath(((GeneralPathClipped)s).getGeneralPath()));

		
	}

	@Override
	public void fill(geogebra.common.awt.GShape s) {
		if (s instanceof geogebra.awt.GShapeD)
			impl.fill(((geogebra.awt.GShapeD)s).getAwtShape());
		if(s instanceof GeneralPathClipped)
			impl.fill(geogebra.awt.GGeneralPathD.getAwtGeneralPath(((GeneralPathClipped)s).getGeneralPath()));
	}

	@Override
	public geogebra.common.awt.GShape getClip() {
		return new geogebra.awt.GGenericShapeD(impl.getClip());
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		impl.drawRect(x, y, width, height);
		
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		impl.setClip(x, y, width, height);
		
	}

	public void setImpl(java.awt.Graphics2D g) {
		impl = g;
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		impl.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
		
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		impl.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
		
	}
	
}
