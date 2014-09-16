package geogebra.html5.factories;

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
import geogebra.common.awt.GEllipse2DDouble;
import geogebra.common.awt.GEllipse2DFloat;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GFontRenderContext;
import geogebra.common.awt.GGeneralPath;
import geogebra.common.awt.GGradientPaint;
import geogebra.common.awt.GLine2D;
import geogebra.common.awt.GPaint;
import geogebra.common.awt.GPoint2D;
import geogebra.common.awt.GQuadCurve2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GRectangle2D;
import geogebra.common.awt.GShape;
import geogebra.common.awt.MyImage;
import geogebra.common.awt.font.GTextLayout;
import geogebra.common.euclidian.event.ActionListener;
import geogebra.common.euclidian.event.ActionListenerI;
import geogebra.common.euclidian.event.FocusListener;
import geogebra.common.factories.AwtFactory;
import geogebra.common.main.App;
import geogebra.html5.awt.GFontW;
import geogebra.html5.awt.GGradientPaintW;
import geogebra.html5.awt.GTexturePaintW;
import geogebra.html5.gawt.GBufferedImageW;
import geogebra.html5.main.MyImageW;
import geogebra.html5.openjdk.awt.geom.AffineTransform;
import geogebra.html5.openjdk.awt.geom.Arc2D;
import geogebra.html5.openjdk.awt.geom.Area;
import geogebra.html5.openjdk.awt.geom.Ellipse2D;
import geogebra.html5.openjdk.awt.geom.GeneralPath;
import geogebra.html5.openjdk.awt.geom.Line2D;
import geogebra.html5.openjdk.awt.geom.QuadCurve2D;
import geogebra.html5.openjdk.awt.geom.Rectangle;
import geogebra.html5.openjdk.awt.geom.Rectangle2D;

/**
 * Creates AWT wrappers for web
 *
 */
public class AwtFactoryW extends AwtFactory {

	public AwtFactoryW(){
		GColor.initColors(this);
	}
	
	@Override
	public GColor newColor(int RGB) {
		return new geogebra.html5.awt.GColorW(RGB);
	}

	@Override
	public GColor newColor(int red, int green, int blue) {
		return new geogebra.html5.awt.GColorW(red, green, blue);
	}

	@Override
	public GColor newColor(int red, int green, int blue, int alpha) {
		return new geogebra.html5.awt.GColorW(red, green, blue, alpha);
	}

	@Override
	public GColor newColor(float red, float green, float blue, float alpha) {
		return new geogebra.html5.awt.GColorW(red,green,blue,alpha);
	}

	@Override
	public GColor newColor(float red, float green, float blue) {
		return new geogebra.html5.awt.GColorW(red, green, blue);
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
	public GBufferedImage newBufferedImage(int pixelWidth,
	        int pixelHeight, int typeIntArgb) {
		return new GBufferedImageW(pixelWidth, pixelHeight, typeIntArgb);
	}

	@Override
	public GDimension newDimension(int width, int height) {
		return new geogebra.html5.awt.GDimensionW(width, height);
	}

	@Override
	public GPoint2D newPoint2D() {
		return new geogebra.html5.awt.GPoint2DW();
	}
	
	@Override
	public GPoint2D newPoint2D(double x, double y) {
		return new geogebra.html5.awt.GPoint2DW(x, y);
	}


	@Override
	public GRectangle newRectangle(int x, int y) {
		return new Rectangle(x, y);
	}

	@Override
	public GGeneralPath newGeneralPath() {
		// default winding rule changed for ggb50 (for Polygons) #3983
		return new GeneralPath(geogebra.html5.openjdk.awt.geom.GeneralPath.WIND_EVEN_ODD);
	}

	@Override
	public GBasicStroke newMyBasicStroke(float f) {
		return new geogebra.html5.awt.GBasicStrokeW(f,geogebra.html5.awt.GBasicStrokeW.CAP_ROUND,geogebra.html5.awt.GBasicStrokeW.JOIN_ROUND);
	}

	@Override
	public GBasicStroke newBasicStroke(float width, int endCap, int lineJoin,
	        float miterLimit, float[] dash, float f) {
		return new geogebra.html5.awt.GBasicStrokeW(width, endCap, lineJoin, miterLimit, dash, f);
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
	    return new geogebra.html5.awt.GBasicStrokeW(f);
    }

	@Override
	// CAP_BUTT, JOIN_MITER behaves differently on JRE & GWT
	// see #1699
    public GBasicStroke newBasicStrokeJoinMitre(float f) {
	    return new geogebra.html5.awt.GBasicStrokeW(f, GBasicStroke.CAP_SQUARE, GBasicStroke.JOIN_MITER);
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
	@Override
    public Area newArea(GeneralPathClipped hypRight) {
	    AbstractApplication.debug("implementation needed really"); // TODO Auto-generated
	    return null;
    }
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
		return new geogebra.html5.awt.GCubicCurve2DW();
    }

	@Override
    public GBasicStroke newBasicStroke(float f, int cap, int join) {
	    return new geogebra.html5.awt.GBasicStrokeW(f,cap,join);
    }

	@Override
    public GTextLayout newTextLayout(String string, GFont fontLine,
            GFontRenderContext frc) {
	    return new geogebra.html5.awt.font.GTextLayoutW(string,fontLine,(geogebra.html5.awt.GFontRenderContextW) frc);
    }

	@Override
    public GAlphaComposite newAlphaComposite(int srcOver, float alpha) {
	   return new geogebra.html5.awt.GAlphaCompositeW(srcOver,alpha);
    }

	@Override
    public GGradientPaint newGradientPaint(int x, int y, GColor bg2, int x2,
            int i, GColor bg) {
	    return new GGradientPaintW(x,y,bg2,x2,i,bg);
    }
	
	@Override
    public FocusListener newFocusListener(Object listener) {
	    return new geogebra.html5.event.FocusListenerW(listener);
    }

	@Override
    public Component newComponent(Object component) {
		App.debug("newComponent: implementation needed really"); // TODO Auto-generated
	    return null;
    }

	@Override
    public ActionListener newActionListener(ActionListenerI listener) {
		return new geogebra.html5.event.ActionListenerW(listener);
    }

	@Override
    public GFont newFont(String name, int style, int size) {
	    return new GFontW(name, style, size);
    }

	@Override
    public MyImage newMyImage(int pixelWidth, int pixelHeight, int typeIntArgb) {
	    return new MyImageW(new GBufferedImageW(pixelWidth, pixelHeight, typeIntArgb).getImageElement(), false);
    }

	@Override
    public GPaint newTexturePaint(GBufferedImage subimage, GRectangle rect) {
	    return new GTexturePaintW((GBufferedImageW)subimage);
    }

	@Override
    public GPaint newTexturePaint(MyImage subimage, GRectangle rect) {
	    return new GTexturePaintW(new GBufferedImageW(((MyImageW) subimage).getImage()));

    }


}
