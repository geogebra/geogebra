package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.Matrix.Coords3D;
import geogebra.common.kernel.algos.AlgoMacro;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.Traceable;
import geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.RotateableND;
import geogebra.common.plugin.GeoClass;
import geogebra3D.euclidian3D.Drawable3D;

/**
 * Class for cartesian curves in 3D
 * 
 * @author matthieu
 * 
 */
public class GeoCurveCartesian3D extends GeoCurveCartesianND implements
		/*GeoCurveCartesian3DInterface,*/ GeoElement3DInterface, Traceable, RotateableND {

	/** link with drawable3D */
	private Drawable3D drawable3D = null;

	/**
	 * empty constructor (for ConstructionDefaults3D)
	 * 
	 * @param c
	 */
	public GeoCurveCartesian3D(Construction c) {
		super(c);
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
		super(curve.cons);
		set(curve);
	}


	public Coords evaluateTangent(double t) {
		Coords v = new Coords(3);
		for (int i = 0; i < 3; i++)
			v.set(i + 1, funD1[i].evaluate(t));

		return v.normalized();

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
		GeoCurveCartesian3D geoCurve = (GeoCurveCartesian3D) geo;

		fun = new Function[3];
		for (int i = 0; i < 3; i++) {
			fun[i] = new Function(geoCurve.fun[i], kernel);
			// Application.debug(fun[i].toString());
		}

		startParam = geoCurve.startParam;
		endParam = geoCurve.endParam;
		isDefined = geoCurve.isDefined;

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
	public boolean isVector3DValue() {
		return false;
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
	
	
	
	

	public void rotate(NumberValue r, GeoPoint S) {

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
	

}
