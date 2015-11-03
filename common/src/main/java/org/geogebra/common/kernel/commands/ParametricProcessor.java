package org.geogebra.common.kernel.commands;

import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoCurveCartesian;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.Traversing.CollectUndefinedVariables;
import org.geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.Variable;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;

public class ParametricProcessor {
	protected Kernel kernel;
	protected AlgebraProcessor ap;

	public ParametricProcessor(Kernel kernel, AlgebraProcessor ap) {
		this.kernel = kernel;
		this.ap = ap;
	}

	GeoElement[] checkParametricEquation(ValidExpression ve0,
			TreeSet<String> undefinedVariables, boolean autocreateSliders,
			AsyncOperation callback) {
		if (undefinedVariables.isEmpty()) {
			return null;
		}
		boolean parametricExpression = ("X".equals(ve0.getLabel())
				|| undefinedVariables
.contains("t") || undefinedVariables
				.contains(Unicode.thetaStr));
		boolean parametricEquation = ve0.unwrap() instanceof Equation
				&& "X".equals(((Equation) ve0.unwrap()).getLHS().toString(
						StringTemplate.defaultTemplate));
		if (!parametricEquation && !parametricExpression) {
			return null;
		}
		String varName = getPreferredName(undefinedVariables);

		ValidExpression ve = ve0;

		TreeSet<GeoNumeric> num = new TreeSet<GeoNumeric>();
		ap.replaceUndefinedVariables(ve, num, new String[] { varName, "X" });// Iteration[a+1,
																		// a,
																		// {1},4]
		for (GeoNumeric slider : num) {
			undefinedVariables.remove(slider.getLabelSimple());
		}
		if (parametricExpression) {
			try {
				if (varName.equals(Unicode.thetaStr)
						&& ve0.evaluatesToNumber(true)) {
					ve = new MyVecNode(kernel, ve0, new Variable(kernel,
							Unicode.thetaStr));
					((MyVecNode) ve).setMode(Kernel.COORD_POLAR);
				}
				FunctionVariable fv = new FunctionVariable(kernel, varName);
				ExpressionNode exp = ve
						.deepCopy(kernel)
						.traverse(
								VariableReplacer.getReplacer(varName, fv,
										kernel)).wrap();
				exp.resolveVariables();
				GeoElement[] ret = processParametricFunction(exp,
						exp.evaluate(StringTemplate.defaultTemplate), fv, null);
				App.debug("AUTOCREATE" + autocreateSliders);
				if (ret != null && (num.isEmpty() || autocreateSliders)) {
					return ret;
				}
			} catch (Throwable tt) {
				tt.printStackTrace();
				Log.debug("X is not parametric");
			}
			removeSliders(num, undefinedVariables);

		} else if (parametricEquation) {
			App.printStacktrace("EQUATION");
			try {

				FunctionVariable fv = new FunctionVariable(kernel, varName);
				ExpressionNode exp = ((Equation) ve.unwrap())
						.getRHS()
						.deepCopy(kernel)
						.traverse(
								VariableReplacer.getReplacer(varName, fv,
										kernel)).wrap();
				exp.resolveVariables();
				GeoElement[] ret = processParametricFunction(exp,
						exp.evaluate(StringTemplate.defaultTemplate), fv,
						ve.getLabel());
				if (ret != null && (num.isEmpty() || autocreateSliders)) {
					return ret;
				}
			} catch (Throwable tt) {
				tt.printStackTrace();
				Log.debug("X is not parametric");
			}
			removeSliders(num, undefinedVariables);
		}
		removeSliders(num, undefinedVariables);
		return null;
	}
	
	private String getPreferredName(TreeSet<String> undefinedVariables) {
		Iterator<String> t = undefinedVariables.iterator();

		String varName = t.next();
		if (undefinedVariables.contains("t")) {
			varName = "t";
		}
		if (undefinedVariables.contains(Unicode.thetaStr)) {
			varName = Unicode.thetaStr;
		}
		if ("X".equals(varName)) {
			varName = t.next();
		}
		return varName;
	}

