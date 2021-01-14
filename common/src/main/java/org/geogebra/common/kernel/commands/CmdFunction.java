package org.geogebra.common.kernel.commands;

import java.util.ArrayList;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.algos.AlgoFunctionFreehand;
import org.geogebra.common.kernel.algos.AlgoFunctionInterval;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.Traversing.VariablePolyReplacer;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;

/**
 * Function[ &lt;GeoFunction>, &lt;NumberValue>, &lt;NumberValue> ]
 */
public class CmdFunction extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFunction(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		switch (n) {
		case 0:
			return CmdDataFunction.emptyFunction(kernel, c.getLabel());
		case 1:
			GeoElement[] arg = resArgs(c);
			if (arg[0].isGeoList()) {

				AlgoFunctionFreehand algo = new AlgoFunctionFreehand(cons,
						c.getLabel(), (GeoList) arg[0]);

				GeoElement[] ret = { algo.getFunction() };
				return ret;
			}
			throw argErr(c, arg[0]);
		case 4:
			String varName = c.getArgument(1).toString(StringTemplate.defaultTemplate);
			c.setArgument(1, c.getArgument(2));
			c.setArgument(2, c.getArgument(3));
			return proces1VarFunction(c, varName, info);
		case 3:

			// file might be saved with old Function[sin(x),1,2]
			return proces1VarFunction(c, null, info);
		case 7:
			return process2VarFunction(c);
		case 5:
			return process2VarFunctionXY(c);
		default:
			throw argNumErr(c);
		}
	}

	private GeoElement[] process2VarFunctionXY(Command c) {
		GeoElement[] arg = resArgs(c);
		boolean[] ok = new boolean[c.getArgumentNumber()];
		if ((ok[0] = (arg[0] instanceof GeoFunctionNVar)) // function
				&& (ok[1] = arg[1] instanceof GeoNumberValue) // x from
				&& (ok[2] = arg[2] instanceof GeoNumberValue) // x to
				&& (ok[3] = arg[3] instanceof GeoNumberValue) // y from
				&& (ok[4] = arg[4] instanceof GeoNumberValue) // y to

		) {
			GeoElement[] ret = { kernel.getManager3D().function2Var(
					c.getLabel(), (GeoFunctionNVar) arg[0],
					(GeoNumberValue) arg[1], (GeoNumberValue) arg[2],
					(GeoNumberValue) arg[3], (GeoNumberValue) arg[4]) };
			return ret;
		}
		throw argErr(c, getBadArg(ok, arg));
	}

	private GeoElement[] proces1VarFunction(Command c, String varNameOverride, EvalInfo info) {
		String varName = varNameOverride;
		boolean[] ok = new boolean[c.getArgumentNumber()];
		GeoElement[] arg;
		FunctionVariable fv;
		boolean mayUseIndependent;
		if (!cons.isFileLoading()) {
			fv = null;
			if (varName != null || kernel.getConstruction()
					.getRegisteredFunctionVariable() != null) {
				if (varName == null) {
					varName = kernel.getConstruction()
							.getRegisteredFunctionVariable();
				}
				fv = new FunctionVariable(kernel, varName);
				int r = c.getArgument(0).replaceVariables(varName, fv);
				c.getArgument(0).replaceVariables(varName, fv);
				if (r > 0) {
					c.getArgument(1).replaceVariables(varName, fv);
				}
			}
			// new code: convert Function[sin(x),1,2] to If[1<=x<=2, sin(x)]

			arg = resArgs(c);
			if ((ok[0] = (arg[0].isRealValuedFunction()))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))
					&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {

				String label = c.getLabel();

				GeoFunctionable geoFun = (GeoFunctionable) arg[0];
				GeoNumberValue low = (GeoNumberValue) arg[1];
				GeoNumberValue high = (GeoNumberValue) arg[2];

				if (fv == null) {
					fv = new FunctionVariable(kernel);
				}

				// construct the equivalent of parsing a<=x<=b
				ExpressionNode left = new ExpressionNode(kernel, low,
						Operation.LESS_EQUAL, fv);
				ExpressionNode right = new ExpressionNode(kernel, fv,
						Operation.LESS_EQUAL, high);
				ExpressionNode interval = new ExpressionNode(kernel, left,
						Operation.AND_INTERVAL, right);
				Function intervalFun = new Function(interval, fv);
				AlgoDependentFunction intervalAlgo = new AlgoDependentFunction(
						cons, intervalFun, false);
				GeoFunction intervalGeo = intervalAlgo.getFunction();

				ArrayList<GeoFunction> conditions = new ArrayList<>();
				conditions.add(intervalGeo);
				mayUseIndependent = false;

				// copied from CmdIf from here

				ExpressionNode expr = new ExpressionNode(kernel,
						wrap(conditions.get(0), fv, mayUseIndependent),
						Operation.IF, wrap(geoFun, fv, mayUseIndependent));

				Function fun = new Function(expr, fv);
				GeoFunction gf;
				if (mayUseIndependent) {
					gf = new GeoFunction(cons, fun);

				} else {
					AlgoDependentFunction algo = new AlgoDependentFunction(
							cons, fun, true);
					gf = algo.getFunction();
				}
				gf.setLabel(label);
				gf.validate(label == null);
				return new GeoElement[] { gf };

			}
			throw argErr(c, getBadArg(ok, arg));
		}

		// old code, just for when file loading
		EvalInfo argInfo = info.withLabels(false);
		if (varName != null || kernel.getConstruction()
				.getRegisteredFunctionVariable() != null) {
			if (varName == null) {
				varName = kernel.getConstruction()
						.getRegisteredFunctionVariable();
			}
			fv = new FunctionVariable(kernel, varName);
			int r = c.getArgument(0).replaceVariables(varName, fv);
			c.getArgument(0).replaceVariables(varName, fv);
			if (r > 0) {
				final boolean oldFlag = kernel.getConstruction()
						.isSuppressLabelsActive();
				kernel.getConstruction().setSuppressLabelCreation(true);

				c.getArgument(1).resolveVariables(argInfo);
				c.getArgument(2).resolveVariables(argInfo);
				EvalInfo silent = new EvalInfo(false);
				GeoFunction condFun;
				if (c.getArgument(0).unwrap() instanceof Command) {
					condFun = (GeoFunction) kernel.getAlgebraProcessor()
							.processCommand(
									(Command) c.getArgument(0).unwrap(),
									silent)[0];
				} else {
					c.getArgument(0).resolveVariables(argInfo);
					condFun = (GeoFunction) kernel.getAlgebraProcessor()
							.processFunction(
									new Function(c.getArgument(0), fv),
									silent)[0];
				}
				GeoElement low = kernel.getAlgebraProcessor()
						.processExpressionNode(c.getArgument(1), silent)[0];
				GeoElement high = kernel.getAlgebraProcessor()
						.processExpressionNode(c.getArgument(2), silent)[0];
				if (!(low instanceof NumberValue)) {
					throw argErr(c, low);
				}
				if (!(high instanceof NumberValue)) {
					throw argErr(c, high);
				}
				c.getArgument(1).replaceVariables(varName, fv);
				c.getArgument(0).resolveVariables(argInfo);

				kernel.getConstruction().setSuppressLabelCreation(oldFlag);
				return new GeoElement[] { function(c.getLabel(), condFun,
						(GeoNumberValue) low, (GeoNumberValue) high) };
			}
		}
		arg = resArgs(c);
		if ((ok[0] = (arg[0].isRealValuedFunction()))
				&& (ok[1] = (arg[1] instanceof GeoNumberValue))
				&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
			GeoElement[] ret = { function(c.getLabel(),
					(GeoFunctionable) arg[0],
					(GeoNumberValue) arg[1], (GeoNumberValue) arg[2]) };
			return ret;
		}
		throw argErr(c, getBadArg(ok, arg));
	}

	private GeoElement[] process2VarFunction(Command c) {
		// create local variable at position 3 and resolve arguments
		GeoElement[] arg = resArgsLocalNumVar(c, new int[] { 1, 4 }, new int[] { 2, 5 });
		boolean[] ok = new boolean[c.getArgumentNumber()];
		if ((ok[0] = (arg[0] instanceof GeoNumberValue
				|| arg[0] instanceof GeoFunctionNVar)) // function
				&& (ok[1] = arg[1].isGeoNumeric()) // first var
				&& (ok[2] = arg[2] instanceof GeoNumberValue) // from
				&& (ok[3] = arg[3] instanceof GeoNumberValue) // to
				&& (ok[4] = arg[4].isGeoNumeric()) // second var
				&& (ok[5] = arg[5] instanceof GeoNumberValue) // from
				&& (ok[6] = arg[6] instanceof GeoNumberValue) // to
		) {
			if (arg[0] instanceof GeoFunctionNVar) {
				if ("x".equals(arg[1].getLabelSimple())) {
					GeoElement[] ret = {
							kernel.getManager3D().function2Var(
									c.getLabel(), (GeoFunctionNVar) arg[0],
									(GeoNumberValue) arg[2],
									(GeoNumberValue) arg[3],
									(GeoNumberValue) arg[5],
									(GeoNumberValue) arg[6]) };
					return ret;
				}
				GeoElement[] ret = { kernel.getManager3D().function2Var(
						c.getLabel(), (GeoFunctionNVar) arg[0],
						(GeoNumberValue) arg[2], (GeoNumberValue) arg[3],
						(GeoNumberValue) arg[5], (GeoNumberValue) arg[6]) };
				return ret;
			}
			GeoElement[] ret = { kernel.getManager3D().function2Var(
					c.getLabel(), (GeoNumberValue) arg[0],
					(GeoNumeric) arg[1], (GeoNumberValue) arg[2],
					(GeoNumberValue) arg[3], (GeoNumeric) arg[4],
					(GeoNumberValue) arg[5], (GeoNumberValue) arg[6]) };
			return ret;
		}

		throw argErr(c, getBadArg(ok, arg));
	}

	private ExpressionNode wrap(GeoFunctionable boolFun, FunctionVariable fv,
			boolean mayUseIndependent) {
		if (!mayUseIndependent) {
			return new ExpressionNode(kernel, boolFun, Operation.FUNCTION, fv);
		}
		return boolFun.getFunction().getFunctionExpression()
				.deepCopy(kernel).traverse(VariablePolyReplacer.getReplacer(fv))
				.wrap();
	}

	/**
	 * function limited to interval [a, b]
	 */
	final private GeoFunction function(String label, GeoFunctionable f,
			GeoNumberValue a, GeoNumberValue b) {
		AlgoFunctionInterval algo = new AlgoFunctionInterval(cons, f, a,
				b);
		GeoFunction g = algo.getFunction();
		g.setLabel(label);
		return g;
	}
}
