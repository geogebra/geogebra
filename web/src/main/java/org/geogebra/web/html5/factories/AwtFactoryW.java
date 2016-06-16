package org.geogebra.web.html5.factories;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GAlphaComposite;
import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GCubicCurve2D;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GEllipse2DFloat;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGradientPaint;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GPolygon;
import org.geogebra.common.awt.GQuadCurve2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.event.ActionListener;
import org.geogebra.common.euclidian.event.ActionListenerI;
import org.geogebra.common.euclidian.event.FocusListener;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.ggbjdk.java.awt.geom.AffineTransform;
import org.geogebra.ggbjdk.java.awt.geom.Arc2D;
import org.geogebra.ggbjdk.java.awt.geom.Area;
import org.geogebra.ggbjdk.java.awt.geom.Ellipse2D;
import org.geogebra.ggbjdk.java.awt.geom.GeneralPath;
import org.geogebra.ggbjdk.java.awt.geom.Line2D;
import org.geogebra.ggbjdk.java.awt.geom.Polygon;
import org.geogebra.ggbjdk.java.awt.geom.QuadCurve2D;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle2D;
import org.geogebra.web.html5.awt.GAlphaCompositeW;
import org.geogebra.web.html5.awt.GBasicStrokeW;
import org.geogebra.web.html5.awt.GColorW;
import org.geogebra.web.html5.awt.GCubicCurve2DW;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.awt.GFontRenderContextW;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.awt.GGradientPaintW;
import org.geogebra.web.html5.awt.GPoint2DW;
import org.geogebra.web.html5.awt.GTexturePaintW;
import org.geogebra.web.html5.awt.font.GTextLayoutW;
import org.geogebra.web.html5.event.ActionListenerW;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.geogebra.web.html5.main.MyImageW;

/**
 * Creates AWT wrappers for web
 *
 */
public class AwtFactoryW extends AwtFactory {

	public AwtFactoryW() {
		GColor.initColors(this);
	}

	@Override
	public GColor newColor(int RGB) {
		return new GColorW(RGB);
	}

	@Override
	public GColor newColor(int red, int green, int blue) {
		return new GColorW(red, green, blue);
	}

	@Override
	public GColor newColor(int red, int green, int blue, int alpha) {
		return new GColorW(red, green, blue, alpha);
	}

	@Override
	public GColor newColor(float red, float green, float blue, float alpha) {
		return new GColorW(red, green, blue, alpha);
	}

	@Override
	public GColor newColor(float red, float green, float blue) {
		return new GColorW(red, green, blue);
	}

	@Override
	public GAffineTransform newAffineTransform() {
		return new AffineTransform();
	}

	@Override
	public GRectangle2D newRectangle2D() {
		return new Rectangle2D.Double();
	}

	@Override
	public GRectangle newRectangle(int x, int y, int w, int h) {
		return new Rectangle(x, y, w, h);
	}

	@Override
	public GBufferedImage newBufferedImage(int pixelWidth, int pixelHeight,
			float pixelRatio) {
		return new GBufferedImageW(pixelWidth, pixelHeight, pixelRatio);
	}

	@Override
	public GBufferedImage createBufferedImage(int width, int height,
			boolean transparency) {

		return null; // TODO
	}

	@Override
	public GDimension newDimension(int width, int height) {
		return new GDimensionW(width, height);
	}

	@Override
	public GPoint2D newPoint2D() {
		return new GPoint2DW();
	}

	@Override
	public GPoint2D newPoint2D(double x, double y) {
		return new GPoint2DW(x, y);
	}

	@Override
	public GRectangle newRectangle(int x, int y) {
		return new Rectangle(x, y);
	}

	@Override
	public GGeneralPath newGeneralPath() {
		// default winding rule changed for ggb50 (for Polygons) #3983
		return new GeneralPath(GeneralPath.WIND_EVEN_ODD);
	}

