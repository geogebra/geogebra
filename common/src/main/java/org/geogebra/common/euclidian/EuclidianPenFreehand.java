package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoCircleThreePoints;
import org.geogebra.common.kernel.algos.AlgoFocus;
import org.geogebra.common.kernel.algos.AlgoFunctionFreehand;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.PolygonFactory;
import org.geogebra.common.kernel.geos.TestGeo;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.statistics.AlgoFitImplicit;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Freehand processor
 */
public class EuclidianPenFreehand extends EuclidianPen {

	/**
	 * type that is expected to be created
	 */
	public enum ShapeType {
		/** circle */
		circleThreePoints,
		/** normal polygon */
		polygon,
		/** polygon with two moveable points */
		rigidPolygon,
		/** polygon that can be moved by first point */
		vectorPolygon;
	}

	private ShapeType expected = null;
	private static final double CONIC_AXIS_ERROR_RATIO = 10;
	private RecoSegment reco_queue_a = new RecoSegment();
	private RecoSegment reco_queue_b = new RecoSegment();
	private RecoSegment reco_queue_c = new RecoSegment();
	private RecoSegment reco_queue_d = new RecoSegment();
	private RecoSegment reco_queue_e = new RecoSegment();
	private Inertia a = null;
	private Inertia b = null;
	private Inertia c = null;
	private Inertia d = null;
	private int[] brk;
	private int recognizer_queue_length = 0;
	private double score = 0;
	private static final int MAX_POLYGON_SIDES = 4;
	private static final double SLANT_TOLERANCE = 5 * Math.PI / 180;
	private int minX = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;

	private static class Inertia {
		double mass = 0;
		double sx = 0;
		double sxx = 0;
		double sy = 0;
		double sxy = 0;
		double syy = 0;

		protected Inertia() {
			// no public constructor
		}

		protected void copyValuesFrom(Inertia inertia) {
			mass = inertia.mass;
			sx = inertia.sx;
			sxx = inertia.sxx;
			sy = inertia.sy;
			sxy = inertia.sxy;
			syy = inertia.syy;
		}
	}

	/**
	 * @param app
	 *            app
	 * @param view
	 *            euclidian view
	 */
	public EuclidianPenFreehand(App app, EuclidianView view) {
		super(app, view);
	}

	/**
	 * @param expectedType
	 *            defines the expected shape
	 */
	public void setExpected(ShapeType expectedType) {
		this.expected = expectedType;

		resetParameters();
		switch (expected) {
		case circleThreePoints:
			CIRCLE_MAX_SCORE = 0.15;
			CIRCLE_MIN_DET = 0.9;
			break;
		case polygon:
		case rigidPolygon:
		case vectorPolygon:
			RECTANGLE_LINEAR_TOLERANCE = 0.25;
			POLYGON_LINEAR_TOLERANCE = 0.25;
			RECTANGLE_ANGLE_TOLERANCE = 17 * Math.PI / 180;
			break;
		}
	}

	@Override
	public boolean handleMouseReleasedForPenMode(boolean right, int x, int y, boolean isPinchZooming) {
		if (right && !isFreehand()) {
			return false;
		}

		if (expected == null) {
			boolean shapeCreated = mouseReleasedFreehand(x, y);
			penPoints.clear();

			app.refreshViews(); // clear trace

			minX = Integer.MAX_VALUE;
			maxX = Integer.MIN_VALUE;

			return shapeCreated;
		}
		return checkExpectedShape(x, y);
	}

	@Override
	protected void addPointPenMode(GPoint newPoint) {
		if (minX > newPoint.getX()) {
			minX = newPoint.getX();
		}
		if (maxX < newPoint.getX()) {
			maxX = newPoint.getX();
		}
		super.addPointPenMode(newPoint);
	}

	/** @return true if a shape was created, false otherwise */
	private boolean mouseReleasedFreehand(int x, int y) {
		int n = maxX - minX + 1;

		if (n < 0) {
			return false;
		}
		double[] freehand1 = new double[n];

		GeoElement shape = checkShapes(x, y);

		if (shape != null && shape.isGeoLine()) {
			// lines take priority over functions
			penPoints.clear();
			return true;
		}

		// now check if it can be a function (increasing or decreasing x)

		double monotonicTest = 0;

		for (int i = 0; i < penPoints.size() - 1; i++) {
			GPoint p1 = penPoints.get(i);
			GPoint p2 = penPoints.get(i + 1);
			if (Math.signum(p2.x - p1.x) != 1) {
				monotonicTest++;
			}
		}

		Log.debug("mono" + monotonicTest + " "
				+ monotonicTest / penPoints.size());

		monotonicTest = monotonicTest / penPoints.size();

		// allow 10% error
		boolean monotonic = monotonicTest > 0.9 || monotonicTest < 0.1;

		if (!monotonic) {
			// may or may not have recognized a shape eg circle in checkShapes()
			// earlier
			penPoints.clear();
			return shape != null;
		}

		// now definitely a function

		if (shape != null) {
			for (GeoElement geo : shape.getParentAlgorithm().getInput()) {
				geo.remove();
			}
			shape.remove();
		}

		for (int i = 0; i < n; i++) {
			freehand1[i] = Double.NaN;
		}

		for (int i = 0; i < penPoints.size(); i++) {
			GPoint p = penPoints.get(i);
			int index = p.x - minX;
			if (index >= 0 && index < freehand1.length
					&& Double.isNaN(freehand1[index])) {
				freehand1[index] = view.toRealWorldCoordY(p.y);
			}
		}

		// fill in any gaps (eg from fast mouse movement)
		double val = freehand1[0];
		int valIndex = 0;
		double nextVal = Double.NaN;
		int nextValIndex = -1;
		for (int i = 0; i < n; i++) {
			if (Double.isNaN(freehand1[i])) {
				if (i > nextValIndex) {
					nextValIndex = i;
					while (nextValIndex < n
							&& Double.isNaN(freehand1[nextValIndex])) {
						nextValIndex++;
					}
				}
				if (nextValIndex >= n) {
					freehand1[i] = val;
				} else {
					nextVal = freehand1[nextValIndex];
					freehand1[i] = (val * (nextValIndex - i)
							+ nextVal * (i - valIndex))
							/ (nextValIndex - valIndex);
				}
			} else {
				val = freehand1[i];
				valIndex = i;
			}
		}

		Construction cons = app.getKernel().getConstruction();

		GeoList list = new GeoList(cons);
		// checkDecimalFraction() -> shorter XML
		list.add(new GeoNumeric(cons,
				DoubleUtil.checkDecimalFraction(view.toRealWorldCoordX(minX))));
		list.add(new GeoNumeric(cons,
				DoubleUtil.checkDecimalFraction(view.toRealWorldCoordX(maxX))));
		for (int i = 0; i < n; i++) {
			list.add(new GeoNumeric(cons,
					DoubleUtil.checkDecimalFraction(freehand1[i])));
		}

		// create the freehand function
		AlgoFunctionFreehand algo = new AlgoFunctionFreehand(cons, null, list);

		GeoElement fun = algo.getOutput(0);

		// fun.setLineThickness(penSize * PEN_SIZE_FACTOR);
		fun.setLineType(getPenLineStyle());
		fun.setObjColor(getPenColor());

		minX = Integer.MAX_VALUE;
		maxX = Integer.MIN_VALUE;

		return true;
	}

