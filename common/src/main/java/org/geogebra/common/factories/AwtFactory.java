package org.geogebra.common.factories;

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
import org.geogebra.common.awt.GGraphics2D;
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
import org.geogebra.common.main.App;

public abstract class AwtFactory {


	private static volatile AwtFactory prototype = null;

	private static final Object lock = new Object();

	public static AwtFactory getPrototype() {
		return prototype;
	}

	public static void setPrototypeIfNull(AwtFactory p) {

		synchronized (lock) {
			if (prototype == null) {
				prototype = p;
			}
		}
	}

	public abstract GAffineTransform newAffineTransform();

	public abstract GRectangle2D newRectangle2D();

	public abstract GRectangle newRectangle(int x, int y, int w, int h);

	public abstract GBufferedImage newBufferedImage(int pixelWidth,
			int pixelHeight, float pixelRatio);

	public abstract GBufferedImage createBufferedImage(int width, int height,
			boolean transparency);

	public abstract MyImage newMyImage(int pixelWidth, int pixelHeight,
			int typeIntArgb);

	public abstract GDimension newDimension(int width, int height);

	public abstract GPoint2D newPoint2D();

	public abstract GRectangle newRectangle(int i, int j);

	public abstract GRectangle newRectangle();

	public abstract GPoint2D newPoint2D(double d, double coord);

	public abstract GGeneralPath newGeneralPath();

	public abstract GBasicStroke newMyBasicStroke(float f);

	public abstract GBasicStroke newBasicStroke(float f, int cap, int join);

	public abstract GBasicStroke newBasicStroke(float width, int endCap,
			int lineJoin, float miterLimit, float[] dash, float f);

	public abstract GBasicStroke newBasicStroke(float f);

	public abstract GLine2D newLine2D();

	public abstract GRectangle newRectangle(GRectangle bb);

	public abstract GEllipse2DDouble newEllipse2DDouble();

	public abstract GEllipse2DFloat newEllipse2DFloat(int i, int j, int k, int l);

	public abstract GArc2D newArc2D();

	public abstract GQuadCurve2D newQuadCurve2D();

	/* public abstract Area newArea(GeneralPathClipped hypRight); */
	public abstract GArea newArea();

	public abstract GArea newArea(GShape shape);

	public abstract GGeneralPath newGeneralPath(int rule);

	public abstract GCubicCurve2D newCubicCurve2D();

	public abstract GTextLayout newTextLayout(String string, GFont fontLine,
			GFontRenderContext frc);

	public abstract GAlphaComposite newAlphaComposite(
			int srcOver, float alpha);

	public abstract GBasicStroke newBasicStrokeJoinMitre(float f);

	public abstract GGradientPaint newGradientPaint(int x, int y, GColor bg2,
			int x2, int i, GColor bg);

	public abstract FocusListener newFocusListener(Object listener);

	public abstract ActionListener newActionListener(ActionListenerI listener);


	public abstract GPaint newTexturePaint(GBufferedImage subimage,
			GRectangle rect);

	public abstract GPaint newTexturePaint(MyImage subimage, GRectangle rect);

	public abstract GFont newFont(String name, int style, int size);

	public abstract GPolygon newPolygon();

	public static GAffineTransform getTranslateInstance(double tx, double ty) {
		GAffineTransform Tx = prototype.newAffineTransform();
		Tx.setToTranslation(tx, ty);
		return Tx;
	}

	public static GAffineTransform getScaleInstance(double sx, double sy) {
		GAffineTransform Tx = prototype.newAffineTransform();
		Tx.setToScale(sx, sy);
		return Tx;
	}

	public static GAffineTransform getRotateInstance(double theta) {
		GAffineTransform Tx = prototype.newAffineTransform();
		Tx.setToRotation(theta);
		return Tx;
	}

	public static GAffineTransform getRotateInstance(double theta, double x,
			double y) {
		GAffineTransform Tx = prototype.newAffineTransform();
		Tx.setToRotation(theta, x, y);
		return Tx;
	}

	public void fillAfterImageLoaded(GShape fillShape, GGraphics2D g2,
			GBufferedImage subImage2, App application) {
		// needed in web only

	}

}
