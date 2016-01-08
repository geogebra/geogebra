package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.Matrix.Coords3;
import org.geogebra.common.kernel.Matrix.CoordsDouble3;
import org.geogebra.common.kernel.algos.AlgoMacro;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Functional2Var;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.GeoClass;

/**
 * Class for cartesian curves in 3D
 * 
 * @author matthieu
 * 
 */
public class GeoSurfaceCartesian3D extends GeoSurfaceCartesianND implements
		Functional2Var, SurfaceEvaluable, Traceable, CasEvaluableFunction,
		Region {

	/**
	 * empty constructor (for ConstructionDefaults3D)
	 * 
	 * @param c
	 */
	public GeoSurfaceCartesian3D(Construction c) {
		super(c);
	}

	/**
	 * common constructor
	 * 
	 * @param c
	 * @param fun
	 */
	public GeoSurfaceCartesian3D(Construction c, ExpressionNode point,
			FunctionNVar fun[]) {
		super(c, point, fun);
	}

	/**
	 * 
	 * @param curve
	 */
	public GeoSurfaceCartesian3D(GeoSurfaceCartesian3D curve) {
		super(curve.cons);
		set(curve);
	}

	private double[] tmp = new double[2];

	@Override
	public void evaluatePoint(double u, double v, Coords3 p) {
		tmp[0] = u;
		tmp[1] = v;
		p.set(fun[0].evaluate(tmp), fun[1].evaluate(tmp), fun[2].evaluate(tmp));
	}

	private Coords der1 = new Coords(3), der2 = new Coords(3), normal = new Coords(3);
	private CoordsDouble3 p1 = new CoordsDouble3(), p2 = new CoordsDouble3();
	
	private boolean setNormalFromNeighbours(Coords3 p, double u, double v, Coords3 n){
		
		evaluatePoint(u + SurfaceEvaluable.NUMERICAL_DELTA, v, p1);
		if (!p1.isDefined()){
			return false;
		}
		evaluatePoint(u, v + SurfaceEvaluable.NUMERICAL_DELTA, p2);
		if (!p2.isDefined()){
			return false;
		}
		
		der1.setX(p1.x - p.getXd());
		der1.setY(p1.y - p.getYd());
		der1.setZ(p1.z - p.getZd());
		der2.setX(p2.x - p.getXd());
		der2.setY(p2.y - p.getYd());
		der2.setZ(p2.z - p.getZd());
		
		normal.setCrossProduct(der1, der2);
		n.setNormalizedIfPossible(normal);
	
		return true;
	}

	@Override
	public boolean evaluateNormal(Coords3 p, double u, double v, Coords3 n) {
		tmp[0] = u;
		tmp[1] = v;

		double val;
		for (int i = 0; i < 3; i++) {
			val = fun1[0][i].evaluate(tmp);
			if (Double.isNaN(val)){
				return setNormalFromNeighbours(p, u, v, n);
			}
			der1.set(i + 1, val);
			
			val = fun1[1][i].evaluate(tmp);
			if (Double.isNaN(val)){
				return setNormalFromNeighbours(p, u, v, n);
			}
			der2.set(i + 1, val);
		}

		normal.setCrossProduct(der1, der2);
		n.setNormalizedIfPossible(normal);
		
		return true;

	}

	/**
	 * set the jacobian matrix for bivariate newton method
	 * 
	 * @param uv
	 * @param vx
	 * @param vy
	 * @param vz
	 * @param matrix
	 */
	public void setJacobianForBivariate(double[] uv, double vx,
			double vy,
			double vz, CoordMatrix matrix) {

		final double dfxu = fun1[0][0].evaluate(uv);
		final double dfyu = fun1[0][1].evaluate(uv);
		final double dfzu = fun1[0][2].evaluate(uv);
		final double dfxv = fun1[1][0].evaluate(uv);
		final double dfyv = fun1[1][1].evaluate(uv);
		final double dfzv = fun1[1][2].evaluate(uv);

		matrix.set(1, 1, vz * dfyu - vy * dfzu);
		matrix.set(1, 2, vz * dfyv - vy * dfzv);

		matrix.set(2, 1, vx * dfzu - vz * dfxu);
		matrix.set(2, 2, vx * dfzv - vz * dfxv);

	}

	/**
	 * set vector for bivariate newton method
	 * 
	 * @param uv
	 * @param vx
	 * @param vy
	 * @param vz
	 * @param cx
	 * @param cy
	 * @param vector
	 */
	public void setVectorForBivariate(double[] uv, double[] xyz, double vx,
			double vy, double vz, double cx, double cy, double cz, Coords vector) {

		xyz[0] = fun[0].evaluate(uv);
		xyz[1] = fun[1].evaluate(uv);
		xyz[2] = fun[2].evaluate(uv);

		vector.setX(vz * xyz[1] - vy * xyz[2] + cx);
		vector.setY(vx * xyz[2] - vz * xyz[0] + cy);
		vector.setZ(vy * xyz[0] - vx * xyz[1] + cz);
	}

	@Override
	public GeoElement copy() {
		return new GeoSurfaceCartesian3D(this);
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void set(GeoElementND geo) {
		GeoSurfaceCartesian3D geoSurface = (GeoSurfaceCartesian3D) geo;

		fun = new FunctionNVar[3];
		for (int i = 0; i < 3; i++) {
			fun[i] = new FunctionNVar(geoSurface.fun[i], kernel);
			// Application.debug(fun[i].toString());
		}
		
		fun1 = null;
		fun2 = null;

		startParam = geoSurface.startParam;
		endParam = geoSurface.endParam;
		isDefined = geoSurface.isDefined;

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
		return GeoClass.SURFACECARTESIAN3D;
	}

	@Override
	public Coords getLabelPosition() {
		return new Coords(4); // TODO
	}

	@Override
	public Coords getMainDirection() {
		return Coords.VZ; // TODO
	}

	@Override
	public boolean isGeoElement3D() {
		return true;
	}

	/** to be able to fill it with an alpha value */
	@Override
	public boolean isFillable() {
		return true;
	}

	// /////////////////////////
	// FUNCTIONAL2VAR

	@Override
	public Coords evaluatePoint(double u, double v) {
		Coords p = new Coords(3);
		tmp[0] = u;
		tmp[1] = v;
		for (int i = 0; i < 3; i++) {
			p.set(i + 1, fun[i].evaluate(tmp));
		}
		return p;
	}

	/**
	 * evaluate point at parameters u,v
	 * 
	 * @param u
	 *            first parameter
	 * @param v
	 *            second parameter
	 * @param p
	 *            point
	 */
	public void evaluatePoint(double u, double v, Coords p) {
		tmp[0] = u;
		tmp[1] = v;
		for (int i = 0; i < 3; i++) {
			p.set(i + 1, fun[i].evaluate(tmp));
		}
	}

	@Override
	public Coords evaluateNormal(double u, double v) {

		Coords n = new Coords(4);

		tmp[0] = u;
		tmp[1] = v;

		double val;
		for (int i = 0; i < 3; i++) {
			val = fun1[0][i].evaluate(tmp);
			der1.set(i + 1, val);
			
			val = fun1[1][i].evaluate(tmp);
			der2.set(i + 1, val);
		}

		n.setCrossProduct(der1, der2);
		n.normalize();
		return n;
	}

	// /////////////////////////
	// SPECIFIC XML

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		// level of detail
		if (getLevelOfDetail() == LevelOfDetail.QUALITY){
			sb.append("\t<levelOfDetailQuality val=\"true\"/>\n");
		}

	}

	// /////////////////////////
	// LEVEL OF DETAIL

	private LevelOfDetail levelOfDetail = LevelOfDetail.SPEED;

	@Override
	public LevelOfDetail getLevelOfDetail() {
		return levelOfDetail;
	}

	@Override
	public void setLevelOfDetail(LevelOfDetail lod) {
		levelOfDetail = lod;			
	}
	
	@Override
	public boolean hasLevelOfDetail() {
		return true;
	}

	// ////////////////
	// TRACE
	// ////////////////

	private boolean trace;

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
	public void setUsingCasCommand(String ggbCasCmd, CasEvaluableFunction f,
			boolean symbolic, MyArbitraryConstant arbconst) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getVarString(StringTemplate tpl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FunctionVariable[] getFunctionVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearCasEvalMap(String string) {
		// TODO Auto-generated method stub

	}

	public FunctionNVar[] getFunctions() {
		return fun;
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}
	
	
	@Override
	public void setAllVisualPropertiesExceptEuclidianVisible(GeoElement geo, boolean keepAdvanced) {
		super.setAllVisualPropertiesExceptEuclidianVisible(geo, keepAdvanced);

		if (geo.hasLevelOfDetail()) {
			levelOfDetail = ((SurfaceEvaluable) geo).getLevelOfDetail();
		}
	}

	@Override
	public ValueType getValueType() {
		return ValueType.PARAMETRIC3D;
	}

	@Override
	public ExpressionValue evaluateSurface(double u, double v) {
		tmp[0] = u;
		tmp[1] = v;
		return new Geo3DVec(kernel, fun[0].evaluate(tmp), fun[1].evaluate(tmp),
				fun[2].evaluate(tmp));
	}

	@Override
	public boolean isRegion() {
		return isRegion3D();
	}

	@Override
	public boolean isRegion3D() {
		return kernel.getApplication().has(Feature.SURFACE_IS_REGION);
	}

	private double[] xyzuv;

	public void pointChangedForRegion(GeoPointND P) {

		Coords coords = P.getInhomCoordsInD3();

		if (hasLastHitParameters()) {
			RegionParameters rp = P.getRegionParameters();
			rp.setT1(lastHitU);
			rp.setT2(lastHitV);
			Coords c = Coords.createInhomCoorsInD3();
			evaluatePoint(lastHitU, lastHitV, c);
			setDerivatives();
			Coords n = evaluateNormal(lastHitU, lastHitV);
			rp.setNormal(n);
			P.setCoords(c, false);
			P.updateCoords();
			resetLastHitParameters();
			return;
		}

		if (xyzuv == null) {
			xyzuv = new double[5];
		}

		getClosestParameters(coords.getX(), coords.getY(), coords.getZ(), xyzuv);

		RegionParameters rp = P.getRegionParameters();
		rp.setT1(xyzuv[3]);
		rp.setT2(xyzuv[4]);
		Coords n = evaluateNormal(xyzuv[3], xyzuv[4]);
		rp.setNormal(n);
		P.setCoords(new Coords(xyzuv[0], xyzuv[1], xyzuv[2], 1), false);
		P.updateCoords();

		resetLastHitParameters();

	}

	private double[] xyz, xyzDu, xyzDv, xyzDuu, xyzDuv, xyzDvv, xyzDvu, uv;

	private Coords bivariateVector, bivariateDelta;

	private CoordMatrix jacobian;

	private void getClosestParameters(double x0, double y0, double z0,
			double[] xyzuv) {

		// set derivatives if needed
		setSecondDerivatives();

		// create fields if needed
		if (xyz == null) {
			xyz = new double[3];
		}

		if (xyzDu == null) {
			xyzDu = new double[3];
			xyzDv = new double[3];
			xyzDuu = new double[3];
			xyzDuv = new double[3];
			xyzDvu = new double[3];
			xyzDvv = new double[3];
			uv = new double[2];
		}


		if (jacobian == null) {
			jacobian = new CoordMatrix(2, 2);
			bivariateVector = new Coords(3);
			bivariateDelta = new Coords(2);
		}

		// init to no solution
		double dist = Double.POSITIVE_INFINITY;
		xyzuv[0] = Double.NaN;

		// make several tries
		double uMin = getMinParameter(0);
		double uMax = getMaxParameter(0);
		double vMin = getMinParameter(1);
		double vMax = getMaxParameter(1);
		double du = (uMax - uMin) / BIVARIATE_SAMPLES;
		double dv = (vMax - vMin) / BIVARIATE_SAMPLES;
		for (int ui = 0; ui <= BIVARIATE_SAMPLES; ui++) {
			uv[0] = uMin + ui * du;
			for (int vi = 0; vi <= BIVARIATE_SAMPLES; vi++) {
				uv[1] = vMin + vi * dv;
				double error = findBivariate(x0, y0, z0, uv);
				if (!Double.isNaN(error)) {
					// check if the hit point is the closest
					double dx = (xyz[0] - x0);
					double dy = (xyz[1] - y0);
					double dz = (xyz[2] - z0);
					double d = dx * dx + dy * dy + dz * dz;

					if (d < dist) {
						dist = d;
						xyzuv[0] = xyz[0];
						xyzuv[1] = xyz[1];
						xyzuv[2] = xyz[2];
						xyzuv[3] = uv[0];
						xyzuv[4] = uv[1];
					}

				}

			}

		}
	}
	
	private static final int BIVARIATE_JUMPS = 10;
	private static final int BIVARIATE_SAMPLES = 8;

	private double findBivariate(double x0, double y0, double z0, double[] uv) {

		for (int i = 0; i < BIVARIATE_JUMPS; i++) {
			// compare point to current f(u,v) point
			xyz[0] = fun[0].evaluate(uv);
			xyz[1] = fun[1].evaluate(uv);
			xyz[2] = fun[2].evaluate(uv);

			double dx = xyz[0] - x0;
			double dy = xyz[1] - y0;
			double dz = xyz[2] - z0;


			// calculate derivatives values
			xyzDu[0] = fun1[0][0].evaluate(uv);
			xyzDu[1] = fun1[0][1].evaluate(uv);
			xyzDu[2] = fun1[0][2].evaluate(uv);

			xyzDv[0] = fun1[1][0].evaluate(uv);
			xyzDv[1] = fun1[1][1].evaluate(uv);
			xyzDv[2] = fun1[1][2].evaluate(uv);

			xyzDuu[0] = fun2[0][0][0].evaluate(uv);
			xyzDuu[1] = fun2[0][0][1].evaluate(uv);
			xyzDuu[2] = fun2[0][0][2].evaluate(uv);

			xyzDuv[0] = fun2[1][0][0].evaluate(uv);
			xyzDuv[1] = fun2[1][0][1].evaluate(uv);
			xyzDuv[2] = fun2[1][0][2].evaluate(uv);

			xyzDvu[0] = fun2[0][1][0].evaluate(uv);
			xyzDvu[1] = fun2[0][1][1].evaluate(uv);
			xyzDvu[2] = fun2[0][1][2].evaluate(uv);

			xyzDvv[0] = fun2[1][1][0].evaluate(uv);
			xyzDvv[1] = fun2[1][1][1].evaluate(uv);
			xyzDvv[2] = fun2[1][1][2].evaluate(uv);

			// set bivariate vector
			bivariateVector.setX(dx * xyzDu[0] + dy * xyzDu[1] + dz * xyzDu[2]);
			bivariateVector.setY(dx * xyzDv[0] + dy * xyzDv[1] + dz * xyzDv[2]);

			// if bivariate vector is small enough: point found
			double error = bivariateVector.calcSquareNorm();
			if (Kernel.isZero(error)) {
				return error;
			}

			// set jacobian matrix
			double xyzDuDv = xyzDu[0] * xyzDv[0] + xyzDu[1] * xyzDv[1]
					+ xyzDu[2] * xyzDv[2];
			jacobian.set(1, 1, xyzDu[0] * xyzDu[0] + xyzDu[1] * xyzDu[1]
					+ xyzDu[2] * xyzDu[2] + dx * xyzDuu[0] + dy * xyzDuu[1]
					+ dz * xyzDuu[2]);
			jacobian.set(1, 2, xyzDuDv + dx * xyzDuv[0] + dy * xyzDuv[1] + dz
					* xyzDuv[2]);

			jacobian.set(2, 1, xyzDuDv + dx * xyzDvu[0] + dy * xyzDvu[1] + dz
					* xyzDvu[2]);
			jacobian.set(2, 2, xyzDv[0] * xyzDv[0] + xyzDv[1] * xyzDv[1]
					+ xyzDv[2] * xyzDv[2] + dx * xyzDvv[0] + dy * xyzDvv[1]
					+ dz * xyzDvv[2]);

			// solve jacobian
			jacobian.pivotDegenerate(bivariateDelta, bivariateVector);

			// if no solution, dismiss
			if (!bivariateDelta.isDefined()) {
				return Double.NaN;
			}

			// calc new parameters
			uv[0] -= bivariateDelta.getX();
			uv[1] -= bivariateDelta.getY();

			// check bounds
			randomBackInIntervalsIfNeeded(uv);


		}

		return Double.NaN;

	}

	/**
	 * check if parameters u, v are between min/max parameters; if not, replace
	 * by a random number in interval
	 * 
	 * @param uv
	 *            u,v parameters
	 */
	public void randomBackInIntervalsIfNeeded(double[] uv) {
		if (uv[0] > getMaxParameter(0) || uv[0] < getMinParameter(0)) {
			uv[0] = getRandomBetween(getMinParameter(0), getMaxParameter(0));
		}

		if (uv[1] > getMaxParameter(1) || uv[1] < getMinParameter(1)) {
			uv[1] = getRandomBetween(getMinParameter(1), getMaxParameter(1));
		}
	}

	private double getRandomBetween(double a, double b) {
		return a + (b - a) * cons.getApplication().getRandomNumber();
	}

	public void regionChanged(GeoPointND P) {
		pointChangedForRegion(P);
	}

	public boolean isInRegion(GeoPointND P) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInRegion(double x0, double y0) {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * reset last hitted parameters
	 */
	public void resetLastHitParameters() {
		hasLastHitParameters = false;
	}

	private boolean hasLastHitParameters = false;

	private boolean hasLastHitParameters() {
		return hasLastHitParameters;
	}

	private double lastHitU, lastHitV;

	/**
	 * set last hit u,v parameters
	 * 
	 * @param u
	 *            first parameter
	 * @param v
	 *            second parameter
	 */
	public void setLastHitParameters(double u, double v) {
		lastHitU = u;
		lastHitV = v;
		hasLastHitParameters = true;
	}

}
