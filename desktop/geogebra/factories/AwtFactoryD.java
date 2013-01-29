package geogebra.factories;

import geogebra.awt.GEllipse2DDoubleD;
import geogebra.common.awt.Component;
import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GAlphaComposite;
import geogebra.common.awt.GArc2D;
import geogebra.common.awt.GArea;
import geogebra.common.awt.GBasicStroke;
import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GColor;
import geogebra.common.awt.GCubicCurve2D;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GEllipse2DFloat;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GFontRenderContext;
import geogebra.common.awt.GGeneralPath;
import geogebra.common.awt.GGradientPaint;
import geogebra.common.awt.GLine2D;
import geogebra.common.awt.GPoint2D;
import geogebra.common.awt.GQuadCurve2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GRectangle2D;
import geogebra.common.awt.GShape;
import geogebra.common.awt.font.GTextLayout;
import geogebra.common.euclidian.event.ActionListenerI;
import geogebra.common.euclidian.event.FocusListener;
import geogebra.common.factories.AwtFactory;

public class AwtFactoryD extends AwtFactory{
	
	public AwtFactoryD(){
		GColor.initColors(this);
	}
	@Override
	public GColor newColor(int RGB) {
		return new geogebra.awt.GColorD(RGB);
	}
	
	@Override
	public GColor newColor(int red, int green, int blue) {
		return new geogebra.awt.GColorD(red, green, blue);
	}
	
	@Override
	public GColor newColor(int red, int green, int blue, int alpha) {
		return new geogebra.awt.GColorD(red, green, blue, alpha);
	}
	
	@Override
	public GColor newColor(float red, float green, float blue, float alpha) {
		return new geogebra.awt.GColorD(red, green, blue, alpha);
	}

	@Override
	public GColor newColor(float red, float green, float blue) {
		return new geogebra.awt.GColorD(red, green, blue);
	}
	
	@Override
	public GAffineTransform newAffineTransform() {
		return new geogebra.awt.GAffineTransformD();
	}

	@Override
	public GRectangle2D newRectangle2D() {
		return new geogebra.awt.GGenericRectangle2DD();
	}
	
	@Override
	public GRectangle newRectangle(int x,int y,int w, int h) {
		return new geogebra.awt.GRectangleD(x,y,w,h);
	}

	@Override
	public GBufferedImage newBufferedImage(int pixelWidth,
			int pixelHeight, int type) {
		// TODO Auto-generated method stub
		return new geogebra.awt.GBufferedImageD(pixelWidth,pixelHeight,type);
	}

	@Override
	public GDimension newDimension(int width, int height) {
		return new geogebra.awt.GDimensionD(width, height);
	}

	@Override
	public GPoint2D newPoint2D() {
		return new geogebra.awt.GPoint2DD();
	}
	
	@Override
	public GPoint2D newPoint2D(double x,double y) {
		return new geogebra.awt.GPoint2DD(x,y);
	}

	/*@Override
	public Point newPoint() {
		return new geogebra.awt.Point();
	}

	@Override
	public Point newPoint(int x, int y) {
		return new geogebra.awt.Point(x,y);
	}*/

	@Override
	public GRectangle newRectangle(int i, int j) {
		return new geogebra.awt.GRectangleD(i, j);
	}
	
	@Override
	public GGeneralPath newGeneralPath(){
		return new geogebra.awt.GGeneralPathD();
	}

	@Override
	public GBasicStroke newMyBasicStroke(float f) {
		return new geogebra.awt.GBasicStrokeD(f,
				GBasicStroke.CAP_ROUND,GBasicStroke.JOIN_ROUND);
	}

	@Override
	public GBasicStroke newBasicStroke(float width, int endCap, int lineJoin,
			float miterLimit, float[] dash, float f) {
		java.awt.BasicStroke s = new java.awt.BasicStroke(width,endCap,lineJoin,
				miterLimit,dash,f);
		return new geogebra.awt.GBasicStrokeD(s);
	}

	@Override
	public GBasicStroke newBasicStroke(float f) {
		return new geogebra.awt.GBasicStrokeD(f);
	}
	
	@Override
	// CAP_BUTT, JOIN_MITER behaves differently on JRE & GWT
	// see #1699
	public GBasicStroke newBasicStrokeJoinMitre(float f) {
		return new geogebra.awt.GBasicStrokeD(f, GBasicStroke.CAP_SQUARE, GBasicStroke.JOIN_MITER);
	}
	
	@Override
	public GLine2D newLine2D() {
		return new geogebra.awt.GLine2DD();
	}

	@Override
	public GRectangle newRectangle(GRectangle bb) {
		return new geogebra.awt.GRectangleD(bb);
	}
	
	@Override
	public GRectangle newRectangle() {
		return new geogebra.awt.GRectangleD();
	}

	@Override
	public GEllipse2DDoubleD newEllipse2DDouble() {
		return new geogebra.awt.GEllipse2DDoubleD();
				
				
	}

	@Override
	public GEllipse2DFloat newEllipse2DFloat(int i, int j, int k, int l) {
		return new geogebra.awt.GEllipse2DFloatD(i,j,k,l);
	}

	@Override
	public GArc2D newArc2D() {
		return new geogebra.awt.GArc2DD();
	}

	@Override
	public GQuadCurve2D newQuadCurve2D() {
		return new geogebra.awt.GQuadCurve2DD();
	}

	/*
	@Override
	public Area newArea(GeneralPathClipped gp) {
		return new geogebra.awt.Area(gp);
	}
	*/

	@Override
	public GArea newArea() {
		return new geogebra.awt.GAreaD();
	}

	@Override
	public GArea newArea(GShape shape) {
		return new geogebra.awt.GAreaD(shape);
	}

	@Override
	public GGeneralPath newGeneralPath(int rule) {
		return new geogebra.awt.GGeneralPathD(rule);
	}

	@Override
	public GCubicCurve2D newCubicCurve2D() {
		return new geogebra.awt.GCubicCurve2DD();
	}

	@Override
	public GBasicStroke newBasicStroke(float f, int cap, int join) {
		return new geogebra.awt.GBasicStrokeD(f, cap, join);
	}

	@Override
	public GTextLayout newTextLayout(String string, GFont fontLine,
			GFontRenderContext frc) {
		return new geogebra.awt.GTextLayoutD(string,fontLine,frc);
	}

	@Override
	public GAlphaComposite newAlphaComposite(int rule, float alpha) {
		return new geogebra.awt.GAlphaCompositeD(java.awt.AlphaComposite.getInstance(rule, alpha));
	}

	@Override
	public GGradientPaint newGradientPaint(int x, int y, GColor bg2, int x2,
			int i, GColor bg) {
		return new geogebra.awt.GGradientPaintD(x, y, bg2, x2, i, bg);
	}
	@Override
	public FocusListener newFocusListener(Object listener) {
		return new geogebra.euclidian.event.FocusListener(listener);
	}

	@Override
	public Component newComponent(Object component) {
		return new geogebra.awt.GComponentD(component);
	}
	
	@Override
	public geogebra.euclidian.event.ActionListenerD newActionListener(ActionListenerI listener) {
		return new geogebra.euclidian.event.ActionListenerD(listener);
	}

	
}
