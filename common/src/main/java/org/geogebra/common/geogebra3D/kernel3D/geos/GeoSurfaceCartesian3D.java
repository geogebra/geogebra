package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.Matrix.Coords3;
import org.geogebra.common.kernel.Matrix.CoordsDouble3;
import org.geogebra.common.kernel.algos.AlgoMacro;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Functional2Var;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.plugin.GeoClass;

/**
 * Class for cartesian curves in 3D
 * 
 * @author matthieu
 * 
 */
public class GeoSurfaceCartesian3D extends GeoSurfaceCartesianND implements
		Functional2Var, SurfaceEvaluable, Traceable,
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
	 */
	public GeoSurfaceCartesian3D(Construction c, FunctionNVar fun[]) {
		super(c, fun);
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
		
		fun1 = null;


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
		tmp[0] = u;
		tmp[1] = v;
		for (int i = 0; i < 3; i++) {
			p.set(i + 1, fun[i].evaluate(tmp));
		}
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
		if (getLevelOfDetail() == LevelOfDetail.QUALITY){
			sb.append("\t<levelOfDetailQuality val=\"true\"/>\n");
		}

	}

	// /////////////////////////
	// LEVEL OF DETAIL

	private LevelOfDetail levelOfDetail = LevelOfDetail.SPEED;

	public LevelOfDetail getLevelOfDetail() {
		return levelOfDetail;
	}

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
	
	
	@Override
	public void setAllVisualPropertiesExceptEuclidianVisible(GeoElement geo, boolean keepAdvanced) {
		super.setAllVisualPropertiesExceptEuclidianVisible(geo, keepAdvanced);

		if (geo.hasLevelOfDetail()) {
			levelOfDetail = ((SurfaceEvaluable) geo).getLevelOfDetail();
		}
	}


}
