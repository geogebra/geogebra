package org.geogebra.common.kernel.kernelND;

import java.util.TreeMap;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.DistanceFunction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ParametricCurveDistanceFunction;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.optimization.ExtremumFinderI;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * Abstract class for cartesian curves in any dimension
 * 
 * @author Mathieu
 *
 */
public abstract class GeoCurveCartesianND extends GeoElement
		implements ParametricCurve, VarString, CasEvaluableFunction {

	/**
	 * samples to find interval with closest parameter position to given point
	 */
	protected static final int CLOSEST_PARAMETER_SAMPLES = 100;

	/** coordinates functions */
	protected final Function[] fun;
	/** coordinates with expanded function references */
	protected final Function[] funExpanded;
	/** flag for each coordinate whether it depends on a function */
	protected final boolean[] containsFunctions;
	/** derivative functions */
	protected Function[] funD1;
	/** second derivative functions */
	protected Function[] funD2;
	/** start parameter */
	protected double startParam;
	/** end parameter */
	protected double endParam;

	/** flag for isDefined() */
	protected boolean isDefined = true;
	/**
	 * distFun(t) evaluates distance this(t) to a distant point (attribute of
	 * the distFun)
	 */
	protected DistanceFunction distFun;

	private ExpressionNode point;
	/** derivative */
	protected GeoCurveCartesianND derivGeoFun;
	private boolean hideRangeInFormula;

	/**
	 * common constructor
	 * 
	 * @param c
	 *            construction
	 * @param dimension
	 *            dimension 2 or 3
	 * @param point
	 *            defining expression as point
	 */
	public GeoCurveCartesianND(Construction c, int dimension,
			ExpressionNode point) {
		super(c);
		this.fun = new Function[dimension];
		this.funExpanded = new Function[dimension];
		this.containsFunctions = new boolean[dimension];
		this.point = point;
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
	 * @param fun
	 *            functions of parameter
	 * @param point
	 *            defining expression as point
	 */
	public GeoCurveCartesianND(Construction c, Function[] fun,
			ExpressionNode point) {
		super(c);

		this.fun = fun;
		this.funExpanded = new Function[fun.length];
		this.containsFunctions = new boolean[fun.length];
		this.point = point;
		setConstructionDefaults();
	}

	/**
	 * set functions
	 * 
	 * @param fun
	 *            functions
	 */
	public void setFun(Function[] fun) {
		for (int i = 0; i < fun.length; i++) {
			if (this.fun[i] == null) {
				this.fun[i] = fun[i];
			} else {
				this.fun[i].setExpression(fun[i].getExpression());
			}
		}
	}

	@Override
	public boolean isGeoCurveCartesian() {
		return true;
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
	public void setInterval(double startParam, double endParam) {

		this.startParam = startParam;
		this.endParam = endParam;

		isDefined = startParam <= endParam;
	}

	/**
	 * Returns the start parameter value for this path (may be
	 * Double.NEGATIVE_INFINITY)
	 * 
	 * @return start parameter
	 */
	@Override
	public double getMinParameter() {
		return startParam;
	}

	/**
	 * Returns the largest possible parameter value for this path (may be
	 * Double.POSITIVE_INFINITY)
	 * 
	 * @return end parameter
	 */
	@Override
	public double getMaxParameter() {
		return endParam;
	}

	/**
	 * returns all class-specific xml tags for getXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		// line thickness and type
		getLineStyleXML(sb);

	}

	@Override
	public boolean isPath() {
		return true;
	}

	@Override
	final public boolean isDefined() {
		return isDefined && getFun(0) != null;
	}

	/**
	 * @param defined
	 *            new value of defined flag
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
			// sbToString.append('(');
			// sbToString.append(funX.getVarString());
			// sbToString.append(") = ");
			// changed to ':' to make LaTeX output better
			sbToString.append(':');
		}
		sbToString.append(toValueString(tpl));
		return sbToString.toString();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		if (isDefined) {
			StringBuilder sbTemp = new StringBuilder(80);

			sbTemp.setLength(0);

			// needed for c*c -> dot product in CAS
			if (tpl.hasCASType()) {
				sbTemp.append("point(");
			}
			sbTemp.append('(');

			for (int i = 0; i < fun.length; i++) {

				// quick fix for NPE
				// https://play.google.com/apps/publish/?dev_acc=05873811091523087820#ErrorClusterDetailsPlace:p=org.geogebra.android.g3d&et=CRASH&sh=false&lr=LAST_7_DAYS&ecn=java.lang.NullPointerException&tf=SourceFile&tc=%2509at+org.geogebra.common.kernel.kernelND.GeoCurveCartesianND.toValueString(GeoCurveCartesianND.java:239)&tm=b&nid&an&c&s=new_status_desc&ed=1477717276985
				if (fun[i] != null) {
					sbTemp.append(fun[i].toValueString(tpl));
					if (i < fun.length - 1) {
						sbTemp.append(", ");
					}
				}
			}

			if (tpl.hasCASType()) {
				sbTemp.append(')');
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
	@Override
	public String toSymbolicString(StringTemplate tpl) {
		StringBuilder sbTemp = null;
		if (isDefined) {
			sbTemp = new StringBuilder(80);

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

	/**
	 * @param i
	 *            dimension index
	 * @return i-th function
	 */
	@Override
	public Function getFun(int i) {
		if (i >= fun.length) {
			return new Function(new ExpressionNode(kernel, 0),
					fun[0].getFunctionVariable());
		}
		return fun[i];
	}

	@Override
	public final void update(boolean drag) {
		super.update(drag);
		for (int i = 0; i < this.funExpanded.length; i++) {
			this.funExpanded[i] = null;
		}
	}

	@Override
	public FunctionVariable[] getFunctionVariables() {
		return getFun(0).getFunctionVariables();
	}

	@Override
	public String getVarString(StringTemplate tpl) {
		return getFun(0).getVarString(tpl);
	}

	/**
	 * Set this curve by applying CAS command to f.
	 */
	@Override
	public void setUsingCasCommand(String ggbCasCmd, CasEvaluableFunction f,
			boolean symbolic, MyArbitraryConstant arbconst) {
		GeoCurveCartesianND c = (GeoCurveCartesianND) f;

		if (c.isDefined() && getFun(0) != null) {
			// register the variable name to make sure parsing of CAS output
			// runs OK, see #3006
			GeoNumeric geo = new GeoNumeric(this.cons);
			this.cons.addLocalVariable(
					getFun(0).getVarString(StringTemplate.defaultTemplate),
					geo);
			this.isDefined = true;
			for (int k = 0; k < getDimension(); k++) {
				setFun(k, (Function) c.getFunExpanded(k)
						.evalCasCommand(ggbCasCmd, symbolic, arbconst));
				this.isDefined = this.isDefined && getFun(k) != null;
			}
			this.cons.removeLocalVariable(
					getFun(0).getVarString(StringTemplate.defaultTemplate));
			if (this.isDefined) {
				setInterval(c.startParam, c.endParam);
			}
		} else {
			this.isDefined = false;
		}
		this.distFun = null;
	}

	@Override
	public void clearCasEvalMap() {
		for (int k = 0; k < getDimension(); k++) {
			if (getFun(k) != null) {
				getFun(k).clearCasEvalMap();
			}
		}
	}

	@Override
	public void printCASEvalMapXML(StringBuilder sb) {
		// not supported for curves
	}

	@Override
	public void updateCASEvalMap(TreeMap<String, String> map) {
		// TODO
	}

	/**
	 * @param i
	 *            dimension index
	 * @param f
	 *            new function
	 */
	protected void setFun(int i, Function f) {
		this.fun[i] = f;
		this.funExpanded[i] = null;
		this.containsFunctions[i] = AlgoDependentFunction
				.containsFunctions(this.fun[i].getExpression());
	}

	/**
	 * @param i
	 *            dimension index
	 * @return function with expanded function calls
	 */
	protected Function getFunExpanded(int i) {
		if (!this.containsFunctions[i]) {
			return getFun(i);
		}
		if (this.funExpanded[i] == null) {
			this.funExpanded[i] = new Function(getFun(i), this.kernel);
			ExpressionNode expr = AlgoDependentFunction
					.expandFunctionDerivativeNodes(
							getFun(i).getExpression().deepCopy(this.kernel),
							false)
					.wrap();
			this.funExpanded[i].setExpression(expr);
		}
		return this.funExpanded[i];
	}

	/**
	 * Set this curve to the n-th derivative of c
	 * 
	 * @param curve
	 *            curve whose derivative we want
	 * 
	 * @param n
	 *            order of derivative
	 */
	public void setDerivative(GeoCurveCartesianND curve, int n) {
		if (curve.isDefined()) {
			this.isDefined = true;
			for (int k = 0; k < getDimension(); k++) {
				// changed from getFunExpanded() (didn't work)
				// now handled in ExpressionNode.derivative() case FUNCTION:
				setFun(k, curve.getFun(k).getDerivative(n, true));
				this.isDefined = this.isDefined && getFun(k) != null;
			}

			if (this.isDefined) {
				setInterval(curve.startParam, curve.endParam);
			}
		} else {
			this.isDefined = false;
		}
		this.distFun = null;
	}

	/**
	 * @return dimension
	 */
	public int getDimension() {
		return this.fun.length;
	}

	/**
	 * 
	 * @param n
	 *            n
	 * @return x, y, z, for n = 0, 1, 2
	 */
	protected String getVariable(int n) {
		if (n < getDimension() && n >= 0) {

			switch (n) {
			case 0:
				return "x";
			case 1:
				return "y";
			case 2:
				return "z";

			}
		}
		Log.debug("problem with variable number");
		return "";

	}

	/**
	 * 
	 * @param p
	 *            point
	 * @param minParameter
	 *            minimal parameter
	 * @return path parameter
	 */
	public final double getClosestParameterForCurvature(GeoPointND p,
			double minParameter) {
		if (p.getDefinition() != null
				&& p.getDefinition().getOperation() == Operation.VEC_FUNCTION
				&& p.getDefinition().getLeft() == this) {
			return p.getDefinition().getRight().evaluateDouble();
		}
		return getClosestParameter(p, minParameter);
	}

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
	public final double getClosestParameter(GeoPointND P, double startValue) {
		double startVal = startValue;
		if (this.distFun == null) {
			this.distFun = createDistanceFunction();
		}

		this.distFun.setDistantPoint(P);

		// check if P is on this curve and has the right path parameter already
		if (P.getPath() == this) {
			// point A is on curve c, take its parameter
			PathParameter pp = P.getPathParameter();
			double pathParam = pp.t;
			if (this.distFun.value(pathParam) < Kernel.MIN_PRECISION
					* Kernel.MIN_PRECISION) {
				return pathParam;
			}

			// if we don't have a startValue yet, let's take the path parameter
			// as a guess
			if (Double.isNaN(startVal)) {
				startVal = pathParam;
			}
		}

		// first sample distFun to find a start interval for ExtremumFinder
		double step = (this.endParam - this.startParam)
				/ CLOSEST_PARAMETER_SAMPLES;
		double minVal = this.distFun.value(this.startParam);
		double minParam = this.startParam;
		double t = this.startParam;
		for (int i = 0; i < CLOSEST_PARAMETER_SAMPLES; i++) {
			t = t + step;
			double ft = this.distFun.value(t);
			if (ft < minVal || Double.isNaN(minVal)) {
				// found new minimum
				minVal = ft;
				minParam = t;
			}
		}

		// use interval around our minParam found by sampling
		// to find minimum
		// Math.max/min removed and ParametricCurveDistanceFunction modified
		// instead
		// TRAC-4583 #4567 removed wrong check, put Math.max/min in
		double left = Math.max(this.getMinParameter(), minParam - step);
		double right = Math.min(this.getMaxParameter(), minParam + step);

		ExtremumFinderI extFinder = this.kernel.getExtremumFinder();
		double sampleResult = extFinder.findMinimum(left, right, this.distFun,
				Kernel.MIN_PRECISION);

		sampleResult = adjustRange(sampleResult);

		// if we have a valid startParam we try the interval around it too
		// however, we don't check the same interval again
		if (!Double.isNaN(startVal) && (startVal < left || right < startVal)) {

			// Math.max/min removed and ParametricCurveDistanceFunction modified
			// instead
			left = startVal - step;
			right = startVal + step;

			double startValResult = extFinder.findMinimum(left, right,
					this.distFun, Kernel.MIN_PRECISION);

			startValResult = adjustRange(startValResult);

			if (this.distFun
					.value(startValResult) < this.distFun.value(sampleResult)
							+ Kernel.MIN_PRECISION / 2) {
				return startValResult;
			}
		}

		return sampleResult;
	}

	/**
	 * allow a curve like Curve[sin(t), cos(t), t, 0, 12*2pi] to "join up"
	 * properly at 0 and 12*2pi
	 * 
	 * @param startValResult
	 *            start value
	 * @return startValResult adjusted to be in range [startParam, endParam] if
	 *         it's just outside
	 */
	private double adjustRange(double startValResult) {
		if (startValResult < this.startParam) {
			return startValResult + (this.endParam - this.startParam);
		}

		if (startValResult > this.endParam) {
			return startValResult - (this.endParam - this.startParam);
		}

		return startValResult;
	}

	@Override
	public abstract double evaluateCurvature(double t);

	/**
	 * @return whether range is hidden in algebra
	 */
	public boolean isHiddenRange() {
		return this.hideRangeInFormula;
	}

	/**
	 * Hide range in formula -- needed when the curve is infinite and range is
	 * used for drawing only (e.g. rotated functions)
	 * 
	 * @param b
	 *            true to hide
	 */
	public void setHideRangeInFormula(boolean b) {
		this.hideRangeInFormula = b;
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}

	@Override
	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		if (this.isDefined) {
			StringBuilder sbTemp = new StringBuilder(80);

			String param = getVarString(tpl);

			if (!hideRangeInFormula && point == null) {
				sbTemp.append("\\left.");
					}
					if (point == null) {
				sbTemp.append("\\begin{array}{lll}");

				for (int i = 0; i < getDimension(); i++) {

					if (i > 0) {
						sbTemp.append("\\\\ ");
					}
					sbTemp.append(getVariable(i));
					sbTemp.append(" = ");
					sbTemp.append(getFun(i).toLaTeXString(symbolic, tpl));

				}
				sbTemp.append(" \\end{array}");
					} else {
				sbTemp.append(point.toLaTeXString(true, tpl));
					}

			if (!hideRangeInFormula) {
				if (point == null) {
					sbTemp.append("\\right\\} \\; ");
				} else {
					sbTemp.append(", \\;\\;\\;\\; \\left(");
				}
				sbTemp.append(this.kernel.format(this.startParam, tpl));
				sbTemp.append(" \\le ");
				sbTemp.append(param);
				sbTemp.append(" \\le ");
				sbTemp.append(this.kernel.format(this.endParam, tpl));
				if (point == null) {
					// nothing to do here
				} else {
					sbTemp.append("\\right)");
				}
			}

			return sbTemp.toString();
		}
		return "?";
	}

	/**
	 * @param order
	 *            order of derivative
	 * @return derivative as curve
	 */
	public GeoCurveCartesianND getGeoDerivative(int order) {
		if (this.derivGeoFun == null) {
			this.derivGeoFun = newGeoCurveCartesian(this.cons);
		}

		this.derivGeoFun.setDerivative(this, order);
		return this.derivGeoFun;
	}

	/**
	 * @param cons1
	 *            construction
	 * @return curve in the same dimension
	 */
	protected abstract GeoCurveCartesianND newGeoCurveCartesian(
			Construction cons1);

	@Override
	public abstract ExpressionValue evaluateCurve(double double1);

	@Override
	public boolean isParametric() {
		return true;
	}

	/**
	 * @return defining expression (null if defined per coords)
	 */
	public ExpressionNode getPointExpression() {
		return point;
	}

	/**
	 * update distance function
	 */
	abstract public void updateDistanceFunction();

	@Override
	public void evaluateCurve(double t, double[] f1eval) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param points
	 *            list of vertices
	 * @param repeatLast
	 *            true if we should add last-first edge
	 */
	public final void setFromPolyLine(GeoPointND[] points, boolean repeatLast) {

		int dim = fun.length;
		if (points.length < 2) {
			setUndefined();
			return;
		}
		ExpressionNode[] en = new ExpressionNode[dim];
		for (int i = 0; i < dim; i++) {
			en[i] = new ExpressionNode(this.kernel,
					pointToCoords(points[0]).get(i + 1));
		}
		FunctionVariable fv = new FunctionVariable(this.kernel, "t");
		int nonzeroSegments = 0;
		if (points.length == 2) {
			for (int i = 0; i < dim; i++) {
				double coeff = pointToCoords(points[1]).get(i + 1)
						- pointToCoords(points[0]).get(i + 1);
				en[i] = en[i].plus(new ExpressionNode(this.kernel,
						new MyDouble(this.kernel, coeff), Operation.MULTIPLY,
						fv));
			}
			nonzeroSegments = 1;
		} else {
			nonzeroSegments = buildAbsExpression(points, en, fv, repeatLast);
		}

		for (int j = 0; j < dim; j++) {
			Function xFun = new Function(en[j], fv);
			this.setFun(j, xFun);
		}
		this.setInterval(0, nonzeroSegments);
	}

	private int buildAbsExpression(GeoPointND[] points, ExpressionNode[] en,
			FunctionVariable fv, boolean repeatLast) {
		int dim = fun.length;
		int nonzeroSegments = 0;
		double coef = 0;
		double[] sum = new double[] { 0, 0, 0 };
		double[] cumulative = new double[] { 0, 0, 0 };

		int limit = repeatLast ? points.length + 1 : points.length;

		for (int i = 1; i < limit; i++) {
			int pointIndex = i >= points.length ? 0 : i;
			Coords c1 = pointToCoords(points[pointIndex]);
			Coords c2 = pointToCoords(points[i - 1]);
			if (c1.isEqual(c2)) {
				continue;
			}
			ExpressionNode greater = new ExpressionNode(this.kernel,
					new ExpressionNode(this.kernel, fv, Operation.MINUS,
							new MyDouble(this.kernel, nonzeroSegments)),
					Operation.ABS, null);
			for (int j = 0; j < dim; j++) {
				coef = 0.5 * c1.get(j + 1) - 0.5 * c2.get(j + 1)
						- cumulative[j];
				sum[j] += coef * nonzeroSegments;

				cumulative[j] += coef;
				en[j] = en[j].plus(
						greater.multiply(new MyDouble(this.kernel, coef)));
			}
			nonzeroSegments++;

		}
		for (int j = 0; j < dim; j++) {
			en[j] = en[j].plus(
					new ExpressionNode(this.kernel, fv, Operation.MULTIPLY,
							new MyDouble(this.kernel, cumulative[j])));

			en[j] = en[j].plus(new MyDouble(this.kernel, -sum[j]));

		}
		return nonzeroSegments;
	}

	/**
	 * @param geoPointND
	 *            point
	 * @return inhom coords in current dimension
	 */
	protected Coords pointToCoords(GeoPointND geoPointND) {
		return geoPointND.getInhomCoordsInD2();
	}

	/**
	 * @return distance function
	 */
	protected DistanceFunction createDistanceFunction() {
		return new ParametricCurveDistanceFunction(this);
	}

}