	/**
	 * @param x
	 *            x-coord of new point
	 * @param y
	 *            y-coord of new point
	 * @return geo that fits current points + new point
	 */
	protected GeoElement checkShapes(int x, int y) {
		initShapeRecognition(x, y);
		// AbstractApplication.debug(penPoints);
		// if recognize_shape option is checked

		GeoElement geo;
		if ((geo = tryPolygonOrLine()) != null || (geo = tryCircle()) != null) {
			if (geo.isGeoConic()) {
				geo.setIsShape(app.isWhiteboardActive());
			}
			return geo;
		}

		resetInitialPoint();
		return makeAConic(); // might return null
	}

	/**
	 * Creates predicted shape if possible
	 * 
	 * @param x
	 *            x-coord of new point
	 * @param y
	 *            y-coord of new point
	 */
	private boolean checkExpectedShape(int x, int y) {
		initShapeRecognition(x, y);

		switch (this.expected) {
		case polygon:
		case rigidPolygon:
		case vectorPolygon:
			return createPolygon();
		case circleThreePoints:
			return createCircle();
		}

		return false;
	}

	/**
	 * creates a circle if possible
	 */
	private boolean createCircle() {
		ArrayList<GeoElement> geoCirclePlusPoint;
		if (tryCircleThroughExistingPoints() != null) {
			return true;
		} else if ((geoCirclePlusPoint = getCircleThreePoints()) != null) {

			boolean recreate = false;

			GeoElement geoCircle = geoCirclePlusPoint.get(0);

			ArrayList<GeoPointND> points = ((GeoConicND) geoCirclePlusPoint
					.get(0)).getPointsOnConic();

			if (points == null || points.size() < 3) {
				resetInitialPoint();
				return false;
			}

			ArrayList<GeoPointND> list = new ArrayList<>();
			for (GeoPointND geo : points) {
				if (!geo.isLabelSet()) {
					recreate = true;
					geo.setLabel(null);
				}
				list.add(geo);
			}

			// the circle needs to be recreated to prevent errors in the XML
			if (recreate) {
				geoCircle.remove();
				AlgoCircleThreePoints algo = new AlgoCircleThreePoints(
						app.getKernel().getConstruction(), list.get(0),
						list.get(1), list.get(2));
				geoCircle = algo.getCircle();
				geoCircle.setLabel(null);
				geoCircle.updateRepaint();
			}

			return true;
		}
		resetInitialPoint();
		return false;
	}

	/**
	 * tries to construct a circle through 3 existing points, null otherwise
	 * 
	 * @return {@link GeoElement circle}
	 */
	private GeoElement tryCircleThroughExistingPoints() {
		GeoElement circle = null;
		ArrayList<GeoPoint> list = new ArrayList<>();
		for (GPoint p : this.penPoints) {
			this.view.setHits(p,
					this.view.getEuclidianController().getDefaultEventType());
			if (this.view.getHits().containsGeoPoint()) {
				GeoPoint point = (GeoPoint) this.view.getHits()
						.getFirstHit(TestGeo.GEOPOINT);
				if (!list.contains(point)) {
					list.add(point);
				}
			}
		}

		if (list.size() >= 3) {
			circle = this.app.getKernel().getAlgoDispatcher().circle(null,
					list.get(0), list.get(1), list.get(2));
		}
		return circle;
	}

	/**
	 * creates a polygon if possible
	 */
	private boolean createPolygon() {
		GeoElement polygon = null;

		int n = getPolygonal();
		if (n > 1) { // if it's not a line
			polygon = tryPolygon(n);
		}

		// Postprocessing

		if (polygon != null) {
			ArrayList<GeoPoint> list = new ArrayList<>();
			for (GeoPointND point : ((GeoPolygon) polygon).getPoints()) {
				if (point instanceof GeoPoint) {
					list.add((GeoPoint) point);
				}
			}
			if (list.size() == ((GeoPolygon) polygon).getPoints().length
					&& expected != ShapeType.polygon) {
				// true if all the points are GeoPoints, otherwise the
				// original Polygon will not be deleted
				polygon.remove();
				PolygonFactory factory = new PolygonFactory(this.app.getKernel());
				if (expected == ShapeType.rigidPolygon) {
					factory.rigidPolygon(null,
							list.toArray(new GeoPoint[0]));
				} else {
					factory.vectorPolygon(null,
							list.toArray(new GeoPoint[0]));
				}
				return true;
			}
			return true;
		}
		resetInitialPoint();
		return false;
	}

	private void resetParameters() {
		CIRCLE_MIN_DET = 0.95;
		CIRCLE_MAX_SCORE = 0.10;
		RECTANGLE_LINEAR_TOLERANCE = 0.20;
		POLYGON_LINEAR_TOLERANCE = 0.20;
		RECTANGLE_ANGLE_TOLERANCE = 15 * Math.PI / 180;
	}

	@Override
	public boolean isFreehand() {
		return true;
	}

