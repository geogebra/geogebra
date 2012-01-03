package geogebra.web.factories;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.Arc2D;
import geogebra.common.awt.Area;
import geogebra.common.awt.BasicStroke;
import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.awt.Color;
import geogebra.common.awt.CubicCurve2D;
import geogebra.common.awt.Dimension;
import geogebra.common.awt.Ellipse2DDouble;
import geogebra.common.awt.Ellipse2DFloat;
import geogebra.common.awt.GeneralPath;
import geogebra.common.awt.Line2D;
import geogebra.common.awt.Point2D;
import geogebra.common.awt.QuadCurve2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;
import geogebra.common.awt.RectangularShape;
import geogebra.common.awt.Shape;
import geogebra.common.euclidian.GeneralPathClipped;

public class AwtFactory extends geogebra.common.factories.AwtFactory {

	@Override
	public Color newColor(int RGB) {
		return new geogebra.web.awt.Color(RGB);
	}

	@Override
	public Color newColor(int red, int green, int blue) {
		return new geogebra.web.awt.Color(red, green, blue);
	}

	@Override
	public Color newColor(int red, int green, int blue, int alpha) {
		return new geogebra.web.awt.Color(red, green, blue, alpha);
	}

	@Override
	public Color newColor(float red, float green, float blue, float alpha) {
		return new geogebra.web.awt.Color(red,green,blue,alpha);
	}

	@Override
	public Color newColor(float red, float green, float blue) {
		return new geogebra.web.awt.Color(red, green, blue);
	}

	@Override
	public AffineTransform newAffineTransform() {
		return new geogebra.web.awt.AffineTransform();
	}

	@Override
	public Rectangle2D newRectangle2D() {
		return new geogebra.web.awt.Rectangle2D();
	}

	@Override
	public Rectangle newRectangle(int x, int y, int w, int h) {
		return new geogebra.web.awt.Rectangle(x, y, w, h);
	}

	@Override
	public BufferedImageAdapter newBufferedImage(int pixelWidth,
	        int pixelHeight, int typeIntArgb) {
		return new geogebra.web.awt.BufferedImage(pixelWidth, pixelHeight, typeIntArgb);
	}

	@Override
	public Dimension newDimension(int width, int height) {
		return new geogebra.web.awt.Dimension(width, height);
	}

	@Override
	public Point2D newPoint2D() {
		return new geogebra.web.awt.Point2D();
	}
	
	@Override
	public Point2D newPoint2D(double x, double y) {
		return new geogebra.web.awt.Point2D(x, y);
	}
	
	@Override
	public Rectangle newRectangle(int x, int y) {
		return new geogebra.web.awt.Rectangle(x, y);
	}

	@Override
	public GeneralPath newGeneralPath() {
		return new geogebra.web.awt.GeneralPath();
	}

	@Override
	public BasicStroke newMyBasicStroke(float f) {
		return new geogebra.web.awt.BasicStroke(f,geogebra.web.awt.BasicStroke.CAP_ROUND,geogebra.web.awt.BasicStroke.JOIN_ROUND);
	}

	@Override
	public BasicStroke newBasicStroke(float width, int endCap, int lineJoin,
	        float miterLimit, float[] dash, float f) {
		return new geogebra.web.awt.BasicStroke(width, endCap, lineJoin, miterLimit, dash, f);
	}

	@Override
	public Line2D newLine2D() {
		return new geogebra.web.awt.Line2D();
	}

	@Override
	public Rectangle newRectangle(Rectangle bb) {
		return new geogebra.web.awt.Rectangle(bb);
	}

	@Override
    public Ellipse2DDouble newEllipse2DDouble() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Ellipse2DFloat newEllipse2DFloat(int i, int j, int k, int l) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public BasicStroke newBasicStroke(float f) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Rectangle newRectangle() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Arc2D newArc2D() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public QuadCurve2D newQuadCurve2D() {
	    // TODO Auto-generated method stub
	    return null;
    }

	/*
	@Override
    public Area newArea(GeneralPathClipped hypRight) {
	    // TODO Auto-generated method stub
	    return null;
    }
    */

	@Override
    public Area newArea() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Area newArea(Shape shape) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeneralPath newGeneralPath(int rule) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public CubicCurve2D newCubicCurve2D() {
	    // TODO Auto-generated method stub
	    return null;
    }

}
