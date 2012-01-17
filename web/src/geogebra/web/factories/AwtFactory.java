package geogebra.web.factories;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.AlphaComposite;
import geogebra.common.awt.Arc2D;
import geogebra.common.awt.Area;
import geogebra.common.awt.BasicStroke;
import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.awt.Color;
import geogebra.common.awt.CubicCurve2D;
import geogebra.common.awt.Dimension;
import geogebra.common.awt.Ellipse2DDouble;
import geogebra.common.awt.Ellipse2DFloat;
import geogebra.common.awt.Font;
import geogebra.common.awt.FontRenderContext;
import geogebra.common.awt.GeneralPath;
import geogebra.common.awt.Line2D;
import geogebra.common.awt.Point;
import geogebra.common.awt.Point2D;
import geogebra.common.awt.QuadCurve2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;
import geogebra.common.awt.RectangularShape;
import geogebra.common.awt.Shape;
import geogebra.common.awt.font.TextLayout;
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
	public Point newPoint() {
		return new geogebra.web.awt.Point();
	}

	@Override
	public Point newPoint(int x, int y) {
		return new geogebra.web.awt.Point(x, y);
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
	    return new geogebra.web.awt.Ellipse2DDouble();
    }

	@Override
    public Ellipse2DFloat newEllipse2DFloat(int i, int j, int k, int l) {
		return new geogebra.web.awt.Ellipse2DFloat();
    }

	@Override
    public BasicStroke newBasicStroke(float f) {
	    return new geogebra.web.awt.BasicStroke(f);
    }

	@Override
    public Rectangle newRectangle() {
		return new geogebra.web.awt.Rectangle();
    }

	@Override
    public Arc2D newArc2D() {
		return new geogebra.web.awt.Arc2D();
    }

	@Override
    public QuadCurve2D newQuadCurve2D() {
		return new geogebra.web.awt.QuadCurve2D();
    }

	/*
	@Override
    public Area newArea(GeneralPathClipped hypRight) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }
    */

	@Override
    public Area newArea() {
		return new geogebra.web.awt.Area();
    }

	@Override
    public Area newArea(Shape shape) {
		return new geogebra.web.awt.Area(shape);
    }

	@Override
    public GeneralPath newGeneralPath(int rule) {
		return new geogebra.web.awt.GeneralPath(rule);
    }

	@Override
    public CubicCurve2D newCubicCurve2D() {
		return new geogebra.web.awt.CubicCurve2D();
    }

	@Override
    public BasicStroke newBasicStroke(float f, int cap, int join) {
	    return new geogebra.web.awt.BasicStroke(f,cap,join);
    }

	@Override
    public TextLayout newTextLayout(String string, Font fontLine,
            FontRenderContext frc) {
	    return new geogebra.web.awt.font.TextLayout(string,fontLine,(geogebra.web.awt.FontRenderContext) frc);
    }

	@Override
    public AlphaComposite newAlphaComposite(int srcOver, float alpha) {
	    // TODO Auto-generated method stub
	    return null;
    }

}