	/**
	 * creates a conic form the points in penPoints, if there are enough points
	 * and a conic exists that fits good enough
	 *
	 * @return the conic that fits best to the given points; null in case that
	 *         there are too few points or the thresholds cannot be fulfilled
	 */
	protected GeoConicND makeAConic() {
		// disable ellipse for whiteboard
		if (app.isWhiteboardActive()) {
			return null;
		}

		double px, py;
		// adapted from FitImplicit

		// order 2 ie conic
		int order = 2;

		// sample 10 points from what we're given
		int datasize = 10;

		if (this.penPoints.size() < datasize) {
			return null;
		}

		int step = this.penPoints.size() / datasize;

		Array2DRowRealMatrix M = new Array2DRowRealMatrix(datasize,
				order * (order + 1));

		double[] coeffs = new double[6];

		try {
			int r = 0;
			for (int j = 0; j < datasize; j++) {

				GPoint point = penPoints.get(r);
				r += step;

				px = view.toRealWorldCoordX(point.getX());
				py = view.toRealWorldCoordY(point.getY());

				// uncomment for debugging (to see which points were sampled)
				// new GeoPoint(app.getKernel().getConstruction(), null, px, py,
				// 1);

				int c1 = 0;

				// create powers eg x^2y^0, x^1y^1, x^0*y^2, x, y, 1
				for (int i = 0; i <= order; i++) {
					for (int xpower = 0; xpower <= i; xpower++) {

						int ypower = i - xpower;

						double val = AlgoFitImplicit.power(px, xpower)
								* AlgoFitImplicit.power(py, ypower);
						// Log.debug(val + "x^"+xpower+" * y^"+ypower);

						M.setEntry(j, c1++, val);
					}
				}
			}

			SingularValueDecomposition svd = new SingularValueDecomposition(M);
			RealMatrix V = svd.getV();
			RealVector coeffsRV = V.getColumnVector(5);

			// create powers eg x^2y^0, x^1y^1, x^0*y^2, x, y, 1
			for (int i = 0; i < 6; i++) {
				coeffs[5 - i] = coeffsRV.getEntry(i);
				// Log.debug("coeff of " + i + " = "+ coeffs[i]);
			}

			// double eccentricity = conic.eccentricity;

			// GeoVec2D midpoint = conic.b;

			// Log.debug("size of M = "+M.getColumnDimension()+"
			// "+M.getRowDimension());
			// Log.debug("size of V = "+V.getColumnDimension()+"
			// "+V.getRowDimension());

		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}

		GeoConicND conic = new GeoConic(this.app.getKernel().getConstruction(),
				coeffs);

		GeoPoint point = new GeoPoint(this.app.getKernel().getConstruction(), 0,
				0, 1);
		double error = 0;
		for (GPoint p : penPoints) {
			point.setCoords(view.toRealWorldCoordX(p.x),
					view.toRealWorldCoordY(p.y), 1);
			error += conic.distance(point);
		}
		error /= penPoints.size();

		if (conic.isDefined()
				&& conic.getHalfAxis(0) / error > CONIC_AXIS_ERROR_RATIO
				&& conic.getHalfAxis(1) / error > CONIC_AXIS_ERROR_RATIO) {
			AlgoFocus algo = new AlgoFocus(app.getKernel().getConstruction(),
					new String[] { null, null }, conic);
			GeoPointND[] focus = algo.getFocus();

			int type = conic.getType();
			GeoPoint pointOnConic = this.app.getKernel().getAlgoDispatcher()
					.point(null, conic, null);

			conic.remove();

			GeoPoint f0 = new GeoPoint(app.getKernel().getConstruction(), null,
					focus[0].getInhomX(), focus[0].getInhomY(), 1);
			f0.setEuclidianVisible(false);
			GeoPoint f1 = new GeoPoint(app.getKernel().getConstruction(), null,
					focus[1].getInhomX(), focus[1].getInhomY(), 1);
			f1.setEuclidianVisible(false);
			GeoPoint additionalPoint = new GeoPoint(
					app.getKernel().getConstruction(), null,
					pointOnConic.getInhomX(), pointOnConic.getInhomY(), 1);
			additionalPoint.setEuclidianVisible(false);

			conic = this.app.getKernel().getAlgoDispatcher()
					.ellipseHyperbola(null, f0, f1, additionalPoint, type);
		} else {
			conic.remove();
			conic = null;
		}

		if (conic != null) {
			conic.setIsShape(app.isWhiteboardActive());
			conic.setLabelVisible(false);
		}
		return conic;
	}

	/**
	 * @return {@link GeoElement} if polygon or line could be created,
	 *         {@code null} otherwise
	 */
	protected GeoElement tryPolygonOrLine() {
		int n = getPolygonal();

		if (n <= 0) {
			return null;
		} else if (n == 1) {
			return tryLine();
		}
		return tryPolygon(n);
	}

	/**
	 * @return {@link GeoElement} if polygon or line could be created,
	 *         {@code null} otherwise
	 */
	private GeoElement tryLine() {
		RecoSegment rs = getRecoSegment(0);
		rs.startpt = brk[0];
		rs.endpt = brk[1];
		get_segment_geometry(a, rs);

		if (Math.abs(rs.angle) < SLANT_TOLERANCE) {
			rs.angle = 0;
			rs.y1 = rs.y2 = rs.ycenter;
		}
		if (Math.abs(rs.angle) > Math.PI / 2 - SLANT_TOLERANCE) {
			rs.angle = (rs.angle > 0) ? (Math.PI / 2) : (-Math.PI / 2);
			rs.x1 = rs.x2 = rs.xcenter;
		}
		double x_first = view.toRealWorldCoordX(rs.x1);
		double y_first = view.toRealWorldCoordY(rs.y1);
		double x_last = view.toRealWorldCoordX(rs.x2);
		double y_last = view.toRealWorldCoordY(rs.y2);

		GeoPoint p;
		if (this.initialPoint != null) {
			p = initialPoint;
		} else {
			p = new GeoPoint(app.getKernel().getConstruction(), null, x_first,
					y_first, 1.0);
		}
		GeoPoint q = new GeoPoint(app.getKernel().getConstruction(), null,
				x_last, y_last, 1.0);

		return getJoinPointsSegment(p, q);
	}

