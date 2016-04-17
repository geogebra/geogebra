package org.geogebra.desktop.export.epsgraphics;

import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GAffineTransformD;
import org.geogebra.desktop.awt.GBasicStrokeD;
import org.geogebra.desktop.awt.GBufferedImageD;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.awt.GFontD;
import org.geogebra.desktop.awt.GGenericRectangle2DD;
import org.geogebra.desktop.awt.GLine2DD;

import com.himamis.retex.renderer.desktop.font.FontD;
import com.himamis.retex.renderer.desktop.graphics.ColorD;
import com.himamis.retex.renderer.desktop.graphics.StrokeD;
import com.himamis.retex.renderer.desktop.graphics.TransformD;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.geom.Line2D;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.RoundRectangle2D;
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
	private LinkedList<GAffineTransform> transformationStack = new LinkedList<GAffineTransform>();

	public EpsGraphicsWrapper(EpsGraphicsD impl) {
		this.impl = impl;
	}

	public EpsGraphicsD getImpl() {
		return impl;
	}

	public void setStroke(Stroke stroke) {
		impl.setStroke(new GBasicStrokeD((BasicStroke) stroke));
	}

	public Stroke getStroke() {
		return new StrokeD(GBasicStrokeD.getAwtStroke(impl.getStroke()));
	}

	public void setColor(Color color) {
		impl.setColor(new GColorD((java.awt.Color) color));
	}

	public Color getColor() {
		return new ColorD(GColorD.getAwtColor(impl.getColor()));
	}

	public Transform getTransform() {
		return new TransformD(
				GAffineTransformD.getAwtAffineTransform(impl.getTransform()));
	}

	public Font getFont() {
		return new FontD(GFontD.getAwtFont(impl.getFont()));

	}

	public void setFont(Font font) {
		impl.setFont(new GFontD(((FontD) font).impl));
	}

	public void fillRect(int x, int y, int width, int height) {
		impl.fillRect(x, y, width, height);
	}

	public void fill(Rectangle2D rectangle) {
		impl.fill(new GGenericRectangle2DD(
				(java.awt.geom.Rectangle2D) rectangle));
	}

	public void draw(Rectangle2D rectangle) {
		impl.draw(new GGenericRectangle2DD(
				(java.awt.geom.Rectangle2D) rectangle));
	}

	public void draw(RoundRectangle2D rectangle) {
		Log.debug("unimplemented - not needed?");
		// impl.draw((Shape) rectangle);
	}

	public void draw(Line2D line) {
		impl.draw(new GLine2DD((java.awt.geom.Line2D) line));
	}

	public void drawChars(char[] data, int offset, int length, int x, int y) {
		impl.drawChars(data, offset, length, x, y);

	}

	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		Log.debug("unimplemented - not needed?");
		// impl.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		Log.debug("unimplemnted - not needed?");
		// impl.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	public void translate(double x, double y) {
		impl.translate(x, y);
	}

	public void scale(double x, double y) {
		impl.scale(x, y);
	}

	public void rotate(double theta, double x, double y) {
		impl.rotate(theta, x, y);
	}

	public void rotate(double theta) {
		impl.rotate(theta);
	}

	public void drawImage(Image image, int x, int y) {
		impl.drawImage(new GBufferedImageD(((BufferedImage) image)), x, y);
	}

	public void drawImage(Image image, Transform transform) {
		Log.debug("unimplemented - not needed?");
		// impl.drawImage(new GBufferedImageD(((BufferedImage) image)),
		// new GAffineTransformD(((AffineTransform) transform)));
	}

	public FontRenderContext getFontRenderContext() {
		Log.debug("unimplemented - not needed?");
		// return new
		// FontRenderContextD(FontRenderContextDimpl.getFontRenderContext());
		return null;
	}

	public void dispose() {
		//
	}

	public void setRenderingHint(int key, int value) {
		//
	}

	public int getRenderingHint(int key) {
		return -1;
	}



	private static int getRenderingValue(Object value) {
		return -1;
	}

	public void saveTransformation() {
		transformationStack.add(new GAffineTransformD(
				GAffineTransformD.getAwtAffineTransform(impl.getTransform())));
	}

	public void restoreTransformation() {
		GAffineTransform last = transformationStack.removeLast();
		impl.setTransform(last);
	}
}
