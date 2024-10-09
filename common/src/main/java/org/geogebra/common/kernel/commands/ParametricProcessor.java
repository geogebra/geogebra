package org.geogebra.common.kernel.commands;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoCurveCartesian;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.algos.AlgoSurfaceCartesianND;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.Traversing.CollectUndefinedVariables;
import org.geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.VectorArithmetic;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Processing
 * 
 * f(a)=(a,a) X=(a,a) (t,t)
 *
 */
public class ParametricProcessor {
	/**
	 * kernel
	 */
	@Weak
	protected Kernel kernel;
	/**
	 * Algebra processor
	 */
	@Weak
	protected AlgebraProcessor ap;

	/**
	 * @param kernel
	 *            kernel
	 * @param ap
	 *            algebra processor
	 */
	public ParametricProcessor(Kernel kernel, AlgebraProcessor ap) {
		this.kernel = kernel;
		this.ap = ap;
	}

	/**
	 * @param ve0
	 *            expression; should have label X or contain t, theta (also
	 *            equations X=... handled here for some reason)
	 * @param undefinedVariables
	 *            list of names of undefined variables
	 * @param callback
	 *            call this after sliders are created
	 * @param info
	 *            processing information
	 * @return resulting elements
	 */
	final GeoElement[] checkParametricEquation(ValidExpression ve0,
			TreeSet<String> undefinedVariables,
			AsyncOperation<GeoElementND[]> callback, EvalInfo info) {
		if (undefinedVariables.isEmpty()) {
			return null;
		}
		boolean parametricExpression = "X".equals(ve0.getLabel())
				|| undefinedVariables.contains("t");
		boolean parametricEquation = ve0.unwrap() instanceof Equation
				&& "X".equals(((Equation) ve0.unwrap()).getLHS()
						.toString(StringTemplate.defaultTemplate));
		if (!parametricEquation && !parametricExpression) {
			return null;
		}
		GeoElement replaceable = ap.getReplaceable(ve0.getLabels());
		Construction cons = kernel.getConstruction();
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		if (replaceable != null) {
			cons.setSuppressLabelCreation(true);
		}
		String varName = getPreferredName(undefinedVariables);

		ValidExpression ve = ve0;

		TreeSet<GeoNumeric> num = new TreeSet<>();
		// Iteration[a+1, a, {1},4]
		ap.replaceUndefinedVariables(ve, num, new String[] {varName, "X" },
				info.isMultipleUnassignedAllowed());
		for (GeoNumeric slider : num) {
			undefinedVariables.remove(slider.getLabelSimple());
		}
		if (parametricExpression) {
			try {
				FunctionVariable fv = new FunctionVariable(kernel, varName);

				ExpressionNode exp = ve.deepCopy(kernel).traverse(
						VariableReplacer.getReplacer(varName, fv, kernel))
						.wrap();
				exp.resolveVariables(new EvalInfo(false));
				GeoElement[] ret = processParametricFunction(exp,
						exp.evaluate(StringTemplate.defaultTemplate),
						new FunctionVariable[] { fv },
						getParametricLabel(ve), info);
				if (ret != null
						&& (num.isEmpty() || (info.isAutocreateSliders())
								&& info.isLabelOutput())) {
					cons.setSuppressLabelCreation(oldMacroMode);
					ap.processReplace(replaceable, ret, null, info);
					return ret;
				}
			} catch (Throwable tt) {
				Log.debug("X is not parametric:" + tt.getMessage());
			}
			removeSliders(num, undefinedVariables);

		} else if (parametricEquation) {
			// TODO this branch should be handled by processEquation, so maybe
			// not needed at all
			try {

				FunctionVariable fv = new FunctionVariable(kernel, varName);
				ExpressionNode exp = ((Equation) ve.unwrap()).getRHS()
						.deepCopy(kernel).traverse(VariableReplacer
								.getReplacer(varName, fv, kernel))
						.wrap();
				exp.resolveVariables(info);
				GeoElement[] ret = processParametricFunction(exp,
						exp.evaluate(StringTemplate.defaultTemplate),
						new FunctionVariable[] { fv }, ve.getLabel(), info);
				if (ret != null
						&& (num.isEmpty() || info.isAutocreateSliders())) {
					cons.setSuppressLabelCreation(oldMacroMode);
					ap.processReplace(replaceable, ret, null, info);
					return ret;
				}
			} catch (Throwable tt) {
				Log.debug(tt);
				Log.debug("X is not parametric");
			}
			removeSliders(num, undefinedVariables);
		}
		removeSliders(num, undefinedVariables);
		cons.setSuppressLabelCreation(oldMacroMode);
		return null;
	}