	private static class RecoSegment {
		int startpt = 0;
		int endpt = 0;
		double xcenter = 0;
		double ycenter = 0;
		double angle = 0;
		double radius = 0;
		double x1 = 0;
		double y1 = 0;
		double x2 = 0;
		double y2 = 0;
		boolean reversed;

		protected RecoSegment() {
			// no public constructor
		}
	}

	/**
	 * @param n
	 *            number of vertices
	 * @return polygon
	 */
	protected GeoElement tryPolygon(int n) {
		int j;
		RecoSegment temp1;
		optimize_polygonal(n);

		while (n + recognizer_queue_length > MAX_POLYGON_SIDES) {
			j = 1;
			temp1 = reco_queue_b;
			while (j < recognizer_queue_length && temp1.startpt != 0) {
				j++;
				if (j == 2) {
					temp1 = reco_queue_c;
				}
				if (j == 3) {
					temp1 = reco_queue_d;
				}
				if (j == 4) {
					temp1 = reco_queue_e;
				}
			}
			recognizer_queue_length = recognizer_queue_length - j;
			int te1 = 0;
			int te2 = j;
			RecoSegment t1;
			RecoSegment t2;
			for (int k = 0; k < recognizer_queue_length; ++k) {
				t1 = getRecoSegment(te1);
				t2 = getRecoSegment(te2);

				t1.startpt = t2.startpt;
				t1.endpt = t2.endpt;
				t1.xcenter = t2.xcenter;
				t1.ycenter = t2.ycenter;
				t1.angle = t2.angle;
				t1.radius = t2.radius;
				t1.x1 = t2.x1;
				t1.x2 = t2.x2;
				t1.y1 = t2.y2;
				t1.y2 = t2.y2;
				t1.reversed = t2.reversed;
				te1++;
				te2++;
			}
		}

		RecoSegment rs;
		Inertia ss = null;
		int temp_reco = recognizer_queue_length;
		recognizer_queue_length = recognizer_queue_length + n;
		for (j = 0; j < n; ++j) {
			rs = getRecoSegment(temp_reco + j);

			if (j == 0) {
				ss = a;
			} else if (j == 1) {
				ss = b;
			} else if (j == 2) {
				ss = c;
			} else if (j == 3) {
				ss = d;
			}
			rs.startpt = brk[j];
			rs.endpt = brk[j + 1];
			get_segment_geometry(ss, rs);
		}

		GeoElement geo;
		if ((geo = try_rectangle()) != null
				|| (geo = try_closed_polygon(3)) != null
				|| (geo = try_closed_polygon(4)) != null) {
			recognizer_queue_length = 0;
			return geo;
		}
		return null;
	}

	private GeoElement try_rectangle() {
		int nsides = 4;

		if (recognizer_queue_length < nsides) {
			return null;
		}

		int i;
		double dist, avg_angle = 0;

		RecoSegment rs = getRecoSegment(recognizer_queue_length - nsides);
		RecoSegment r1;
		RecoSegment r2;
		// AbstractApplication.debug(rs.startpt);
		if (rs.startpt != 0) {
			return null;
		}
		for (i = 0; i < nsides; ++i) {
			r1 = getRecoSegment(recognizer_queue_length - nsides + i);
			r2 = getRecoSegment(
					recognizer_queue_length - nsides + ((i + 1) % nsides));
			// AbstractApplication.debug(Math.abs(Math.abs(r1.angle-r2.angle)-Math.PI/2)
			// > RECTANGLE_ANGLE_TOLERANCE);
			if (Math.abs(Math.abs(r1.angle - r2.angle)
					- Math.PI / 2) > RECTANGLE_ANGLE_TOLERANCE) {
				return null;
			}
			avg_angle = avg_angle + r1.angle;
			if (r2.angle > r1.angle) {
				avg_angle = avg_angle + ((i + 1) * Math.PI / 2);
			} else {
				avg_angle = avg_angle - ((i + 1) * Math.PI / 2);
			}
			r1.reversed = ((r1.x2 - r1.x1) * (r2.xcenter - r1.xcenter)
					+ (r1.y2 - r1.y1) * (r2.ycenter - r1.ycenter)) < 0;
		}
		for (i = 0; i < nsides; ++i) {
			r1 = getRecoSegment(recognizer_queue_length - nsides + i);
			r2 = getRecoSegment(
					recognizer_queue_length - nsides + ((i + 1) % nsides));
			dist = Math.hypot(
					(r1.reversed ? r1.x1 : r1.x2)
							- (r2.reversed ? r2.x2 : r2.x1),
					(r1.reversed ? r1.y1 : r1.y2)
							- (r2.reversed ? r2.y2 : r2.y1));
			if (dist > RECTANGLE_LINEAR_TOLERANCE * (r1.radius + r2.radius)) {
				return null;
			}
		}
		avg_angle = avg_angle / nsides;
		if (Math.abs(avg_angle) < SLANT_TOLERANCE) {
			avg_angle = 0;
		}
		if (Math.abs(avg_angle) > Math.PI / 2 - SLANT_TOLERANCE) {
			avg_angle = Math.PI / 2;
		}
		for (i = 0; i < nsides; ++i) {
			r1 = getRecoSegment(recognizer_queue_length - nsides + i);
			r1.angle = avg_angle + i * Math.PI / 2;
		}

		double[] pt = new double[2];
		double[] points = new double[10];

		for (i = 0; i < nsides; ++i) {
			r1 = getRecoSegment(recognizer_queue_length - nsides + i);
			r2 = getRecoSegment(
					recognizer_queue_length - nsides + ((i + 1) % nsides));
			calc_edge_isect(r1, r2, pt);
			points[2 * i + 2] = pt[0];
			points[2 * i + 3] = pt[1];
		}
		points[0] = points[2 * nsides];
		points[1] = points[2 * nsides + 1];

		// in case a initialPoint is defined, move the polygon so that its first
		// point matches the initialPoint
		double offsetInitialPointX = 0;
		double offsetInitialPointY = 0;

		// in case the initialPoint cannot be used and can be deleted safely,
		// delete it
		if (initialPoint != null && !initialPoint.isIndependent()
				&& deleteInitialPoint) {
			this.initialPoint.remove();
			this.initialPoint = null;
		}

		Construction cons = app.getKernel().getConstruction();
		GeoPointND[] pts = new GeoPointND[nsides];
		double x_first;
		double y_first;

		for (i = 0; i < nsides; ++i) {
			x_first = view.toRealWorldCoordX(points[2 * i])
					+ offsetInitialPointX;
			y_first = view.toRealWorldCoordY(points[2 * i + 1])
					+ offsetInitialPointY;

			if (i == 0 && this.initialPoint != null
					&& this.initialPoint.isIndependent()) {
				offsetInitialPointX = this.initialPoint.x - x_first;
				offsetInitialPointY = this.initialPoint.y - y_first;
				pts[0] = this.initialPoint;
			} else {
				// null -> created labeled point
				pts[i] = new GeoPoint(cons, null, x_first, y_first, 1.0);
			}
		}
		Log.debug("Rectangle Recognized");
		return createPolygonFromPoints(pts);
	}

