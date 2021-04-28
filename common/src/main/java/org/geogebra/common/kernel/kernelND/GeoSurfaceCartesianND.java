package org.geogebra.common.kernel.kernelND;

import org.apache.commons.math3.util.Cloner;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.algos.AlgoMacro;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionExpander;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.DescriptionMode;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

/**
 * Abstract class for cartesian curves in any dimension
 * 
 * @author Mathieu
 *
 */
public abstract class GeoSurfaceCartesianND extends GeoElement
		implements SurfaceEvaluable, VarString, Translateable, Dilateable,
		CasEvaluableFunction {
	protected static final int BIVARIATE_SAMPLES = 8;
	protected static final int BIVARIATE_JUMPS = 10;
	private static final int GRADIENT_JUMPS = 100;

	private static FunctionExpander functionExpander;

	/** coordinates functions */
	protected FunctionNVar[] fun;
	/** derivative functions */
	protected FunctionNVar[][] fun1;
	/** second derivative functions */
	protected FunctionNVar[][][] fun2;
	/** start parameters */
	protected double[] startParam;
	/** end parameters */
	protected double[] endParam;

	/** flag for isDefined() */
	protected boolean isDefined = true;
	private ExpressionNode point;
	protected double[] xyz;
	protected double[] xyzDu;
	protected double[] xyzDv;
	protected double[] xyzDuu;
	protected double[] xyzDuv;
	protected double[] xyzDvv;
	protected double[] xyzDvu;
	protected double[] uv;

	protected Coords bivariateVector;
	protected Coords bivariateDelta;

	protected CoordMatrix jacobian;
	private LevelOfDetail levelOfDetail = LevelOfDetail.SPEED;
	protected FunctionVariable complexVariable;

	/**
	 * common constructor
	 * 
	 * @param c
	 *            construction
	 */
	public GeoSurfaceCartesianND(Construction c) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings
	}

	/**
	 * constructor with functions
	 * 
	 * @param c
	 *            construction
	 * @param point
	 *            point expression
	 * @param fun
	 *            functions
	 */
	public GeoSurfaceCartesianND(Construction c, ExpressionNode point,
			FunctionNVar[] fun) {
		this(c);
		this.fun = fun;
		this.point = point;
	}

	/**
	 * set derivatives (if not already done)
	 */
	@Override
	public void setDerivatives() {

		if (fun1 != null || fun == null) {
			return;
		}

		// set derivatives
		FunctionVariable[] vars = fun[0].getFunctionVariables();

		fun1 = new FunctionNVar[vars.length][];
		for (int j = 0; j < vars.length; j++) {
			fun1[j] = new FunctionNVar[fun.length];
		}

		if (functionExpander == null) {
			functionExpander = new FunctionExpander();
		}
		for (int i = 0; i < fun.length; i++) {
			ExpressionValue ve = fun[i].deepCopy(getKernel())
					.traverse(functionExpander);
			for (int j = 0; j < vars.length; j++) {
				fun1[j][i] = new FunctionNVar(
						ve.derivative(vars[j], getKernel()).wrap(), vars);
			}
		}
	}

	/**
	 * set first and second derivatives (if not already done)
	 */
	public void setSecondDerivatives() {

		if (fun2 != null) {
			return;
		}

		// ensure first derivatives are set
		setDerivatives();

		// set second derivatives
		FunctionVariable[] vars = fun[0].getFunctionVariables();

		fun2 = new FunctionNVar[vars.length][][];
		for (int k = 0; k < vars.length; k++) {
			fun2[k] = new FunctionNVar[vars.length][];
			for (int j = 0; j < vars.length; j++) {
				fun2[k][j] = new FunctionNVar[fun.length];
			}

			if (functionExpander == null) {
				functionExpander = new FunctionExpander();
			}
			for (int i = 0; i < fun.length; i++) {
				ExpressionValue ve = fun1[k][i].deepCopy(getKernel())
						.traverse(functionExpander);
				for (int j = 0; j < vars.length; j++) {
					fun2[k][j][i] = new FunctionNVar(
							ve.derivative(vars[j], getKernel()).wrap(), vars);
					// Log.debug(k + "," + j + "," + i + ": " + fun2[k][j][i]);
				}
			}
		}
	}

	/**
	 * reset derivatives
	 */
	@Override
	public void resetDerivatives() {
		fun1 = null;
		fun2 = null;
	}

	/**
	 * Replaces geo and all its dependent geos in this function's expression by
	 * copies of their values.
	 * 
	 * @param geo
	 *            Element to be replaced
	 */
	@Override
	public void replaceChildrenByValues(GeoElement geo) {
		for (FunctionNVar functionNVar : fun) {
			if (functionNVar != null) {
				functionNVar.replaceChildrenByValues(geo);
			}
		}
	}

	/**
	 * Sets the start and end parameter value of this curve.
	 * 
	 * @param startParam
	 *            start parameter
	 * @param endParam
	 *            end parameter
	 */
	public void setIntervals(double[] startParam, double[] endParam) {
		this.startParam = startParam;
		this.endParam = endParam;
		isDefined = true;

		for (int i = 0; i < startParam.length && isDefined; i++) {
			isDefined = startParam[i] <= endParam[i];
		}
	}

	/**
	 * @param i
	 *            index of parameter
	 * @return the ith start parameter value for this surface (may be
	 *         Double.NEGATIVE_INFINITY)
	 * 
	 */
	@Override
	public double getMinParameter(int i) {
		return startParam[i];
	}

	/**
	 * @param i
	 *            index of parameter
	 * @return the largest possible ith parameter value for this surface (may be
	 *         Double.POSITIVE_INFINITY)
	 * 
	 */
	@Override
	public double getMaxParameter(int i) {
		return endParam[i];
	}

	/**
	 * returns all class-specific xml tags for getXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		// line thickness and type
		// getLineStyleXML(sb);

	}

	@Override
	final public boolean isDefined() {
		return isDefined && fun != null;
	}

	/**
	 * @param defined
	 *            flag to mark as defined/undefined
	 */
	public void setDefined(boolean defined) {
		isDefined = defined;
	}

	@Override
	public void setUndefined() {
		isDefined = false;
	}

	@Override
	public String toString(StringTemplate tpl) {
		if (!isDefined()) {
			return "?";
		}
		StringBuilder sbToString = new StringBuilder(80);

		sbToString.append(label);
		sbToString.append('(');
		if (complexVariable != null) {
			sbToString
					.append(complexVariable.toString(tpl));
		} else {
			sbToString
					.append(fun[0].getFunctionVariables()[0].toString(tpl));
			sbToString.append(',');
			sbToString
					.append(fun[0].getFunctionVariables()[1].toString(tpl));
		}
		sbToString.append(") = ");
		sbToString.append(toValueString(tpl));
		return sbToString.toString();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		if (isDefined()) {
			if (getDefinition() != null) {
				return getDefinition().toString(tpl);
			}
			StringBuilder sbTemp = new StringBuilder(80);
			sbTemp.setLength(0);
			sbTemp.append(tpl.leftBracket());

			for (int i = 0; i < fun.length; i++) {
				sbTemp.append(fun[i].toValueString(tpl));
				if (i < fun.length - 1) {
					sbTemp.append(", ");
				}
			}

			sbTemp.append(tpl.rightBracket());
			return sbTemp.toString();
		}
		return "?";
	}

	/**
	 * @param tpl
	 *            string template
	 * @return symbolic string representation
	 */
	@Override
	public String toSymbolicString(StringTemplate tpl) {
		if (isDefined()) {
			StringBuilder sbTemp = new StringBuilder(80);
			sbTemp.setLength(0);
			sbTemp.append('(');

			for (int i = 0; i < fun.length; i++) {
				sbTemp.append(fun[i].toString(tpl));
				if (i < fun.length - 1) {
					sbTemp.append(", ");
				}
			}

			sbTemp.append(')');
			return sbTemp.toString();
		}
		return "?";
	}

	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		if (isDefined()) {
			StringBuilder sbTemp = new StringBuilder(80);
			if (getDefinition() != null) {
				return getDefinition().toString(tpl);
			}
			if (point == null) {
				sbTemp.append("\\left(\\begin{array}{c}");

				for (int i = 0; i < fun.length; i++) {
					sbTemp.append(fun[i].toLaTeXString(symbolic, tpl));
					if (i < fun.length - 1) {
						sbTemp.append("\\\\");
					}
				}

				sbTemp.append("\\end{array}\\right)");
			} else {
				sbTemp.append(point.toLaTeXString(true, tpl));
			}
			return sbTemp.toString();
		}
		return "?";
	}

	@Override
	public boolean isGeoSurfaceCartesian() {
		return true;
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}

	/**
	 * @return point expression if defined as (f(u,v),g(u,v),h(u,v))
	 */
	public ExpressionNode getPointExpression() {
		return point;
	}

	/**
	 * @param u
	 *            value of first parameter
	 * @param v
	 *            value of second parameter
	 * @return 3D point
	 */
	public ExpressionValue evaluateSurface(double u, double v) {
		// override this in the 3D version
		return null;
	}

	/**
	 * @return whether this is a rotational surface; overridden in 3D
	 */
	public boolean isSurfaceOfRevolutionAroundOx() {
		return false;
	}

	/**
	 * @param startParam
	 *            start parameters
	 */
	public void setStartParameter(double[] startParam) {
		this.startParam = startParam;
	}

	/**
	 * @param endParam
	 *            end parameters
	 */
	public void setEndParameter(double[] endParam) {
		this.endParam = endParam;
	}

	/**
	 * calc closest point to line (x0,y0,z0) (vx,vy,vz) with (uold, vold) start
	 * parameters
	 * 
	 * @param x0
	 *            point x
	 * @param y0
	 *            point y
	 * @param z0
	 *            point z
	 * @param vx
	 *            direction x
	 * @param vy
	 *            direction y
	 * @param vz
	 *            direction z
	 * @param xyzuv1
	 *            output coords
	 * 
	 * @return true if found
	 */
	protected boolean getClosestParameters(double uold, double vold, double x0,
			double y0, double z0, double vx, double vy, double vz,
			double[] xyzuv1) {

		// check (uold,vold) are correct starting parameters
		if (Double.isNaN(uold) || Double.isNaN(vold)) {
			return false;
		}

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

		// init to no solution
		xyzuv1[0] = Double.NaN;

		// make several tries
		uv[0] = uold;
		uv[1] = vold;
		if (findMinimumDistanceGradient(x0, y0, z0, vx, vy, vz, uv)) {
			xyzuv1[0] = xyz[0];
			xyzuv1[1] = xyz[1];
			xyzuv1[2] = xyz[2];
			xyzuv1[3] = uv[0];
			xyzuv1[4] = uv[1];
			// Log.debug(">>> " + xyzuv[0] + "," + xyzuv[1] + "," + xyzuv[2]);
			return true;
		}

		return false;
	}

	// private static final int GRADIENT_SAMPLES = 8;

	private boolean findMinimumDistanceGradient(double x0, double y0, double z0,
			double vx, double vy, double vz, double[] uvOut) {

		for (int i = 0; i < GRADIENT_JUMPS; i++) {
			// calc current f(u,v) point
			xyz[0] = fun[0].evaluate(uvOut);
			xyz[1] = fun[1].evaluate(uvOut);
			xyz[2] = fun[2].evaluate(uvOut);

			// calculate derivatives values
			xyzDu[0] = fun1evaluate(0, 0, uvOut);
			xyzDu[1] = fun1evaluate(0, 1, uvOut);
			xyzDu[2] = fun1evaluate(0, 2, uvOut);

			xyzDv[0] = fun1evaluate(1, 0, uvOut);
			xyzDv[1] = fun1evaluate(1, 1, uvOut);
			xyzDv[2] = fun1evaluate(1, 2, uvOut);

			xyzDuu[0] = fun2evaluate(0, 0, 0, uvOut);
			xyzDuu[1] = fun2evaluate(0, 0, 1, uvOut);
			xyzDuu[2] = fun2evaluate(0, 0, 2, uvOut);

			xyzDuv[0] = fun2evaluate(1, 0, 0, uvOut);
			xyzDuv[1] = fun2evaluate(1, 0, 1, uvOut);
			xyzDuv[2] = fun2evaluate(1, 0, 2, uvOut);

			xyzDvu[0] = fun2evaluate(0, 1, 0, uvOut);
			xyzDvu[1] = fun2evaluate(0, 1, 1, uvOut);
			xyzDvu[2] = fun2evaluate(0, 1, 2, uvOut);

			xyzDvv[0] = fun2evaluate(1, 1, 0, uvOut);
			xyzDvv[1] = fun2evaluate(1, 1, 1, uvOut);
			xyzDvv[2] = fun2evaluate(1, 1, 2, uvOut);

			// we want to minimize (x,y,z)-to-line distance,
			// i.e. norm of vector:
			// (xyz[2] - z0) * vx - (xyz[0] - x0) * vz;
			// (xyz[0] - x0) * vy - (xyz[1] - y0) * vx;
			// (xyz[1] - y0) * vz - (xyz[2] - z0) * vy;

			// help values
			double nx = (xyz[2] - z0) * vx - (xyz[0] - x0) * vz;
			double ny = (xyz[0] - x0) * vy - (xyz[1] - y0) * vx;
			double nz = (xyz[1] - y0) * vz - (xyz[2] - z0) * vy;
			double nxDu = xyzDu[2] * vx - xyzDu[0] * vz;
			double nyDu = xyzDu[0] * vy - xyzDu[1] * vx;
			double nzDu = xyzDu[1] * vz - xyzDu[2] * vy;
			double nxDv = xyzDv[2] * vx - xyzDv[0] * vz;
			double nyDv = xyzDv[0] * vy - xyzDv[1] * vx;
			double nzDv = xyzDv[1] * vz - xyzDv[2] * vy;

			// calc gradient /2
			double gu = nxDu * nx // nx
					+ nyDu * ny // ny
					+ nzDu * nz; // nz
			double gv = nxDv * nx // nx
					+ nyDv * ny // ny
					+ nzDv * nz; // nz

			// calc Hessien /2
			double huu = (xyzDuu[2] * vx - xyzDuu[0] * vz) * nx // nx
					+ (xyzDuu[0] * vy - xyzDuu[1] * vx) * ny // ny
					+ (xyzDuu[1] * vz - xyzDuu[2] * vy) * nz // nz
					+ nxDu * nxDu // nx
					+ nyDu * nyDu // ny
					+ nzDu * nzDu; // nz
			double huv = (xyzDuv[2] * vx - xyzDuv[0] * vz) * nx // nx
					+ (xyzDuv[0] * vy - xyzDuv[1] * vx) * ny // ny
					+ (xyzDuv[1] * vz - xyzDuv[2] * vy) * nz // nz
					+ nxDu * nxDv // nx
					+ nyDu * nyDv // ny
					+ nzDu * nzDv; // nz
			double hvv = (xyzDvv[2] * vx - xyzDvv[0] * vz) * nx // nx
					+ (xyzDvv[0] * vy - xyzDvv[1] * vx) * ny // ny
					+ (xyzDvv[1] * vz - xyzDvv[2] * vy) * nz // nz
					+ nxDv * nxDv // nx
					+ nxDv * nyDv // ny
					+ nxDv * nzDv; // nz
			double hvu = (xyzDvu[2] * vx - xyzDvu[0] * vz) * nx // nx
					+ (xyzDvu[0] * vy - xyzDvu[1] * vx) * ny // ny
					+ (xyzDvu[1] * vz - xyzDvu[2] * vy) * nz // nz
					+ nxDu * nxDv // nx
					+ nyDu * nyDv // ny
					+ nzDu * nzDv; // nz

			// Hessien * gradient
			double Hgu = huu * gu + hvu * gv;
			double Hgv = huv * gu + hvv * gv;

			// best step: gradient*gradient/(gradient * (Hessien * gradient))
			double gnorm = gu * gu + gv * gv;
			double d = gnorm / (2 * (gu * Hgu + gv * Hgv));

			// new u,v
			double du = d * gu;
			double dv = d * gv;
			uvOut[0] -= du;
			uvOut[1] -= dv;

			// back to interval if needed
			if (uvOut[0] < getMinParameter(0)) {
				uvOut[0] = getMinParameter(0);
			} else if (uvOut[0] > getMaxParameter(0)) {
				uvOut[0] = getMaxParameter(0);
			}
			if (uvOut[1] < getMinParameter(1)) {
				uvOut[1] = getMinParameter(1);
			} else if (uvOut[1] > getMaxParameter(1)) {
				uvOut[1] = getMaxParameter(1);
			}

			if (DoubleUtil.isZero(gnorm)) {
				return true;
			}

		}

		return false;

	}

	/**
	 * @param x0
	 *            point x
	 * @param y0
	 *            point y
	 * @param z0
	 *            point z
	 * @param xzyzuvOut
	 *            (x,y,z,u,v) where x,y,z are coords of closest point and u,v
	 *            are parameters
	 */
	protected void getClosestParameters(double x0, double y0, double z0,
			double[] xzyzuvOut) {

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
		xzyzuvOut[0] = Double.NaN;

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
				double error = findBivariateNormalZero(x0, y0, z0, uv);
				if (!Double.isNaN(error)) {
					// check if the hit point is the closest
					double dx = (xyz[0] - x0);
					double dy = (xyz[1] - y0);
					double dz = (xyz[2] - z0);
					double d = dx * dx + dy * dy + dz * dz;

					if (d < dist) {
						dist = d;
						xzyzuvOut[0] = xyz[0];
						xzyzuvOut[1] = xyz[1];
						xzyzuvOut[2] = xyz[2];
						xzyzuvOut[3] = uv[0];
						xzyzuvOut[4] = uv[1];
					}
				}
			}
		}
	}

	private double findBivariateNormalZero(double x0, double y0, double z0,
			double[] uvOut) {

		for (int i = 0; i < BIVARIATE_JUMPS; i++) {
			// compare point to current f(u,v) point
			xyz[0] = fun[0].evaluate(uvOut);
			xyz[1] = fun[1].evaluate(uvOut);
			xyz[2] = fun[2].evaluate(uvOut);

			// calculate derivatives values
			xyzDu[0] = fun1evaluate(0, 0, uvOut);
			xyzDu[1] = fun1evaluate(0, 1, uvOut);
			xyzDu[2] = fun1evaluate(0, 2, uvOut);

			xyzDv[0] = fun1evaluate(1, 0, uvOut);
			xyzDv[1] = fun1evaluate(1, 1, uvOut);
			xyzDv[2] = fun1evaluate(1, 2, uvOut);

			xyzDuu[0] = fun2evaluate(0, 0, 0, uvOut);
			xyzDuu[1] = fun2evaluate(0, 0, 1, uvOut);
			xyzDuu[2] = fun2evaluate(0, 0, 2, uvOut);

			xyzDuv[0] = fun2evaluate(1, 0, 0, uvOut);
			xyzDuv[1] = fun2evaluate(1, 0, 1, uvOut);
			xyzDuv[2] = fun2evaluate(1, 0, 2, uvOut);

			xyzDvu[0] = fun2evaluate(0, 1, 0, uvOut);
			xyzDvu[1] = fun2evaluate(0, 1, 1, uvOut);
			xyzDvu[2] = fun2evaluate(0, 1, 2, uvOut);

			xyzDvv[0] = fun2evaluate(1, 1, 0, uvOut);
			xyzDvv[1] = fun2evaluate(1, 1, 1, uvOut);
			xyzDvv[2] = fun2evaluate(1, 1, 2, uvOut);

			// set bivariate vector
			double dx = xyz[0] - x0;
			double dy = xyz[1] - y0;
			double dz = xyz[2] - z0;
			bivariateVector.setX(dx * xyzDu[0] + dy * xyzDu[1] + dz * xyzDu[2]);
			bivariateVector.setY(dx * xyzDv[0] + dy * xyzDv[1] + dz * xyzDv[2]);

			// if bivariate vector is small enough: point found
			double error = bivariateVector.calcSquareNorm();
			if (DoubleUtil.isZero(error)) {
				return error;
			}

			// set jacobian matrix
			double xyzDuDv = xyzDu[0] * xyzDv[0] + xyzDu[1] * xyzDv[1]
					+ xyzDu[2] * xyzDv[2];
			jacobian.set(1, 1,
					xyzDu[0] * xyzDu[0] + xyzDu[1] * xyzDu[1]
							+ xyzDu[2] * xyzDu[2] + dx * xyzDuu[0]
							+ dy * xyzDuu[1] + dz * xyzDuu[2]);
			jacobian.set(1, 2,
					xyzDuDv + dx * xyzDuv[0] + dy * xyzDuv[1] + dz * xyzDuv[2]);

			jacobian.set(2, 1,
					xyzDuDv + dx * xyzDvu[0] + dy * xyzDvu[1] + dz * xyzDvu[2]);
			jacobian.set(2, 2,
					xyzDv[0] * xyzDv[0] + xyzDv[1] * xyzDv[1]
							+ xyzDv[2] * xyzDv[2] + dx * xyzDvv[0]
							+ dy * xyzDvv[1] + dz * xyzDvv[2]);

			// solve jacobian
			jacobian.pivotDegenerate(bivariateDelta, bivariateVector);

			// if no solution, dismiss
			if (!bivariateDelta.isDefined()) {
				return Double.NaN;
			}

			// calc new parameters
			uvOut[0] -= bivariateDelta.getX();
			uvOut[1] -= bivariateDelta.getY();

			// check bounds
			randomBackInIntervalsIfNeeded(uvOut);

		}

		return Double.NaN;

	}

	/**
	 * check if parameters u, v are between min/max parameters; if not, replace
	 * by a random number in interval
	 * 
	 * @param uvInOut
	 *            u,v parameters
	 */
	public void randomBackInIntervalsIfNeeded(double[] uvInOut) {
		if (uvInOut[0] > getMaxParameter(0)
				|| uvInOut[0] < getMinParameter(0)) {
			uvInOut[0] = getRandomBetween(getMinParameter(0),
					getMaxParameter(0));
		}

		if (uvInOut[1] > getMaxParameter(1)
				|| uvInOut[1] < getMinParameter(1)) {
			uvInOut[1] = getRandomBetween(getMinParameter(1),
					getMaxParameter(1));
		}
	}

	private double getRandomBetween(double a, double b) {
		return a + (b - a) * cons.getApplication().getRandomNumber();
	}

	/**
	 * @param i
	 *            coordinate index
	 * @param j
	 *            derivative variable index
	 * @param k
	 *            derivative variable index
	 * @param d
	 *            variable values
	 * @return second derivative
	 */
	protected double fun2evaluate(int i, int j, int k, double[] d) {
		return fun2[i][j][k].evaluate(d);
	}

	/**
	 * @param i
	 *            coordinate index
	 * @param j
	 *            derivative variable index
	 * @param d
	 *            variable values
	 * @return first derivative
	 */
	protected double fun1evaluate(int i, int j, double[] d) {
		return fun1[i][j].evaluate(d);
	}

	/**
	 * Mirror in point.
	 * 
	 * @param Q
	 *            center
	 */
	public void mirror(Coords Q) {
		dilate(new MyDouble(kernel, -1.0), Q);
	}

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
	public void translate(Coords v) {
		// current expressions
		for (int i = 0; i < 3; i++) {
			ExpressionNode expr = fun[i].deepCopy(kernel).getExpression();
			ExpressionNode trans = expr.plus(v.get(i + 1));
			fun[i].setExpression(trans);
		}
	}

	@Override
	public void setUsingCasCommand(String ggbCasCmd, CasEvaluableFunction f,
			boolean symbolic, MyArbitraryConstant arbconst) {
		GeoSurfaceCartesianND c = (GeoSurfaceCartesianND) f;

		if (c.getDefinition() != null) {
			FunctionNVar transformed = new Function(c.getDefinition(), complexVariable)
					.evalCasCommand(ggbCasCmd, symbolic, arbconst);
			setDefinition(transformed.getFunctionExpression());
		}
	}

	@Override
	public String getVarString(StringTemplate tpl) {
		if (complexVariable != null) {
			return complexVariable.toString(tpl);
		}
		return fun[0].getVarString(tpl);
	}

	@Override
	public FunctionVariable[] getFunctionVariables() {
		if (complexVariable != null) {
			return new FunctionVariable[]{complexVariable};
		}
		return fun[0].getFunctionVariables();
	}

	@Override
	public void clearCasEvalMap() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return cartesian coords as functions
	 */
	public FunctionNVar[] getFunctions() {
		return fun;
	}

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

	@Override
	public void set(GeoElementND geo) {
		if (!geo.isGeoSurfaceCartesian()) {
			setUndefined();
			return;
		}
		GeoSurfaceCartesianND geoSurface = (GeoSurfaceCartesianND) geo;
		int dim = this.isGeoElement3D() ? 3 : 2;
		fun = new FunctionNVar[dim];
		for (int i = 0; i < dim; i++) {
			fun[i] = new FunctionNVar(geoSurface.fun[i], kernel);
		}

		fun1 = null;
		fun2 = null;

		startParam = Cloner.clone(geoSurface.startParam);
		endParam = Cloner.clone(geoSurface.endParam);
		isDefined = geoSurface.isDefined;

		// macro OUTPUT
		if (geo.getConstruction() != cons && isAlgoMacroOutput()) {
			if (!geo.isIndependent()) {
				// this object is an output object of AlgoMacro
				// we need to check the references to all geos in its function's
				// expression
				AlgoMacro algoMacro = (AlgoMacro) getParentAlgorithm();
				for (int i = 0; i < dim; i++) {
					algoMacro.initFunction(fun[i]);
				}
			}
		}
		if (geoSurface.point != point) {
			point = geoSurface.point.deepCopy(kernel);
		}
		if (geoSurface.complexVariable != null) {
			complexVariable = geoSurface.complexVariable;
			if (geoSurface.isDefined && geoSurface.getDefinition() != null) {
				setDefinition(geoSurface.getDefinition().deepCopy(kernel));
			}
		}
	}

	public void setComplexVariable(FunctionVariable fv) {
		complexVariable = fv;
	}

	@Override
	public boolean isEqual(GeoElementND other) {
		if (other instanceof GeoSurfaceCartesianND) {
			GeoSurfaceCartesianND otherSurface = (GeoSurfaceCartesianND) other;
			if (point != null && otherSurface.point != null) {
				return isDifferenceZeroInCAS(other);
			}
		}
		return false;
	}

	@Override
	public String getAssignmentLHS(StringTemplate tpl) {
		if (complexVariable != null) {
			return tpl.printVariableName(label) + tpl.leftBracket()
					+ getVarString(tpl) + tpl.rightBracket();
		}
		return super.getAssignmentLHS(tpl);
	}

	@Override
	public String getTypeString() {
		if (complexVariable != null) {
			return "ComplexFunction";
		}
		return super.getTypeString();
	}

	@Override
	public DescriptionMode getDescriptionMode() {
		if (complexVariable != null) {
			return DescriptionMode.DEFINITION;
		}
		return super.getDescriptionMode();
	}

	@Override
	public boolean isMoveable() { // to make SetValue work
		return getDefinition() != null
				&& !getDefinition().inspect(Inspecting.dynamicGeosFinder);
	}

	public FunctionVariable getComplexVariable() {
		return complexVariable;
	}
}
