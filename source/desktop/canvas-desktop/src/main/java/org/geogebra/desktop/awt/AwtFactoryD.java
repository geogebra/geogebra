/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.awt;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GAlphaComposite;
import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGradientPaint;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GQuadCurve2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.awt.font.GTextLayout;

public class AwtFactoryD extends AwtFactory {

	@Override
	public GAffineTransform newAffineTransform() {
		return new GAffineTransformD();
	}

	@Override
	public GRectangle2D newRectangle2D() {
		return new GGenericRectangle2DD();
	}

	@Override
	public GRectangle newRectangle(int x, int y, int width, int height) {
		return new GRectangleD(x, y, width, height);
	}

	@Override
	public GBufferedImage newBufferedImage(int pixelWidth, int pixelHeight,
			double pixelRatio) {
		return new GBufferedImageD(pixelWidth, pixelHeight,
				GBufferedImage.TYPE_INT_ARGB);
	}

	@Override
	public GBufferedImage createBufferedImage(int width, int height,
			boolean transparency) throws OutOfMemoryError {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();

			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			BufferedImage bufImg = gc.createCompatibleImage(width, height,
					transparency ? Transparency.TRANSLUCENT
							: Transparency.BITMASK);
			// Graphics2D g = (Graphics2D)bufImg.getGraphics();
			// g.setBackground(new Color(0,0,0,0));
			// g.clearRect(0,0,width,height);

			return new GBufferedImageD(bufImg);
		} catch (Exception e) {
			// headless mode: getDefaultScreenDevice throws headless exception
			return new GBufferedImageD(width, height,
					GBufferedImage.TYPE_INT_ARGB);
		}
	}

	@Override
	public GDimension newDimension(int width, int height) {
		return new GDimensionD(width, height);
	}

	/*
	 * @Override public Point newPoint() { return new geogebra.awt.Point(); }
	 *
	 * @Override public Point newPoint(int x, int y) { return new
	 * geogebra.awt.Point(x,y); }
	 */

	@Override
	public GRectangle newRectangle(int width, int height) {
		return new GRectangleD(width, height);
	}

	@Override
	public GGeneralPath newGeneralPath() {
		return new GGeneralPathD();
	}

	@Override
	public GBasicStroke newMyBasicStroke(double f) {
		return new GBasicStrokeD(f, GBasicStroke.CAP_ROUND,
				GBasicStroke.JOIN_ROUND);
	}

	@Override
	public GBasicStroke newBasicStroke(double width, int endCap, int lineJoin,
			double miterLimit, double[] dash) {
		BasicStroke s = new BasicStroke((float) width, endCap, lineJoin,
				(float) miterLimit, doubleToFloat(dash), 0f);
		return new GBasicStrokeD(s);
	}

	@Override
	public GBasicStroke newBasicStroke(double f) {
		return new GBasicStrokeD(f);
	}

	@Override
	// CAP_BUTT, JOIN_MITER behaves differently on JRE & GWT
	// see #1699
	public GBasicStroke newBasicStrokeJoinMitre(double f) {
		return new GBasicStrokeD(f, GBasicStroke.CAP_SQUARE,
				GBasicStroke.JOIN_MITER);
	}

	@Override
	public GLine2D newLine2D() {
		return new GLine2DD();
	}

	@Override
	public GRectangle newRectangle(GRectangle bb) {
		return new GRectangleD(bb);
	}

	@Override
	public GRectangle newRectangle() {
		return new GRectangleD();
	}

	@Override
	public GEllipse2DDoubleD newEllipse2DDouble() {
		return new GEllipse2DDoubleD();

	}

	@Override
	public GEllipse2DDouble newEllipse2DDouble(double x, double y, double width,
			double height) {
		return new GEllipse2DDoubleD(x, y, width, height);
	}

	@Override
	public GArc2D newArc2D() {
		return new GArc2DD();
	}

	@Override
	public GQuadCurve2D newQuadCurve2D() {
		return new GQuadCurve2DD();
	}

	/*
	 * @Override public Area newArea(GeneralPathClipped gp) { return new
	 * geogebra.awt.Area(gp); }
	 */

	@Override
	public GArea newArea() {
		return new GAreaD();
	}

	@Override
	public GArea newArea(GShape shape) {
		return new GAreaD(shape);
	}

	@Override
	public GGeneralPath newGeneralPath(int rule) {
		return new GGeneralPathD(rule);
	}

	@Override
	public GBasicStroke newBasicStroke(double f, int cap, int join) {
		return new GBasicStrokeD(f, cap, join);
	}

	@Override
	public GTextLayout newTextLayout(String string, GFont fontLine,
			GFontRenderContext frc) {
		return new GTextLayoutD(string, fontLine, frc);
	}

	@Override
	public GAlphaComposite newAlphaComposite(double alpha) {
		return new GAlphaCompositeD(
				AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
						(float) alpha));
	}

	@Override
	public GGradientPaint newGradientPaint(double x1, double y1, GColor color1,
			double x2, double y2, GColor color2) {
		return new GGradientPaintD(x1, y1, color1, x2, y2, color2);
	}

	@Override
	public GPaint newTexturePaint(GBufferedImage subimage, GRectangle rect) {
		return new GTexturePaintD(subimage, rect);
	}

	@Override
	public GPaint newTexturePaint(MyImage subimage, GRectangle rect) {
		return new GTexturePaintD(new TexturePaint(
				(BufferedImage) ((ImageD) subimage).getImage(),
				GRectangleD.getAWTRectangle(rect)));
	}

	@Override
	public GFont newFont(String name, int style, int size) {
		return new GFontD(new Font(name, style, size));
	}

	/**
	 * @param s common stroke
	 * @return desktop-specific stroke
	 */
	public static BasicStroke getAwtStroke(GBasicStroke s) {
		if (!(s instanceof GBasicStrokeD)) {
			if (s != null) {
				Log.debug("other type: " + s.getClass());
			}
			return null;
		}
		return ((GBasicStrokeD) s).getImpl();
	}

}