	private void optimize_polygonal(int nsides) {
		double cost, newcost;
		boolean improved;
		Inertia temp1 = new Inertia();
		Inertia temp2 = new Inertia();

		for (int i = 1; i < nsides; ++i) {
			copyInertiaToTemp(temp1, temp2, i);
			cost = getCost(temp1, temp2);
			improved = false;
			while (brk[i] > brk[i - 1] + 1) {
				incr_inertia(brk[i] - 1, temp1, -1);
				incr_inertia(brk[i] - 1, temp2, 1);
				newcost = getCost(temp1, temp2);
				if (newcost >= cost) {
					break;
				}
				improved = true;
				cost = newcost;
				brk[i]--;
				copyInertiaFromTemp(temp1, temp2, i);
			}
			if (improved) {
				continue;
			}

			copyInertiaToTemp(temp1, temp2, i);

			while (brk[i] < brk[i + 1] - 1) {
				incr_inertia(brk[i], temp1, 1);
				incr_inertia(brk[i], temp2, -1);
				newcost = getCost(temp1, temp2);
				if (newcost >= cost) {
					break;
				}
				cost = newcost;
				brk[i]++;
				copyInertiaFromTemp(temp1, temp2, i);
			}
		}
	}

	private void get_segment_geometry(Inertia s, RecoSegment r) {
		int i;
		int start = r.startpt;
		r.xcenter = center_x(s);
		r.ycenter = center_y(s);
		double a1 = i_xx(s);
		double b1 = i_xy(s);
		double c1 = i_yy(s);
		r.angle = Math.atan2(2 * b1, a1 - c1) / 2;
		r.radius = Math.sqrt(3 * (a1 + c1));
		double lmin = 0;
		double lmax = 0;
		double l;
		for (i = start; i <= r.endpt; ++i) {
			l = (penPoints.get(start).x - r.xcenter) * Math.cos(r.angle)
					+ (penPoints.get(start).y - r.ycenter) * Math.sin(r.angle);
			if (l < lmin) {
				lmin = l;
			}
			if (l > lmax) {
				lmax = l;
			}
			start++;
		}
		r.x1 = r.xcenter + lmin * Math.cos(r.angle);
		r.y1 = r.ycenter + lmin * Math.sin(r.angle);
		r.x2 = r.xcenter + lmax * Math.cos(r.angle);
		r.y2 = r.ycenter + lmax * Math.sin(r.angle);
	}

	private RecoSegment getRecoSegment(int n) {
		switch (n) {
		case 0:
			return reco_queue_a;
		case 1:
			return reco_queue_b;
		case 2:
			return reco_queue_c;
		case 3:
			return reco_queue_d;
		case 4:
			return reco_queue_e;
		}
		return null;
	}

	private static void calc_edge_isect(RecoSegment r1, RecoSegment r2,
			double[] pt) {
		double t = (r2.xcenter - r1.xcenter) * Math.sin(r2.angle)
				- (r2.ycenter - r1.ycenter) * Math.cos(r2.angle);
		t = t / Math.sin(r2.angle - r1.angle);
		pt[0] = r1.xcenter + t * Math.cos(r1.angle);
		pt[1] = r1.ycenter + t * Math.sin(r1.angle);
	}

	private GeoElement try_closed_polygon(int nsides) {
		if (recognizer_queue_length < nsides) {
			return null;
		}

		RecoSegment rs = getRecoSegment(recognizer_queue_length - nsides);
		if (rs.startpt != 0) {
			return null;
		}

		RecoSegment r1;
		RecoSegment r2;
		int i;
		double dist;
		double[] pt = new double[2];

		for (i = 0; i < nsides; ++i) {
			r1 = getRecoSegment(recognizer_queue_length - nsides + i);
			r2 = getRecoSegment(
					recognizer_queue_length - nsides + (i + 1) % nsides);
			calc_edge_isect(r1, r2, pt);
			r1.reversed = (Math.hypot(pt[0] - r1.x1, pt[1] - r1.y1)) < (Math
					.hypot(pt[0] - r1.x2, pt[1] - r1.y2));
		}
		for (i = 0; i < nsides; ++i) {
			r1 = getRecoSegment(recognizer_queue_length - nsides + i);
			r2 = getRecoSegment(
					recognizer_queue_length - nsides + (i + 1) % nsides);
			calc_edge_isect(r1, r2, pt);
			dist = Math.hypot((r1.reversed ? r1.x1 : r1.x2) - pt[0],
					(r1.reversed ? r1.y1 : r1.y2) - pt[1])
					+ Math.hypot((r2.reversed ? r2.x2 : r2.x1) - pt[0],
							(r2.reversed ? r2.y2 : r2.y1) - pt[1]);
			if (dist > POLYGON_LINEAR_TOLERANCE * (r1.radius + r2.radius)) {
				return null;
			}
		}

		double[] points = new double[nsides * 2 + 2];

		for (i = 0; i < nsides; ++i) {
			r1 = getRecoSegment(recognizer_queue_length - nsides + i);
			r2 = getRecoSegment(
					recognizer_queue_length - nsides + (i + 1) % nsides);
			calc_edge_isect(r1, r2, pt);
			points[2 * i + 2] = pt[0];
			points[2 * i + 3] = pt[1];
		}
		points[0] = points[2 * nsides];
		points[1] = points[2 * nsides + 1];

		GeoPointND[] pts = new GeoPointND[nsides];

		for (i = 0; i < nsides; ++i) {
			if (i == 0 && this.initialPoint != null) {
				pts[0] = initialPoint;
				initialPoint = null;
			} else {
				// null -> created labeled point
				pts[i] = new GeoPoint(app.getKernel().getConstruction(), null,
						view.toRealWorldCoordX(points[2 * i]),
						view.toRealWorldCoordY(points[2 * i + 1]), 1.0);
			}
		}
		if (nsides == 3) {
			Log.debug("Triangle Recognized");
		} else {
			Log.debug("Quadrilateral Recognized");
		}
		return createPolygonFromPoints(pts);
	}

