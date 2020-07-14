package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolver;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoLocusStroke;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.StringUtil;

/**
 * Class for polylines created using pen
 * 
 * @author Zbynek
 */
public class GeoLocusStroke extends GeoLocus
		implements MatrixTransformable, Translateable, Transformable, Mirrorable,
		PointRotateable, Dilateable {

	private static final double MIN_CURVE_ANGLE = Math.PI / 60; // 3degrees
	private static final int MAX_SEGMENT_LENGTH = 50;

	/** cache the part of XML that follows after expression label="stroke1" */
	private StringBuilder xmlPoints;

	private String splitParentLabel;

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
	public DescriptionMode getDescriptionMode() {
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
		resetXMLPointBuilder();
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
		resetXMLPointBuilder();
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
	 * Splits the stroke into two separate strokes
	 * @param rectangle the real rectangle to use for the boundary of the split
	 * @return a list of one or two elements, containing the inside
	 * 		and outside part of the stroke, if it exists
	 */
	public ArrayList<GeoElement> split(GRectangle2D rectangle) {
		ArrayList<MyPoint> inside = new ArrayList<>();
		ArrayList<MyPoint> outside = new ArrayList<>();

		for (int i = 0; i < getPoints().size() - 1; i++) {
			if (getPoints().get(i).getSegmentType() == SegmentType.CONTROL) {
				continue;
			}

			if (!getPoints().get(i).isDefined()) {
				ensureTrailingNaN(inside);
				ensureTrailingNaN(outside);

				continue;
			}

			boolean insideF = rectangle.contains(getPoints().get(i).x, getPoints().get(i).y);
			if (insideF) {
				inside.add(getPoints().get(i));
			} else {
				outside.add(getPoints().get(i));
			}

			for (MyPoint intersection : getAllIntersectionPoints(i, rectangle)) {
				inside.add(intersection);
				outside.add(new MyPoint(intersection.getX(), intersection.getY()));

				if (insideF) {
					ensureTrailingNaN(inside);
				} else {
					ensureTrailingNaN(outside);
				}

				insideF = !insideF;
			}
		}

		MyPoint last = getPoints().get(getPointLength() - 1);
		if (rectangle.contains(last.x, last.y)) {
			inside.add(last);
		} else {
			outside.add(last);
		}

		ArrayList<GeoElement> result = new ArrayList<>();
		if (inside.size() != 0) {
			result.add(partialStroke(inside));
		}
		if (outside.size() != 0) {
			result.add(partialStroke(outside));
		}

		return result;
	}

	private GeoElement partialStroke(ArrayList<MyPoint> inside) {
		AlgoLocusStroke insideStroke = new AlgoLocusStroke(cons, inside);
		insideStroke.getPenStroke().splitParentLabel = getLabelSimple();
		return insideStroke.getPenStroke();
	}

	@Override
	public List<GeoElement> getPartialSelection(boolean removeOriginal) {
		EuclidianView view = this.getKernel().getApplication().getActiveEuclidianView();

		List<GeoElement> splits = new ArrayList<>();
		DrawableND drawable = view.getDrawableFor(this);
		if (drawable == null) {
			return super.getPartialSelection(removeOriginal);
		}
		if (drawable.getPartialHitClip() == null) {
			splits.add(this);
		} else {
			GRectangle viewRectangle = drawable.getPartialHitClip();
			GRectangle2D realRectangle = AwtFactory.getPrototype().newRectangle2D();
			realRectangle.setRect(
					view.toRealWorldCoordX(viewRectangle.getX()),
					view.toRealWorldCoordY(viewRectangle.getY() + viewRectangle.getHeight()),
					viewRectangle.getWidth() * view.getInvXscale(),
					viewRectangle.getHeight() * view.getInvYscale()
			);

			splits = this.split(realRectangle);

			for (GeoElement split : splits) {
				split.setLabel(null);
				split.setVisualStyle(this);
				split.setEuclidianVisible(true);
				split.update();
			}

			if (removeOriginal) {
				this.remove();
			}
		}
		return splits;
	}

	/**
	 * Deletes part of the pen stroke
	 * @param rectangle the real reactangle, the inside part of which
	 * 		should be removed from the pen stroke
	 * @return true, if the pen stroke still has points left after the deletion
	 */
	public boolean deletePart(GRectangle2D rectangle) {
		ArrayList<MyPoint> outside = new ArrayList<>();
		for (int i = 0; i < getPoints().size(); i++) {
			MyPoint currentPoint = getPoints().get(i);
			if (!currentPoint.isDefined() || currentPoint.getSegmentType() == SegmentType.CONTROL) {
				continue;
			}
			boolean inside = rectangle.contains(currentPoint.x, currentPoint.y);
			if (!inside) {
				outside.add(currentPoint);
			}
			MyPoint nextPoint = getNextPoint(i);
			if (!nextPoint.isDefined()) {
				ensureTrailingNaN(outside);
				continue;
			}
			boolean nextInside = rectangle.contains(nextPoint.x, nextPoint.y);
			List<MyPoint> intersections = getAllIntersectionPoints(i, rectangle);
			if (inside && nextInside) {
				// both points inside
				if (intersections.size() == 2) {
					ensureTrailingNaN(outside);
					outside.addAll(intersections);
					ensureTrailingNaN(outside);
				}
			} else if (inside) {
				// going from inside to outside
				if (intersections.size() == 0) {
					outside.add(currentPoint);
				} else {
					ensureTrailingNaN(outside);
					outside.add(intersections.get(0));
				}
			} else if (nextInside) {
				// going from outside to inside
				if (intersections.size() == 0) {
					outside.add(nextPoint);
				} else {
					outside.add(intersections.get(0));
					ensureTrailingNaN(outside);
				}
			} else {
				// both points outside
				if (intersections.size() == 2) {
					outside.add(intersections.get(0));
					ensureTrailingNaN(outside);
					outside.add(intersections.get(1));
				}
			}
		}
		clearPoints();
		doAppendPointArray(outside);
		updateCascade();
		return !outside.isEmpty();
	}

	/**
	 * Check for bezier segments longer than MAX_SEGMENT_LENGTH and split them
	 * Returns the stroke points only, no control points.
	 */
	private ArrayList<MyPoint> increaseDensity() {
		ArrayList<MyPoint> densePoints = new ArrayList<>();
		int parts = 5;
		int i = 1;
		double rwLength = app.getActiveEuclidianView().getInvXscale() * MAX_SEGMENT_LENGTH;
		densePoints.add(getPoints().get(0));
		while (i < getPoints().size()) {
			MyPoint pt0 = getPoints().get(i - 1);
			MyPoint pt1 = getPoints().get(i);
			if (pt1.getSegmentType() == SegmentType.CONTROL) {
				MyPoint pt2 = getPoints().get(i + 1);
				MyPoint pt3 = getPoints().get(i + 2);
				if (pt3.distance(pt0) > rwLength) {
					double[] xCoeff = bezierCoeffs(pt0.x, pt1.x, pt2.x, pt3.x);
					double[] yCoeff = bezierCoeffs(pt0.y, pt1.y, pt2.y, pt3.y);
					for (int sub = 1; sub < parts; sub++) {
						double t = sub / (double) parts;
						MyPoint subPoint = new MyPoint(evalCubic(xCoeff, t), evalCubic(yCoeff, t));
						densePoints.add(subPoint);
					}
				}
				i += 2;
			} else {
				densePoints.add(pt1);
				i++;
			}
		}
		return densePoints;
	}

	private MyPoint getNextPoint(int i) {
		if (getPoints().get(i + 1).getSegmentType() == SegmentType.CONTROL) {
			return getPoints().get(i + 3);
		}
		return getPoints().get(i + 1);
	}

	private void ensureTrailingNaN(List<MyPoint> data) {
		if (data.size() > 0 && data.get(data.size() - 1).isDefined()) {
			data.add(new MyPoint(Double.NaN, Double.NaN));
		}
	}

	private ArrayList<MyPoint> getAllIntersectionPoints(final int index, GRectangle2D rectangle) {
		double x = rectangle.getX();
		double y = rectangle.getY();
		double width = rectangle.getWidth();
		double height = rectangle.getHeight();

		ArrayList<MyPoint> interPointList = new ArrayList<>();
		if (getPoints().get(index + 1).getSegmentType() == SegmentType.CONTROL) {
			MyPoint point1 = getPoints().get(index);
			MyPoint control1 = getPoints().get(index + 1);
			MyPoint control2 = getPoints().get(index + 2);
			MyPoint point2 = getPoints().get(index + 3);

			// Top line
			getIntersectionPoints(interPointList, point1, control1, control2, point2,
					x, y, x + width, y);

			// Bottom line
			getIntersectionPoints(interPointList, point1, control1, control2, point2,
					x, y + height, x + width, y + height);

			// Left side
			getIntersectionPoints(interPointList, point1, control1, control2, point2,
					x, y, x, y + height);

			// Right side
			getIntersectionPoints(interPointList, point1, control1, control2, point2,
					x + width, y, x + width, y + height);
		} else {
			MyPoint point1 = getPoints().get(index);
			MyPoint point2 = getPoints().get(index + 1);

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
					x, y + height, x + width, y + height);
			if (bottomInter != null) {
				interPointList.add(bottomInter);
			}
			// Left side
			MyPoint leftInter = getIntersectionPoint(x1, y1, x2, y2,
					x, y, x, y + height);
			if (leftInter != null) {
				interPointList.add(leftInter);
			}
			// Right side
			MyPoint rightInter = getIntersectionPoint(x1, y1, x2, y2,
					x + width, y, x + width, y + height);
			if (rightInter != null) {
				interPointList.add(rightInter);
			}
		}

		final MyPoint p = getPoints().get(index);
		Collections.sort(interPointList, new Comparator<MyPoint>() {
			@Override
			public int compare(MyPoint p1, MyPoint p2) {
				return Double.compare(p.distanceSq(p1), p.distanceSq(p2));
			}
		});
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
			if (t < Kernel.MAX_PRECISION || t > 1 - Kernel.MAX_PRECISION) {
				continue;
			}
			double x = evalCubic(bx, t);
			double y = evalCubic(by, t);

			if (onSegment(x1, y1, x, y, x2, y2)) {
				interPointList.add(new MyPoint(x, y));
			}
		}
	}

	private static double evalCubic(double[] bx, double t) {
		return bx[0] * t * t * t + bx[1] * t * t + bx[2] * t + bx[3];
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
				|| DoubleUtil.isEqual(segStart, segEnd);
	}

	// data has to have at least 2 defined points after each other
	private static boolean canBeBezierCurve(List<MyPoint> data) {
		boolean firstDefFound = false;
		for (MyPoint datum : data) {
			if (datum.isDefined()) {
				if (firstDefFound) {
					return true;
				}
				firstDefFound = true;
			} else {
				firstDefFound = false;
			}
		}
		return false;
	}

	/**
	 * Append the given points to the locus stroke
	 *
	 * @param data
	 *            points
	 */
	public void appendPointArray(ArrayList<MyPoint> data) {
		doAppendPointArray(data);
		ArrayList<MyPoint> densePoints = increaseDensity();
		if (densePoints.size() > data.size()) {
			clearPoints();
			doAppendPointArray(densePoints);
		}

		updateCascade();
	}

	private void doAppendPointArray(ArrayList<MyPoint> data) {
		resetXMLPointBuilder();
		setDefined(true);
		// to use bezier curve we need at least 2 points
		// stroke is: (A),(?),(A),(B) -> size 4
		if (canBeBezierCurve(data)) {
			addBezierCurve(data);
		} else {
			addNonBezierPoints(data);
		}
	}

	private void addBezierCurve(ArrayList<MyPoint> data) {
		int index = 0;
		while (index <= data.size()) {
			List<MyPoint> partOfStroke = getPartOfPenStroke(index, data);

			int strokeSize = partOfStroke.size();

			if (!partOfStroke.isEmpty()) {
				addPointMoveTo(partOfStroke.get(0));
				if (strokeSize < 3) {
					// if we found single point
					// just add it to the list without control points
					addPointLineTo(partOfStroke.get(partOfStroke.size() - 1));
				} else {
					addBezierCurveWithControlPoints(partOfStroke);
				}
			}

			if (index < data.size()) {
				ensureTrailingNaN(getPoints());
			}

			index = index + Math.max(partOfStroke.size(), 1);
		}
	}

	private void addBezierCurveWithControlPoints(List<MyPoint> partOfStroke) {
		ArrayList<double[]> controlPoints = getControlPoints(partOfStroke);
		for (int i = 1; i < partOfStroke.size(); i++) {
			MyPoint ctrl1 = new MyPoint(controlPoints.get(0)[i - 1],
					controlPoints.get(1)[i - 1],
					SegmentType.CONTROL);
			MyPoint ctrl2 = new MyPoint(controlPoints.get(2)[i - 1],
					controlPoints.get(3)[i - 1],
					SegmentType.CONTROL);

			MyPoint startPoint = partOfStroke.get(i - 1);
			MyPoint endPoint = partOfStroke.get(i);

			if (angle(startPoint, ctrl1, endPoint) > MIN_CURVE_ANGLE
					|| angle(startPoint, ctrl2, endPoint) > MIN_CURVE_ANGLE) {
				getPoints().add(ctrl1);
				getPoints().add(ctrl2);
				addPointCurveTo(endPoint);
			} else {
				addPointLineTo(endPoint);
			}
		}
	}

	private void addNonBezierPoints(ArrayList<MyPoint> data) {
		if (data.size() > 0) {
			addPointMoveTo(data.get(0));
			for (int i = 1; i < data.size(); i++) {
				addPointLineTo(data.get(i));
			}
			ensureTrailingNaN(getPoints());
		}
	}

	private void addPointMoveTo(MyPoint point) {
		getPoints().add(point.withType(SegmentType.MOVE_TO));
	}

	private void addPointLineTo(MyPoint point) {
		getPoints().add(point.withType(SegmentType.LINE_TO));
	}

	private void addPointCurveTo(MyPoint point) {
		getPoints().add(point.withType(SegmentType.CURVE_TO));
	}

	private static double angle(MyPoint a, MyPoint b, MyPoint c) {
		double dx1 = a.x - b.x;
		double dx2 = c.x - b.x;
		double dy1 = a.y - b.y;
		double dy2 = c.y - b.y;

		return Math.PI - MyMath.angle(dx1, dy1, dx2, dy2);
	}

	// returns the part of array started at index until first undef point
	private static List<MyPoint> getPartOfPenStroke(int index,
			List<MyPoint> data) {
		ArrayList<MyPoint> partOfStroke = new ArrayList<>(
				data.size() - index + 1);
		for (int i = index; i < data.size() && data.get(i).isDefined()
				&& (data.get(i).getSegmentType() != SegmentType.MOVE_TO
				|| i == index); i++) {
			partOfStroke.add(data.get(i));
		}
		return partOfStroke;
	}

	// calculate control points for bezier curve
	private static ArrayList<double[]> getControlPoints(List<MyPoint> data) {
		ArrayList<double[]> values = new ArrayList<>();

		if (data.size() == 0) {
			return values;
		}

		double[] a = new double[data.size() - 1];
		double[] b = new double[data.size() - 1];
		double[] c = new double[data.size() - 1];
		double[] rX = new double[data.size() - 1];
		double[] rY = new double[data.size() - 1];
		int n = data.size() - 1;
		/* left most segment */
		a[0] = 0;
		b[0] = 2;
		c[0] = 1;
		rX[0] = data.get(0).getX() + 2 * data.get(1).getX();
		rY[0] = data.get(0).getY() + 2 * data.get(1).getY();
		/* internal segments */
		for (int i = 1; i < n - 1; i++) {
			a[i] = 1;
			b[i] = 4;
			c[i] = 1;
			rX[i] = 4 * data.get(i).getX() + 2 * data.get(i + 1).getX();
			rY[i] = 4 * data.get(i).getY() + 2 * data.get(i + 1).getY();
		}
		/* right segment */
		a[n - 1] = 2;
		b[n - 1] = 7;
		c[n - 1] = 0;
		rX[n - 1] = 8 * data.get(n - 1).getX() + data.get(n).getX();
		rY[n - 1] = 8 * data.get(n - 1).getY() + data.get(n).getY();

		/* solves Ax=b with the Thomas algorithm (from Wikipedia) */
		for (int i = 1; i < n; i++) {
			double m = a[i] / b[i - 1];
			b[i] = b[i] - m * c[i - 1];
			rX[i] = rX[i] - m * rX[i - 1];
			rY[i] = rY[i] - m * rY[i - 1];
		}

		double[] xCoordsP1 = new double[data.size() - 1];
		double[] xCoordsP2 = new double[data.size() - 1];
		double[] yCoordsP1 = new double[data.size() - 1];
		double[] yCoordsP2 = new double[data.size() - 1];
		xCoordsP1[n - 1] = rX[n - 1] / b[n - 1];
		yCoordsP1[n - 1] = rY[n - 1] / b[n - 1];
		for (int i = n - 2; i >= 0; --i) {
			xCoordsP1[i] = (rX[i] - c[i] * xCoordsP1[i + 1]) / b[i];
			yCoordsP1[i] = (rY[i] - c[i] * yCoordsP1[i + 1]) / b[i];
		}

		/* we have p1, now compute p2 */
		for (int i = 0; i < n - 1; i++) {
			xCoordsP2[i] = 2 * data.get(i + 1).getX() - xCoordsP1[i + 1];
			yCoordsP2[i] = 2 * data.get(i + 1).getY() - yCoordsP1[i + 1];
		}
		xCoordsP2[n - 1] = 0.5 * (data.get(n).getX() + xCoordsP1[n - 1]);
		yCoordsP2[n - 1] = 0.5 * (data.get(n).getY() + yCoordsP1[n - 1]);

		values.add(xCoordsP1);
		values.add(yCoordsP1);
		values.add(xCoordsP2);
		values.add(yCoordsP2);
		return values;
	}

	public String getSplitParentLabel() {
		return splitParentLabel;
	}

	@Override
	public void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		if (!StringUtil.empty(splitParentLabel)) {
			sb.append("\t<parentLabel val=\"");
			sb.append(StringUtil.encodeXML(splitParentLabel));
			sb.append("\"/>");
		}
	}

	public void setSplitParentLabel(String string) {
		this.splitParentLabel = string;
	}
}
