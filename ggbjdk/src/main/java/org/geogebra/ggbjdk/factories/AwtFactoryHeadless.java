package org.geogebra.ggbjdk.factories;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GQuadCurve2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.ggbjdk.java.awt.geom.AffineTransform;
import org.geogebra.ggbjdk.java.awt.geom.Arc2D;
import org.geogebra.ggbjdk.java.awt.geom.Area;
import org.geogebra.ggbjdk.java.awt.geom.Ellipse2D;
import org.geogebra.ggbjdk.java.awt.geom.GeneralPath;
import org.geogebra.ggbjdk.java.awt.geom.Line2D;
import org.geogebra.ggbjdk.java.awt.geom.Path2D;
import org.geogebra.ggbjdk.java.awt.geom.Point2D;
import org.geogebra.ggbjdk.java.awt.geom.QuadCurve2D;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle2D;

/**
 * Class used for testing.
 */
public abstract class AwtFactoryHeadless extends AwtFactory {

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
	public GRectangle newRectangle(int w, int h) {
		return new Rectangle(w, h);
	}

	@Override
	public GRectangle newRectangle() {
		return new Rectangle();
	}

	@Override
	public GPoint2D newPoint2D() {
		return new Point2D.Double();
	}

	@Override
	public GPoint2D newPoint2D(double x, double y) {
		return new Point2D.Double(x, y);
	}

	@Override
	public GGeneralPath newGeneralPath() {
		return new GeneralPath(Path2D.WIND_EVEN_ODD);
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
	public GEllipse2DDouble newEllipse2DDouble(double x, double y, double w,
			double h) {
		return new Ellipse2D.Double(x, y, w, h);
	}

	@Override
	public GArc2D newArc2D() {
		return new Arc2D.Double();
	}

	@Override
	public GQuadCurve2D newQuadCurve2D() {
		return new QuadCurve2D.Double();
	}

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

}