	private void copyInertiaToTemp(Inertia temp1, Inertia temp2, int i) {
		if (i - 1 == 0) {
			temp1.copyValuesFrom(a);
			temp2.copyValuesFrom(b);
		} else if (i - 1 == 1) {
			temp1.copyValuesFrom(b);
			temp2.copyValuesFrom(c);
		} else if (i - 1 == 2) {
			temp1.copyValuesFrom(c);
			temp2.copyValuesFrom(d);
		}
	}

	private void copyInertiaFromTemp(Inertia temp1, Inertia temp2, int i) {
		if (i - 1 == 0) {
			a.copyValuesFrom(temp1);
			b.copyValuesFrom(temp2);
		} else if (i - 1 == 1) {
			b.copyValuesFrom(temp1);
			c.copyValuesFrom(temp2);
		} else if (i - 1 == 2) {
			c.copyValuesFrom(temp1);
			d.copyValuesFrom(temp2);
		}
	}

	/**
	 * @return number of vertices
	 */
	protected int getPolygonal() {
		brk = new int[5];
		a = new Inertia();
		b = new Inertia();
		c = new Inertia();
		d = new Inertia();

		// AbstractApplication.debug(penPoints);

		return this.findPolygonal(0, penPoints.size() - 1, MAX_POLYGON_SIDES, 0,
				0);
	}

	/*
	 * ported from xournal by Neel Shah
	 */
	private int findPolygonal(int start, int end, int n, int offset1,
			int offset2) {
		Inertia s = new Inertia();
		Inertia s1 = new Inertia();
		Inertia s2 = new Inertia();
		int k, i1 = 0, i2 = 0, n1 = 0, n2;
		double det1, det2;
		int nsides = n;
		// AbstractApplication.debug(start);
		// AbstractApplication.debug(end);
		if (end == start) {
			return 0; // no way
		}
		if (nsides <= 0) {
			return 0;
		}
		if (end - start < 5) {
			nsides = 1; // too small for a polygon
		}
		// look for a linear piece that's big enough
		for (k = 0; k < nsides; ++k) {
			i1 = start + (k * (end - start)) / nsides;
			// AbstractApplication.debug(i1);
			i2 = start + ((k + 1) * (end - start)) / nsides;
			// AbstractApplication.debug(i2);
			calc_inertia(i1, i2, s);
			if (i_det(s) < LINE_MAX_DET) {
				break;
			}
		}
		if (k == nsides) {
			return 0;
		}
		while (true) {
			if (i1 > start) {
				s1.copyValuesFrom(s);
				this.incr_inertia(i1 - 1, s1, 1);
				det1 = i_det(s1);
			} else {
				det1 = 1;
			}

			if (i2 < end) {
				s2.copyValuesFrom(s);
				this.incr_inertia(i2, s2, 1);
				det2 = i_det(s2);
			} else {
				det2 = 1;
			}

			if (det1 < det2 && det1 < LINE_MAX_DET) {
				i1--;
				s.copyValuesFrom(s1);
			} else if (det2 < det1 && det2 < LINE_MAX_DET) {
				i2++;
				s.copyValuesFrom(s2);
			} else {
				break;
			}
		}
		if (i1 > start) {
			n1 = this.findPolygonal(start, i1,
					(i2 == end) ? (nsides - 1) : (nsides - 2), offset1,
					offset2);
			if (n1 == 0) {
				return 0;
			}
		} else {
			n1 = 0;
		}
		brk[n1 + offset1] = i1;
		brk[n1 + 1 + offset1] = i2;

		if (offset2 + n1 == 0) {
			a.copyValuesFrom(s);
		} else if (offset2 + n1 == 1) {
			b.copyValuesFrom(s);
		} else if (offset2 + n1 == 2) {
			c.copyValuesFrom(s);
		} else if (offset2 + n1 == 3) {
			d.copyValuesFrom(s);
		}

		if (i2 < end) {
			n2 = this.findPolygonal(i2, end, nsides - n1 - 1, offset1 + n1 + 1,
					offset2 + n1 + 1);
			if (n2 == 0.) {
				return 0;
			}
		} else {
			n2 = 0;
		}
		return n1 + n2 + 1;
	}

	/**
	 *
	 * @param points
	 *            {@link GeoPointND}
	 * @return {@link GeoElement Polygon} created of given points
	 */
	private GeoElement createPolygonFromPoints(GeoPointND[] points) {
		points[0].setHighlighted(false);

		AlgoPolygon algo = new AlgoPolygon(app.getKernel().getConstruction(),
				null, points);
		GeoElement poly = algo.getOutput(0);
		// poly.setLineThickness(penSize * PEN_SIZE_FACTOR);
		// poly.setLineType(penLineStyle);
		// poly.setObjColor(penColor);
		if (view.getEuclidianController()
				.getPreviousMode() != EuclidianConstants.MODE_POLYGON) {
			poly.setIsShape(app.isWhiteboardActive());
			poly.setAlphaValue(0);
			poly.setBackgroundColor(GColor.WHITE);
			poly.setObjColor(GColor.BLACK);
			poly.updateRepaint();
			for (GeoPointND point : points) {
				point.setEuclidianVisible(false);
			}
			if (poly instanceof GeoPolygon) {
				for (GeoSegmentND geoSeg : ((GeoPolygon) poly).getSegments()) {
					((GeoSegment) geoSeg).setSelectionAllowed(false);
					((GeoSegment) geoSeg).setLabelVisible(false);
				}
			}
		}
		return poly;
	}