	private void removeSliders(TreeSet<GeoNumeric> num,
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
	 * @return paramteric curve (or line, conic)
	 */
	protected GeoElement[] processParametricFunction(ExpressionNode exp,
			ExpressionValue ev, FunctionVariable fv, String label) {
		Construction cons = kernel.getConstruction();
		if (ev instanceof VectorValue
				&& ((VectorValue) ev).getMode() != Kernel.COORD_COMPLEX) {
			GeoNumeric locVar = new GeoNumeric(cons);
			locVar.setLocalVariableLabel(fv.getSetVarString());
			exp.replace(fv, locVar);

			ExpressionNode cx = ap.computeCoord(exp, 0);
			ExpressionNode cy = ap.computeCoord(exp, 1);
			ExpressionValue[] coefX = new ExpressionValue[5], coefY = new ExpressionValue[5];
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

				ExpressionNode den = a
						.power(2)
						.multiply(d.power(2))
						.plus(b.power(2).multiply(c.power(2)))
						.subtract(
								a.multiply(b).multiply(c).multiply(d)
										.multiply(2));
				Equation eq = new Equation(kernel, xx.plus(xy).plus(yy).wrap(),
						den);
				eq.setLabel(label);
				eq.initEquation();
				return ap.processConic(eq);
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
				Equation eq = new Equation(kernel, coefX[1].wrap().multiply(py)
						.subtract(coefY[1].wrap().multiply(px)), coefX[1]
						.wrap().multiply(coefY[0])
						.subtract(coefX[0].wrap().multiply(coefY[1])));
				eq.setForceLine();
				eq.initEquation();
				eq.setLabel(label);
				GeoElement[] line = ap.processLine(eq);
				((GeoLineND) line[0]).setToParametric(fv.getSetVarString());
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
				eq = new Equation(kernel, d.power(2).multiply(px)
						.multiply(coefX[2])
						.plus(d.power(2).multiply(py).multiply(coefY[2])), t
						.power(2)
						.multiply(
								coefY[2].wrap().power(2)
										.plus(coefX[2].wrap().power(2)))
						.plus(t.multiply(
								coefY[1].wrap()
										.multiply(coefY[2])
										.plus(coefX[1].wrap()
												.multiply(coefX[2]))).multiply(
								d))
						.plus(d.power(2).multiply(
								coefY[0].wrap()
										.multiply(coefY[2])
										.plus(coefX[0].wrap()
												.multiply(coefX[2])))));
				eq.initEquation();
				return ap.processConic(eq);
			}
			AlgoDependentNumber nx = new AlgoDependentNumber(cons, cx, false);
			cons.removeFromConstructionList(nx);
			AlgoDependentNumber ny = new AlgoDependentNumber(cons, cy, false);
			cons.removeFromConstructionList(ny);

			boolean trig = cx.has2piPeriodicOperations();
			GeoNumeric from = new GeoNumeric(cons, trig ? 0 : -10);
			GeoNumeric to = new GeoNumeric(cons, trig ? 2 * Math.PI : 10);
			AlgoCurveCartesian ac = new AlgoCurveCartesian(cons,
 exp.deepCopy(
					kernel).wrap(),
					new NumberValue[] { nx.getNumber(), ny.getNumber() },
					locVar, from, to);
			ac.getCurve().setLabel(label);
			return ac.getOutput();
		}
		App.debug("InvalidFunction:"
				+ exp.toString(StringTemplate.defaultTemplate));
		throw new MyError(kernel.getApplication().getLocalization(),
				"InvalidFunction");

	}

	/**
	 * @param ve
	 *            expression that might be RHS of parametric equation, eg (t,t)
	 * @param fallback
	 *            what to return if ve is not parametric equation
	 * @return parametric curve (or line, conic) or fallback
	 */
	public ValidExpression checkParametricEquationF(ValidExpression ve,
			ValidExpression fallback, Construction cons) {
		CollectUndefinedVariables collecter = new Traversing.CollectUndefinedVariables();
		ve.traverse(collecter);
		final TreeSet<String> undefinedVariables = collecter.getResult();
		if (undefinedVariables.size() == 1) {
			try {
				String varName = undefinedVariables.first();
				FunctionVariable fv = new FunctionVariable(kernel, varName);
				ExpressionNode exp = ve
						.deepCopy(kernel)
						.traverse(
								VariableReplacer.getReplacer(varName, fv,
										kernel)).wrap();
				exp.resolveVariables();
				boolean flag = cons.isSuppressLabelsActive();
				cons.setSuppressLabelCreation(true);
				GeoElement[] ret = processParametricFunction(exp,
						exp.evaluate(StringTemplate.defaultTemplate), fv, null);
				cons.setSuppressLabelCreation(flag);
				if (ret != null) {
					return ret[0].wrap();
				}
			} catch (Throwable t) {
				t.printStackTrace();
				Log.debug("X is not parametric");
			}
		}
		return fallback;
	}

	public GeoElement[] processXEquation(Equation equ) {
		CollectUndefinedVariables collecter = new Traversing.CollectUndefinedVariables();
		equ.traverse(collecter);
		final TreeSet<String> undefinedVariables = collecter.getResult();
		String varName = this.getPreferredName(undefinedVariables);
		FunctionVariable fv = new FunctionVariable(kernel, varName);
		ExpressionNode exp = equ.getRHS().deepCopy(kernel)
				.traverse(VariableReplacer.getReplacer(varName, fv, kernel))
				.wrap();
		exp.resolveVariables();
		GeoElement[] ret = processParametricFunction(exp,
				exp.evaluate(StringTemplate.defaultTemplate), fv,
				equ.getLabel());
		return ret;
	}
}
