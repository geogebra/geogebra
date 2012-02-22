package geogebra.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.AlphaComposite;
import geogebra.common.awt.AttributedCharacterIterator;
import geogebra.common.awt.BasicStroke;
import geogebra.common.awt.BufferedImage;
import geogebra.common.awt.BufferedImageOp;
import geogebra.common.awt.Color;
import geogebra.common.awt.Composite;
import geogebra.common.awt.Font;
import geogebra.common.awt.FontRenderContext;
import geogebra.common.awt.GlyphVector;
import geogebra.common.awt.GraphicsConfiguration;
import geogebra.common.awt.Image;
import geogebra.common.awt.ImageObserver;
import geogebra.common.awt.Key;
import geogebra.common.awt.Paint;
import geogebra.common.awt.RenderableImage;
import geogebra.common.awt.RenderedImage;
import geogebra.common.awt.RenderingHints;
import geogebra.common.euclidian.GeneralPathClipped;

import java.awt.Shape;
import java.util.Map;

/**
 * Desktop implementation of Graphics2D; wraps the java.awt.Graphics2D class
 * @author kondr
 *
 */
public class Graphics2D extends geogebra.common.awt.Graphics2D{
	private java.awt.Graphics2D impl;

	public Graphics2D(java.awt.Graphics2D g2Dtemp) {
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
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
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
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x,
			float y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setComposite(Composite comp) {
		impl.setComposite(geogebra.awt.Composite.getAwtComposite(comp));
	}

	@Override
	public void setPaint(Paint paint) {
		if(paint instanceof geogebra.awt.GradientPaint){
			impl.setPaint(((geogebra.awt.GradientPaint)paint).getPaint());
			return;
		}else if(paint instanceof Color){
			impl.setPaint(geogebra.awt.Color.getAwtColor((Color)paint));
		}
		
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
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
	public RenderingHints getRenderingHints() {
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
	public void transform(AffineTransform Tx) {
		impl.transform(geogebra.awt.AffineTransform.getAwtAffineTransform(Tx));
		
	}

	@Override
	public void setTransform(AffineTransform Tx) {
		impl.setTransform(geogebra.awt.AffineTransform.getAwtAffineTransform(Tx));
		
	}

	@Override
	public AffineTransform getTransform() {
		return new geogebra.awt.AffineTransform(impl.getTransform());
	}

	@Override
	public Paint getPaint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Composite getComposite() {
		return new geogebra.awt.Composite(impl.getComposite());
	}

	@Override
	public void setBackground(Color color) {
		impl.setColor(geogebra.awt.Color.getAwtColor(color));
	}

	@Override
	public Color getBackground() {
		return new geogebra.awt.Color(impl.getColor());
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return new geogebra.awt.FontRenderContext(impl.getFontRenderContext());
	}

	@Override
	public Color getColor() {
		return new geogebra.awt.Color(impl.getColor());
	}

	@Override
	public Font getFont() {
		return new geogebra.awt.Font(impl.getFont());
	}

	

	public static java.awt.Graphics2D getAwtGraphics(geogebra.common.awt.Graphics2D g2) {
		return ((Graphics2D)g2).impl;
	}

	@Override
	public void setFont(Font font) {
		impl.setFont(geogebra.awt.Font.getAwtFont(font));
		
	}

	@Override
	public void setStroke(BasicStroke s) {
		impl.setStroke(geogebra.awt.BasicStroke.getAwtStroke(s));
		
	}

	@Override
	public void setColor(Color selColor) {
		impl.setColor(geogebra.awt.Color.getAwtColor(selColor));
		
	}

	@Override
	public BasicStroke getStroke() {
		return (geogebra.awt.BasicStroke) impl.getStroke();
	}

	@Override
	public void clip(geogebra.common.awt.Shape shape) {
		impl.clip(((geogebra.awt.Shape)shape).getAwtShape());
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x,
			int y) {
		impl.drawImage(geogebra.awt.BufferedImage.getAwtBufferedImage(img), (geogebra.awt.BufferedImageOp) op, x, y);
		
	}

	@Override
	public void drawImage(BufferedImage img, int x, int y,
			BufferedImageOp op) {
		impl.drawImage(geogebra.awt.BufferedImage.getAwtBufferedImage(img),(geogebra.awt.BufferedImageOp) op, x, y);
		
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
	public void setClip(geogebra.common.awt.Shape shape) {
		if (shape == null){
			impl.setClip(null);
		} else if (shape instanceof geogebra.common.awt.Shape){
			impl.setClip(geogebra.awt.GenericShape.getAwtShape((geogebra.awt.Shape)shape));
		}
	}

	@Override
	public void draw(geogebra.common.awt.Shape s) {
		if (s instanceof geogebra.awt.Shape)
			impl.draw(((geogebra.awt.Shape)s).getAwtShape());
		if(s instanceof GeneralPathClipped)
			impl.draw(geogebra.awt.GeneralPath.getAwtGeneralPath(((GeneralPathClipped)s).getGeneralPath()));

		
	}

	@Override
	public void fill(geogebra.common.awt.Shape s) {
		if (s instanceof geogebra.awt.Shape)
			impl.fill(((geogebra.awt.Shape)s).getAwtShape());
		if(s instanceof GeneralPathClipped)
			impl.fill(geogebra.awt.GeneralPath.getAwtGeneralPath(((GeneralPathClipped)s).getGeneralPath()));

	}

	@Override
	public geogebra.common.awt.Shape getClip() {
		return new geogebra.awt.GenericShape(impl.getClip());
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
