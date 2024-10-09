package org.geogebra.desktop.awt;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Stack;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GComposite;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.factories.AwtFactoryD;
import org.geogebra.desktop.gui.MyImageD;

/**
 * Desktop implementation of Graphics2D; wraps the java.awt.Graphics2D class
 * 
 * @author Zbynek
 * 
 */
public class GGraphics2DD implements GGraphics2D {

	private final Stack<Shape> clipStack;
	private Graphics2D impl;

	/**
	 * 
	 * @param g2Dtemp
	 *            wrapped graphics
	 */
	public GGraphics2DD(Graphics2D g2Dtemp) {
		impl = g2Dtemp;
		clipStack = new Stack<>();
	}

	@Override
	public void drawString(String str, int x, int y) {
		impl.drawString(str, x, y);
	}

	@Override
	public void drawString(String str, double x, double y) {
		impl.drawString(str, (float) x, (float) y);
	}

	@Override
	public void setComposite(GComposite comp) {
		impl.setComposite(GCompositeD.getAwtComposite(comp));
	}

	@Override
	public void setPaint(GPaint paint) {
		if (paint instanceof GColor) {
			impl.setPaint(GColorD.getAwtColor((GColor) paint));
		} else if (paint instanceof GGradientPaintD) {
			impl.setPaint(((GGradientPaintD) paint).getPaint());
		} else if (paint instanceof GTexturePaintD) {
			impl.setPaint(((GTexturePaintD) paint).getPaint());
		} else {
			Log.error("unknown paint type");
		}
	}

	private static Key getAwtHintKey(int key) {

		switch (key) {
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints
				.KEY_ANTIALIASING:
			return RenderingHints.KEY_ANTIALIASING;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints
				.KEY_RENDERING:
			return RenderingHints.KEY_RENDERING;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints
				.KEY_TEXT_ANTIALIASING:
			return RenderingHints.KEY_TEXT_ANTIALIASING;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints
				.KEY_INTERPOLATION:
			return RenderingHints.KEY_INTERPOLATION;
		}

		return null;
	}

	private static Object getAwtHintValue(int value) {

		switch (value) {
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints
				.VALUE_ANTIALIAS_ON:
			return RenderingHints.VALUE_ANTIALIAS_ON;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints
				.VALUE_RENDER_QUALITY:
			return RenderingHints.VALUE_RENDER_QUALITY;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints
				.VALUE_TEXT_ANTIALIAS_ON:
			return RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints
				.VALUE_INTERPOLATION_BILINEAR:
			return RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints
				.VALUE_INTERPOLATION_NEAREST_NEIGHBOR:
			return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints
				.VALUE_INTERPOLATION_BICUBIC:
			return RenderingHints.VALUE_INTERPOLATION_BICUBIC;

		}

		return null;
	}

	@Override
	public void setRenderingHint(int key, int value) {
		impl.setRenderingHint(getAwtHintKey(key), getAwtHintValue(value));
	}

	@Override
	public void translate(double tx, double ty) {
		impl.translate(tx, ty);
	}

	@Override
	public void scale(double sx, double sy) {
		impl.scale(sx, sy);
	}

	@Override
	public void transform(GAffineTransform Tx) {
		impl.transform(GAffineTransformD.getAwtAffineTransform(Tx));
	}

	@Override
	public GComposite getComposite() {
		return new GCompositeD(impl.getComposite());
	}

	@Override
	public GColor getBackground() {
		return GColorD.newColor(impl.getBackground());
	}

	@Override
	public GFontRenderContext getFontRenderContext() {
		return new GFontRenderContextD(impl.getFontRenderContext());
	}

	@Override
	public GColor getColor() {
		return GColorD.newColor(impl.getColor());
	}

	@Override
	public GFont getFont() {
		return new GFontD(impl.getFont());
	}

	/**
	 * @param g2
	 *            graphics object
	 * @return wrapped implementation
	 */
	public static Graphics2D getAwtGraphics(GGraphics2D g2) {
		return ((GGraphics2DD) g2).impl;
	}

	@Override
	public void setFont(GFont font) {
		impl.setFont(GFontD.getAwtFont(font));
	}

	@Override
	public void setStroke(GBasicStroke s) {
		impl.setStroke(AwtFactoryD.getAwtStroke(s));
	}

	@Override
	public void setColor(GColor selColor) {
		impl.setColor(GColorD.getAwtColor(selColor));
	}

	@Override
	public void drawImage(GBufferedImage img, int x, int y) {
		impl.drawImage(GBufferedImageD.getAwtBufferedImage(img), x, y, null);
	}

