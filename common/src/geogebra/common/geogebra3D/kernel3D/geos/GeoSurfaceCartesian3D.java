package geogebra.common.geogebra3D.kernel3D.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoMacro;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.Functional2Var;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.Traceable;
import geogebra.common.kernel.kernelND.GeoLevelOfDetail;
import geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import geogebra.common.kernel.kernelND.LevelOfDetail;
import geogebra.common.kernel.kernelND.SurfaceEvaluable;
import geogebra.common.plugin.GeoClass;

/**
 * Class for cartesian curves in 3D
 * 
 * @author matthieu
 * 
 */
public class GeoSurfaceCartesian3D extends GeoSurfaceCartesianND implements
		Functional2Var, SurfaceEvaluable, GeoLevelOfDetail, Traceable,
		CasEvaluableFunction {

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
	 * @param fun1
	 */
	public GeoSurfaceCartesian3D(Construction c, FunctionNVar fun[], FunctionNVar[][] fun1) {
		super(c, fun, fun1);
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

	private void evaluateSurface(double u, double v, Coords p) {
		tmp[0] = u;
		tmp[1] = v;
		evaluateSurface(tmp, p);
	}

	public void evaluatePoint(double u, double v, Coords p) {
		evaluateSurface(u, v, p);
	}

	private Coords der1 = new Coords(3), der2 = new Coords(3);

	public void evaluateNormal(double u, double v, Coords normal) {
		tmp[0] = u;
		tmp[1] = v;

		for (int i = 0; i < 3; i++) {
			der1.set(i + 1, fun1[0][i].evaluate(tmp));
			der2.set(i + 1, fun1[1][i].evaluate(tmp));
		}

		normal.setCrossProduct(der1, der2);

	}

	private void evaluateSurface(double[] uv, Coords p) {
		for (int i = 0; i < 3; i++) {
			p.set(i + 1, fun[i].evaluate(uv));
		}

	}

	/*
	 * public Coords3D evaluateCurve3D(double t){ return new
	 * Coords3D(fun[0].evaluate(t),fun[1].evaluate(t),fun[2].evaluate(t),1); }
	 * 
	 * public Coords3D evaluateTangent3D(double t){ return new Coords3D(
	 * funD1[0].evaluate(t),funD1[1].evaluate(t),
	 * funD1[2].evaluate(t),1).normalize();
	 * 
	 * }
	 */

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
	public void set(GeoElement geo) {
		GeoSurfaceCartesian3D geoSurface = (GeoSurfaceCartesian3D) geo;

		fun = new FunctionNVar[3];
		for (int i = 0; i < 3; i++) {
			fun[i] = new FunctionNVar(geoSurface.fun[i], kernel);
			// Application.debug(fun[i].toString());
		}

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

	public Coords evaluatePoint(double u, double v) {
		Coords p = new Coords(3);
		evaluateSurface(u, v, p);
		return p;
	}

	public Coords evaluateNormal(double u, double v) {
		return new Coords(0, 0, 1, 0); // TODO
	}

	// /////////////////////////
	// SPECIFIC XML

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		// level of detail
		sb.append("\t<levelOfDetail val=\"");
		sb.append(getLevelOfDetail().getValue());
		sb.append("\"/>\n");

	}

	// /////////////////////////
	// LEVEL OF DETAIL

	private LevelOfDetail levelOfDetail = new LevelOfDetail();

	public LevelOfDetail getLevelOfDetail() {
		return levelOfDetail;
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

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

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

}
