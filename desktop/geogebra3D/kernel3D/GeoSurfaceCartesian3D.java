package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoMacro;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.Functional2Var;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLevelOfDetail;
import geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import geogebra.common.kernel.kernelND.LevelOfDetail;
import geogebra.common.kernel.kernelND.SurfaceEvaluable;
import geogebra.common.plugin.GeoClass;
import geogebra3D.euclidian3D.Drawable3D;

/**
 * Class for cartesian curves in 3D
 * 
 * @author matthieu
 * 
 */
public class GeoSurfaceCartesian3D extends GeoSurfaceCartesianND implements
		GeoElement3DInterface, Functional2Var, SurfaceEvaluable, GeoLevelOfDetail {

	/** link with drawable3D */
	private Drawable3D drawable3D = null;

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

	public Coords evaluateSurface(double u, double v) {
		return evaluateSurface(new double[] { u, v });
	}

	public Coords evaluateSurface(double[] uv) {
		Coords p = new Coords(3);
		for (int i = 0; i < 3; i++)
			p.set(i + 1, fun[i].evaluate(uv));

		return p;
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

		fun = new Function[3];
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

	public Drawable3D getDrawable3D() {

		return drawable3D;
	}


	public GeoElement getGeoElement2D() {
		return null;
	}

	@Override
	public Coords getLabelPosition() {
		return new Coords(4); // TODO
	}

	@Override
	public Coords getMainDirection() {
		return null;
	}

	public boolean hasGeoElement2D() {
		return false;
	}

	public void setDrawable3D(Drawable3D d) {

		drawable3D = d;

	}

	public void setGeoElement2D(GeoElement geo) {

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
		return evaluateSurface(u, v);
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

}
