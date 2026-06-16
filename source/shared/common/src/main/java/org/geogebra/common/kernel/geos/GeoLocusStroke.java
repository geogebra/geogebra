/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolver;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoLocusStroke;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.ScientificFormatAdapter;
import org.geogebra.common.util.Smoothing;
import org.geogebra.common.util.StringUtil;

/**
 * Class for polylines created using pen
 * @author Zbynek
 */
public class GeoLocusStroke extends GeoLocus
		implements MatrixTransformable, Translateable, Transformable, Mirrorable,
		Dilateable {
	private static final double MIN_CURVE_ANGLE = Math.PI / 60;
	private static final double MAX_SEGMENT_LENGTH = 50.0;
	/** cache for full Bezier coords (including control points) */
	private StringBuilder bezierXmlPoints;

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

	@Override
	public void set(GeoElementND geo) {
		super.set(geo);
		resetXMLPointBuilder();
	}

	/**
	 * Run a callback for points, skipping the control points.
	 * @param handler handler to be called for each point
	 */
	public void processPointsWithoutControl(
			AsyncOperation<MyPoint> handler) {
		MyPoint last = null;
		for (MyPoint pt : getPoints()) {
			if (pt.getSegmentType() != SegmentType.CONTROL) {
				// also ignore third point added to simple segment
				// to be able to calc control points
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
			pt.setLocation(a00 * x + a01 * y, a10 * x + a11 * y);
		}
		resetXMLPointBuilder();
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		for (MyPoint pt : getPoints()) {
			double x = pt.x;
			double y = pt.y;
			double z = a20 * x + a21 * y + a22;
			pt.setLocation((a00 * x + a01 * y + a02) / z,
					(a10 * x + a11 * y + a12) / z);
		}
		resetXMLPointBuilder();
	}

	@Override
	public void dilate(NumberValue r, Coords S) {
		double rval = r.getDouble();
		double crval = 1 - rval;

		for (MyPoint pt : getPoints()) {
			pt.setLocation(rval * pt.x + crval * S.getX(),
					rval * pt.y + crval * S.getY());
		}
		resetXMLPointBuilder();
	}

	@Override
	public void mirror(Coords Q) {
		for (MyPoint pt : getPoints()) {
			pt.setLocation(2 * Q.getX() - pt.x, 2 * Q.getY() - pt.y);
		}
		resetXMLPointBuilder();
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
			double x = pt.x - qx;
			double y = pt.y - qy;
			// mirror and translate Q
			pt.setLocation(x * cos + y * sin + qx,
					x * sin - y * cos + qy);
		}
		resetXMLPointBuilder();
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

			pt.setLocation((x - qx) * cos + (qy - y) * sin + qx,
					(x - qx) * sin + (y - qy) * cos + qy);
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

			pt.setLocation(x * cos - y * sin, x * sin + y * cos);
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
			pt.setLocation(pt.x + v.getX(), pt.y + v.getY());
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
		bezierXmlPoints = null;
	}

	@Override
	public String toOutputValueString(StringTemplate tpl) {
		return label;
	}

	/**
	 * Splits the stroke into two separate strokes
	 * @param rectangle the real rectangle to use for the boundary of the split
	 * @return a list of one or two elements, containing the inside
	 * and outside part of the stroke, if it exists
	 */
	public ArrayList<GeoElement> split(GRectangle2D rectangle) {
		ArrayList<MyPoint> inside = new ArrayList<>();
		ArrayList<MyPoint> outside = new ArrayList<>();
		ArrayList<MyPoint> pendingControls = new ArrayList<>();
		boolean skipControls = false;

		for (int i = 0; i < getPoints().size() - 1; i++) {
			MyPoint point = getPoints().get(i);

			if (point.getSegmentType() == SegmentType.CONTROL) {
				if (!skipControls) {
					pendingControls.add(point);
				}
				continue;
			}

			if (!point.isDefined()) {
				pendingControls.clear();
				skipControls = false;
				ensureTrailingNaN(inside);
				ensureTrailingNaN(outside);
				continue;
			}

			boolean insideF = rectangle.contains(point.x, point.y);
			if (insideF) {
				inside.addAll(pendingControls);
				inside.add(point);
			} else {
				outside.addAll(pendingControls);
				outside.add(point);
			}
			pendingControls.clear();

			ArrayList<BezierSplitPoint> intersections = getAllIntersectionPoints(i, rectangle);
			boolean isBezierSegment = i + 3 < getPoints().size()
					&& getPoints().get(i + 1).getSegmentType() == SegmentType.CONTROL
					&& getPoints().get(i + 2).getSegmentType() == SegmentType.CONTROL;
			skipControls = !intersections.isEmpty();
			if (isBezierSegment && intersections.size() == 1
						&& intersections.get(0).isBezier) {
				splitBezierAt(point,
						getPoints().get(i + 1), getPoints().get(i + 2), getPoints().get(i + 3),
						intersections.get(0).t,
						insideF ? inside : outside,
						insideF ? outside : inside,
						pendingControls);
			} else if (isBezierSegment && intersections.size() == 2
					&& intersections.get(0).isBezier && intersections.get(1).isBezier) {
				double t1 = Math.min(intersections.get(0).t, intersections.get(1).t);
				double t2 = Math.max(intersections.get(0).t, intersections.get(1).t);
				splitBezierAtTwo(point,
						getPoints().get(i + 1), getPoints().get(i + 2), getPoints().get(i + 3),
						t1, t2,
						insideF ? inside : outside,
						insideF ? outside : inside,
						pendingControls);
			} else {
				for (BezierSplitPoint bsp : intersections) {
					MyPoint intersection = bsp.point;
					inside.add(insideF ? intersection
							: intersection.withType(SegmentType.MOVE_TO));
					outside.add(new MyPoint(intersection.getX(), intersection.getY(),
							insideF ? SegmentType.MOVE_TO : SegmentType.LINE_TO));

					if (insideF) {
						ensureTrailingNaN(inside);
					} else {
						ensureTrailingNaN(outside);
					}

					insideF = !insideF;
				}
			}
		}

		MyPoint last = getPoints().get(getPointLength() - 1);
		if (rectangle.contains(last.x, last.y)) {
			inside.addAll(pendingControls);
			inside.add(last);
		} else {
			outside.addAll(pendingControls);
			outside.add(last);
		}

		ArrayList<GeoElement> result = new ArrayList<>();
		if (!inside.isEmpty()) {
			result.add(partialStroke(inside));
		}
		if (!outside.isEmpty()) {
			result.add(partialStroke(outside));
		}

		return result;
	}

	private GeoElement partialStroke(ArrayList<MyPoint> points) {
		AlgoLocusStroke algo = new AlgoLocusStroke(cons, new ArrayList<>());
		GeoLocusStroke stroke = algo.getPenStroke();
		stroke.setDefined(true);
		stroke.getPoints().addAll(points);
		stroke.splitParentLabel = getLabelSimple();
		return stroke;
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
				split.setVisualStyle(this);
				split.setEuclidianVisible(true);
				split.setLabel(null);
			}

			if (removeOriginal) {
				this.remove();
			}
		}
		return splits;
	}

	/**
	 * Deletes part of the pen stroke
	 * @param rectangle the real rectangle, the inside part of which
	 * should be removed from the pen stroke
	 * @return true, if the pen stroke still has points left after the deletion
	 */
	public boolean deletePart(GRectangle2D rectangle) {
		ArrayList<MyPoint> outside = new ArrayList<>();
		ArrayList<MyPoint> controls = new ArrayList<>();
		boolean skipControls = false;
		for (int i = 0; i < getPoints().size(); i++) {
			MyPoint currentPoint = getPoints().get(i);
			boolean defined = currentPoint.isDefined();
			if (!defined || currentPoint.getSegmentType() == SegmentType.CONTROL) {
				if (defined && !skipControls) {
					controls.add(currentPoint);
				}
				continue;
			}
			boolean inside = rectangle.contains(currentPoint.x, currentPoint.y);
			if (!inside) {
				outside.addAll(controls);
				boolean lineTo = currentPoint.getSegmentType() == SegmentType.CURVE_TO
						&& controls.isEmpty();
				outside.add(lineTo ? currentPoint.withType(SegmentType.LINE_TO) : currentPoint);
				controls.clear();
			}
			MyPoint nextPoint = getNextPoint(i);
			if (nextPoint == null || !nextPoint.isDefined()) {
				ensureTrailingNaN(outside);
				continue;
			}
			boolean nextInside = rectangle.contains(nextPoint.x, nextPoint.y);
			List<BezierSplitPoint> intersections = getAllIntersectionPoints(i, rectangle);
			skipControls = inside || nextInside || !intersections.isEmpty();
			if (inside && nextInside) {
				// both points inside
				if (intersections.size() == 2) {
					ensureTrailingNaN(outside);
					outside.add(intersections.get(0).point.withType(SegmentType.MOVE_TO));
					outside.add(intersections.get(1).point);
					ensureTrailingNaN(outside);
				}
			} else if (inside) {
				// going from inside to outside
				if (intersections.isEmpty()) {
					outside.add(currentPoint.withType(SegmentType.MOVE_TO));
				} else {
					ensureTrailingNaN(outside);
					outside.add(intersections.get(0).point.withType(SegmentType.MOVE_TO));
				}
			} else if (nextInside) {
				// going from outside to inside
				if (intersections.isEmpty()) {
					// next point right at the edge = no intersections
					if (nextPoint.getSegmentType() == SegmentType.CURVE_TO) {
						outside.add(getPoints().get(i + 1));
						outside.add(getPoints().get(i + 2));
					}
					outside.add(nextPoint);
				} else {
					outside.add(intersections.get(0).point);
					ensureTrailingNaN(outside);
				}
			} else {
				// both points outside
				if (intersections.size() == 2) {
					outside.add(intersections.get(0).point);
					ensureTrailingNaN(outside);
					outside.add(intersections.get(1).point.withType(SegmentType.MOVE_TO));
				}
			}
		}
		clearPoints();
		getPoints().addAll(outside);
		resetXMLPointBuilder();
		updateCascade();
		return !outside.isEmpty();
	}

	private MyPoint getNextPoint(int i) {
		if (i + 1 >= getPoints().size()) {
			return null;
		}
		if (getPoints().get(i + 1).getSegmentType() == SegmentType.CONTROL
				&& i + 3 < getPoints().size()) {
			return getPoints().get(i + 3);
		}
		return getPoints().get(i + 1);
	}

	void ensureTrailingNaN(List<MyPoint> data) {
		if (!data.isEmpty() && data.get(data.size() - 1).isDefined()) {
			data.add(new MyPoint(Double.NaN, Double.NaN));
		}
	}

	/**
	 * Splits a Bezier segment at {@code t}.
	 * The first sub-curve (anchor -> split) is appended to {@code first};
	 * the second sub-curve's start point is appended to {@code second} and its
	 * control points are stored in {@code pendingControls} for the next anchor.
	 */
	private void splitBezierAt(MyPoint anchor, MyPoint control1, MyPoint control2,
			MyPoint nextAnchor, double t, List<MyPoint> first, List<MyPoint> second,
			List<MyPoint> pendingControls) {
		double mid01x = anchor.x + (control1.x - anchor.x) * t;
		double mid01y = anchor.y + (control1.y - anchor.y) * t;
		double mid12x = control1.x + (control2.x - control1.x) * t;
		double mid12y = control1.y + (control2.y - control1.y) * t;
		double mid23x = control2.x + (nextAnchor.x - control2.x) * t;
		double mid23y = control2.y + (nextAnchor.y - control2.y) * t;

		double mid012x = mid01x + (mid12x - mid01x) * t;
		double mid012y = mid01y + (mid12y - mid01y) * t;
		double mid123x = mid12x + (mid23x - mid12x) * t;
		double mid123y = mid12y + (mid23y - mid12y) * t;

		double splitX = mid012x + (mid123x - mid012x) * t;
		double splitY = mid012y + (mid123y - mid012y) * t;

		first.add(new MyPoint(mid01x, mid01y, SegmentType.CONTROL));
		first.add(new MyPoint(mid012x, mid012y, SegmentType.CONTROL));
		first.add(new MyPoint(splitX, splitY, SegmentType.CURVE_TO));
		ensureTrailingNaN(first);

		second.add(new MyPoint(splitX, splitY, SegmentType.MOVE_TO));
		pendingControls.add(new MyPoint(mid123x, mid123y, SegmentType.CONTROL));
		pendingControls.add(new MyPoint(mid23x, mid23y, SegmentType.CONTROL));
	}

	/**
	 * Splits a cubic Bezier segment at two parameters {@code t1 < t2}.
	 * The outer sub-curves (anchor -> split1 and split2 -> nextAnchor) go to {@code same};
	 * the middle sub-curve (split1 -> split2) goes to {@code other}.
	 */
	private void splitBezierAtTwo(MyPoint anchor, MyPoint control1, MyPoint control2,
			MyPoint nextAnchor, double t1, double t2,
			List<MyPoint> same, List<MyPoint> other, List<MyPoint> pendingControls) {
		double mid01x = anchor.x + (control1.x - anchor.x) * t1;
		double mid01y = anchor.y + (control1.y - anchor.y) * t1;
		double mid12x = control1.x + (control2.x - control1.x) * t1;
		double mid12y = control1.y + (control2.y - control1.y) * t1;
		double mid23x = control2.x + (nextAnchor.x - control2.x) * t1;
		double mid23y = control2.y + (nextAnchor.y - control2.y) * t1;
		double mid012x = mid01x + (mid12x - mid01x) * t1;
		double mid012y = mid01y + (mid12y - mid01y) * t1;
		double mid123x = mid12x + (mid23x - mid12x) * t1;
		double mid123y = mid12y + (mid23y - mid12y) * t1;
		double split1X = mid012x + (mid123x - mid012x) * t1;
		double split1Y = mid012y + (mid123y - mid012y) * t1;

		same.add(new MyPoint(mid01x, mid01y, SegmentType.CONTROL));
		same.add(new MyPoint(mid012x, mid012y, SegmentType.CONTROL));
		same.add(new MyPoint(split1X, split1Y, SegmentType.CURVE_TO));
		ensureTrailingNaN(same);

		double t2prime = (t2 - t1) / (1 - t1);
		double sub01x = split1X + (mid123x - split1X) * t2prime;
		double sub01y = split1Y + (mid123y - split1Y) * t2prime;
		double sub12x = mid123x + (mid23x - mid123x) * t2prime;
		double sub12y = mid123y + (mid23y - mid123y) * t2prime;
		double sub23x = mid23x + (nextAnchor.x - mid23x) * t2prime;
		double sub23y = mid23y + (nextAnchor.y - mid23y) * t2prime;
		double sub012x = sub01x + (sub12x - sub01x) * t2prime;
		double sub012y = sub01y + (sub12y - sub01y) * t2prime;
		double sub123x = sub12x + (sub23x - sub12x) * t2prime;
		double sub123y = sub12y + (sub23y - sub12y) * t2prime;
		double split2X = sub012x + (sub123x - sub012x) * t2prime;
		double split2Y = sub012y + (sub123y - sub012y) * t2prime;

		other.add(new MyPoint(split1X, split1Y, SegmentType.MOVE_TO));
		other.add(new MyPoint(sub01x, sub01y, SegmentType.CONTROL));
		other.add(new MyPoint(sub012x, sub012y, SegmentType.CONTROL));
		other.add(new MyPoint(split2X, split2Y, SegmentType.CURVE_TO));
		ensureTrailingNaN(other);

		same.add(new MyPoint(split2X, split2Y, SegmentType.MOVE_TO));
		pendingControls.add(new MyPoint(sub123x, sub123y, SegmentType.CONTROL));
		pendingControls.add(new MyPoint(sub23x, sub23y, SegmentType.CONTROL));
	}

	private ArrayList<BezierSplitPoint> getAllIntersectionPoints(final int index,
			GRectangle2D rectangle) {
		double x = rectangle.getX();
		double y = rectangle.getY();
		double width = rectangle.getWidth();
		double height = rectangle.getHeight();

		ArrayList<BezierSplitPoint> interPointList = new ArrayList<>();
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
				interPointList.add(new BezierSplitPoint(topInter));
			}
			// Bottom line
			MyPoint bottomInter = getIntersectionPoint(x1, y1, x2, y2,
					x, y + height, x + width, y + height);
			if (bottomInter != null) {
				interPointList.add(new BezierSplitPoint(bottomInter));
			}
			// Left side
			MyPoint leftInter = getIntersectionPoint(x1, y1, x2, y2,
					x, y, x, y + height);
			if (leftInter != null) {
				interPointList.add(new BezierSplitPoint(leftInter));
			}
			// Right side
			MyPoint rightInter = getIntersectionPoint(x1, y1, x2, y2,
					x + width, y, x + width, y + height);
			if (rightInter != null) {
				interPointList.add(new BezierSplitPoint(rightInter));
			}
		}

		final MyPoint p = getPoints().get(index);
		interPointList.sort(Comparator.comparingDouble(bp -> p.distanceSq(bp.point)));
		return interPointList;
	}

	private static void getIntersectionPoints(ArrayList<BezierSplitPoint> interPointList,
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
				interPointList.add(new BezierSplitPoint(new MyPoint(x, y), t));
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
		if (!DoubleUtil.isZero(d)) {
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
			double interPointX, double interPointY, double segEndX, double segEndY) {
		return onSegmentCoord(segStartX, interPointX, segEndX)
				&& onSegmentCoord(segStartY, interPointY, segEndY);
	}

	private static boolean onSegmentCoord(double segStart, double interPoint, double segEnd) {
		return (interPoint <= Math.max(segStart, segEnd)
				&& interPoint >= Math.min(segStart, segEnd))
				|| DoubleUtil.isEqual(segStart, segEnd);
	}

	/**
	 * @param data stroke vertices
	 */
	public void appendVertexPointArray(List<MyPoint> data) {
		doAppendPointArray(data);
		ArrayList<MyPoint> densePoints = increaseDensity();
		if (densePoints.size() > data.size()) {
			clearPoints();
			doAppendPointArray(densePoints);
		}
	}

	private ArrayList<MyPoint> increaseDensity() {
		ArrayList<MyPoint> densePoints = new ArrayList<>();
		int parts = 5;
		int i = 1;
		double rwLength = app.getActiveEuclidianView().getInvXscale() * MAX_SEGMENT_LENGTH;
		ArrayList<MyPoint> points = getPoints();

		if (!points.isEmpty()) {
			densePoints.add(points.get(0));
		}
		while (i < points.size()) {
			MyPoint pt0 = points.get(i - 1);
			MyPoint pt1 = points.get(i);
			if (pt1.getSegmentType() == SegmentType.CONTROL) {
				MyPoint pt2 = points.get(i + 1);
				MyPoint pt3 = points.get(i + 2);
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

	private void doAppendPointArray(List<MyPoint> data) {
		resetXMLPointBuilder();
		setDefined(true);

		int index = 0;
		while (index <= data.size()) {
			int strokeSize = getPartOfPenStroke(index, data);

			if (strokeSize != 0) {
				getPoints().add(data.get(index).withType(SegmentType.MOVE_TO));
				if (strokeSize < 3) {
					// stroke is either single point (then draw line to self)
					// or a segment (also just draw a line, no smoothing)
					addPointLineTo(data.get(index + strokeSize - 1));
				} else {
					addBezierCurveWithControlPoints(data, index, strokeSize);
				}
			}

			if (index < data.size()) {
				ensureTrailingNaN(getPoints());
			}

			index = index + Math.max(strokeSize, 1);
		}
	}

	private void addBezierCurveWithControlPoints(List<MyPoint> stroke, int start, int length) {
		List<MyPoint> strokeMaybeAveraged;
		if (length > 9) {
			strokeMaybeAveraged = averageClosePoints(stroke, start, length);
		} else {
			strokeMaybeAveraged = stroke.subList(start, start + length);
		}
		ArrayList<double[]> controlPoints =
				getControlPoints(strokeMaybeAveraged, strokeMaybeAveraged.size());
		for (int i = 1; i < strokeMaybeAveraged.size(); i++) {
			MyPoint ctrl1 = new MyPoint(controlPoints.get(0)[i - 1],
					controlPoints.get(1)[i - 1],
					SegmentType.CONTROL);
			MyPoint ctrl2 = new MyPoint(controlPoints.get(2)[i - 1],
					controlPoints.get(3)[i - 1],
					SegmentType.CONTROL);

			MyPoint startPoint = strokeMaybeAveraged.get(i - 1);
			MyPoint endPoint = strokeMaybeAveraged.get(i);

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

	// calculate control points for bezier curve
	private static ArrayList<double[]> getControlPoints(List<MyPoint> stroke,
			int length) {
		ArrayList<double[]> values = new ArrayList<>();

		if (length == 0) {
			return values;
		}

		int n = length - 1;
		double[] a = new double[n];
		double[] b = new double[n];
		double[] c = new double[n];
		double[] rX = new double[n];
		double[] rY = new double[n];
		/* left most segment */
		a[0] = 0;
		b[0] = 2;
		c[0] = 1;
		rX[0] = stroke.get(0).getX() + 2 * stroke.get(0 + 1).getX();
		rY[0] = stroke.get(0).getY() + 2 * stroke.get(0 + 1).getY();
		/* internal segments */
		for (int i = 1; i < n - 1; i++) {
			a[i] = 1;
			b[i] = 4;
			c[i] = 1;
			rX[i] = 4 * stroke.get(0 + i).getX() + 2 * stroke.get(0 + i + 1).getX();
			rY[i] = 4 * stroke.get(0 + i).getY() + 2 * stroke.get(0 + i + 1).getY();
		}
		/* right segment */
		a[n - 1] = 2;
		b[n - 1] = 7;
		c[n - 1] = 0;
		rX[n - 1] = 8 * stroke.get(0 + n - 1).getX() + stroke.get(0 + n).getX();
		rY[n - 1] = 8 * stroke.get(0 + n - 1).getY() + stroke.get(0 + n).getY();

		/* solves Ax=b with the Thomas algorithm (from Wikipedia) */
		for (int i = 1; i < n; i++) {
			double m = a[i] / b[i - 1];
			b[i] = b[i] - m * c[i - 1];
			rX[i] = rX[i] - m * rX[i - 1];
			rY[i] = rY[i] - m * rY[i - 1];
		}

		double[] xCoordsP1 = new double[n];
		double[] xCoordsP2 = new double[n];
		double[] yCoordsP1 = new double[n];
		double[] yCoordsP2 = new double[n];
		xCoordsP1[n - 1] = rX[n - 1] / b[n - 1];
		yCoordsP1[n - 1] = rY[n - 1] / b[n - 1];
		for (int i = n - 2; i >= 0; --i) {
			xCoordsP1[i] = (rX[i] - c[i] * xCoordsP1[i + 1]) / b[i];
			yCoordsP1[i] = (rY[i] - c[i] * yCoordsP1[i + 1]) / b[i];
		}

		/* we have p1, now compute p2 */
		for (int i = 0; i < n - 1; i++) {
			xCoordsP2[i] = 2 * stroke.get(0 + i + 1).getX() - xCoordsP1[i + 1];
			yCoordsP2[i] = 2 * stroke.get(0 + i + 1).getY() - yCoordsP1[i + 1];
		}
		xCoordsP2[n - 1] = 0.5 * (stroke.get(0 + n).getX() + xCoordsP1[n - 1]);
		yCoordsP2[n - 1] = 0.5 * (stroke.get(0 + n).getY() + yCoordsP1[n - 1]);

		values.add(xCoordsP1);
		values.add(yCoordsP1);
		values.add(xCoordsP2);
		values.add(yCoordsP2);
		return values;
	}

	private static double angle(MyPoint a, MyPoint b, MyPoint c) {
		double dx1 = a.x - b.x;
		double dx2 = c.x - b.x;
		double dy1 = a.y - b.y;
		double dy2 = c.y - b.y;

		return Math.PI - MyMath.angle(dx1, dy1, dx2, dy2);
	}

	private void addPointLineTo(MyPoint point) {
		getPoints().add(point.withType(SegmentType.LINE_TO));
	}

	private void addPointCurveTo(MyPoint point) {
		getPoints().add(point.withType(SegmentType.CURVE_TO));
	}

	private List<MyPoint> averageClosePoints(List<MyPoint> stroke, int start, int length) {
		ArrayList<MyPoint> averagedPoints = new ArrayList<>();
		//do not use first point for averaging
		averagedPoints.add(stroke.get(start));
		int end = start + length;
		for (int i = start + 1; i < end; i++) {
			if (i < end - 2) {
				// check if next point is close enough for averaging
				if (areClosePoints(stroke.get(i), stroke.get(i + 1))) {
					averagedPoints.add(calculateAveragePoint(stroke.get(i), stroke.get(i + 1)));
					i++;
				} else {
					averagedPoints.add(stroke.get(i));
				}
			} else {
				averagedPoints.add(stroke.get(i));
			}
		}
		return averagedPoints;
	}

	private MyPoint calculateAveragePoint(MyPoint a, MyPoint b) {
		double newX = (a.getX() + b.getX()) / 2;
		double newY = (a.getY() + b.getY()) / 2;

		return new MyPoint(newX, newY, SegmentType.LINE_TO);
	}

	private boolean areClosePoints(MyPoint a, MyPoint b) {
		EuclidianView view = app.getActiveEuclidianView();
		double screenCoordXA = view.toScreenCoordXd(a.getX());
		double screenCoordYA = view.toScreenCoordYd(a.getY());
		double screenCoordXB = view.toScreenCoordXd(b.getX());
		double screenCoordYB = view.toScreenCoordYd(b.getY());

		return Math.abs(screenCoordXA - screenCoordXB) < 4
				&& Math.abs(screenCoordYA - screenCoordYB) < 4;
	}

	/**
	 * Append the given points to the locus stroke
	 * @param data points
	 */
	public void appendPointArray(List<? extends GPoint2D> data, EuclidianView view) {
		resetXMLPointBuilder();
		setDefined(true);

		int index = 0;
		while (index <= data.size()) {
			int strokeSize = getPartOfPenStroke(index, data);
			List<? extends GPoint2D> toTransform = data.subList(index, index + strokeSize);
			if (!toTransform.isEmpty()) {
				List<? extends GPoint2D> transformed = Smoothing.transform(toTransform);
				if (view != null) {
					transformed = scalePoints(transformed, view);
				}
				if (transformed.size() < 3) {
					if (!transformed.isEmpty()) {
						addSegment(transformed);
					} else {
						transformed = view == null ? toTransform : scalePoints(toTransform, view);
						addSegment(transformed);
					}
					index = index + Math.max(strokeSize, 1);
					continue;
				}
				processContinuous(transformed, view == null ? 1 : view.getInvXscale(),
						getPoints()::add);
			}

			if (index < data.size()) {
				ensureTrailingNaN(getPoints());
			}

			index = index + Math.max(strokeSize, 1);
		}
	}

	private List<? extends GPoint2D> scalePoints(List<? extends GPoint2D> penPoints,
			EuclidianView view) {
		ArrayList<MyPoint> newPts = new ArrayList<>(penPoints.size());
		for (GPoint2D p : penPoints) {
			double x = view.toRealWorldCoordX(p.getX());
			double y = view.toRealWorldCoordY(p.getY());

			// change -2.4600000000000004 to -2.46 for smaller XML
			newPts.add(new MyPoint(DoubleUtil.checkDecimalFraction(x),
					DoubleUtil.checkDecimalFraction(y)));
		}
		return newPts;
	}

	/**
	 * Convert smoothing result into a curve.
	 * @param transformed smoothed points
	 * @param curve curve plotter
	 */
	public static void processContinuous(List<? extends GPoint2D> transformed, double scale,
			Consumer<MyPoint> curve) {
		MyPoint control = new MyPoint(transformed.get(1).x, transformed.get(1).y,
				SegmentType.CONTROL);
		MyPoint last = new MyPoint((transformed.get(1).x + transformed.get(2).x) / 2,
				(transformed.get(1).y + transformed.get(2).y) / 2, SegmentType.CURVE_TO);
		MyPoint start = new MyPoint(transformed.get(0).x, transformed.get(0).y,
				SegmentType.MOVE_TO);
		curve.accept(start);
		curve.accept(control.barycenter(1 / 3.0, start, SegmentType.CONTROL));
		curve.accept(control.barycenter(1 / 3.0, last, SegmentType.CONTROL));
		curve.accept(last);

		for (int i = 2, max = transformed.size() - 1; i < max; i++) {
			GPoint2D a = transformed.get(i);
			GPoint2D b = transformed.get(i + 1);
			control.setLocation(2 * last.x - control.x, 2 * last.y - control.y);
			MyPoint next = new MyPoint(.5 * (a.x + b.x), .5 * (a.y + b.y), SegmentType.CURVE_TO);
			if (areCollinear(last, control, next, scale)) {
				curve.accept(next.withType(SegmentType.LINE_TO));
				last = next;
				continue;
			}
			curve.accept(control.barycenter(1 / 3.0, last, SegmentType.CONTROL));
			last = next;
			curve.accept(control.barycenter(1 / 3.0, last, SegmentType.CONTROL));
			curve.accept(last);
		}
		GPoint2D strokeEnd = transformed.get(transformed.size() - 1);
		curve.accept(new MyPoint(strokeEnd.x, strokeEnd.y));
	}

	// collinear points + bezier buggy in Safari (?)
	private static boolean areCollinear(MyPoint a, MyPoint b, MyPoint c, double scale) {
		double dx1 = a.x - b.x;
		double dx2 = a.x - c.x;
		double dy1 = a.y - b.y;
		double dy2 = a.y - c.y;
		return DoubleUtil.isZero(dx1 * dy2 - dx2 * dy1, scale * scale);
	}

	private void addSegment(List<? extends GPoint2D> transformed) {
		getPoints().add(new MyPoint(transformed.get(0).x, transformed.get(0).y,
				SegmentType.MOVE_TO));
		int last = transformed.size() - 1;
		getPoints().add(
				new MyPoint(transformed.get(last).x, transformed.get(last).y));
	}

	// returns the length of array started at index until first undef point
	private static int getPartOfPenStroke(int index,
			List<? extends GPoint2D> data) {
		int i = index;
		while (i < data.size() && Double.isFinite(data.get(i).getX())
				&& (getSegmentTypeFor(data.get(i)) != SegmentType.MOVE_TO
				|| i == index)) {
			i++;
		}
		return i - index;
	}

	private static SegmentType getSegmentTypeFor(GPoint2D gPoint2D) {
		return gPoint2D instanceof MyPoint pt ? pt.getSegmentType() : SegmentType.LINE_TO;
	}

	public String getSplitParentLabel() {
		return splitParentLabel;
	}

	@Override
	public void getStyleXML(XMLStringBuilder sb) {
		super.getStyleXML(sb);
		if (!StringUtil.empty(splitParentLabel)) {
			sb.startTag("parentLabel").attr("val", splitParentLabel).endTag();
		}
	}

	public void setSplitParentLabel(String string) {
		this.splitParentLabel = string;
	}

	/**
	 * Update coordinates of all points
	 * @param coords flat array of coordinates x1, y1, x2, y2, ...
	 */
	public void setCoords(double[] coords) {
		myPointList.clear();
		resetXMLPointBuilder();
		setDefined(true);

		ArrayList<MyPoint> points = new ArrayList<>();
		for (int i = 0; i < coords.length - 1; i += 2) {
			double x = coords[i];
			double y = coords[i + 1];
			if (!Double.isFinite(x)) {
				if (!points.isEmpty()) {
					processContinuousAdd(points);
					points.clear();
				}
			} else {
				points.add(new MyPoint(x, y));
			}
		}

		if (!points.isEmpty()) {
			processContinuousAdd(points);
		}
		updateRepaint();
	}

	private void processContinuousAdd(ArrayList<MyPoint> points) {
		if (points.size() < 3) {
			addSegment(points);
		} else {
			processContinuous(points, 1, getPoints()::add);
		}
		ensureTrailingNaN(getPoints());
	}

	@Override
	public void getXMLTags(XMLStringBuilder builder) {
		if (bezierXmlPoints == null) {
			bezierXmlPoints = new StringBuilder();
			appendBezierCoords(bezierXmlPoints);
		}
		builder.startTag("strokeBezierCoords").attrRaw("val", bezierXmlPoints).endTag();
		super.getXMLTags(builder);
	}

	/**
	 * @return smoothed points as a string
	 */
	public String getPointString() {
		if (bezierXmlPoints == null) {
			bezierXmlPoints = new StringBuilder();
			appendBezierCoords(bezierXmlPoints);
		}
		return bezierXmlPoints.toString();
	}

	/**
	 * Append exact stroke coordinates for XML, including Bezier control points.
	 * @param sb StringBuilder
	 */
	private void appendBezierCoords(final StringBuilder sb) {
		final ScientificFormatAdapter formatter = FormatFactory.getPrototype()
				.getFastScientificFormat(5);
		for (int i = 0; i < myPointList.size(); i++) {
			MyPoint pt = myPointList.get(i);
			if (i > 0) {
				sb.append(",");
			}
			if (!pt.isDefined()) {
				sb.append("NaN,NaN,0");
			} else {
				sb.append(formatter.format(pt.x));
				sb.append(",");
				sb.append(formatter.format(pt.y));
				sb.append(",");
				sb.append(pt.getSegmentType().code);
			}
		}
	}

	/**
	 * Restore exact stroke coordinates from {@code x}, {@code y}, {@code code}
	 * triplets as produced by {@link #appendBezierCoords}.
	 * @param triplets Array of triplets
	 */
	public void setBezierCoords(double[] triplets) {
		myPointList.clear();
		resetXMLPointBuilder();
		setDefined(true);
		for (int i = 0; i + 2 < triplets.length; i += 3) {
			double x = triplets[i];
			double y = triplets[i + 1];
			if (Double.isFinite(x)) {
				SegmentType segmentType = SegmentType.fromCode((int) triplets[i + 2]);
				if (segmentType == SegmentType.MOVE_TO && !myPointList.isEmpty()) {
					ensureTrailingNaN(myPointList);
				}
				myPointList.add(new MyPoint(x, y, segmentType));
			} else {
				myPointList.add(new MyPoint(Double.NaN, Double.NaN));
			}
		}
		ensureTrailingNaN(getPoints());
		updateRepaint();
	}

	/**
	 * Intersection point on a stroke segment. {@code isBezier} indicates whether
	 * the segment is cubic; if so, {@code t} is the curve parameter at the crossing.
	 */
	private static final class BezierSplitPoint {
		final MyPoint point;
		final double t;
		final boolean isBezier;

		BezierSplitPoint(MyPoint point, double t) {
			this.point = point;
			this.t = t;
			this.isBezier = true;
		}

		BezierSplitPoint(MyPoint point) {
			this.point = point;
			this.t = 0;
			this.isBezier = false;
		}
	}
}
