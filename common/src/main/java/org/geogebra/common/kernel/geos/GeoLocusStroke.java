package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolver;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoLocusStroke;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.MyMath;

import java.util.ArrayList;

/**
 * Class for polylines created using pen
 * 
 * @author Zbynek
 */
public class GeoLocusStroke extends GeoLocus
		implements MatrixTransformable, Translateable, Transformable, Mirrorable,
		PointRotateable, Dilateable {

	/** cache the part of XML that follows after expression label="stroke1" */
	private StringBuilder xmlPoints;

	/**
	 * @param cons
	 *            construction
	 */
	public GeoLocusStroke(Construction cons) {
		super(cons);
		setVisibleInView3D(false);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.PENSTROKE;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return label;
	}

	@Override
	public boolean hasLineOpacity() {
		return true;
	}

	@Override
	public boolean isPinnable() {
		return true;
	}

	@Override
	final public boolean isAlgebraViewEditable() {
		return false;
	}

	@Override
	public boolean isLabelVisible() {
		return false;
	}

	@Override
	public DescriptionMode needToShowBothRowsInAV() {
		return DescriptionMode.VALUE;
	}

	@Override
	public GeoElement copy() {
		GeoLocusStroke ret = new GeoLocusStroke(cons);
		ret.set(this);
		return ret;
	}

	/**
	 * Run a callback for points, skipping the control points.
	 * 
	 * @param handler
	 *            handler to be called for each point
	 */
	public void processPointsWithoutControl(
			AsyncOperation<MyPoint> handler) {
		MyPoint last = null;
		for (MyPoint pt : getPoints()) {
			if (pt.getSegmentType() != SegmentType.CONTROL) {
				// also ignore third point added to simple segment
				// to able to calc control points
				if (!(last != null
						&& last.getSegmentType() == pt.getSegmentType()
						&& last.isEqual(pt))) {
					handler.callback(pt);
					last = pt;
				}
			}
		}
	}

	@Override
	public boolean isMatrixTransformable() {
		return true;
	}

	@Override
	public void matrixTransform(double a00, double a01, double a10,
			double a11) {
		for (MyPoint pt : getPoints()) {
			double x = pt.x;
			double y = pt.y;
			pt.x = a00 * x + a01 * y;
			pt.y = a10 * x + a11 * y;
		}
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		for (MyPoint pt : getPoints()) {
			double x = pt.x;
			double y = pt.y;
			double z = a20 * x + a21 * y + a22;
			pt.x = (a00 * x + a01 * y + a02) / z;
			pt.y = (a10 * x + a11 * y + a12) / z;
		}
	}

	@Override
	public void dilate(NumberValue r, Coords S) {
		double rval = r.getDouble();
		double crval = 1 - rval;

		for (MyPoint pt : getPoints()) {
			pt.x = rval * pt.x + crval * S.getX();
			pt.y = rval * pt.y + crval * S.getY();
		}
	}

	@Override
	public void mirror(Coords Q) {
		for (MyPoint pt : getPoints()) {
			pt.x = 2 * Q.getX() - pt.x;
			pt.y = 2 * Q.getY() - pt.y;
		}
	}

	@Override
	public void mirror(GeoLineND g1) {
		GeoLine g = (GeoLine) g1;

		// Y = S(phi).(X - Q) + Q
		// where Q is a point on g, S(phi) is the mirrorTransform(phi)
		// and phi/2 is the line's slope angle

		// get arbitrary point of line
		double qx, qy;
		if (Math.abs(g.getX()) > Math.abs(g.getY())) {
			qx = -g.getZ() / g.getX();
			qy = 0.0d;
		} else {
			qx = 0.0d;
			qy = -g.getZ() / g.getY();
		}

		// S(phi)
		double phi = 2.0 * Math.atan2(-g.getX(), g.getY());

		double cos = Math.cos(phi);
		double sin = Math.sin(phi);

		for (MyPoint pt : getPoints()) {
			// translate -Q
			pt.x -= qx;
			pt.y -= qy;

			double x0 = pt.x * cos + pt.y * sin;
			pt.y = pt.x * sin - pt.y * cos;
			pt.x = x0;

			// translate back +Q
			pt.x += qx;
			pt.y += qy;
		}
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {
		Coords Q = S.getInhomCoords();

		double phi = r.getDouble();
		double cos = MyMath.cos(phi);
		double sin = Math.sin(phi);

		double qx = Q.getX();
		double qy = Q.getY();

		for (MyPoint pt : getPoints()) {
			double x = pt.x;
			double y = pt.y;

			pt.x = (x - qx) * cos + (qy - y) * sin + qx;
			pt.y = (x - qx) * sin + (y - qy) * cos + qy;
		}
	}

	@Override
	public void rotate(NumberValue r) {
		double phi = r.getDouble();
		double cos = MyMath.cos(phi);
		double sin = Math.sin(phi);

		for (MyPoint pt : getPoints()) {
			double x = pt.x;
			double y = pt.y;

			pt.x = x * cos - y * sin;
			pt.y = x * sin + y * cos;
		}
	}

	@Override
	public boolean isTranslateable() {
		return true;
	}

	@Override
	public void translate(Coords v) {
		for (MyPoint pt : getPoints()) {
			pt.x += v.getX();
			pt.y += v.getY();
		}

		resetXMLPointBuilder();
	}

	@Override
	public boolean isMoveable() {
		return true;
	}

	@Override
	public boolean isFillable() {
		return false;
	}

	@Override
	public boolean isAlgebraDuplicateable() {
		return false;
	}

	@Override
	public boolean isPenStroke() {
		return true;
	}

	/**
	 * Reset list of points for XML
	 */
	public void resetXMLPointBuilder() {
		xmlPoints = null;
	}

	/**
	 * @return builder fox XML representation of points
	 */
	public StringBuilder getXMLPointBuilder() {
		return xmlPoints;
	}

	/**
	 * @param xmlPointBuilder
	 *            builder fox XML representation of points
	 */
	public void setXMLPointBuilder(StringBuilder xmlPointBuilder) {
		this.xmlPoints = xmlPointBuilder;
	}

	@Override
	public String toOutputValueString(StringTemplate tpl) {
		return label;
	}

	/**
	 * @return list of points without the control points
	 */
	public ArrayList<MyPoint> getPointsWithoutControl() {
		final ArrayList<MyPoint> pointsNoControl = new ArrayList<>();
		processPointsWithoutControl(new AsyncOperation<MyPoint>() {

			@Override
			public void callback(MyPoint obj) {
				pointsNoControl.add(obj);
			}
		});
		return pointsNoControl;
	}

	public ArrayList<GeoLocusStroke> split(GRectangle2D rectangle) {
		ArrayList<MyPoint> points = getPointsWithoutControl();

		ArrayList<MyPoint> inside = new ArrayList<>();
		ArrayList<MyPoint> outside = new ArrayList<>();

		boolean insideF;

		if (rectangle.contains(points.get(0).x, points.get(0).y)) {
			inside.add(points.get(0));
			insideF = true;
		} else {
			outside.add(points.get(0));
			insideF = false;
		}

		for (int i = 1; i < points.size(); i++) {
			for (MyPoint intersection : getAllIntersectionPoints(
					points.get(i - 1), points.get(i),
					rectangle.getX(), rectangle.getY(),
					rectangle.getWidth(), rectangle.getHeight())) {
				inside.add(intersection);
				outside.add(new MyPoint(intersection.getX(), intersection.getY()));

				if (insideF) {
					inside.add(new MyPoint(Double.NaN, Double.NaN));
				} else {
					outside.add(new MyPoint(Double.NaN, Double.NaN));
				}

				insideF = !insideF;
			}

			if (insideF) {
				inside.add(points.get(i));
			} else {
				outside.add(points.get(i));
			}
		}

		ArrayList<GeoLocusStroke> result = new ArrayList<>();
		if (inside.size() != 0) {
			AlgoLocusStroke insideStroke = new AlgoLocusStroke(cons, inside);
			result.add(insideStroke.getPenStroke());
		}
		if (outside.size() != 0) {
			AlgoLocusStroke outsideStroke = new AlgoLocusStroke(cons, outside);
			result.add(outsideStroke.getPenStroke());
		}

		return result;
	}

	public ArrayList<MyPoint> getAllIntersectionPoints(MyPoint point1, MyPoint point2,
			double x, double y, double width, double height) {
		// intersection points
		ArrayList<MyPoint> interPointList = new ArrayList<>();

		if (point2.getSegmentType() == SegmentType.CURVE_TO) {
			int i = getPoints().indexOf(point2);
			MyPoint control1 = getPoints().get(i - 2);
			MyPoint control2 = getPoints().get(i - 1);

			// Top line
			getIntersectionPoints(interPointList, point1, control1, control2, point2,
					x, y, x + width, y);

			// Bottom line
			getIntersectionPoints(interPointList, point1, control1, control2, point2,
					x, y - height, x + width, y - height);

			// Left side
			getIntersectionPoints(interPointList, point1, control1, control2, point2,
					x, y, x, y - height);

			// Right side
			getIntersectionPoints(interPointList, point1, control1, control2, point2,
					x + width, y, x + width, y - height);
		} else {
			double x1 = point1.getX();
			double y1 = point1.getY();
			double x2 = point2.getX();
			double y2 = point2.getY();

			// Top line
			MyPoint topInter = getIntersectionPoint(x1, y1, x2, y2,
					x, y, x + width, y);
			if (topInter != null) {
				interPointList.add(topInter);
			}
			// Bottom line
			MyPoint bottomInter = getIntersectionPoint(x1, y1, x2, y2,
					x, y - height, x + width, y - height);
			if (bottomInter != null) {
				interPointList.add(bottomInter);
			}
			// Left side
			MyPoint leftInter = getIntersectionPoint(x1, y1, x2, y2,
					x, y, x, y - height);
			if (leftInter != null) {
				interPointList.add(leftInter);
			}
			// Right side
			MyPoint rightInter = getIntersectionPoint(x1, y1, x2, y2,
					x + width, y, x + width, y - height);
			if (rightInter != null) {
				interPointList.add(rightInter);
			}
		}

		return interPointList;
	}

	private static void getIntersectionPoints(ArrayList<MyPoint> interPointList,
			  MyPoint point1, MyPoint control1, MyPoint control2, MyPoint point2,
			  double x1, double y1, double x2, double y2) {
		double A = y2 - y1;
		double B = x1 - x2;
		double C = x1 * (y1 - y2) + y1 * (x2 - x1);

		double[] bx = bezierCoeffs(point1.x, control1.x, control2.x, point2.x);
		double[] by = bezierCoeffs(point1.y, control1.y, control2.y, point2.y);

		double[] P = {
				A * bx[3] + B * by[3] + C,
				A * bx[2] + B * by[2],
				A * bx[1] + B * by[1],
				A * bx[0] + B * by[0],
		};

		double[] r = new double[3];
		int roots = EquationSolver.solveCubicS(P, r, Kernel.MAX_PRECISION);

		for (int i = 0; i < roots; i++) {
			double t = r[i];
			if (t < 0 || t > 1) {
				continue;
			}

			double x = bx[0] * t * t * t + bx[1] * t * t + bx[2] * t + bx[3];
			double y = by[0] * t * t * t + by[1] * t * t + by[2] * t + by[3];

			if (onSegment(x1, y1, x, y, x2, y2)) {
				interPointList.add(new MyPoint(x, y));
			}
		}
	}

	private static double[] bezierCoeffs(double P0, double P1, double P2, double P3) {
		return new double[] {
				-P0 + 3 * P1 - 3 * P2 + P3,
				3 * P0 - 6 * P1 + 3 * P2,
				-3 * P0 + 3 * P1,
				P0,
		};
	}

	private static MyPoint getIntersectionPoint(double x1, double y1, double x2, double y2,
										  double x3, double y3, double x4, double y4) {
		MyPoint p = null;

		double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
		// are not parallel
		if (d != 0.0) {
			// coords of intersection point with line
			double xi = ((x3 - x4) * (x1 * y2 - y1 * x2)
					- (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
			double yi = ((y3 - y4) * (x1 * y2 - y1 * x2)
					- (y1 - y2) * (x3 * y4 - y3 * x4)) / d;
			// needed to get only the intersection points with segment
			// and not with line
			if (onSegment(x1, y1, xi, yi, x2, y2)
					&& onSegment(x3, y3, xi, yi, x4, y4)) {
				p = new MyPoint(xi, yi);
			}
		}
		return p;
	}

	// check if intersection point is on segment
	private static boolean onSegment(double segStartX, double segStartY,
									 double interPointX, double interPointY, double segEndX,
									 double segEndY) {
		return onSegmentCoord(segStartX, interPointX, segEndX)
				&& onSegmentCoord(segStartY, interPointY, segEndY);
	}

	private static boolean onSegmentCoord(double segStart, double interPoint,
										  double segEnd) {
		return (interPoint <= Math.max(segStart, segEnd)
				&& interPoint >= Math.min(segStart, segEnd))
				|| (segStart == segEnd);
	}
}