	@Override
	public GBasicStroke newMyBasicStroke(float f) {
		return new GBasicStrokeW(f, GBasicStrokeW.CAP_ROUND,
				GBasicStrokeW.JOIN_ROUND);
	}

	@Override
	public GBasicStroke newBasicStroke(float width, int endCap, int lineJoin,
	        float miterLimit, float[] dash, float f) {
		return new GBasicStrokeW(width, endCap, lineJoin,
		        miterLimit, dash, f);
	}

	@Override
	public GLine2D newLine2D() {
		return new Line2D.Double();
	}

	@Override
	public GRectangle newRectangle(GRectangle bb) {
		return new Rectangle(bb);
	}

	@Override
	public GEllipse2DDouble newEllipse2DDouble() {
		return new Ellipse2D.Double();
	}

	@Override
	public GEllipse2DFloat newEllipse2DFloat(int i, int j, int k, int l) {
		return new Ellipse2D.Float(i, j, k, l);
	}

	@Override
	public GBasicStroke newBasicStroke(float f) {
		return new GBasicStrokeW(f);
	}

	@Override
	// CAP_BUTT, JOIN_MITER behaves differently on JRE & GWT
	// see #1699
	public GBasicStroke newBasicStrokeJoinMitre(float f) {
		return new GBasicStrokeW(f, GBasicStroke.CAP_SQUARE,
		        GBasicStroke.JOIN_MITER);
	}

	@Override
	public GRectangle newRectangle() {
		return new Rectangle();
	}

	@Override
	public GArc2D newArc2D() {
		return new Arc2D.Double();
	}

	@Override
	public GQuadCurve2D newQuadCurve2D() {
		return new QuadCurve2D.Double();
	}

	/*
	 * @Override public Area newArea(GeneralPathClipped hypRight) {
	 * AbstractApplication.debug("implementation needed really"); // TODO
	 * Auto-generated return null; }
	 */

	@Override
	public GArea newArea() {
		return new Area();
	}

	@Override
	public GArea newArea(GShape shape) {
		return new Area(shape);
	}

	@Override
	public GGeneralPath newGeneralPath(int rule) {
		return new GeneralPath(rule);
	}

	@Override
	public GCubicCurve2D newCubicCurve2D() {
		return new GCubicCurve2DW();
	}

	@Override
	public GBasicStroke newBasicStroke(float f, int cap, int join) {
		return new GBasicStrokeW(f, cap, join);
	}

	@Override
	public GTextLayout newTextLayout(String string, GFont fontLine,
	        GFontRenderContext frc) {
		return new GTextLayoutW(string, fontLine, (GFontRenderContextW) frc);
	}

	@Override
	public GAlphaComposite newAlphaComposite(int srcOver, float alpha) {
		return new GAlphaCompositeW(srcOver, alpha);
	}

	@Override
	public GGradientPaint newGradientPaint(int x, int y, GColor bg2, int x2,
	        int i, GColor bg) {
		return new GGradientPaintW(x, y, bg2, x2, i, bg);
	}

	@Override
	public FocusListener newFocusListener(Object listener) {
		return new FocusListenerW(listener);
	}

	@Override
	public ActionListener newActionListener(ActionListenerI listener) {
		return new ActionListenerW(listener);
	}

	@Override
	public GFont newFont(String name, int style, int size) {
		return new GFontW(name, style, size);
	}

	@Override
	public MyImage newMyImage(int pixelWidth, int pixelHeight, int typeIntArgb) {
		return new MyImageW(new GBufferedImageW(pixelWidth, pixelHeight,
 1)
						.getImageElement(),
				false);
	}

	@Override
	public GPaint newTexturePaint(GBufferedImage subimage, GRectangle rect) {
		return new GTexturePaintW((GBufferedImageW) subimage);
	}

	@Override
	public GPaint newTexturePaint(MyImage subimage, GRectangle rect) {
		return new GTexturePaintW(new GBufferedImageW(
		        ((MyImageW) subimage).getImage()));

	}

	@Override
	public GPolygon newPolygon() {
		return new Polygon();
	}

}