	@Override
	public void drawImage(MyImage img, int x, int y) {
		MyImageD imgD = (MyImageD) img;
		imgD.render(impl, x, y);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		impl.fillRect(x, y, width, height);
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		impl.clearRect(x, y, width, height);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		impl.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void setClip(GShape shape, boolean saveContext) {
		setClip(shape);
	}

	@Override
	public void setClip(GShape shape) {
		if (shape == null) {
			resetClip();
		} else if (shape instanceof GShapeD) {
			Shape oldClip = impl.getClip();
			if (oldClip == null) {
				impl.setClip(GGenericShapeD.getAwtShape(shape));
				return;
			}
			impl.clip(GGenericShapeD.getAwtShape(shape));
			clipStack.push(oldClip);
		}
	}

	@Override
	public void resetClip() {
		if (clipStack.isEmpty()) {
			impl.setClip(null);
		} else {
			impl.setClip(clipStack.pop());
		}
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		impl.drawRect(x, y, width, height);
	}

	@Override
	public void setClip(int x, int y, int width, int height,
			boolean saveContext) {
		setClip(x, y, width, height);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		Shape oldClip = impl.getClip();
		if (oldClip == null) {
			impl.setClip(x, y, width, height);
			return;
		}
		impl.clipRect(x, y, width, height);
		clipStack.push(oldClip);
	}

	/**
	 * Replace wrapped graphics
	 * 
	 * @param g
	 *            implementation
	 */
	public void setImpl(Graphics2D g) {
		impl = g;
		clipStack.clear();
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth,
			int arcHeight) {
		impl.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth,
			int arcHeight) {
		impl.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void setAntialiasing() {
		setAntialiasing(impl);
	}

	/**
	 * @param g2
	 *            initialise g2 for best quality rendering
	 */
	public static void setAntialiasing(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
	}

	@Override
	public void setTransparent() {
		impl.setComposite(AlphaComposite.Src);
	}

	@Override
	public void draw(GShape shape) {
		impl.draw(GGenericShapeD.getAwtShape(shape));
	}

	@Override
	public void fill(GShape shape) {
		impl.fill(GGenericShapeD.getAwtShape(shape));
	}

	@Override
	public Object setInterpolationHint(
			boolean needsInterpolationRenderingHint) {
		Graphics2D g2 = impl;
		Object oldInterpolationHint = g2
				.getRenderingHint(RenderingHints.KEY_INTERPOLATION);

		if (oldInterpolationHint == null) {
			oldInterpolationHint = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		}

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

	@Override
	public void resetInterpolationHint(Object hint) {
		impl.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
	}

	@Override
	public void updateCanvasColor() {
		// TODO Auto-generated method stub
	}

	private Line2D.Double line;

	private Line2D.Double getLine() {
		if (line == null) {
			line = new Line2D.Double();
		}

		return line;
	}

	@Override
	public void drawStraightLine(double x1, double y1, double x2, double y2) {
		getLine().setLine(x1, y1, x2, y2);

		// turn off "pure" to avoid blurry axes
		impl.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_DEFAULT);

		impl.draw(line);

		impl.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
	}

	@Override
	public void startGeneralPath() {
		// do nothing, used in web
	}

	@Override
	public void addStraightLineToGeneralPath(double x1, double y1, double x2, double y2) {
		drawStraightLine(x1, y1, x2, y2);
	}

	@Override
	public void endAndDrawGeneralPath() {
		// do nothing, used in web
	}

	/**
	 * Dispose wrapped implementation
	 */
	public void dispose() {
		impl.dispose();
	}

	/**
	 * @param img
	 *            image
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public void drawImageScaled(MyImageD img, int width, int height) {
		if (img.isSVG()) {
			saveTransform();
			scale((double) width / img.getWidth(),
					(double) height / img.getHeight());
			img.render(impl, 0, 0);
			restoreTransform();
		} else {
			impl.drawImage(img.getImage(), 0, 0, width, height, null);
		}
	}

	private AffineTransform affineTransform;

	@Override
	public void saveTransform() {
		affineTransform = impl.getTransform();
	}

	@Override
	public void restoreTransform() {
		if (affineTransform == null) {
			throw new RuntimeException("Save transform was not called!");
		}
		impl.setTransform(affineTransform);
		affineTransform = null;
	}

	@Override
	public void drawImage(MyImage img, int sx, int sy, int sw, int sh, int dx,
			int dy, int dw, int dh) {
		MyImageD myImageD = (MyImageD) img;
		myImageD.render(impl, sx, sy, sw, sh, dx, dy, dw, dh);
	}

	@Override
	public void drawImage(MyImage img, int dx, int dy, int dw, int dh) {
		impl.drawImage(((MyImageD) img).getImage(), dx, dy, dx, dy, null);
	}

	public Graphics2D getNativeImplementation() {
		return impl;
	}

}
