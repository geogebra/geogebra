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
import geogebra.common.awt.GLine2D;
import geogebra.common.awt.GPaint;
import geogebra.common.awt.GRenderableImage;
import geogebra.common.awt.GRenderedImage;
import geogebra.common.awt.GRenderingHints;
import geogebra.common.awt.GShape;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.factories.AwtFactory;
import geogebra.common.main.App;
import geogebra.euclidian.EuclidianViewD;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Map;

/**
 * Desktop implementation of Graphics2D; wraps the java.awt.Graphics2D class
 * 
 * @author kondr
 * 
 */
public class GGraphics2DD implements geogebra.common.awt.GGraphics2D {
	private java.awt.Graphics2D impl;

	public GGraphics2DD(java.awt.Graphics2D g2Dtemp) {
		impl = g2Dtemp;
	}

	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		impl.draw3DRect(x, y, width, height, raised);

	}

	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		impl.fill3DRect(x, y, width, height, raised);
	}

	public boolean drawImage(GImage img, GAffineTransform xform,
			GImageObserver obs) {
		// TODO Auto-generated method stub
		return false;
	}

	public void drawRenderedImage(GRenderedImage img, GAffineTransform xform) {
		// TODO Auto-generated method stub

	}

	public void drawRenderableImage(GRenderableImage img, GAffineTransform xform) {
		// TODO Auto-generated method stub

	}

	public void drawString(String str, int x, int y) {
		impl.drawString(str, x, y);

	}

	public void drawString(String str, float x, float y) {
		impl.drawString(str, x, y);

	}

	public void drawString(GAttributedCharacterIterator iterator, int x, int y) {
		// TODO Auto-generated method stub

	}

	public void drawString(GAttributedCharacterIterator iterator, float x,
			float y) {
		// TODO Auto-generated method stub

	}

	public void drawGlyphVector(GGlyphVector g, float x, float y) {
		// TODO Auto-generated method stub

	}

	public GGraphicsConfiguration getDeviceConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setComposite(GComposite comp) {
		impl.setComposite(geogebra.awt.GCompositeD.getAwtComposite(comp));
	}

	public void setPaint(GPaint paint) {
		if (paint instanceof GColor) {
			impl.setPaint(GColorD.getAwtColor((GColor) paint));
		} else if (paint instanceof GGradientPaintD) {
			impl.setPaint(((GGradientPaintD) paint).getPaint());
			return;
		} else if (paint instanceof GTexturePaintD) {
			impl.setPaint(((GTexturePaintD) paint).getPaint());
			return;
		} else {
			App.error("unknown paint type");
		}

	}

	public void setRenderingHint(GKey hintKey, Object hintValue) {
		// TODO Auto-generated method stub

	}

	public Object getRenderingHint(GKey hintKey) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRenderingHints(Map<?, ?> hints) {
		// TODO Auto-generated method stub

	}

	public void addRenderingHints(Map<?, ?> hints) {
		// TODO Auto-generated method stub

	}

	public GRenderingHints getRenderingHints() {
		// TODO Auto-generated method stub
		return null;
	}

	public void translate(int x, int y) {
		impl.translate(x, y);

	}

	public void translate(double tx, double ty) {
		impl.translate(tx, ty);

	}

	public void rotate(double theta) {
		impl.rotate(theta);

	}

	public void rotate(double theta, double x, double y) {
		impl.rotate(theta, x, y);

	}

	public void scale(double sx, double sy) {
		impl.scale(sx, sy);

	}

	public void shear(double shx, double shy) {
		impl.shear(shx, shy);

	}

	public void transform(GAffineTransform Tx) {
		impl.transform(geogebra.awt.GAffineTransformD.getAwtAffineTransform(Tx));

	}

	public void setTransform(GAffineTransform Tx) {
		impl.setTransform(geogebra.awt.GAffineTransformD
				.getAwtAffineTransform(Tx));

	}

	public GAffineTransform getTransform() {
		return new geogebra.awt.GAffineTransformD(impl.getTransform());
	}

	public GPaint getPaint() {
		java.awt.Paint paint = impl.getPaint();
		if (paint instanceof java.awt.Color)
			return new geogebra.awt.GColorD((java.awt.Color) paint);
		else if (paint instanceof java.awt.GradientPaint)
			return new geogebra.awt.GGradientPaintD(
					(java.awt.GradientPaint) paint);

		// other types of paint are currently not used in setPaint
		return null;
	}

	public GComposite getComposite() {
		return new geogebra.awt.GCompositeD(impl.getComposite());
	}

	public void setBackground(GColor color) {
		impl.setBackground(geogebra.awt.GColorD.getAwtColor(color));
	}

	public GColor getBackground() {
		return new geogebra.awt.GColorD(impl.getBackground());
	}

	public GFontRenderContext getFontRenderContext() {
		return new geogebra.awt.GFontRenderContextD(impl.getFontRenderContext());
	}

	public GColor getColor() {
		return new geogebra.awt.GColorD(impl.getColor());
	}

	public GFont getFont() {
		return new geogebra.awt.GFontD(impl.getFont());
	}

	public static java.awt.Graphics2D getAwtGraphics(
			geogebra.common.awt.GGraphics2D g2) {
		return ((GGraphics2DD) g2).impl;
	}

	public void setFont(GFont font) {
		impl.setFont(geogebra.awt.GFontD.getAwtFont(font));

	}

	public void setStroke(GBasicStroke s) {
		impl.setStroke(geogebra.awt.GBasicStrokeD.getAwtStroke(s));

	}

	public void setColor(GColor selColor) {
		impl.setColor(geogebra.awt.GColorD.getAwtColor(selColor));

	}

	public GBasicStroke getStroke() {
		return (geogebra.awt.GBasicStrokeD) impl.getStroke();
	}

	public void clip(geogebra.common.awt.GShape shape) {
		impl.clip(((geogebra.awt.GShapeD) shape).getAwtShape());
	}

	public void drawImage(GBufferedImage img, GBufferedImageOp op, int x, int y) {
		impl.drawImage(geogebra.awt.GBufferedImageD.getAwtBufferedImage(img),
				(geogebra.awt.GBufferedImageOpD) op, x, y);
	}

	public void drawImage(GBufferedImage img, int x, int y) {
		impl.drawImage(GBufferedImageD.getAwtBufferedImage(img), x, y, null);
	}

	public void drawImage(geogebra.common.awt.GImage img, int x, int y) {
		impl.drawImage(GGenericImageD.getAwtImage(img), x, y, null);

	}

	public void fillRect(int x, int y, int width, int height) {
		impl.fillRect(x, y, width, height);
	}

	public void clearRect(int x, int y, int width, int height) {
		impl.clearRect(x, y, width, height);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		impl.drawLine(x1, y1, x2, y2);
	}

	public void setClip(geogebra.common.awt.GShape shape) {
		if (shape == null) {
			impl.setClip(null);
		} else if (shape instanceof geogebra.awt.GShapeD) {
			impl.setClip(geogebra.awt.GGenericShapeD.getAwtShape(shape));
		}
	}

	public void draw(geogebra.common.awt.GShape s) {
		if (s instanceof geogebra.awt.GShapeD)
			impl.draw(((geogebra.awt.GShapeD) s).getAwtShape());
		if (s instanceof GeneralPathClipped)
			impl.draw(geogebra.awt.GGeneralPathD
					.getAwtGeneralPath(((GeneralPathClipped) s)
							.getGeneralPath()));

	}

	public void fill(geogebra.common.awt.GShape s) {
		if (s instanceof geogebra.awt.GShapeD)
			impl.fill(((geogebra.awt.GShapeD) s).getAwtShape());
		if (s instanceof GeneralPathClipped)
			impl.fill(geogebra.awt.GGeneralPathD
					.getAwtGeneralPath(((GeneralPathClipped) s)
							.getGeneralPath()));
	}

	public geogebra.common.awt.GShape getClip() {
		return new geogebra.awt.GGenericShapeD(impl.getClip());
	}

	public void drawRect(int x, int y, int width, int height) {
		impl.drawRect(x, y, width, height);

	}

	public void setClip(int x, int y, int width, int height) {
		impl.setClip(x, y, width, height);

	}

	public void setImpl(java.awt.Graphics2D g) {
		impl = g;
	}

	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		impl.drawRoundRect(x, y, width, height, arcWidth, arcHeight);

	}

	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		impl.fillRoundRect(x, y, width, height, arcWidth, arcHeight);

	}

	public void setAntialiasing() {
		EuclidianViewD.setAntialiasing(impl);

	}

	public void setTransparent() {
		impl.setComposite(AlphaComposite.Src);

	}

	public void drawWithValueStrokePure(GShape shape) {
		Object oldHint = impl
				.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
		impl.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		impl.draw(geogebra.awt.GGenericShapeD.getAwtShape(shape));
		impl.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, oldHint);

	}

	public void fillWithValueStrokePure(GShape shape) {
		// TODO Auto-generated method stub

	}

	public Object setInterpolationHint(boolean needsInterpolationRenderingHint) {
		java.awt.Graphics2D g2 = impl;
		Object oldInterpolationHint = g2
				.getRenderingHint(RenderingHints.KEY_INTERPOLATION);

		if (oldInterpolationHint == null)
			oldInterpolationHint = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

		if (needsInterpolationRenderingHint) {
			// improve rendering quality for transformed images
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		} else {
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}
		return oldInterpolationHint;
	}

	public void resetInterpolationHint(Object hint) {
		impl.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
	}

	public Graphics2D getImpl() {
		// TODO Auto-generated method stub
		return impl;
	}

	@Override
	public void updateCanvasColor() {
		// TODO Auto-generated method stub

	}

	private GLine2D line = AwtFactory.prototype.newLine2D();

	@Override
	public void drawStraightLine(double x1, double y1, double x2, double y2) {
		line.setLine(x1, y1, x2, y2);
		this.draw(line);
	}

}
