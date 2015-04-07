package org.geogebra.desktop.factories;

import java.awt.Font;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import org.geogebra.common.awt.Component;
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
import org.geogebra.common.awt.GQuadCurve2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.event.ActionListenerI;
import org.geogebra.common.euclidian.event.FocusListener;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.desktop.awt.GEllipse2DDoubleD;
import org.geogebra.desktop.awt.GFontD;
import org.geogebra.desktop.awt.GRectangleD;
import org.geogebra.desktop.gui.MyImageD;

public class AwtFactoryD extends AwtFactory {

	public AwtFactoryD() {
		GColor.initColors(this);
	}

	@Override
	public GColor newColor(int RGB) {
		return new org.geogebra.desktop.awt.GColorD(RGB);
	}

	@Override
	public GColor newColor(int red, int green, int blue) {
		return new org.geogebra.desktop.awt.GColorD(red, green, blue);
	}

	@Override
	public GColor newColor(int red, int green, int blue, int alpha) {
		return new org.geogebra.desktop.awt.GColorD(red, green, blue, alpha);
	}

	@Override
	public GColor newColor(float red, float green, float blue, float alpha) {
		return new org.geogebra.desktop.awt.GColorD(red, green, blue, alpha);
	}

	@Override
	public GColor newColor(float red, float green, float blue) {
		return new org.geogebra.desktop.awt.GColorD(red, green, blue);
	}

	@Override
	public GAffineTransform newAffineTransform() {
		return new org.geogebra.desktop.awt.GAffineTransformD();
	}

	@Override
	public GRectangle2D newRectangle2D() {
		return new org.geogebra.desktop.awt.GGenericRectangle2DD();
	}

	@Override
	public GRectangle newRectangle(int x, int y, int w, int h) {
		return new org.geogebra.desktop.awt.GRectangleD(x, y, w, h);
	}

	@Override
	public GBufferedImage newBufferedImage(int pixelWidth, int pixelHeight,
			int type) {
		return new org.geogebra.desktop.awt.GBufferedImageD(pixelWidth, pixelHeight, type);
	}

	@Override
	public MyImage newMyImage(int pixelWidth, int pixelHeight, int typeIntArgb) {
		return new MyImageD(new java.awt.image.BufferedImage(pixelWidth,
				pixelHeight, typeIntArgb));
	}

	@Override
	public GDimension newDimension(int width, int height) {
		return new org.geogebra.desktop.awt.GDimensionD(width, height);
	}

	@Override
	public GPoint2D newPoint2D() {
		return new org.geogebra.desktop.awt.GPoint2DD();
	}

	@Override
	public GPoint2D newPoint2D(double x, double y) {
		return new org.geogebra.desktop.awt.GPoint2DD(x, y);
	}

	/*
	 * @Override public Point newPoint() { return new geogebra.awt.Point(); }
	 * 
	 * @Override public Point newPoint(int x, int y) { return new
	 * geogebra.awt.Point(x,y); }
	 */

	@Override
	public GRectangle newRectangle(int i, int j) {
		return new org.geogebra.desktop.awt.GRectangleD(i, j);
	}

	@Override
	public GGeneralPath newGeneralPath() {
		return new org.geogebra.desktop.awt.GGeneralPathD();
	}

	@Override
	public GBasicStroke newMyBasicStroke(float f) {
		return new org.geogebra.desktop.awt.GBasicStrokeD(f, GBasicStroke.CAP_ROUND,
				GBasicStroke.JOIN_ROUND);
	}

	@Override
	public GBasicStroke newBasicStroke(float width, int endCap, int lineJoin,
			float miterLimit, float[] dash, float f) {
		java.awt.BasicStroke s = new java.awt.BasicStroke(width, endCap,
				lineJoin, miterLimit, dash, f);
		return new org.geogebra.desktop.awt.GBasicStrokeD(s);
	}

	@Override
	public GBasicStroke newBasicStroke(float f) {
		return new org.geogebra.desktop.awt.GBasicStrokeD(f);
	}