	protected static String getParametricLabel(ValidExpression ve) {
		return "X".equals(ve.getLabel()) ? null : ve.getLabel();
	}

	private static String getPreferredName(TreeSet<String> undefinedVariables) {

		if (undefinedVariables.contains("t")) {
			return "t";
		}
		if (undefinedVariables.contains(Unicode.theta_STRING)) {
			return Unicode.theta_STRING;
		}
		Iterator<String> t = undefinedVariables.iterator();

		String varName = t.next();
		if ("X".equals(varName)) {
			if (t.hasNext()) {
				return t.next();
			}
			return "t";
		} else if ("y".equals(varName)) {
			return Unicode.lambda_STRING;
		}
		return varName;
	}

	private static void removeSliders(TreeSet<GeoNumeric> num,
			TreeSet<String> undefined) {
		for (GeoNumeric slider : num) {
			slider.remove();
			undefined.add(slider.getLabelSimple());
		}

	}

	/**
	 * @param ev
	 *            expression
	 * @return input expression or zero expression if input is null
	 */
	protected ExpressionNode expr(ExpressionValue ev) {
		if (ev == null) {
			return new ExpressionNode(kernel, 0);
		}
		return ev.wrap();
	}

	/**
	 * @param exp
	 *            expression
	 * @param ev
	 *            evaluated exp
	 * @param fv
	 *            function variable
	 * @param label
	 *            label for output
	 * @param info
	 *            processing information
	 * @return paramteric curve (or line, conic)
	 */
	protected GeoElement[] processParametricFunction(ExpressionNode exp,
			ExpressionValue ev, FunctionVariable[] fv, String label,
			EvalInfo info) {
		Construction cons = kernel.getConstruction();
		if (ev instanceof MyList) {
			GeoElement ge = kernel.getAlgebraProcessor().listExpression(exp);
			ge.setLabel(exp.getLabel());
			return ge.asArray();
		}
		if (fv.length < 2 && ev instanceof VectorValue) {
			if (((VectorValue) ev).getToStringMode() == Kernel.COORD_COMPLEX) {
				return complexSurface(exp, fv[0], label);
			}
			GeoNumeric locVar = getLocalVar(exp, fv[0]);
			if (exp.getOperation().isIf()) {
				ExpressionNode exp1 = exp.getRightTree();
				ExpressionNode cx = VectorArithmetic.computeCoord(exp1, 0);
				ExpressionNode cy = VectorArithmetic.computeCoord(exp1, 1);
				return cartesianCurve(cons, label, exp1, locVar,
						new ExpressionNode[] { cx, cy },
						exp.getLeftTree());
			}
			ExpressionNode cx = VectorArithmetic.computeCoord(exp, 0);
			ExpressionNode cy = VectorArithmetic.computeCoord(exp, 1);

			ExpressionValue[] coefX = new ExpressionValue[5],
					coefY = new ExpressionValue[5];
			if (ap.getTrigCoeffs(cx, coefX, new ExpressionNode(kernel, 1.0),
					locVar)
					&& ap.getTrigCoeffs(cy, coefY,
							new ExpressionNode(kernel, 1.0), locVar)) {

				ExpressionNode a, b, c, d, xx, xy, yy;

				ExpressionNode x = new FunctionVariable(kernel, "x").wrap()
						.subtract(expr(coefX[0]));
				ExpressionNode y = new FunctionVariable(kernel, "y").wrap()
						.subtract(expr(coefY[0]));

				if (coefX[1] != null || coefX[2] != null) {
					a = expr(coefX[1]);
					b = expr(coefX[2]);
					c = expr(coefY[1]);
					d = expr(coefY[2]);
					xx = c.power(2).plus(d.power(2)).multiply(x).multiply(x);
					xy = c.multiply(a).plus(d.multiply(b)).multiply(-2)
							.multiply(x).multiply(y);
					yy = a.power(2).plus(b.power(2)).multiply(y).multiply(y);
				} else {
					a = expr(coefX[3]);
					b = expr(coefX[4]);
					c = expr(coefY[3]);
					d = expr(coefY[4]);
					xx = c.power(2).subtract(d.power(2)).multiply(x)
							.multiply(x);
					xy = c.multiply(a).subtract(d.multiply(b)).multiply(-2)
							.multiply(x).multiply(y);
					yy = a.power(2).subtract(b.power(2)).multiply(y)
							.multiply(y);
				}

				ExpressionNode den = a.power(2).multiply(d.power(2))
						.plus(b.power(2).multiply(c.power(2)))
						.subtract(a.multiply(b).multiply(c).multiply(d)
								.multiply(2));
				Equation eq = new Equation(kernel, xx.plus(xy).plus(yy).wrap(),
						den);
				return paramConic(eq, exp, label, fv[0].getSetVarString(),
						info);
			}

			coefX = ap.arrayOfZeros(coefX.length);
			coefY = ap.arrayOfZeros(coefY.length);

			int degX = ap.getPolyCoeffs(cx, coefX,
					new ExpressionNode(kernel, 1.0), locVar);
			int degY = ap.getPolyCoeffs(cy, coefY,
					new ExpressionNode(kernel, 1.0), locVar);

			// line
			if ((degX >= 0 && degY >= 0) && (degX < 2 && degY < 2)) {
				FunctionVariable px = new FunctionVariable(kernel, "x");
				FunctionVariable py = new FunctionVariable(kernel, "y");
				Equation eq = new Equation(kernel,
						coefX[1].wrap().multiply(py)
								.subtract(coefY[1].wrap().multiply(px)),
						coefX[1].wrap().multiply(coefY[0])
								.subtract(coefX[0].wrap().multiply(coefY[1])));
				eq.setForceLine();
				eq.initEquation();
				eq.setLabel(label);
				Traversing.GeoNumericReplacer repl = Traversing.GeoNumericReplacer
						.getReplacer(locVar, fv[0], kernel);
				// replace GeoNumeric with function variable
				exp.traverse(repl);
				GeoElement[] line = ap.processLine(eq, buildParamEq(exp, label), info);
				((GeoLineND) line[0]).setToUser();
				line[0].update();
				return line;
				// parabola
				// x=att+bt+c
				// y=dtt+et+f
				// t=(d*x-a*y-d*c+a*f)/(d*b-a*e)
			} else if (degX >= 0 && degY >= 0) {
				FunctionVariable px = new FunctionVariable(kernel, "x");
				FunctionVariable py = new FunctionVariable(kernel, "y");
				Log.debug(coefX[2] + "," + coefX[1] + "," + coefX[0]);
				ExpressionNode t = px.wrap().multiply(coefY[2])
						.subtract(py.wrap().multiply(coefX[2]))
						.plus(coefX[2].wrap().multiply(coefY[0]))
						.subtract(coefY[2].wrap().multiply(coefX[0]));

				ExpressionNode d = coefX[1].wrap().multiply(coefY[2])
						.subtract(coefY[1].wrap().multiply(coefX[2]));

				Equation eq;

				// Numerically unstable
				eq = new Equation(kernel,
						d.power(2).multiply(px).multiply(coefX[2])
								.plus(d.power(2).multiply(py)
										.multiply(coefY[2])),
						t.power(2)
								.multiply(coefY[2].wrap().power(2)
										.plus(coefX[2].wrap().power(2)))
								.plus(t.multiply(
										coefY[1].wrap().multiply(coefY[2])
												.plus(coefX[1].wrap()
														.multiply(coefX[2])))
										.multiply(d))
								.plus(d.power(2).multiply(coefY[0].wrap()
										.multiply(coefY[2]).plus(coefX[0].wrap()
												.multiply(coefX[2])))));

				return paramConic(eq, exp, label, fv[0].getSetVarString(),
						info);
			}
			return cartesianCurve(cons, label, exp, locVar,
					new ExpressionNode[] { cx, cy }, null);
		} else if (ev instanceof Function) {
			return ap.processFunction((Function) ev, info);
		} else if (ev instanceof FunctionNVar) {
			return ap.processFunctionNVar((FunctionNVar) ev, info);
		}
		Log.debug("InvalidFunction:"
				+ exp.toString(StringTemplate.defaultTemplate) + ","
				+ ev.getClass() + "," + fv.length);
		throw new MyError(kernel.getApplication().getLocalization(),
				Errors.InvalidFunction);

	}

