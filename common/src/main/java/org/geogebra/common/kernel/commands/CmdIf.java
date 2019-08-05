package org.geogebra.common.kernel.commands;

import java.util.ArrayList;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.algos.AlgoDependentFunctionNVar;
import org.geogebra.common.kernel.algos.AlgoIf;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.GetItem;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.arithmetic.Traversing.VariablePolyReplacer;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * If[ &lt;GeoBoolean>, &lt;GeoElement> ]
 * 
 * If[ &lt;GeoBoolean>, &lt;GeoElement>, &lt;GeoElement> ]
 */
public class CmdIf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIf(Kernel kernel) {
		super(kernel);
	}

	// @Override
	// public ExpressionValue simplify(Command c) {
	// return expandIf(kernelA, c);
	// }

	/**
	 * @param kernelA
	 *            kernel
	 * @param c
	 *            collection of arguments
	 * @return node with OPERATION_IF_ELSE or OPERATION_IF_LIST
	 */
	public static ExpressionNode expandIf(Kernel kernelA, GetItem c) {
		MyList conditions = new MyList(kernelA);
		MyList alternatives = new MyList(kernelA);
		int num = c.getLength();
		if (num == 3) {
			return new ExpressionNode(kernelA,
					new MyNumberPair(kernelA, c.getItem(0), c.getItem(1)),
					Operation.IF_ELSE, c.getItem(2));
		}
		for (int i = 0; i < num - 1; i += 2) {
			conditions.addListElement(c.getItem(i));
		}
		for (int i = 1; i < num; i += 2) {
			alternatives.addListElement(c.getItem(i));
		}
		if (MyDouble.isOdd(num)) {
			alternatives.addListElement(c.getItem(num - 1));
		}
		Log.debug(conditions.size() + ":" + alternatives.size());
		return new ExpressionNode(kernelA, conditions, Operation.IF_LIST,
				alternatives);

	}

	// @Override
	/*
	 * public ExpressionValue simplify(Command c) { ExpressionNode ret = null;
	 * if (c.getArgumentNumber() == 2) { ret = new ExpressionNode(kernelA,
	 * c.getArgument(0), Operation.IF, c.getArgument(1)); } if
	 * (c.getArgumentNumber() == 3) { ret = new ExpressionNode(kernelA, new
	 * MyNumberPair(kernelA, c.getArgument(0), c.getArgument(1)),
	 * Operation.IF_ELSE, c.getArgument(2)); } if (ret != null) {
	 * ret.resolveVariables(); return ret; } return super.simplify(c); }
	 */
	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		if (n < 2) {
			throw argNumErr(c);
		}

		if (kernel.getConstruction().getRegisteredFunctionVariable() != null) {
			String[] varName = kernel.getConstruction()
					.getRegisteredFunctionVariables();
			FunctionVariable[] fv = new FunctionVariable[varName.length];

			int r = kernel.getAlgebraProcessor()
					.replaceVariables(c.getArgument(0), varName, fv);
			if (r > 0) {
				return specialFunction(c, varName, fv, info);
			}
		}
		arg = resArgs(c, info);
		if (arg[0] instanceof GeoBoolean) {
			// standard case: simple boolean condition
			ArrayList<GeoBoolean> cond = new ArrayList<>();
			ArrayList<GeoElement> alternatives = new ArrayList<>();
			for (int i = 0; i < n - 1; i += 2) {
				if (arg[i] instanceof GeoBoolean) {
					cond.add((GeoBoolean) arg[i]);
				} else {
					throw argErr(c, arg[i]);
				}
				alternatives.add(arg[i + 1]);
			}
			if (MyDouble.isOdd(n)) {
				alternatives.add(arg[n - 1]);
			}
			return new AlgoIf(cons, c.getLabel(), cond, alternatives)
					.getOutput();
		}
		// SPECIAL CASE for functions:
		// boolean function in x as condition
		// example: If[ x < 2, x^2, x + 2 ]
		// DO NOT change instanceof here (see
		// GeoFunction.isGeoFunctionable())

		ArrayList<FunctionalNVar> conditions = new ArrayList<>();
		ArrayList<FunctionalNVar> functions = new ArrayList<>();
		int vars = 1;
		for (int i = 0; i < n - 1; i += 2) {
			if (arg[i] instanceof FunctionalNVar
					&& ((FunctionalNVar) arg[i]).isBooleanFunction()) {
				conditions.add((FunctionalNVar) arg[i]);
				vars = vars > 1 ? vars
						: ((FunctionalNVar) arg[i])
								.getFunctionVariables().length;
				if ("y".equals(((FunctionalNVar) arg[i])
						.getVarString(StringTemplate.defaultTemplate))) {
					vars = Math.max(vars, 2);
				}
			} else {
				throw argErr(c, arg[i]);
			}
			vars = checkAdd(c, functions, arg[i + 1], vars);
		}
		if (MyDouble.isOdd(n)) {
			vars = checkAdd(c, functions, arg[n - 1], vars);
		}
		return new GeoElement[] {
				functionIf(c.getLabel(), conditions, functions, vars) };

	}

	private int checkAdd(Command c, ArrayList<FunctionalNVar> functions,
			GeoElement fn, int vars) {
		if (fn.isRealValuedFunction() && !(fn instanceof GeoLine)) {
			/*
			 * if(vars > 1){ GeoFunctionNVar cast = new
			 * GeoFunctionNVar(kernelA.getConstruction()); cast.set(fn);
			 * functions.add(cast); }else { functions.add(((GeoFunctionable)
			 * fn).getGeoFunction()); }
			 */
			functions.add(((GeoFunctionable) fn).getGeoFunction());
			if ("y".equals(((GeoFunctionable) fn).getGeoFunction()
					.getVarString(StringTemplate.defaultTemplate))) {
				return Math.max(vars, 2);
			}
			return vars;
		} else if (fn instanceof GeoFunctionNVar) {
			functions.add((GeoFunctionNVar) fn);
			return 2;
		} else {
			throw argErr(c, fn);
		}

	}

	private GeoElement[] specialFunction(Command c, String[] varName,
			FunctionVariable[] fv, EvalInfo info) {
		EvalInfo argInfo = info.withLabels(false);
		boolean oldFlag = kernel.getConstruction().isSuppressLabelsActive();
		kernel.getConstruction().setSuppressLabelCreation(true);
		ArrayList<FunctionalNVar> conditions = new ArrayList<>();
		ArrayList<FunctionalNVar> functions = new ArrayList<>();

		int n = c.getArgumentNumber();
		int vars = varName.length;
		for (int i = 0; i < n - 1; i += 2) {
			kernel.getAlgebraProcessor().replaceVariables(c.getArgument(i),
					varName, fv);
			FunctionalNVar current = resolveFunction(c, i, fv, vars, argInfo);
			if (current.isBooleanFunction()) {
				conditions.add(current);
			} else {
				throw argErr(c, current);
			}
			kernel.getAlgebraProcessor().replaceVariables(c.getArgument(i + 1),
					varName, fv);
			vars = checkAdd(c, functions,
					(GeoElement) resolveFunction(c, i + 1, fv, vars, argInfo),
					vars);
		}
		if (MyDouble.isOdd(n)) {
			kernel.getAlgebraProcessor().replaceVariables(c.getArgument(n - 1),
					varName, fv);
			vars = checkAdd(c, functions,
					(GeoElement) resolveFunction(c, n - 1, fv, vars, argInfo),
					vars);
		}
		kernel.getConstruction().setSuppressLabelCreation(oldFlag);
		return new GeoElement[] {
				functionIf(c.getLabel(), conditions, functions, vars) };
	}

	private FunctionalNVar resolveFunction(Command c, int i,
			FunctionVariable[] fv, int vars, EvalInfo argInfo) {
		ExpressionNode arg = c.getArgument(i);
		arg.resolveVariables(argInfo);
		// If we have a ready function rather than expression, just use it #4674
		if (arg.unwrap() instanceof GeoFunction
				|| arg.unwrap() instanceof GeoFunctionNVar) {
			return (FunctionalNVar) arg.unwrap();
		}
		EvalInfo info = new EvalInfo(false);
		if (vars < 2) {
			return (GeoFunction) kernel.getAlgebraProcessor()
					.processFunction(new Function(arg, fv[0]), info)[0];
		}
		return (GeoFunctionNVar) kernel.getAlgebraProcessor()
				.processFunctionNVar(new FunctionNVar(arg, fv), info)[0];
	}

	/**
	 * If-then-else construct for functions. example: If[ x < 2, x^2, x + 2 ]
	 */
	final private GeoElement functionIf(String label,
			ArrayList<FunctionalNVar> conditions,
			ArrayList<FunctionalNVar> functions, int vars) {
		FunctionVariable[] fv;
		if (vars == conditions.get(0).getFunctionVariables().length) {
			fv = conditions.get(0).getFunctionVariables();
		} else if (cons.getRegisteredFunctionVariable() != null) {
			int regVars = cons.getRegisteredFunctionVariables().length;
			fv = new FunctionVariable[regVars];
			for (int i = 0; i < fv.length; i++) {
				fv[i] = new FunctionVariable(kernel,
						cons.getRegisteredFunctionVariables()[i]);
			}
		} else {
			fv = new FunctionVariable[vars];
			for (int i = 0; i < fv.length; i++) {
				fv[i] = new FunctionVariable(kernel, ((char) ('x' + i)) + "");
			}
		}

		ExpressionNode expr;

		boolean mayUseIndependent = true;
		for (int i = 0; i < functions.size(); i++) {
			if (Inspecting.dynamicGeosFinder.check(functions.get(i))
					|| (i < conditions.size() && Inspecting.dynamicGeosFinder
							.check(conditions.get(i)))) {
				mayUseIndependent = false;
				break;
			}
		}

		if (functions.size() == 1) {
			expr = new ExpressionNode(kernel,
					wrap(conditions.get(0), fv, mayUseIndependent),
					Operation.IF,
					wrap(functions.get(0), fv, mayUseIndependent));
		} else if (functions.size() == 2 && conditions.size() == 1) {
			expr = new ExpressionNode(kernel,
					new MyNumberPair(kernel,
							wrap(conditions.get(0), fv, mayUseIndependent),
							wrap(functions.get(0), fv, mayUseIndependent)),
					Operation.IF_ELSE,
					wrap(functions.get(1), fv, mayUseIndependent));
		} else {
			MyList cond = new MyList(kernel), funs = new MyList(kernel);
			for (FunctionalNVar f : conditions) {
				cond.addListElement(wrap(f, fv, mayUseIndependent));
			}
			for (FunctionalNVar f : functions) {
				funs.addListElement(wrap(f, fv, mayUseIndependent));
			}
			expr = new ExpressionNode(kernel, cond, Operation.IF_LIST, funs);
		}
		if (vars < 2) {
			Function fun = new Function(expr, fv[0]);
			GeoFunction gf;
			if (mayUseIndependent) {
				gf = new GeoFunction(cons, fun);

			} else {
				AlgoDependentFunction algo = new AlgoDependentFunction(cons,
						fun, true);
				gf = algo.getFunction();
			}
			if (gf.validate(label == null)) {
				gf.setLabel(label);
				gf.validate(label == null);
				return gf;
			}
			throw new MyError(loc, Errors.InvalidFunction);
		}
		FunctionNVar fun = new FunctionNVar(expr, fv);
		if (mayUseIndependent) {
			GeoFunctionNVar ret = new GeoFunctionNVar(cons, fun);
			ret.setLabel(label);
			return ret;
		}
		AlgoDependentFunctionNVar algo = new AlgoDependentFunctionNVar(cons,
				fun);
		algo.getFunction().setLabel(label);
		return algo.getFunction();
	}

	private ExpressionNode wrap(FunctionalNVar boolFun, FunctionVariable[] fv,
			boolean mayUseIndependent) {
		if (!mayUseIndependent) {
			if (fv.length == 1) {
				return new ExpressionNode(kernel, boolFun, Operation.FUNCTION,
						fv[0]);
			}
			MyList arg = new MyList(kernel);
			for (int i = 0; i < fv.length; i++) {
				arg.addListElement(fv[i]);
			}
			return new ExpressionNode(kernel, boolFun, Operation.FUNCTION_NVAR,
					arg);
		}
		ExpressionValue exp = boolFun.getFunctionExpression().deepCopy(kernel);
		for (int i = 0; i < fv.length; i++) {
			exp = exp.traverse(VariablePolyReplacer.getReplacer(fv[i]));
		}
		return exp.wrap();
	}
}
