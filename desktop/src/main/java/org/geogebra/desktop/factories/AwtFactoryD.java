package org.geogebra.desktop.factories;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GAlphaComposite;
import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GCubicCurve2D;
import org.geogebra.common.awt.GDimension;
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
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.event.ActionListenerI;
import org.geogebra.common.euclidian.event.FocusListener;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GAffineTransformD;
import org.geogebra.desktop.awt.GAlphaCompositeD;
import org.geogebra.desktop.awt.GArc2DD;
import org.geogebra.desktop.awt.GAreaD;
import org.geogebra.desktop.awt.GBasicStrokeD;
import org.geogebra.desktop.awt.GBufferedImageD;
import org.geogebra.desktop.awt.GCubicCurve2DD;
import org.geogebra.desktop.awt.GDimensionD;
import org.geogebra.desktop.awt.GEllipse2DDoubleD;
import org.geogebra.desktop.awt.GEllipse2DFloatD;
import org.geogebra.desktop.awt.GFontD;
import org.geogebra.desktop.awt.GGeneralPathD;
import org.geogebra.desktop.awt.GGenericRectangle2DD;
import org.geogebra.desktop.awt.GGradientPaintD;
import org.geogebra.desktop.awt.GLine2DD;
import org.geogebra.desktop.awt.GPoint2DD;
import org.geogebra.desktop.awt.GPolygonD;
import org.geogebra.desktop.awt.GQuadCurve2DD;
import org.geogebra.desktop.awt.GRectangleD;
import org.geogebra.desktop.awt.GTextLayoutD;
import org.geogebra.desktop.awt.GTexturePaintD;
import org.geogebra.desktop.euclidian.event.ActionListenerD;
import org.geogebra.desktop.euclidian.event.FocusListenerD;
import org.geogebra.desktop.gui.MyImageD;

public class AwtFactoryD extends AwtFactory {

	public AwtFactoryD() {
	}

	@Override
	public GAffineTransform newAffineTransform() {
		return new GAffineTransformD();
	}

	@Override
	public GRectangle2D newRectangle2D() {
		return new GGenericRectangle2DD();
	}

	@Override
	public GRectangle newRectangle(int x, int y, int w, int h) {
		return new GRectangleD(x, y, w, h);
	}

	@Override
	public GBufferedImage newBufferedImage(int pixelWidth, int pixelHeight,
			float pixelRatio) {
		return new GBufferedImageD(pixelWidth,
				pixelHeight, GBufferedImage.TYPE_INT_ARGB);
	}

	@Override
	public GBufferedImage createBufferedImage(int width, int height,
			boolean transparency) throws OutOfMemoryError {

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();

		GraphicsDevice gs = ge.getDefaultScreenDevice();

		GraphicsConfiguration gc = gs.getDefaultConfiguration();
		BufferedImage bufImg = gc
				.createCompatibleImage(width, height,
						(transparency ? Transparency.TRANSLUCENT
								: Transparency.BITMASK));

		// Graphics2D g = (Graphics2D)bufImg.getGraphics();

		// g.setBackground(new Color(0,0,0,0));

		// g.clearRect(0,0,width,height);

		return new GBufferedImageD(bufImg);

	}

	@Override
	public MyImage newMyImage(int pixelWidth, int pixelHeight, int typeIntArgb) {
		return new MyImageD(
				new BufferedImage(pixelWidth, pixelHeight, typeIntArgb));
	}

	@Override
	public GDimension newDimension(int width, int height) {
		return new GDimensionD(width, height);
	}

	@Override
	public GPoint2D newPoint2D() {
		return new GPoint2DD();
	}

	@Override
	public GPoint2D newPoint2D(double x, double y) {
		return new GPoint2DD(x, y);
	}

	/*
	 * @Override public Point newPoint() { return new geogebra.awt.Point(); }
	 * 
	 * @Override public Point newPoint(int x, int y) { return new
	 * geogebra.awt.Point(x,y); }
	 */

	@Override
	public GRectangle newRectangle(int i, int j) {
		return new GRectangleD(i, j);
	}

	@Override
	public GGeneralPath newGeneralPath() {
		return new GGeneralPathD();
	}

	@Override
	public GBasicStroke newMyBasicStroke(float f) {
		return new GBasicStrokeD(f, GBasicStroke.CAP_ROUND,
				GBasicStroke.JOIN_ROUND);
	}

	@Override
	public GBasicStroke newBasicStroke(float width, int endCap, int lineJoin,
			float miterLimit, float[] dash, float f) {
		BasicStroke s = new BasicStroke(width, endCap, lineJoin,
				miterLimit, dash, f);
		return new GBasicStrokeD(s);
	}

	@Override
	public GBasicStroke newBasicStroke(float f) {
		return new GBasicStrokeD(f);
	}

	@Override
	// CAP_BUTT, JOIN_MITER behaves differently on JRE & GWT
	// see #1699
	public GBasicStroke newBasicStrokeJoinMitre(float f) {
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
	public GEllipse2DFloat newEllipse2DFloat(int i, int j, int k, int l) {
		return new GEllipse2DFloatD(i, j, k, l);
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
	public GCubicCurve2D newCubicCurve2D() {
		return new GCubicCurve2DD();
	}

	@Override
	public GBasicStroke newBasicStroke(float f, int cap, int join) {
		return new GBasicStrokeD(f, cap, join);
	}

	@Override
	public GTextLayout newTextLayout(String string, GFont fontLine,
			GFontRenderContext frc) {
		return new GTextLayoutD(string, fontLine, frc);
	}

	@Override
	public GAlphaComposite newAlphaComposite(int rule, float alpha) {
		return new GAlphaCompositeD(AlphaComposite.getInstance(rule, alpha));
	}

	@Override
	public GGradientPaint newGradientPaint(int x, int y, GColor bg2, int x2,
			int i, GColor bg) {
		return new GGradientPaintD(x, y, bg2, x2, i, bg);
	}

	@Override
	public FocusListener newFocusListener(Object listener) {
		return new FocusListenerD(listener);
	}

	@Override
	public ActionListenerD newActionListener(ActionListenerI listener) {
		return new ActionListenerD(listener);
	}

	@Override
	public GPaint newTexturePaint(GBufferedImage subimage, GRectangle rect) {
		return new GTexturePaintD(subimage, rect);
	}

	@Override
	public GPaint newTexturePaint(MyImage subimage, GRectangle rect) {
		return new GTexturePaintD(new TexturePaint(
				(BufferedImage) ((MyImageD) subimage).getImage(),
				GRectangleD.getAWTRectangle(rect)));
	}

	@Override
	public GFont newFont(String name, int style, int size) {
		return new GFontD(new Font(name, style, size));
	}

	@Override
	public GPolygon newPolygon() {
		return new GPolygonD();
	}

	public BasicStroke getAwtStroke(GBasicStroke s) {
		if (!(s instanceof GBasicStrokeD)) {
			if (s != null) {
				Log.debug("other type: " + s.getClass());
			}
			return null;
		}
		return ((GBasicStrokeD) s).getImpl();
	}

	static public java.awt.BasicStroke getDefaultStrokeAwt() {
		return ((AwtFactoryD) AwtFactory.getPrototype())
				.getAwtStroke(EuclidianStatic.getDefaultStroke());
	}

}
