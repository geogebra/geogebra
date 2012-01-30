package geogebra.common.factories;

import geogebra.common.awt.GradientPaint;
import geogebra.common.awt.Area;
import geogebra.common.awt.CubicCurve2D;
import geogebra.common.awt.Font;
import geogebra.common.awt.FontRenderContext;
import geogebra.common.awt.QuadCurve2D;
import geogebra.common.awt.BasicStroke;
import geogebra.common.awt.BufferedImage;
import geogebra.common.awt.Color;
import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.Ellipse2DDouble;
import geogebra.common.awt.Ellipse2DFloat;
import geogebra.common.awt.Line2D;
import geogebra.common.awt.Point;
import geogebra.common.awt.Point2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Rectangle2D;
import geogebra.common.awt.GeneralPath;
import geogebra.common.awt.Dimension;
import geogebra.common.awt.Arc2D;
import geogebra.common.awt.Shape;
import geogebra.common.awt.font.TextLayout;

public abstract class AwtFactory {
	public static AwtFactory prototype = null;
	public abstract Color newColor(int RGB);
	public abstract Color newColor(int red, int green, int blue);
	public abstract Color newColor(int red, int green, int blue, int alpha);
	public abstract Color newColor(float red, float green, float blue, float alpha);
	public abstract Color newColor(float red, float green, float blue);
	public abstract AffineTransform newAffineTransform();
	public abstract Rectangle2D newRectangle2D();
	public abstract Rectangle newRectangle(int x,int y,int w,int h);
	public abstract BufferedImage newBufferedImage(int pixelWidth,
			int pixelHeight, int typeIntArgb);
	public abstract Dimension newDimension(int width,
			int height);
	public abstract Point2D newPoint2D();
	public abstract Rectangle newRectangle(int i, int j);
	public abstract Rectangle newRectangle();
	public abstract Point2D newPoint2D(double d, double coord);
	public abstract GeneralPath newGeneralPath();
	public abstract BasicStroke newMyBasicStroke(float f);
	public abstract BasicStroke newBasicStroke(float f,int cap,int join);
	public abstract BasicStroke newBasicStroke(float width, int endCap, int lineJoin,
			float miterLimit, float[] dash, float f);
	public abstract BasicStroke newBasicStroke(float f);
	public abstract Line2D newLine2D();
	public abstract Rectangle newRectangle(Rectangle bb);
	public abstract Ellipse2DDouble newEllipse2DDouble();
	public abstract Ellipse2DFloat newEllipse2DFloat(int i, int j, int k, int l);
	public abstract Arc2D newArc2D();
	public abstract QuadCurve2D newQuadCurve2D();
	/*public abstract Area newArea(GeneralPathClipped hypRight);*/
	public abstract Area newArea();
	public abstract Area newArea(Shape shape);
	public abstract GeneralPath newGeneralPath(int rule);
	public abstract CubicCurve2D newCubicCurve2D();
	public abstract TextLayout newTextLayout(String string, Font fontLine,
			FontRenderContext frc);
	public abstract geogebra.common.awt.AlphaComposite newAlphaComposite(int srcOver,
			float alpha);
	public abstract BasicStroke newBasicStrokeJoinMitre(float f);
	public abstract GradientPaint newGradientPaint(int x, int y,
			geogebra.common.awt.Color bg2, int x2, int i,
			geogebra.common.awt.Color bg);
	
}
