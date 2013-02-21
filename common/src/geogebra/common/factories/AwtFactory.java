package geogebra.common.factories;

import geogebra.common.awt.GAffineTransform;
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
import geogebra.common.awt.font.GTextLayout;
import geogebra.common.euclidian.event.ActionListener;
import geogebra.common.euclidian.event.ActionListenerI;
import geogebra.common.euclidian.event.FocusListener;

public abstract class AwtFactory {
	public static AwtFactory prototype = null;
	public abstract GColor newColor(int RGB);
	public abstract GColor newColor(int red, int green, int blue);
	public abstract GColor newColor(int red, int green, int blue, int alpha);
	public abstract GColor newColor(float red, float green, float blue, float alpha);
	public abstract GColor newColor(float red, float green, float blue);
	public abstract GAffineTransform newAffineTransform();
	public abstract GRectangle2D newRectangle2D();
	public abstract GRectangle newRectangle(int x,int y,int w,int h);
	public abstract GBufferedImage newBufferedImage(int pixelWidth,
			int pixelHeight, int typeIntArgb);
	public abstract GDimension newDimension(int width,
			int height);
	public abstract GPoint2D newPoint2D();
	public abstract GRectangle newRectangle(int i, int j);
	public abstract GRectangle newRectangle();
	public abstract GPoint2D newPoint2D(double d, double coord);
	public abstract GGeneralPath newGeneralPath();
	public abstract GBasicStroke newMyBasicStroke(float f);
	public abstract GBasicStroke newBasicStroke(float f,int cap,int join);
	public abstract GBasicStroke newBasicStroke(float width, int endCap, int lineJoin,
			float miterLimit, float[] dash, float f);
	public abstract GBasicStroke newBasicStroke(float f);
	public abstract GLine2D newLine2D();
	public abstract GRectangle newRectangle(GRectangle bb);
	public abstract GEllipse2DDouble newEllipse2DDouble();
	public abstract GEllipse2DFloat newEllipse2DFloat(int i, int j, int k, int l);
	public abstract GArc2D newArc2D();
	public abstract GQuadCurve2D newQuadCurve2D();
	/*public abstract Area newArea(GeneralPathClipped hypRight);*/
	public abstract GArea newArea();
	public abstract GArea newArea(GShape shape);
	public abstract GGeneralPath newGeneralPath(int rule);
	public abstract GCubicCurve2D newCubicCurve2D();
	public abstract GTextLayout newTextLayout(String string, GFont fontLine,
			GFontRenderContext frc);
	public abstract geogebra.common.awt.GAlphaComposite newAlphaComposite(int srcOver,
			float alpha);
	public abstract GBasicStroke newBasicStrokeJoinMitre(float f);
	public abstract GGradientPaint newGradientPaint(int x, int y,
			geogebra.common.awt.GColor bg2, int x2, int i,
			geogebra.common.awt.GColor bg);
	public abstract FocusListener newFocusListener(Object listener);
	public abstract ActionListener newActionListener(ActionListenerI listener);
	public abstract geogebra.common.awt.Component newComponent(Object component);
	public abstract GPaint newTexturePaint(GBufferedImage subimage, GRectangle rect);
	
}
