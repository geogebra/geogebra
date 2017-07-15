package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionExpander;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.plugin.Operation;

/**
 * Abstract class for cartesian curves in any dimension
 * 
 * @author Mathieu
 *
 */
public abstract class GeoSurfaceCartesianND extends GeoElement
		implements SurfaceEvaluable, VarString, Translateable, Dilateable {
	protected static final int BIVARIATE_SAMPLES = 8;
	protected static final int BIVARIATE_JUMPS = 10;
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
	protected double[] xyz, xyzDu, xyzDv, xyzDuu, xyzDuv, xyzDvv, xyzDvu, uv;

	protected Coords bivariateVector, bivariateDelta;

	protected CoordMatrix jacobian;

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

	private static FunctionExpander functionExpander;

	/**
	 * Replaces geo and all its dependent geos in this function's expression by
	 * copies of their values.
	 * 
	 * @param geo
	 *            Element to be replaced
	 */
	public void replaceChildrenByValues(GeoElement geo) {

		for (int i = 0; i < fun.length; i++) {
			if (fun[i] != null) {
				fun[i].replaceChildrenByValues(geo);
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
	public void setIntervals(double[] startParam, double endParam[]) {

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
		StringBuilder sbToString = new StringBuilder(80);

		sbToString.setLength(0);
		if (isLabelSet()) {
			sbToString.append(label);
			if (fun != null) {
				sbToString.append('(');
				sbToString
						.append(fun[0].getFunctionVariables()[0].toString(tpl));
				sbToString.append(',');
				sbToString
						.append(fun[0].getFunctionVariables()[1].toString(tpl));
				sbToString.append(") = ");
			}
		}
		sbToString.append(toValueString(tpl));
		return sbToString.toString();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		if (isDefined()) {
			StringBuilder sbTemp = new StringBuilder(80);

			sbTemp.setLength(0);
			sbTemp.append('(');

			for (int i = 0; i < fun.length; i++) {
				sbTemp.append(fun[i].toValueString(tpl));
				if (i < fun.length - 1) {
					sbTemp.append(", ");
				}
			}

			sbTemp.append(')');
			return sbTemp.toString();
		}
		return "?";
	}

	/**
	 * @param tpl
	 *            string template
	 * @return symbolic string representation
	 */
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
				sbTemp.append(point.toLaTeXString(symbolic, tpl));
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

			double dx = xyz[0] - x0;
			double dy = xyz[1] - y0;
			double dz = xyz[2] - z0;

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

	protected double fun2evaluate(int i, int j, int k, double[] d) {
		return fun2[i][j][k].evaluate(d);
	}

	protected double fun1evaluate(int i, int j, double[] d) {
		return fun1[i][j].evaluate(d);
	}

	public void mirror(Coords Q) {
		dilate(new MyDouble(kernel, -1.0), Q);
	}

	public void dilate(NumberValue ratio, Coords P) {
		translate(P.mul(-1));
		for (int i = 0; i < 3; i++) {
			ExpressionNode expr = fun[i].deepCopy(kernel).getExpression();
			fun[i].setExpression(new ExpressionNode(kernel, ratio,
					Operation.MULTIPLY, expr));
		}
		translate(P);

	}

	public void translate(Coords v) {

		// current expressions
		for (int i = 0; i < 3; i++) {
			ExpressionNode expr = fun[i].deepCopy(kernel).getExpression();
			ExpressionNode trans = expr.plus(v.get(i + 1));
			fun[i].setExpression(trans);
		}

	}

}
