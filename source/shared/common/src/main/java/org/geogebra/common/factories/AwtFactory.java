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

	/**
	 * @return a new affine transform
	 */
	public abstract GAffineTransform newAffineTransform();

	/**
	 * @return a new rectangle
	 */
	public abstract GRectangle2D newRectangle2D();

	/**
	 * Creates a rectangle.
	 * @param x left
	 * @param y top
	 * @param width width
	 * @param height height
	 * @return rectangle of given size
	 */
	public abstract GRectangle newRectangle(int x, int y, int width, int height);

	/**
	 * @param pixelWidth width in pixels
	 * @param pixelHeight height in pixels
	 * @param pixelRatio ratio between physical pixels and DIP
	 * @return a new buffered image
	 */
	public abstract GBufferedImage newBufferedImage(int pixelWidth,
			int pixelHeight, double pixelRatio);

	/**
	 * @param pixelWidth
	 *            width
	 * @param pixelHeight
	 *            height
	 * @param g2
	 *            graphics (used for the pixel ratio)
	 * @return image
	 */
	public GBufferedImage newBufferedImage(int pixelWidth, int pixelHeight,
			GGraphics2D g2) {
		return newBufferedImage(pixelWidth, pixelHeight, 1);
	}

	/**
	 * @param width width in pixels
	 * @param height height in pixels
	 * @param transparency whether to enable alpha
	 * @return a new image
	 */
	public abstract GBufferedImage createBufferedImage(int width, int height,
			boolean transparency);

	/**
	 * @param width width
	 * @param height height
	 * @return a new dimension
	 */
	public abstract GDimension newDimension(int width, int height);

	/**
	 * @param width width
	 * @param height height
	 * @return a new rectangle with given size
	 */
	public abstract GRectangle newRectangle(int width, int height);

	/**
	 * @return a new empty rectangle
	 */
	public abstract GRectangle newRectangle();

	/**
	 * @return a new empty general path
	 */
	public abstract GGeneralPath newGeneralPath();

	/**
	 * Creates a stroke with round cap and join.
	 * @param f thickness
	 * @return a new stroke
	 */
	public abstract GBasicStroke newMyBasicStroke(double f);

	/**
	 * Creates a stroke with given cap and join.
	 * @param f thickness
	 * @param cap one of GBasicStroke.CAP_* values
	 * @param join one of GBasicStroke.JOIN_* values
	 * @return a new stroke
	 */
	public abstract GBasicStroke newBasicStroke(double f, int cap, int join);

	/**
	 *
	 * @param width stroke width
	 * @param endCap one of GBasicStroke.CAP_* values
	 * @param lineJoin one of GBasicStroke.JOIN_* values
	 * @param miterLimit miter limit
	 * @param dash dash pattern
	 * @return stroke definition
	 */
	public abstract GBasicStroke newBasicStroke(double width, int endCap,
			int lineJoin, double miterLimit, double[] dash);

	/**
	 * @param f thickness
	 * @return stroke with square cap and miter join
	 */
	public abstract GBasicStroke newBasicStroke(double f);

	/**
	 * @return a new line segment
	 */
	public abstract GLine2D newLine2D();

	/**
	 * @param bb bounding box
	 * @return a new rectangle wih given bounds
	 */
	public abstract GRectangle newRectangle(GRectangle bb);

	/**
	 * @return a new empty ellipse
	 */
	public abstract GEllipse2DDouble newEllipse2DDouble();

	/**
	 * Creates an ellipse with axes parallel to the coordinate grid.
	 * @param x left
	 * @param y top
	 * @param width width
	 * @param height height
	 * @return ellipse of given size.
	 */
	public abstract GEllipse2DDouble newEllipse2DDouble(double x, double y,
			double width, double height);

	/**
	 * @return a new empty arc
	 */
	public abstract GArc2D newArc2D();

	/**
	 * @return a new empty quadratic curve
	 */
	public abstract GQuadCurve2D newQuadCurve2D();

	/**
	 * @return a new area
	 */
	public abstract GArea newArea();

	/**
	 * Converts a shape to an Area object.
	 * @param shape shape
	 * @return a new area
	 */
	public abstract GArea newArea(GShape shape);

	/**
	 * @param rule winding rule (GpathIterator.WIND_EVEN_ODD or GpathIterator.WIND_NON_ZERO).
	 * @return a new general path
	 */
	public abstract GGeneralPath newGeneralPath(int rule);

	/**
	 * @param string text
	 * @param fontLine font
	 * @param frc font rendering context
	 * @return text layout
	 */
	public abstract GTextLayout newTextLayout(String string, GFont fontLine,
			GFontRenderContext frc);

	/**
	 * @param alpha opacity
	 * @return a new alpha composite
	 */
	public abstract GAlphaComposite newAlphaComposite(double alpha);

	/**
	 * @param f thickness
	 * @return stroke with given thickness, square cap and a miter join
	 */
	public abstract GBasicStroke newBasicStrokeJoinMitre(double f);

	/**
	 * Creates new linear gradient paint.
	 * @param x1 start point's x-coordinate
	 * @param y1 start point's x-coordinate
	 * @param color1 stat color
	 * @param x2 end point's x-coordinate
	 * @param y2 end point's x-coordinate
	 * @param color2 end color
	 * @return gradient paint
	 */
	public abstract GGradientPaint newGradientPaint(double x1, double y1,
			GColor color1, double x2, double y2, GColor color2);

	/**
	 * @param subimage texture image
	 * @param rect target rectangle
	 * @return texture paint
	 */
	public abstract GPaint newTexturePaint(GBufferedImage subimage,
			GRectangle rect);

	/**
	 * @param subimage texture image
	 * @param rect target rectangle
	 * @return texture paint
	 */
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

	/**
	 * @param width width in SVG units
	 * @param height height in SVG units
	 * @return graphics for SVG export
	 */
	public GGraphics2D getSVGGraphics(int width, int height) {
		return null;
	}

	/**
	 * @param width width in pixels
	 * @param height height in pixels
	 * @return graphics for PDF export
	 */
	public GGraphics2D getPDFGraphics(int width, int height) {
		return null;
	}
}
