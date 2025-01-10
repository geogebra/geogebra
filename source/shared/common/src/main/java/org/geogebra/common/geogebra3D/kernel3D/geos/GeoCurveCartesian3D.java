package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.DistanceFunction;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.algos.AlgoMacro;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.RotatableND;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.roots.RealRootUtil;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;

/**
 * Class for cartesian curves in 3D
 * 
 * @author mathieu
 * 
 */
public class GeoCurveCartesian3D extends GeoCurveCartesianND implements
		RotatableND, Translateable,
		MirrorableAtPlane, Transformable, Dilateable {
	private CoordMatrix4x4 tmpMatrix4x4;
	private boolean trace;
	
	/**
	 * empty constructor (for ConstructionDefaults3D)
	 * 
	 * @param c
	 *            construction
	 */
	public GeoCurveCartesian3D(Construction c) {
		super(c, 3, null);
	}

	/**
	 * common constructor
	 * 
	 * @param c
	 *            construction
	 * @param fun
	 *            functions
	 * @param point
	 *            point expression (may be null)
	 */
	public GeoCurveCartesian3D(Construction c, Function[] fun,
			ExpressionNode point) {
		super(c, fun, point);
	}

	/**
	 * copy constructor
	 * 
	 * @param curve
	 *            original
	 */
	public GeoCurveCartesian3D(GeoCurveCartesian3D curve) {
		super(curve.cons, 3, null);
		set(curve);
	}

	@Override
	public Function getFun(int i) {
		return fun[i];
	}

	/**
	 * @param t
	 *            parameter
	 * @return derivative at given point
	 */
	public Coords evaluateTangent(double t) {
		updateDerivatives();
		Coords v = new Coords(3);
		for (int i = 0; i < 3; i++) {
			v.set(i + 1, funD1[i].value(t));
		}

		return v.normalized();

	}

	@Override
	public void evaluateCurve(double t, double[] out) {

		for (int i = 0; i < 3; i++) {
			out[i] = fun[i].value(t);
		}
	}

	@Override
	public double[] newDoubleArray() {
		return new double[3];
	}

	/**
	 * @param t
	 *            curve parameter
	 * @return resulting coords
	 */
	public Coords evaluateCurve3D(double t) {
		return new Coords(fun[0].value(t), fun[1].value(t), fun[2].value(t), 1);
	}

	/**
	 * Returns the curvature at the specified point
	 * 
	 * @param t
	 *            parameter
	 */
	@Override
	public double evaluateCurvature(double t) {
		updateDerivatives();
		Coords D1 = new Coords(3);
		Coords D2 = new Coords(3);

		for (int i = 0; i < 3; i++) {
			D1.set(i + 1, funD1[i].value(t));
		}

		for (int i = 0; i < 3; i++) {
			D2.set(i + 1, funD2[i].value(t));
		}

		// compute curvature using the formula k = |f'' x f'| / |f'|^3
		Coords cross = D1.crossProduct(D2);
		// Log.debug(cross.norm() / Math.pow(D1.norm(), 3));
		return cross.norm() / Math.pow(D1.norm(), 3);
	}

	private void updateDerivatives() {
		int dim = 3;
		funD1 = new Function[dim];
		funD2 = new Function[dim];
		for (int i = 0; i < dim; i++) {
			funD1[i] = getFun(i).getDerivative(1, true);
			funD2[i] = getFun(i).getDerivative(2, true);
		}

	}

	@Override
	public GeoElement copy() {
		return new GeoCurveCartesian3D(this);
	}

	@Override
	public void set(GeoElementND geo) {

		if (!(geo instanceof GeoCurveCartesianND)) {
			return;
		}

		GeoCurveCartesianND geoCurve = (GeoCurveCartesianND) geo;

		// fun = new Function[3];
		for (int i = 0; i < 2; i++) {
			fun[i] = new Function(geoCurve.getFun(i), kernel);
		}
		if (geoCurve.isGeoElement3D()) {
			fun[2] = new Function(geoCurve.getFun(2), kernel);
		} else { // t -> (x,y,0) 2D curve
			fun[2] = new Function(new ExpressionNode(kernel, 0),
					new FunctionVariable(kernel, "t"));
		}

		startParam = geoCurve.getMinParameter();
		endParam = geoCurve.getMaxParameter();
		isDefined = geoCurve.isDefined();

		// macro OUTPUT
		if (geo.getConstruction() != cons && isAlgoMacroOutput()) {
			if (!geo.isIndependent()) {
				// this object is an output object of AlgoMacro
				// we need to check the references to all geos in its function's
				// expression
				AlgoMacro algoMacro = (AlgoMacro) getParentAlgorithm();
				for (int i = 0; i < 3; i++) {
					algoMacro.initFunction(fun[i]);
				}
			}
		}

		// distFun = new ParametricCurveDistanceFunction(this);
	}

	@Override
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.CURVE_CARTESIAN3D;
	}

	@Override
	public Coords getLabelPosition() {
		return Coords.O; // TODO
	}

	@Override
	public boolean isGeoElement3D() {
		return true;
	}

	@Override
	public boolean hasFillType() {
		return false;
	}

	// ////////////////
	// TRACE
	// ////////////////

	@Override
	public boolean isTraceable() {
		return true;
	}

	@Override
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	@Override
	public boolean getTrace() {
		return trace;
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}
		SurfaceTransform.rotate(fun, kernel, r, S, tmpMatrix4x4);

	}

	@Override
	public void rotate(NumberValue r) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}
		SurfaceTransform.rotate(fun, kernel, r, tmpMatrix4x4);

	}

	@Override
	public void rotate(NumberValue r, Coords S,
			GeoDirectionND orientation) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}
		SurfaceTransform.rotate(fun, kernel, r, S, orientation, tmpMatrix4x4);
	}

	@Override
	public double[] getDefinedInterval(double a, double b) {

		return getDefinedInterval(a, b, fun[0], fun[1], fun[2]);

	}

	/**
	 * @param a
	 *            start parameter
	 * @param b
	 *            end parameter
	 * @param funX
	 *            x(t)
	 * @param funY
	 *            y(t)
	 * @param funZ
	 *            z(t)
	 * @return an interval within [a, b] where the funX, funY, funZ are defined.
	 * 
	 */
	static public double[] getDefinedInterval(double a, double b,
			UnivariateFunction funX, UnivariateFunction funY,
			UnivariateFunction funZ) {

		// compute interval for x(t)
		double[] interval = RealRootUtil.getDefinedInterval(funX, a, b);

		// compute interval for y(t) and update interval
		RealRootUtil.updateDefinedIntervalIntersecting(funY, a, b, interval);

		// compute interval for z(t) and update interval
		RealRootUtil.updateDefinedIntervalIntersecting(funZ, a, b, interval);

		return interval;

	}

	/**
	 * @param a
	 *            start parameter
	 * @param b
	 *            end parameter
	 * @param funX
	 *            x(t)
	 * @param funY
	 *            y(t)
	 * @param funZ
	 *            z(t)
	 * @param fun
	 *            additional function for view for plane
	 * @return an interval within [a, b] where the funX, funY, funZ are defined.
	 * 
	 */
	static public double[] getDefinedInterval(double a, double b,
			UnivariateFunction funX, UnivariateFunction funY, UnivariateFunction funZ,
			UnivariateFunction fun) {

		// compute interval for x(t), y(t), z(t)
		double[] interval = getDefinedInterval(a, b, funX, funY, funZ);

		// compute interval for fun(t) and update interval
		RealRootUtil.updateDefinedIntervalIntersecting(fun, a, b, interval);

		return interval;

	}

	// /////////////////////////////////////
	// PATH
	// /////////////////////////////////////

	@Override
	public boolean isClosedPath() {
		return false;
	}

	@Override
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	@Override
	public void pointChanged(GeoPointND P) {

		// get closest parameter position on curve
		PathParameter pp = P.getPathParameter();
		double t = getClosestParameter(P, pp.t);
		pp.t = t;
		pathChanged(P, false);
	}

	@Override
	public boolean isOnPath(GeoPointND PI, double eps) {

		if (PI.getPath() == this) {
			return true;
		}

		// get closest parameter position on curve
		PathParameter pp = PI.getPathParameter();
		double t = getClosestParameter(PI, pp.t);
		Coords coords = PI.getInhomCoordsInD3();
		boolean onPath = Math.abs(fun[0].value(t) - coords.getX()) <= eps
				&& Math.abs(fun[1].value(t) - coords.getY()) <= eps
				&& Math.abs(fun[2].value(t) - coords.getZ()) <= eps;
		return onPath;
	}

	@Override
	public void pathChanged(GeoPointND PI) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		pathChanged(PI, !getKernel().usePathAndRegionParameters(PI));

	}

	private void pathChanged(GeoPointND P, boolean changePoint) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (changePoint) {
			pointChanged(P);
			return;
		}

		PathParameter pp = P.getPathParameter();
		if (pp.t < startParam) {
			pp.t = startParam;
		} else if (pp.t > endParam) {
			pp.t = endParam;
		}

		// calc point for given parameter
		P.setCoords(evaluateCurve3D(pp.t), false);
	}

	@Override
	public void updateDistanceFunction() {
		if (distFun == null) {
			distFun = new CurveCartesian3DDistanceFunction(this);
		}
	}

	@Override
	protected DistanceFunction createDistanceFunction() {
		return new CurveCartesian3DDistanceFunction(this);
	}

	// /////////////////////////////////////
	// DISTANCE FUNCTION
	// /////////////////////////////////////

	private static class CurveCartesian3DDistanceFunction
			implements DistanceFunction {

		private Coords distCoords;
		private Coords distDirection;

		private GeoCurveCartesian3D curve;

		/**
		 * Creates a function for evaluating squared distance of (px,py) from
		 * curve (px and py must be entered using a setter)
		 * 
		 * @param curve
		 *            curve
		 */
		public CurveCartesian3DDistanceFunction(GeoCurveCartesian3D curve) {
			this.curve = curve;
		}

		/**
		 * Sets the point to be used in the distance function
		 * 
		 * @param p
		 *            point
		 */
		@Override
		public void setDistantPoint(GeoPointND p) {

			if (p.isGeoElement3D()) {
				GeoPoint3D p3D = (GeoPoint3D) p;

				if (p3D.hasWillingCoords()) {
					distCoords = p3D.getWillingCoords();
				} else {
					distCoords = p3D.getInhomCoordsInD3();
				}

				distDirection = p3D.getWillingDirection(); // maybe undefined

			} else {
				distCoords = p.getInhomCoordsInD3();
				distDirection = null;
			}
		}

		/**
		 * Returns the square of the distance between the currently set distance
		 * point and this curve at parameter position t
		 */
		@Override
		public double value(double t) {

			Coords eval = curve.evaluateCurve3D(t);
			if (distDirection == null || !distDirection.isDefined()) {
				return eval.squareDistance3(distCoords);
			}

			return eval.squareDistLine3(distCoords, distDirection);
		}

	}

	// ///////////////////////////////////
	// TRANSLATE
	// ///////////////////////////////////

	@Override
	public void translate(Coords v) {

		// current expressions
		for (int i = 0; i < 3; i++) {
			ExpressionNode expr = fun[i].deepCopy(kernel).getExpression();
			ExpressionNode trans = expr.plus(v.get(i + 1));
			fun[i].setExpression(trans);
		}

	}

	@Override
	public boolean isTranslateable() {
		return true;
	}

	// //////////////////////
	// MIRROR
	// //////////////////////

	@Override
	public void mirror(Coords Q) {
		dilate(new MyDouble(kernel, -1.0), Q);
	}

	@Override
	public void mirror(GeoLineND line) {

		SurfaceTransform.mirror(fun, kernel, line);

	}

	@Override
	public void mirror(GeoCoordSys2D plane) {

		SurfaceTransform.mirror(fun, kernel, plane);

	}

	// //////////////////////
	// DILATE
	// //////////////////////

	@Override
	public void dilate(NumberValue ratio, Coords P) {
		translate(P.mul(-1));
		for (int i = 0; i < 3; i++) {
			ExpressionNode expr = fun[i].deepCopy(kernel).getExpression();
			fun[i].setExpression(new ExpressionNode(kernel, ratio,
					Operation.MULTIPLY, expr));
		}
		translate(P);
	}

	@Override
	public void clearCasEvalMap() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isFunctionInX() {
		return false;
	}

	/*
	 * public GeoVec2D evaluateCurve(double t) { double z =
	 * getFun(2).evaluate(t); if (Double.isNaN(z) || Double.isInfinite(z) ||
	 * !Kernel.isZero(z)){ // won't be visible in 2D view return new
	 * GeoVec2D(this.kernel, Double.NaN, Double.NaN); } return new
	 * GeoVec2D(this.kernel, getFun(0).evaluate(t), getFun(1).evaluate(t)); }
	 */

	@Override
	public double distanceMax(double[] p1, double[] p2) {
		return Math.max(
				Math.max(Math.abs(p1[0] - p2[0]), Math.abs(p1[1] - p2[1])),
				Math.abs(p1[2] - p2[2]));
	}

	/**
	 * eg f(t) for 3D Curve
	 * 
	 * @param t
	 *            parameter
	 * @return 3D Point
	 */
	@Override
	public Geo3DVec evaluateCurve(double t) {
		return new Geo3DVec(this.kernel, getFun(0).value(t), getFun(1).value(t),
				getFun(2).value(t));
	}

	@Override
	protected GeoCurveCartesianND newGeoCurveCartesian(Construction cons1) {
		return new GeoCurveCartesian3D(cons1);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.PARAMETRIC3D;
	}

	@Override
	public UnivariateFunction getUnivariateFunctionX() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UnivariateFunction getUnivariateFunctionY() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Coords pointToCoords(GeoPointND geoPointND) {
		return geoPointND.getInhomCoordsInD3();
	}

}