	@Override
	// CAP_BUTT, JOIN_MITER behaves differently on JRE & GWT
	// see #1699
	public GBasicStroke newBasicStrokeJoinMitre(float f) {
		return new org.geogebra.desktop.awt.GBasicStrokeD(f, GBasicStroke.CAP_SQUARE,
				GBasicStroke.JOIN_MITER);
	}

	@Override
	public GLine2D newLine2D() {
		return new org.geogebra.desktop.awt.GLine2DD();
	}

	@Override
	public GRectangle newRectangle(GRectangle bb) {
		return new org.geogebra.desktop.awt.GRectangleD(bb);
	}

	@Override
	public GRectangle newRectangle() {
		return new org.geogebra.desktop.awt.GRectangleD();
	}

	@Override
	public GEllipse2DDoubleD newEllipse2DDouble() {
		return new org.geogebra.desktop.awt.GEllipse2DDoubleD();

	}

	@Override
	public GEllipse2DFloat newEllipse2DFloat(int i, int j, int k, int l) {
		return new org.geogebra.desktop.awt.GEllipse2DFloatD(i, j, k, l);
	}

	@Override
	public GArc2D newArc2D() {
		return new org.geogebra.desktop.awt.GArc2DD();
	}

	@Override
	public GQuadCurve2D newQuadCurve2D() {
		return new org.geogebra.desktop.awt.GQuadCurve2DD();
	}

	/*
	 * @Override public Area newArea(GeneralPathClipped gp) { return new
	 * geogebra.awt.Area(gp); }
	 */

	@Override
	public GArea newArea() {
		return new org.geogebra.desktop.awt.GAreaD();
	}

	@Override
	public GArea newArea(GShape shape) {
		return new org.geogebra.desktop.awt.GAreaD(shape);
	}

	@Override
	public GGeneralPath newGeneralPath(int rule) {
		return new org.geogebra.desktop.awt.GGeneralPathD(rule);
	}

	@Override
	public GCubicCurve2D newCubicCurve2D() {
		return new org.geogebra.desktop.awt.GCubicCurve2DD();
	}

	@Override
	public GBasicStroke newBasicStroke(float f, int cap, int join) {
		return new org.geogebra.desktop.awt.GBasicStrokeD(f, cap, join);
	}

	@Override
	public GTextLayout newTextLayout(String string, GFont fontLine,
			GFontRenderContext frc) {
		return new org.geogebra.desktop.awt.GTextLayoutD(string, fontLine, frc);
	}

	@Override
	public GAlphaComposite newAlphaComposite(int rule, float alpha) {
		return new org.geogebra.desktop.awt.GAlphaCompositeD(
				java.awt.AlphaComposite.getInstance(rule, alpha));
	}

	@Override
	public GGradientPaint newGradientPaint(int x, int y, GColor bg2, int x2,
			int i, GColor bg) {
		return new org.geogebra.desktop.awt.GGradientPaintD(x, y, bg2, x2, i, bg);
	}

	@Override
	public FocusListener newFocusListener(Object listener) {
		return new org.geogebra.desktop.euclidian.event.FocusListener(listener);
	}

	@Override
	public Component newComponent(Object component) {
		return new org.geogebra.desktop.awt.GComponentD(component);
	}

	@Override
	public org.geogebra.desktop.euclidian.event.ActionListenerD newActionListener(
			ActionListenerI listener) {
		return new org.geogebra.desktop.euclidian.event.ActionListenerD(listener);
	}

	@Override
	public GPaint newTexturePaint(GBufferedImage subimage, GRectangle rect) {
		return new org.geogebra.desktop.awt.GTexturePaintD(subimage, rect);
	}

	@Override
	public GPaint newTexturePaint(MyImage subimage, GRectangle rect) {
		return new org.geogebra.desktop.awt.GTexturePaintD(new TexturePaint(
				(BufferedImage) ((MyImageD) subimage).getImage(),
				GRectangleD.getAWTRectangle(rect)));
	}

	@Override
	public GFont newFont(String name, int style, int size) {
		return new GFontD(new Font(name, style, size));
	}

}