	/**
	 * @param exp
	 *            expression
	 * @param fv
	 *            variables
	 * @param dim
	 *            dimension
	 * @return surface
	 */
	protected GeoElement[] processSurface(ExpressionNode exp,
			FunctionVariable[] fv, int dim, boolean complex) {
		GeoNumeric loc0 = getLocalVar(exp, fv[0]);
		GeoNumeric loc1 = getLocalVar(exp, fv[1]);
		Construction cons = kernel.getConstruction();
		GeoNumberValue[] coords = new GeoNumberValue[dim];
		for (int i = 0; i < dim; i++) {
			ExpressionNode cx = VectorArithmetic.computeCoord(exp, i, complex);
			AlgoDependentNumber nx = new AlgoDependentNumber(cons, cx, false);
			cons.removeFromConstructionList(nx);
			coords[i] = nx.getNumber();
		}
		AlgoSurfaceCartesianND algo = new AlgoSurfaceCartesianND(cons,
				exp,
				coords,
				new GeoNumeric[] { loc0, loc1 },
				new GeoNumberValue[] { num(-10), num(-10) },
				new GeoNumberValue[] { num(10), num(10) });
		return algo.getOutput();
	}

	private GeoNumberValue num(double d) {
		return new GeoNumeric(kernel.getConstruction(), d);
	}