	private GeoElement getJoinPointsSegment(GeoPoint first, GeoPoint last) {
		Construction cons = app.getKernel().getConstruction();
		AlgoJoinPointsSegment algo = new AlgoJoinPointsSegment(cons, null,
				first, last);
		first.setEuclidianVisible(false);
		last.setEuclidianVisible(false);
		GeoElement line = algo.getOutput(0);
		line.updateRepaint();
		line.setIsShape(app.isWhiteboardActive());
		line.setLabelVisible(false);
		return line;
	}

	private GeoElement tryCircle() {
		ArrayList<GeoElement> circlePlusPoint = getCircleThreePoints();
		if (circlePlusPoint != null) {
			GeoConicND circle = (GeoConicND) circlePlusPoint.get(0);
			circle.setIsShape(app.isWhiteboardActive());
			if (app.isWhiteboardActive()) {
				return circle;
			}
			// midpoint
			GeoPoint m = new GeoPoint(app.getKernel().getConstruction(), null,
					circle.getMidpoint().getX(), circle.getMidpoint().getY(),
					1.0);
			m.setEuclidianVisible(false);

			// point on the circle
			GeoPoint p = (GeoPoint) circlePlusPoint.get(1);
			p.setLabel(null);
			p.setEuclidianVisible(false);

			// delete the circle that was created in makeACircle
			circle.remove();

			// create a new circle with midpoint and point
			return app.getKernel().getAlgoDispatcher().circle(null, m, p);
		}
		return null;
	}

	/**
	 * @return circle through three points
	 */
	protected ArrayList<GeoElement> getCircleThreePoints() {
		Inertia s = new Inertia();
		this.calc_inertia(0, penPoints.size() - 1, s);
		if (i_det(s) > CIRCLE_MIN_DET) {
			score = this.score_circle(0, penPoints.size() - 1, s);
			if (score < CIRCLE_MAX_SCORE) {
				return this.makeACircle(center_x(s), center_y(s), i_rad(s));
			}
		}
		return null;
	}

	private ArrayList<GeoElement> makeACircle(double x, double y, double r) {
		ArrayList<GPoint> temp = new ArrayList<>();
		int npts, i = 0;
		npts = (int) (2 * r);
		if (npts < 12) {
			npts = 12;
		}
		GPoint p;
		for (i = 0; i <= npts; i++) {
			p = new GPoint();
			p.x = (int) (x + r * Math.cos((2 * i * Math.PI) / npts));
			p.y = (int) (y + r * Math.sin((2 * i * Math.PI) / npts));
			temp.add(p);
		}
		int size = temp.size();
		double x1 = view.toRealWorldCoordX(temp.get(0).x);
		double y1 = view.toRealWorldCoordY(temp.get(0).y);
		double x2 = view.toRealWorldCoordX(temp.get(size / 3).x);
		double y2 = view.toRealWorldCoordY(temp.get(size / 3).y);
		double x3 = view.toRealWorldCoordX(temp.get(2 * size / 3).x);
		double y3 = view.toRealWorldCoordY(temp.get(2 * size / 3).y);
		if (x2 == x1) {
			x1 = view.toRealWorldCoordX(temp.get(size / 4).x);
			y1 = view.toRealWorldCoordY(temp.get(size / 4).y);
		}
		if (x2 == x3) {
			x3 = view.toRealWorldCoordX(temp.get(11 * size / 12).x);
			y3 = view.toRealWorldCoordY(temp.get(11 * size / 12).y);
		}
		GeoPoint p1 = new GeoPoint(app.getKernel().getConstruction(), x1, y1,
				1.0);
		GeoPoint q = new GeoPoint(app.getKernel().getConstruction(), x2, y2,
				1.0);
		GeoPoint z = new GeoPoint(app.getKernel().getConstruction(), x3, y3,
				1.0);
		AlgoCircleThreePoints algo = new AlgoCircleThreePoints(
				app.getKernel().getConstruction(), p1, q, z);

		GeoConicND circle = algo.getCircle();
		Equation equ = getEquationOfConic(circle.getFlatMatrix());
		equ.initEquation();
		GeoElement[] geos = view.getKernel().getAlgebraProcessor()
				.processConic(equ, equ.wrap(), new EvalInfo(true));
		geos[0].setEuclidianVisible(true);
		circle.remove();
		algo.remove();
		circle = (GeoConicND) geos[0];
		// circle.setLineThickness(penSize * PEN_SIZE_FACTOR);
		// circle.setLineType(penLineStyle);
		// circle.setObjColor(penColor);
		circle.updateRepaint();

		ArrayList<GeoElement> ret = new ArrayList<>();

		ret.add(circle);
		ret.add(p1);

		return ret;
	}

	private void calc_inertia(int start, int end, Inertia s) {
		s.mass = 0.;
		s.sx = 0.;
		s.sxx = 0.;
		s.sxy = 0.;
		s.sy = 0.;
		s.syy = 0.;
		int[] temp1 = new int[4];
		temp1[0] = penPoints.get(start).x;
		temp1[1] = penPoints.get(start).y;
		temp1[2] = penPoints.get(start + 1).x;
		temp1[3] = penPoints.get(start + 1).y;
		int coeff = 1;
		double dm = coeff
				* Math.hypot(temp1[2] - temp1[0], temp1[3] - temp1[1]);
		s.mass = s.mass + dm;
		s.sx = s.sx + (dm * temp1[0]);
		s.sxx = s.sxx + (dm * temp1[0] * temp1[0]);
		s.sxy = s.sxy + (dm * temp1[0] * temp1[1]);
		s.sy = s.sy + (dm * temp1[1]);
		s.syy = s.syy + (dm * temp1[1] * temp1[1]);
		for (int i = start + 1; i < end; ++i) {
			temp1[0] = penPoints.get(i).x;
			temp1[1] = penPoints.get(i).y;
			temp1[2] = penPoints.get(i + 1).x;
			temp1[3] = penPoints.get(i + 1).y;
			dm = coeff * Math.hypot(temp1[2] - temp1[0], temp1[3] - temp1[1]);
			s.mass = s.mass + dm;
			s.sx = s.sx + (dm * temp1[0]);
			s.sxx = s.sxx + (dm * temp1[0] * temp1[0]);
			s.sxy = s.sxy + (dm * temp1[0] * temp1[1]);
			s.sy = s.sy + (dm * temp1[1]);
			s.syy = s.syy + (dm * temp1[1] * temp1[1]);
		}
	}

