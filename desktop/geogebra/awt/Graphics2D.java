package geogebra.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.AlphaComposite;
import geogebra.common.awt.AttributedCharacterIterator;
import geogebra.common.awt.BasicStroke;
import geogebra.common.awt.BufferedImageAdapter;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPaint(Paint paint) {
		setPaint((geogebra.common.awt.Color)paint);
		
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Paint getPaint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Composite getComposite() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getColor() {
		return new geogebra.awt.Color(impl.getColor());
	}

	@Override
	public Font getFont() {
		return new geogebra.awt.Font(impl.getFont());
	}

	@Override
	public void setPaint(Color fillColor) {
		impl.setPaint(geogebra.awt.Color.getAwtColor(fillColor));
		
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
	public void draw(Object shape) {
		if(shape instanceof java.awt.Shape)
			impl.draw((java.awt.Shape)shape);
		if(shape instanceof geogebra.awt.Shape)
			impl.draw(((geogebra.awt.Shape)shape).getAwtShape());
	}

	@Override
	public void fill(Object shape) {
		if(shape instanceof java.awt.Shape)
			impl.fill((java.awt.Shape)shape);
		if(shape instanceof geogebra.awt.Shape)
			impl.fill(((geogebra.awt.Shape)shape).getAwtShape());
		
	}

	@Override
	public BasicStroke getStroke() {
		return (geogebra.awt.BasicStroke) impl.getStroke();
	}

	@Override
	public void drawImage(BufferedImageAdapter img, BufferedImageOp op, int x,
			int y) {
		impl.drawImage(BufferedImage.getAwtBufferedImage(img), (geogebra.awt.BufferedImageOp) op, x, y);
		
	}

	@Override
	public void drawImage(BufferedImageAdapter img, int x, int y,
			BufferedImageOp op) {
		impl.drawImage(BufferedImage.getAwtBufferedImage(img),(geogebra.awt.BufferedImageOp) op, x, y);
		
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
	public void setComposite(AlphaComposite alphaComp) {
		impl.setComposite((java.awt.Composite) alphaComp);
	}
	
}
