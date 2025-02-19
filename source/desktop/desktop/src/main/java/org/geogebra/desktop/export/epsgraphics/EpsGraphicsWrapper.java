package org.geogebra.desktop.export.epsgraphics;

import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GAffineTransformD;
import org.geogebra.desktop.awt.GBasicStrokeD;
import org.geogebra.desktop.awt.GBufferedImageD;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.awt.GFontD;
import org.geogebra.desktop.awt.GGenericRectangle2DD;
import org.geogebra.desktop.awt.GLine2DD;
import org.geogebra.desktop.factories.AwtFactoryD;

import com.himamis.retex.renderer.desktop.font.FontD;
import com.himamis.retex.renderer.desktop.graphics.ColorD;
import com.himamis.retex.renderer.desktop.graphics.StrokeD;
import com.himamis.retex.renderer.desktop.graphics.TransformD;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.geom.Line2D;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.RoundRectangle2D;
import com.himamis.retex.renderer.share.platform.geom.Shape;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.Stroke;
import com.himamis.retex.renderer.share.platform.graphics.Transform;

/**
 * 
 * wrapper so that LaTeX can be drawn to EPS
 * 
 * @author michael
 *
 */
public class EpsGraphicsWrapper implements Graphics2DInterface {

	private EpsGraphicsD impl;
	private LinkedList<GAffineTransform> transformationStack = new LinkedList<>();

	GGeneralPath path = AwtFactory.getPrototype().newGeneralPath();

	public EpsGraphicsWrapper(EpsGraphicsD impl) {
		this.impl = impl;
	}

	public EpsGraphicsD getImpl() {
		return impl;
	}

	@Override
	public void setStroke(Stroke stroke) {
		if (stroke instanceof BasicStroke) {
			impl.setStroke(new GBasicStrokeD((BasicStroke) stroke));
		}
	}

	@Override
	public Stroke getStroke() {
		return new StrokeD(((AwtFactoryD) AwtFactory.getPrototype())
				.getAwtStroke(impl.getStroke()));
	}

	@Override
	public void setColor(Color color) {
		if (color instanceof java.awt.Color) {
			impl.setColor(GColorD.newColor((java.awt.Color) color));
		}
	}

	@Override
	public Color getColor() {
		return new ColorD(GColorD.getAwtColor(impl.getColor()));
	}

	@Override
	public Transform getTransform() {
		return new TransformD(
				GAffineTransformD.getAwtAffineTransform(impl.getTransform()));
	}

	@Override
	public Font getFont() {
		return new FontD(GFontD.getAwtFont(impl.getFont()));

	}

	@Override
	public void setFont(Font font) {
		impl.setFont(new GFontD(((FontD) font).impl));
	}

	@Override
	public void fillRect(double x, double y, double width, double height) {
		impl.fillRect((int) x, (int) y, (int) width, (int) height);
	}

	@Override
	public void fill(Shape rectangle) {
		if (rectangle instanceof java.awt.geom.Rectangle2D) {
			impl.fill(new GGenericRectangle2DD(
					(java.awt.geom.Rectangle2D) rectangle));
		}
	}

	@Override
	public void startDrawing() {
		path.reset();
	}

	@Override
	public void moveTo(double x, double y) {
		path.moveTo(x, y);
	}

	@Override
	public void lineTo(double x, double y) {
		path.lineTo(x, y);
	}

	@Override
	public void quadraticCurveTo(double x, double y, double x1, double y1) {
		path.quadTo(x, y, x1, y1);
	}

	@Override
	public void bezierCurveTo(double x, double y, double x1, double y1,
			double x2, double y2) {
		path.curveTo(x1, y1, x1, y1, x2, y2);
	}

	@Override
	public void finishDrawing() {
		impl.draw(path);
	}

	@Override
	public void draw(Rectangle2D rectangle) {
		if (rectangle instanceof java.awt.geom.Rectangle2D) {
			impl.draw(new GGenericRectangle2DD(
					(java.awt.geom.Rectangle2D) rectangle));
		}
	}

	@Override
	public void draw(RoundRectangle2D rectangle) {
		Log.debug("unimplemented - not needed?");
		// impl.draw((Shape) rectangle);
	}

	@Override
	public void draw(Line2D line) {
		if (line instanceof java.awt.geom.Line2D) {
			impl.draw(new GLine2DD((java.awt.geom.Line2D) line));
		}
	}

	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		impl.drawChars(data, offset, length, x, y);

	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		Log.debug("unimplemented - not needed?");
		// impl.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		Log.debug("unimplemented - not needed?");
		// impl.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void translate(double x, double y) {
		impl.translate(x, y);
	}

	@Override
	public void scale(double x, double y) {
		impl.scale(x, y);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		impl.rotate(theta, x, y);
	}

	@Override
	public void rotate(double theta) {
		impl.rotate(theta);
	}

	@Override
	public void drawImage(Image image, int x, int y) {
		if (image instanceof BufferedImage) {
			impl.drawImage(new GBufferedImageD((BufferedImage) image), x, y);
		}
	}

	@Override
	public void drawImage(Image image, Transform transform) {
		Log.debug("unimplemented - not needed?");
		// impl.drawImage(new GBufferedImageD(((BufferedImage) image)),
		// new GAffineTransformD(((AffineTransform) transform)));
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		Log.debug("unimplemented - not needed?");
		// return new
		// FontRenderContextD(FontRenderContextDimpl.getFontRenderContext());
		return null;
	}

	@Override
	public void dispose() {
		//
	}

	@Override
	public void setRenderingHint(int key, int value) {
		//
	}

	@Override
	public int getRenderingHint(int key) {
		return -1;
	}

	@Override
	public void saveTransformation() {
		transformationStack.add(new GAffineTransformD(
				GAffineTransformD.getAwtAffineTransform(impl.getTransform())));
	}

	@Override
	public void restoreTransformation() {
		GAffineTransform last = transformationStack.removeLast();
		impl.setTransform(last);
	}

}