	/**
	 * @param exp expression
	 * @param fv complex variable
	 * @param label label
	 * @return function
	 */
	public GeoElement[] complexSurface(ExpressionNode exp,
			FunctionVariable fv, String label) {
		FunctionVariable u = new FunctionVariable(kernel, "u");
		FunctionVariable v = new FunctionVariable(kernel, "v");
		ExpressionNode complex = new ExpressionNode(kernel, u, Operation.PLUS,
				new ExpressionNode(kernel, v, Operation.MULTIPLY,
						kernel.getImaginaryUnit()));
		ExpressionNode exp2 = exp.deepCopy(kernel).replace(fv, complex).wrap();
		GeoElement[] surface =  processSurface(exp2,
				new FunctionVariable[] { u, v },  2, true);
		surface[0].setDefinition(exp);
		((GeoSurfaceCartesianND) surface[0]).setComplexVariable(fv);
		surface[0].setLabel(label);
		return surface;
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param exp
	 *            expression
	 * @param locVar
	 *            variable
	 * @param c
	 *            coordinates
	 * @param condition
	 *            limitation for input variable
	 * @return curve
	 */
	protected GeoElement[] cartesianCurve(Construction cons, String label,
			ExpressionNode exp, GeoNumeric locVar, ExpressionNode[] c,
			ExpressionNode condition) {
		for (int i = 0; i < c.length; i++) {
			checkNumber(c[i]);
		}
		GeoNumberValue[] coords = new GeoNumberValue[c.length];
		for (int i = 0; i < c.length; i++) {
			AlgoDependentNumber nx = new AlgoDependentNumber(cons, c[i], false);
			cons.removeFromConstructionList(nx);
			coords[i] = nx.getNumber();
		}

		GeoNumberValue from = null, to = null;
		if (condition != null) {
			from = getBound(locVar, condition, true);
			to = getBound(locVar, condition, false);
		}
		boolean trig = c[0].has2piPeriodicOperations();
		if (from == null) {
			from = new GeoNumeric(cons, trig ? 0 : -10);

		}
		if (to == null) {
			to = trig ? piTimes(2, cons) : new GeoNumeric(cons, 10);
		}
		AlgoCurveCartesian ac = makeCurveAlgo(cons,
				exp.deepCopy(kernel).wrap(),
				coords, locVar,
				from, to);
		ac.getCurve().setLabel(label);
		return ac.getOutput();
	}

	/**
	 * @param cons
	 *            construction
	 * @param wrap
	 *            expression
	 * @param coords
	 *            coords
	 * @param locVar
	 *            variable
	 * @param from
	 *            min
	 * @param to
	 *            max
	 * @return curve algo
	 */
	protected AlgoCurveCartesian makeCurveAlgo(Construction cons,
			ExpressionNode wrap, GeoNumberValue[] coords, GeoNumeric locVar,
			GeoNumberValue from, GeoNumberValue to) {
		return new AlgoCurveCartesian(cons, wrap, coords, locVar, from, to);
	}

	/**
	 * @param cx
	 *            potential numeric expression
	 */
	protected void checkNumber(ExpressionNode cx) {
		if (!cx.evaluate(StringTemplate.maxPrecision).isNumberValue()) {
			throw new MyError(kernel.getApplication().getLocalization(),
					Errors.InvalidFunction);
		}

	}

	private GeoNumberValue getBound(GeoNumeric locVar, ExpressionNode condition,
			boolean swap) {
		if (condition.getOperation() == Operation.AND
				|| condition.getOperation() == Operation.AND_INTERVAL) {
			GeoNumberValue lt = getBound(locVar, condition.getLeftTree(), swap);
			if (lt != null) {
				return lt;
			}
			GeoNumberValue rt = getBound(locVar, condition.getRightTree(),
					swap);
			if (rt != null) {
				return rt;
			}
		}
		ExpressionValue checkVar = swap ? condition.getLeft()
				: condition.getRight();
		ExpressionValue checkNum = !swap ? condition.getLeft()
				: condition.getRight();
		if ((condition.getOperation() == Operation.GREATER
				|| condition.getOperation() == Operation.GREATER_EQUAL)
				&& checkVar == locVar) {
			return (GeoNumberValue) ap.processNumber(checkNum.wrap(),
					checkNum.evaluate(StringTemplate.defaultTemplate),
					new EvalInfo(false))[0];

		}
		if ((condition.getOperation() == Operation.LESS
				|| condition.getOperation() == Operation.LESS_EQUAL)
				&& checkNum == locVar) {
			return (GeoNumberValue) ap.processNumber(checkVar.wrap(),
					checkVar.evaluate(StringTemplate.defaultTemplate),
					new EvalInfo(false))[0];

		}
		return null;
	}

	private GeoNumberValue piTimes(int i, Construction cons) {
		ExpressionNode en = new ExpressionNode(kernel, new MyDouble(kernel, i),
				Operation.MULTIPLY, new MyDouble(kernel, Math.PI));
		GeoNumeric ret = new GeoNumeric(cons, i * Math.PI);
		ret.setDefinition(en);
		return ret;
	}

	/**
	 * @param exp
	 *            RHS of parametric equation
	 * @param label label used for original definition, keep track to make auto-labeling work
	 * @return equation X = exp
	 */
	protected ExpressionNode buildParamEq(ExpressionNode exp, String label) {
		ExpressionNode ret = new Equation(kernel, new Variable(kernel, "X"), exp).wrap();
		ret.setLabel(label);
		return ret;
	}

	private GeoElement[] paramConic(Equation eq, ExpressionNode exp,
			String label, String param, EvalInfo info) {
		eq.initEquation();
		eq.setForceConic();
		eq.setLabel(label);
		GeoElement[] ret = ap.processConic(eq, buildParamEq(exp, label), info);
		((GeoConicND) ret[0]).toParametric(param);
		((GeoConicND) ret[0]).update();
		return ret;
	}

	/**
	 * Creates a number and replaces all occurences of the variable with it
	 * 
	 * @param exp
	 *            expression
	 * @param fv
	 *            function variable
	 * @return numeric that replaces the variable
	 */
	protected GeoNumeric getLocalVar(ExpressionNode exp, FunctionVariable fv) {
		GeoNumeric locVar = new GeoNumeric(kernel.getConstruction());
		locVar.setSendValueToCas(false);
		locVar.setLocalVariableLabel(fv.getSetVarString());
		exp.replace(fv, locVar);
		return locVar;
	}

	/**
	 * @param ve
	 *            expression that might be RHS of parametric equation, eg (t,t)
	 * @param fallback
	 *            what to return if ve is not parametric equation
	 * @param cons
	 *            construction
	 * @param info
	 *            processing information
	 * @return parametric curve (or line, conic) or fallback
	 */
	public ValidExpression checkParametricEquationF(ValidExpression ve,
			ValidExpression fallback, Construction cons, EvalInfo info) {
		CollectUndefinedVariables collecter = new Traversing.CollectUndefinedVariables();
		ve.inspect(collecter);
		final TreeSet<String> undefinedVariables = collecter.getResult();
		if (undefinedVariables.size() == 1) {
			try {
				String varName = undefinedVariables.first();
				FunctionVariable fv = new FunctionVariable(kernel, varName);
				ExpressionNode exp = ve.deepCopy(kernel).traverse(
						VariableReplacer.getReplacer(varName, fv, kernel))
						.wrap();
				exp.resolveVariables(info);
				boolean flag = cons.isSuppressLabelsActive();
				cons.setSuppressLabelCreation(true);
				GeoElement[] ret = processParametricFunction(exp,
						exp.evaluate(StringTemplate.defaultTemplate),
						new FunctionVariable[] { fv }, null, info);
				cons.setSuppressLabelCreation(flag);
				if (ret != null) {
					return ret[0].wrap();
				}
			} catch (Throwable t) {
				Log.debug(t);
				Log.debug("X is not parametric");
			}
		}
		return fallback;
	}

	/**
	 * @param equ
	 *            aquation with X on LHS
	 * @param info
	 *            processing information
	 * @return parametric curve if possible
	 */
	public GeoElement[] processXEquation(Equation equ, EvalInfo info) {
		CollectUndefinedVariables collecter = new Traversing.CollectUndefinedVariables();
		equ.inspect(collecter);
		final TreeSet<String> undefinedVariables = collecter.getResult();
		// case 3DLine
		if (undefinedVariables.isEmpty()) {
			undefinedVariables.add("y");
		}
		String varName = getPreferredName(undefinedVariables);
		FunctionVariable fv = new FunctionVariable(kernel, varName);
		ExpressionNode exp = equ.getRHS().deepCopy(kernel)
				.traverse(VariableReplacer.getReplacer(varName, fv, kernel))
				.wrap();
		exp.resolveVariables(info);
		GeoElement[] ret = processParametricFunction(exp,
				exp.evaluate(StringTemplate.defaultTemplate),
				new FunctionVariable[] { fv }, equ.getLabel(), info);
		return ret;
	}

	/**
	 * Returns the single free GeoNumeric/MyDouble expression wrapped in this
	 * ExpressionValue. For "a + x(A)" this returns a, for "x(A)" this returns
	 * null where A is a free point. If A is a dependent point, "a + x(A)"
	 * throws an Exception.
	 * 
	 * @param ev
	 *            expression
	 * @return number
	 */
	public NumberValue getCoordNumber(ExpressionValue ev) {
		// simple variable "a"
		if (ev.isLeaf()) {

			// handle (a,1) and (1,a) case
			// 1 is MyDouble
			if (ev.isExpressionNode()) {
				if (((ExpressionNode) ev).getLeft() instanceof MyDouble) {
					return (NumberValue) ((ExpressionNode) ev).getLeft();
				}
			}

			GeoElement geo = kernel.lookupLabel(ev.isGeoElement()
					? ((GeoElement) ev).getLabel(StringTemplate.defaultTemplate)
					: ev.toString(StringTemplate.defaultTemplate));
			if (geo != null && geo.isGeoNumeric()
					&& geo.isPointerChangeable()) {
				return (GeoNumeric) geo;
			}
			return null;
		}

		// return value
		GeoNumeric coordNumeric = null;

		// expression + expression
		ExpressionNode en = (ExpressionNode) ev;
		if (en.getOperation().equals(Operation.PLUS)
				&& en.getLeft() instanceof GeoNumeric) {

			// left branch needs to be a single number variable: get it
			// e.g. a + x(D)
			coordNumeric = (GeoNumeric) en.getLeft();
			if (!coordNumeric.isChangeable()) {
				return null;
			}
			// check that variables in right branch are all independent to avoid
			// circular definitions
			Set<GeoElement> rightVars = en.getRight()
					.getVariables(SymbolicMode.NONE);
			if (rightVars != null) {
				for (GeoElement var : rightVars) {
					if (var.isChildOrEqual(coordNumeric)) {
						return null;
					}
				}
			}
		}

		return coordNumeric;
	}
}
