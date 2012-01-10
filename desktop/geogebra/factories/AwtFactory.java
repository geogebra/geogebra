package geogebra.factories;

import geogebra.awt.Ellipse2DDouble;
import geogebra.common.awt.Arc2D;
import geogebra.common.awt.Area;
import geogebra.common.awt.BasicStroke;
import geogebra.common.awt.CubicCurve2D;
import geogebra.common.awt.Dimension;
import geogebra.common.awt.Ellipse2DFloat;
import geogebra.common.awt.GeneralPath;
import geogebra.common.awt.Line2D;
import geogebra.common.awt.Point;
import geogebra.common.awt.Point2D;
import geogebra.common.awt.QuadCurve2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.awt.Color;
import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.Rectangle2D;
import geogebra.common.awt.RectangularShape;
import geogebra.common.awt.Shape;
import geogebra.common.euclidian.GeneralPathClipped;

public class AwtFactory extends geogebra.common.factories.AwtFactory{
	@Override
	public Color newColor(int RGB) {
		return new geogebra.awt.Color(RGB);
	}
	
	@Override
	public Color newColor(int red, int green, int blue) {
		return new geogebra.awt.Color(red, green, blue);
	}
	
	@Override
	public Color newColor(int red, int green, int blue, int alpha) {
		return new geogebra.awt.Color(red, green, blue, alpha);
	}
	
	@Override
	public Color newColor(float red, float green, float blue, float alpha) {
		return new geogebra.awt.Color(red, green, blue, alpha);
	}

	@Override
	public Color newColor(float red, float green, float blue) {
		return new geogebra.awt.Color(red, green, blue);
	}
	
	@Override
	public AffineTransform newAffineTransform() {
		return new geogebra.awt.AffineTransform();
	}

	@Override
	public Rectangle2D newRectangle2D() {
		return new geogebra.awt.GenericRectangle2D();
	}
	
	@Override
	public Rectangle newRectangle(int x,int y,int w, int h) {
		return new geogebra.awt.Rectangle(x,y,w,h);
	}

	@Override
	public BufferedImageAdapter newBufferedImage(int pixelWidth,
			int pixelHeight, int type) {
		// TODO Auto-generated method stub
		return new geogebra.awt.BufferedImage(pixelWidth,pixelHeight,type);
	}

	@Override
	public Dimension newDimension(int width, int height) {
		return new geogebra.awt.Dimension(width, height);
	}

	@Override
	public Point2D newPoint2D() {
		return new geogebra.awt.Point2D();
	}
	
	@Override
	public Point2D newPoint2D(double x,double y) {
		return new geogebra.awt.Point2D(x,y);
	}

	public Point newPoint() {
		return new geogebra.awt.Point();
	}

	public Point newPoint(int x, int y) {
		return new geogebra.awt.Point(x,y);
	}

	@Override
	public Rectangle newRectangle(int i, int j) {
		return new geogebra.awt.Rectangle(i, j);
	}
	
	@Override
	public GeneralPath newGeneralPath(){
		return new geogebra.awt.GeneralPath();
	}

	@Override
	public BasicStroke newMyBasicStroke(float f) {
		return new geogebra.awt.BasicStroke(f,
				BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
	}

	@Override
	public BasicStroke newBasicStroke(float width, int endCap, int lineJoin,
			float miterLimit, float[] dash, float f) {
		java.awt.BasicStroke s = new java.awt.BasicStroke(width,endCap,lineJoin,
				miterLimit,dash,f);
		return new geogebra.awt.BasicStroke(s);
	}

	@Override
	public BasicStroke newBasicStroke(float f) {
		return new geogebra.awt.BasicStroke(f);
	}
	
	@Override
	public Line2D newLine2D() {
		return new geogebra.awt.Line2D();
	}

	@Override
	public Rectangle newRectangle(Rectangle bb) {
		return new geogebra.awt.Rectangle(bb);
	}
	
	@Override
	public Rectangle newRectangle() {
		return new geogebra.awt.Rectangle();
	}

	@Override
	public Ellipse2DDouble newEllipse2DDouble() {
		return new geogebra.awt.Ellipse2DDouble();
				
				
	}

	@Override
	public Ellipse2DFloat newEllipse2DFloat(int i, int j, int k, int l) {
		return new geogebra.awt.Ellipse2DFloat(i,j,k,l);
	}

	@Override
	public Arc2D newArc2D() {
		return new geogebra.awt.Arc2D();
	}

	@Override
	public QuadCurve2D newQuadCurve2D() {
		return new geogebra.awt.QuadCurve2D();
	}

	/*
	@Override
	public Area newArea(GeneralPathClipped gp) {
		return new geogebra.awt.Area(gp);
	}
	*/

	@Override
	public Area newArea() {
		return new geogebra.awt.Area();
	}

	@Override
	public Area newArea(Shape shape) {
		return new geogebra.awt.Area(shape);
	}

	@Override
	public GeneralPath newGeneralPath(int rule) {
		return new geogebra.awt.GeneralPath(rule);
	}

	@Override
	public CubicCurve2D newCubicCurve2D() {
		return new geogebra.awt.CubicCurve2D();
	}

	@Override
	public BasicStroke newBasicStroke(float f, int cap, int join) {
		return new geogebra.awt.BasicStroke(f, cap, join);
	}

	
}
