package geogebra.common.geogebra3D.kernel3D.geos;

import geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.PathMover;
import geogebra.common.kernel.PathMoverGeneric;
import geogebra.common.kernel.PathParameter;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.Matrix.Coords3D;
import geogebra.common.kernel.algos.AlgoMacro;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.Dilateable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.Traceable;
import geogebra.common.kernel.geos.Transformable;
import geogebra.common.kernel.geos.Translateable;
import geogebra.common.kernel.kernelND.CurveEvaluable;
import geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.RotateableND;
import geogebra.common.kernel.optimization.ExtremumFinder;
import geogebra.common.kernel.roots.RealRootFunction;
import geogebra.common.kernel.roots.RealRootUtil;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;

/**
 * Class for cartesian curves in 3D
 * 
 * @author matthieu
 * 
 */
public class GeoCurveCartesian3D extends GeoCurveCartesianND implements
		CurveEvaluable, Traceable, Path,
		RotateableND, Translateable, MirrorableAtPlane, Transformable, Dilateable{


	/**
	 * empty constructor (for ConstructionDefaults3D)
	 * 
	 * @param c
	 */
	public GeoCurveCartesian3D(Construction c) {
		super(c, 3);
	}

	/**
	 * common constructor
	 * 
	 * @param c
	 * @param fun
	 */
	public GeoCurveCartesian3D(Construction c, Function fun[]) {
		super(c, fun);
	}

	/**
	 * 
	 * @param curve
	 */
	public GeoCurveCartesian3D(GeoCurveCartesian3D curve) {
		super(curve.cons, 3);
		set(curve);
	}
	

	@Override
	public Function getFun(int i){
		return fun[i];
	}


	public Coords evaluateTangent(double t) {
		Coords v = new Coords(3);
		for (int i = 0; i < 3; i++)
			v.set(i + 1, funD1[i].evaluate(t));

		return v.normalized();

	}

	public void evaluateCurve(double t, double [] out){
	 
		for (int i = 0; i < 3; i++){
			out[i] = fun[i].evaluate(t);
		}
	}

	public double[] newDoubleArray(){
		return new double[3];
	}

	public Coords evaluateCurve3D(double t) {
		return new Coords(fun[0].evaluate(t), fun[1].evaluate(t),
				fun[2].evaluate(t), 1);
	}

	public Coords3D evaluateTangent3D(double t) {
		return new Coords3D(funD1[0].evaluate(t), funD1[1].evaluate(t),
				funD1[2].evaluate(t), 1).normalize();

	}

	/**
	 * Returns the curvature at the specified point
	 * 
	 * @param t
	 */
	public double evaluateCurvature(double t) {
		Coords D1 = new Coords(3);
		Coords D2 = new Coords(3);

		for (int i = 0; i < 3; i++)
			D1.set(i + 1, funD1[i].evaluate(t));

		for (int i = 0; i < 3; i++)
			D2.set(i + 1, funD2[i].evaluate(t));

		// compute curvature using the formula k = |f'' x f'| / |f'|^3
		Coords cross = D1.crossProduct(D2);
		return cross.norm() / Math.pow(D1.norm(), 3);
	}

	@Override
	public GeoElement copy() {
		return new GeoCurveCartesian3D(this);
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void set(GeoElement geo) {
		
		if (! (geo instanceof GeoCurveCartesianND)){
			return;
		}
		
		GeoCurveCartesianND geoCurve = (GeoCurveCartesianND) geo;

		//fun = new Function[3];
		for (int i = 0; i < 2; i++) {
			fun[i] = new Function(geoCurve.getFun(i), kernel);
			// Application.debug(fun[i].toString());
		}
		if (geoCurve.isGeoElement3D()){
			fun[2] = new Function(geoCurve.getFun(2), kernel);
		}else{ // t -> (x,y,0) 2D curve
			fun[2] = new Function(new ExpressionNode(kernel, 0), new FunctionVariable(kernel, "t"));
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
				for (int i = 0; i < 3; i++)
					algoMacro.initFunction(fun[i]);
			}
		}

		// distFun = new ParametricCurveDistanceFunction(this);
	}

	@Override
	public boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.CURVECARTESIAN3D;
	}




	@Override
	public Coords getLabelPosition() {
		return new Coords(4); // TODO
	}
	


	@Override
	public boolean isGeoElement3D() {
		return true;
	}
	
	

	//////////////////
	// TRACE
	//////////////////

	private boolean trace;	
	
	@Override
	public boolean isTraceable() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}
	
	
	
	

	public void rotate(NumberValue r, GeoPointND S) {

		transform(CoordMatrix4x4.Rotation4x4(r.getDouble(), S.getInhomCoordsInD(3)));
		
	}

	public void rotate(NumberValue r) {

		transform(CoordMatrix4x4.Rotation4x4(r.getDouble()));
		
	}

	public void rotate(NumberValue r, GeoPointND S, GeoDirectionND orientation) {
		
		transform(CoordMatrix4x4.Rotation4x4(orientation.getDirectionInD3().normalized(), r.getDouble(), S.getInhomCoordsInD(3)));
	}
	
	private void transform(CoordMatrix4x4 m) {

		// current expressions
		ExpressionNode[] expr = new ExpressionNode[3]; 
		for (int i = 0; i<3; i++){
			expr[i] = ((Function) fun[i].deepCopy(kernel)).getExpression();
		}

		for(int row = 0; row < 3 ; row++){
			MyDouble[] coeff = new MyDouble[4];
			for (int i = 0; i<4; i++){
				coeff[i] = new MyDouble(kernel, m.get(row+1, i+1));
			}

			ExpressionNode trans = new ExpressionNode(kernel, coeff[3]);
			for (int i = 0; i<3; i++){
				trans = trans.plus(expr[i].multiply(coeff[i]));
			}

			fun[row].setExpression(trans);
		}

	}

	public void rotate(NumberValue r, GeoLineND line) {

		transform(CoordMatrix4x4.Rotation4x4(line.getDirectionInD3().normalized(), r.getDouble(), line.getStartInhomCoords()));
		
	}

	
	
	

	
	
	public double[] getDefinedInterval(double a, double b){
		
		// compute interval for x(t)
		double[] intervalX = RealRootUtil.getDefinedInterval(
				fun[0], a, b);

		// compute interval for y(t) and update interval
		double[] interval2 = RealRootUtil.getDefinedInterval(
				fun[1], a, b);
		
		if (intervalX[0] < interval2[0]){
			intervalX[0] = interval2[0];
		}
		
		if (intervalX[1] > interval2[1]){
			intervalX[1] = interval2[1];
		}

		// compute interval for z(t) and update interval
		interval2 = RealRootUtil.getDefinedInterval(
				fun[2], a, b);
		
		if (intervalX[0] < interval2[0]){
			intervalX[0] = interval2[0];
		}
		
		if (intervalX[1] > interval2[1]){
			intervalX[1] = interval2[1];
		}

		return intervalX;
	}
	
	
	///////////////////////////////////////
	// PATH
	///////////////////////////////////////
	


	public boolean isClosedPath() {
		return false;
	}

	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}
	
	
	public void pointChanged(GeoPointND P) {

		// get closest parameter position on curve
		PathParameter pp = P.getPathParameter();
		double t = getClosestParameter(P, pp.t);
		pp.t = t;
		pathChanged(P,false);
	}

	public boolean isOnPath(GeoPointND PI, double eps) {

		if (PI.getPath() == this)
			return true;

		// get closest parameter position on curve
		PathParameter pp = PI.getPathParameter();
		double t = getClosestParameter(PI, pp.t);
		Coords coords = PI.getInhomCoordsInD(3);
		boolean onPath = Math.abs(fun[0].evaluate(t) - coords.getX()) <= eps
				&& Math.abs(fun[1].evaluate(t) - coords.getY()) <= eps
				&& Math.abs(fun[2].evaluate(t) - coords.getZ()) <= eps;
		return onPath;
	}

	public void pathChanged(GeoPointND PI) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		pathChanged(PI,!getKernel().usePathAndRegionParameters(PI));

	}
	
	private void pathChanged(GeoPointND P,boolean changePoint) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (changePoint) {
			pointChanged(P);
			return;
		}


		PathParameter pp = P.getPathParameter();
		if (pp.t < startParam)
			pp.t = startParam;
		else if (pp.t > endParam)
			pp.t = endParam;

		// calc point for given parameter
		P.setCoords(evaluateCurve3D(pp.t), false);
	}
	
	private CurveCartesian3DDistanceFunction distFun;

	/**
	 * Returns the parameter value t where this curve has minimal distance to
	 * point P.
	 * 
	 * @param startValue
	 *            an interval around startValue is specially investigated
	 * @param P
	 *            point to which the distance is minimized
	 * @return optimal parameter value t
	 */
	public double getClosestParameter(GeoPointND P, double startValue) {
		double startVal = startValue;
		if (distFun == null)
			distFun = new CurveCartesian3DDistanceFunction(this);

		distFun.setDistantPoint(P);

		// check if P is on this curve and has the right path parameter already
		if (P.getPath() == this || true) {
			// point A is on curve c, take its parameter
			PathParameter pp = P.getPathParameter();
			double pathParam = pp.t;
			if (distFun.evaluate(pathParam) < Kernel.MIN_PRECISION
					* Kernel.MIN_PRECISION)
				return pathParam;

			// if we don't have a startValue yet, let's take the path parameter
			// as a guess
			if (Double.isNaN(startVal))
				startVal = pathParam;
		}

		// first sample distFun to find a start intervall for ExtremumFinder
		double step = (endParam - startParam) / CLOSEST_PARAMETER_SAMPLES;
		double minVal = distFun.evaluate(startParam);
		double minParam = startParam;
		double t = startParam;
		for (int i = 0; i < CLOSEST_PARAMETER_SAMPLES; i++) {
			t = t + step;
			double ft = distFun.evaluate(t);
			if (ft < minVal) {
				// found new minimum
				minVal = ft;
				minParam = t;
			}
		}

		// use interval around our minParam found by sampling
		// to find minimum
		// Math.max/min removed and ParametricCurveDistanceFunction modified instead 
		double left = minParam - step; 
		double right = minParam + step;

		ExtremumFinder extFinder = kernel.getExtremumFinder();
		double sampleResult = extFinder.findMinimum(left, right, distFun,
				Kernel.MIN_PRECISION);
		
		sampleResult = adjustRange(sampleResult);

		// if we have a valid startParam we try the interval around it too
		// however, we don't check the same interval again
		if (!Double.isNaN(startVal)
				&& (startVal < left || right < startVal)) {
			
			// Math.max/min removed and ParametricCurveDistanceFunction modified instead 
			left = startVal - step; 
			right = startVal + step;

			double startValResult = extFinder.findMinimum(left, right, distFun,
					Kernel.MIN_PRECISION);
			
			startValResult = adjustRange(startValResult); 
			
			if (distFun.evaluate(startValResult) < distFun
					.evaluate(sampleResult) + Kernel.MIN_PRECISION/2) {
				return startValResult;
			}
		}

		return sampleResult;
	}

	/** 
	 * allow a curve like Curve[sin(t), cos(t), t, 0, 12*2pi] 
	 * to "join up" properly at 0 and 12*2pi 
	 *  
	 * @param startValResult 
	 * @return startValResult adjusted to be in range [startParam, endParam] if it's just outside 
	 */ 
	private double adjustRange(double startValResult) { 
		if (startValResult < startParam) { 
			return startValResult + (endParam - startParam); 
		} 

		if (startValResult > endParam) { 
			return startValResult - (endParam - startParam); 
		} 

		return startValResult; 
	}
	
	
	///////////////////////////////////////
	// DISTANCE FUNCTION
	///////////////////////////////////////


	private class CurveCartesian3DDistanceFunction implements RealRootFunction {
	
		
		private Coords distCoords, distDirection;

		private GeoCurveCartesian3D curve;

		/**
		 * Creates a function for evaluating squared distance of (px,py)
		 * from curve (px and py must be entered using a setter)
		 * @param curve curve
		 */
		public CurveCartesian3DDistanceFunction(GeoCurveCartesian3D curve) {		
			this.curve = curve;
		}

		/**
		 * Sets the point to be used in the distance function 
		 * @param p point
		 */
		public void setDistantPoint(GeoPointND p) {
			
			if (p.isGeoElement3D()){
				GeoPoint3D p3D = (GeoPoint3D) p;
				
				if (p3D.getWillingCoords() != null){
					distCoords = p3D.getWillingCoords();
				}else{
					distCoords = p3D.getInhomCoordsInD(3);
				}
				
				distDirection = p3D.getWillingDirection(); //maybe null
				
				
			}else{
				distCoords = p.getInhomCoordsInD(3);
				distDirection = null;
			}
		}

		/**
		 * Returns the square of the distance between the currently set
		 * distance point and this curve at parameter position t
		 */
		public double evaluate(double t) {

			Coords eval = curve.evaluateCurve3D(t);		
			if (distDirection == null){
				return eval.squareDistance3(distCoords);
			}
			
			return eval.squareDistLine3(distCoords, distDirection);
		}

	}
	
	
	// ///////////////////////////////////
	// TRANSLATE
	// ///////////////////////////////////

	public void translate(Coords v) {
		
		// current expressions
		for (int i = 0; i<3; i++){
			ExpressionNode expr = ((Function) fun[i].deepCopy(kernel)).getExpression();
			ExpressionNode trans = expr.plus(v.get(i+1));
			fun[i].setExpression(trans);
		}

	}

	@Override
	public boolean isTranslateable() {
		return true;
	}
	
	////////////////////////
	// MIRROR
	////////////////////////
	
	public void mirror(Coords Q) {
		dilate(new MyDouble(kernel, -1.0),Q);
	}

	public void mirror(GeoLineND line) {

		transform(CoordMatrix4x4.AxialSymetry(line.getDirectionInD3().normalized(), line.getStartInhomCoords()));

		
	}
	
	
	public void mirror(GeoPlane3D plane) {

		CoordMatrix4x4 m = plane.getCoordSys().getMatrixOrthonormal();
		transform(CoordMatrix4x4.PlaneSymetry(m.getVz(), m.getOrigin()));
		
	}

	
	////////////////////////
	// DILATE
	////////////////////////




	public void dilate(NumberValue ratio, Coords P) {
		translate(P.mul(-1));
		for (int i = 0; i < 3; i++){
			ExpressionNode expr = ((Function) fun[i].deepCopy(kernel))
					.getExpression();
			fun[i].setExpression(new ExpressionNode(kernel, ratio,
					Operation.MULTIPLY, expr));
		}
		translate(P);
	}

}