	private final static double i_det(Inertia s) {
		double ixx = i_xx(s);
		double iyy = i_yy(s);
		double ixy = i_xy(s);
		if (s.mass <= 0.) {
			return 0.;
		}
		if (ixx + iyy <= 0.) {
			return 0.;
		}
		return 4 * (ixx * iyy - ixy * ixy) / (ixx + iyy) / (ixx + iyy);
	}

	private static double i_xx(Inertia s) {
		if (s.mass <= 0.) {
			return 0.;
		}
		return (s.sxx - s.sx * s.sx / s.mass) / s.mass;
	}

	private static double i_xy(Inertia s) {
		if (s.mass <= 0.) {
			return 0.;
		}
		return (s.sxy - s.sx * s.sy / s.mass) / s.mass;
	}

	private static double i_yy(Inertia s) {
		if (s.mass <= 0.) {
			return 0.;
		}
		return (s.syy - s.sy * s.sy / s.mass) / s.mass;
	}

	private double score_circle(int start, int end, Inertia s) {
		double sum, x0, y0, r0, dm, deltar;
		int i;
		if (s.mass == 0.) {
			return 0;
		}
		sum = 0.;
		x0 = center_x(s);
		y0 = center_y(s);
		r0 = i_rad(s);
		for (i = start; i < end; ++i) {
			dm = Math.hypot(penPoints.get(i + 1).x - penPoints.get(i).x,
					penPoints.get(i + 1).y - penPoints.get(i).y);
			deltar = Math.hypot(penPoints.get(i).x - x0,
					penPoints.get(i).y - y0) - r0;
			sum = sum + (dm * Math.abs(deltar));
		}
		return sum / (s.mass * r0);
	}

	private static double center_x(Inertia s) {
		return s.sx / s.mass;
	}

	private static double center_y(Inertia s) {
		return s.sy / s.mass;
	}

	private static double i_rad(Inertia s) {
		double ixx = i_xx(s);
		double iyy = i_yy(s);
		if (ixx + iyy <= 0.) {
			return 0.;
		}
		return Math.sqrt(ixx + iyy);
	}

	private void incr_inertia(int start, Inertia s, int coeff) {
		// defensive code
		// https://play.google.com/apps/publish/?dev_acc=05873811091523087820#ErrorClusterDetailsPlace:p=org.geogebra.android&et=CRASH&lr=LAST_30_DAYS&ecn=java.lang.ArrayIndexOutOfBoundsException&tf=SourceFile&tc=org.geogebra.a.c.v&tm=a&nid&an&c&s=new_status_desc
		if (start + 1 >= penPoints.size()) {
			// Log.error("problem in incr_inertia "+ start + " " + s + " " +
			// coeff);
			Log.debug("problem in EuclidianPen.incr_inertia " + start + " " + s
					+ " " + coeff);
			return;
		}

		double pt1_x = penPoints.get(start).x;
		double pt1_y = penPoints.get(start).y;
		double pt2_x = penPoints.get(start + 1).x;
		double pt2_y = penPoints.get(start + 1).y;
		double dm = 0;
		dm = coeff * Math.hypot(pt2_x - pt1_x, pt2_y - pt1_y);
		s.mass = s.mass + dm;
		s.sx = s.sx + (dm * pt1_x);
		s.sy = s.sy + (dm * pt1_y);
		s.sxx = s.sxx + (dm * pt1_x * pt1_x);
		s.syy = s.syy + (dm * pt1_y * pt1_y);
		s.sxy = s.sxy + (dm * pt1_x * pt1_y);
	}

	private Equation getEquationOfConic(double[] coeffs) {
		FunctionVariable xx = new FunctionVariable(view.getKernel(), "x");
		FunctionVariable yy = new FunctionVariable(view.getKernel(), "y");
		// x^2
		ExpressionNode xSqr = new ExpressionNode(view.getKernel(), xx,
				Operation.MULTIPLY, xx);
		// x*x
		ExpressionNode xy = new ExpressionNode(view.getKernel(), xx,
				Operation.MULTIPLY, yy);
		// y^2
		ExpressionNode ySqr = new ExpressionNode(view.getKernel(), yy,
				Operation.MULTIPLY, yy);
		ExpressionNode term1 = new ExpressionNode(view.getKernel(),
				new ExpressionNode(view.getKernel(), coeffs[0]),
				Operation.MULTIPLY, xSqr);
		ExpressionNode term2 = new ExpressionNode(view.getKernel(),
				new ExpressionNode(view.getKernel(), coeffs[3] * 2),
				Operation.MULTIPLY, xy);
		ExpressionNode term3 = new ExpressionNode(view.getKernel(),
				new ExpressionNode(view.getKernel(), coeffs[1]),
				Operation.MULTIPLY, ySqr);
		ExpressionNode term4 = new ExpressionNode(view.getKernel(),
				new ExpressionNode(view.getKernel(), coeffs[4] * 2),
				Operation.MULTIPLY, xx);
		ExpressionNode term5 = new ExpressionNode(view.getKernel(),
				new ExpressionNode(view.getKernel(), coeffs[5] * 2),
				Operation.MULTIPLY, yy);

		ExpressionNode term12 = new ExpressionNode(view.getKernel(), term1,
				Operation.PLUS, term2);
		ExpressionNode term34 = new ExpressionNode(view.getKernel(), term3,
				Operation.PLUS, term4);
		ExpressionNode term1234 = new ExpressionNode(view.getKernel(), term12,
				Operation.PLUS, term34);
		ExpressionNode lhs = new ExpressionNode(view.getKernel(), term1234,
				Operation.PLUS, term5);
		ExpressionNode rhs = new ExpressionNode(view.getKernel(), -coeffs[2]);
		Equation equ = new Equation(view.getKernel(), lhs, rhs);
		return equ;
	}

	private static double getCost(Inertia temp1, Inertia temp2) {
		return (i_det(temp1) * i_det(temp1)) + (i_det(temp2) * i_det(temp2));
	}

}
