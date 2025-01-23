package org.geogebra.common.factories;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GAlphaComposite;
import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGradientPaint;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GQuadCurve2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.main.App;

public abstract class AwtFactory {

	private static volatile AwtFactory prototype = null;

	private static final Object lock = new Object();

	public static AwtFactory getPrototype() {
		return prototype;
	}

	/**
	 * @param p
	 *            prototype
	 */
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
			int pixelHeight, double pixelRatio);

	/**
	 * @param pixelWidth
	 *            width
	 * @param pixelHeight
	 *            height
	 * @param g2
	 *            graphics (used for pixel rratio)
	 * @return image
	 */
	public GBufferedImage newBufferedImage(int pixelWidth, int pixelHeight,
			GGraphics2D g2) {
		return newBufferedImage(pixelWidth, pixelHeight, 1);
	}

	public abstract GBufferedImage createBufferedImage(int width, int height,
			boolean transparency);

	public abstract MyImage newMyImage(int pixelWidth, int pixelHeight,
			int typeIntArgb);

	public abstract GDimension newDimension(int width, int height);

	public abstract GRectangle newRectangle(int w, int h);

	public abstract GRectangle newRectangle();

	public abstract GGeneralPath newGeneralPath();

	public abstract GBasicStroke newMyBasicStroke(double f);

	public abstract GBasicStroke newBasicStroke(double f, int cap, int join);

	public abstract GBasicStroke newBasicStroke(double width, int endCap,
			int lineJoin, double miterLimit, double[] dash);

	public abstract GBasicStroke newBasicStroke(double f);

	public abstract GLine2D newLine2D();

	public abstract GRectangle newRectangle(GRectangle bb);

	public abstract GEllipse2DDouble newEllipse2DDouble();

	public abstract GEllipse2DDouble newEllipse2DDouble(double x, double y,
			double w, double h);

	public abstract GArc2D newArc2D();

	public abstract GQuadCurve2D newQuadCurve2D();

	public abstract GArea newArea();

	public abstract GArea newArea(GShape shape);

	public abstract GGeneralPath newGeneralPath(int rule);

	public abstract GTextLayout newTextLayout(String string, GFont fontLine,
			GFontRenderContext frc);

	public abstract GAlphaComposite newAlphaComposite(double alpha);

	public abstract GBasicStroke newBasicStrokeJoinMitre(double f);

	public abstract GGradientPaint newGradientPaint(double x, double y,
			GColor bg2, double x2, double i, GColor bg);

	public abstract GPaint newTexturePaint(GBufferedImage subimage,
			GRectangle rect);

	public abstract GPaint newTexturePaint(MyImage subimage, GRectangle rect);

	/**
	 * @param name
	 *            font name
	 * @param style
	 *            style bitmask (see GFont constants)
	 * @param size
	 *            size
	 * @return font
	 */
	public abstract GFont newFont(String name, int style, int size);

	/**
	 * @param tx
	 *            x shift
	 * @param ty
	 *            y shift
	 * @return translation transform
	 */
	public static GAffineTransform getTranslateInstance(double tx, double ty) {
		GAffineTransform Tx = prototype.newAffineTransform();
		Tx.setToTranslation(tx, ty);
		return Tx;
	}

	/**
	 *
	 * @param sx
	 *            x scale
	 * @param sy
	 *            y scale
	 * @return scale transform
	 */
	public static GAffineTransform getScaleInstance(double sx, double sy) {
		GAffineTransform Tx = prototype.newAffineTransform();
		Tx.setToScale(sx, sy);
		return Tx;
	}

	/**
	 * @param theta
	 *            angle
	 * @return rotation transform
	 */
	public static GAffineTransform getRotateInstance(double theta) {
		GAffineTransform Tx = prototype.newAffineTransform();
		Tx.setToRotation(theta);
		return Tx;
	}

	/**
	 * @param theta
	 *            angle
	 * @param x
	 *            center x
	 * @param y
	 *            center y
	 * @return rotation transform
	 */
	public static GAffineTransform getRotateInstance(double theta, double x,
			double y) {
		GAffineTransform Tx = prototype.newAffineTransform();
		Tx.setToRotation(theta, x, y);
		return Tx;
	}

	/**
	 * @param fillShape
	 *            shape
	 * @param g2
	 *            g2
	 * @param subImage2
	 *            subImage
	 * @param application
	 *            app
	 */
	public void fillAfterImageLoaded(GShape fillShape, GGraphics2D g2,
			GBufferedImage subImage2, App application) {
		// needed in web only
	}

	/**
	 * @param g2
	 *            graphics
	 * @param x1
	 *            x-coord of the first point
	 * @param y1
	 *            y-coord of the first point
	 * @param x2
	 *            x-coord of the second point
	 * @param y2
	 *            y-coord of the second point
	 * @param x3
	 *            x-coord of the third point
	 * @param y3
	 *            y-coord of the third point
	 */
	public static void fillTriangle(GGraphics2D g2, double x1, double y1,
			double x2,
			int y2, int x3, int y3) {
		GGeneralPath gp = AwtFactory.getPrototype().newGeneralPath();
		gp.moveTo(x1, y1);
		gp.lineTo(x2, y2);
		gp.lineTo(x3, y3);
		gp.lineTo(x1, y1);
		GArea shape = AwtFactory.getPrototype().newArea(gp);
		g2.fill(shape);
	}

	/**
	 * web (GWT) doesn't have a native float object so keep things as double as
	 * much as possible
	 * 
	 * @param array
	 *            input array
	 * @return array with elements cast to (float)
	 */
	public static float[] doubleToFloat(double[] array) {
		if (array == null) {
			return null;
		}
		int n = array.length;
		float[] ret = new float[n];
		for (int i = 0; i < n; i++) {
			ret[i] = (float) array[i];
		}
		return ret;
	}

	/**
	 * web (GWT) doesn't have a native float object so keep things as double as
	 * much as possible
	 * 
	 * @param array
	 *            input array
	 * @return array with elements cast to (double)
	 */
	public static double[] floatToDouble(float[] array) {
		if (array == null) {
			return null;
		}
		int n = array.length;
		double[] ret = new double[n];
		for (int i = 0; i < n; i++) {
			// implicit cast
			ret[i] = array[i];
		}
		return ret;
	}

}
