package org.geogebra.common.kernel.commands;

import java.util.ArrayList;

import org.geogebra.common.kernel.Kernel;
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
import org.geogebra.common.kernel.arithmetic.Inspecting;
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
import org.geogebra.common.plugin.Operation;

/**
 * If[ <GeoBoolean>, <GeoElement> ] If[ <GeoBoolean>, <GeoElement>, <GeoElement>
 * ]
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

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		if (n < 2) {
			throw argNumErr(app, c.getName(), n);
		}

		if (kernelA.getConstruction().getRegisteredFunctionVariable() != null) {
			String[] varName = kernelA.getConstruction()
					.getRegisteredFunctionVariables();
			FunctionVariable[] fv = new FunctionVariable[varName.length];

			int r = replaceVariables(c.getArgument(0), varName, fv);
			if (r > 0) {
				return specialFunction(c, varName, fv);
			}
		}
		arg = resArgs(c);
		if (arg[0] instanceof GeoBoolean) {
			// standard case: simple boolean condition
			ArrayList<GeoBoolean> cond = new ArrayList();
			ArrayList<GeoElement> alternatives = new ArrayList();
			for (int i = 0; i < n - 1; i += 2) {
				if (arg[i] instanceof GeoBoolean) {
					cond.add((GeoBoolean) arg[i]);
				} else {
					throw argErr(app, c.getName(), arg[i]);
				}
				alternatives.add(arg[i + 1]);
			}
			if (n % 2 == 1) {
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

		ArrayList<FunctionalNVar> conditions = new ArrayList<FunctionalNVar>();
		ArrayList<FunctionalNVar> functions = new ArrayList<FunctionalNVar>();
		int vars = 1;
		for (int i = 0; i < n - 1; i += 2) {
			if (arg[i] instanceof FunctionalNVar
					&& ((FunctionalNVar) arg[i]).isBooleanFunction()) {
				conditions.add((FunctionalNVar) arg[i]);
				vars = vars > 1 ? vars : ((FunctionalNVar) arg[i])
						.getFunctionVariables().length;
			} else {
				throw argErr(app, c.getName(), arg[i]);
			}
			vars = checkAdd(c, functions, arg[i + 1], vars);
		}
		if (n % 2 == 1) {
			vars = checkAdd(c, functions, arg[n - 1], vars);
		}
		return new GeoElement[] { If(c.getLabel(), conditions, functions, vars) };

	}

	private int replaceVariables(ExpressionNode argument, String[] varName,
			FunctionVariable[] fv) {
		int rep = 0;
		for (int i = 0; i < varName.length; i++) {
			if (fv[i] == null) {
				fv[i] = new FunctionVariable(kernelA, varName[i]);
			}
			rep += argument.replaceVariables(varName[i], fv[i]);
		}
		return rep;
	}

	private int checkAdd(Command c, ArrayList<FunctionalNVar> functions,
			GeoElement fn, int vars) {
		if (fn.isGeoFunctionable() && !(fn instanceof GeoLine)) {
			/*
			 * if(vars > 1){ GeoFunctionNVar cast = new
			 * GeoFunctionNVar(kernelA.getConstruction()); cast.set(fn);
			 * functions.add(cast); }else { functions.add(((GeoFunctionable)
			 * fn).getGeoFunction()); }
			 */
			functions.add(((GeoFunctionable) fn).getGeoFunction());
			return vars;
		} else if (fn instanceof GeoFunctionNVar) {
			functions.add((GeoFunctionNVar) fn);
			return 2;
		} else {
			throw argErr(app, c.getName(), fn);
		}

	}

	private GeoElement[] specialFunction(Command c, String[] varName,
			FunctionVariable[] fv) {

		boolean oldFlag = kernelA.getConstruction().isSuppressLabelsActive();
		kernelA.getConstruction().setSuppressLabelCreation(true);
		ArrayList<FunctionalNVar> conditions = new ArrayList<FunctionalNVar>();
		ArrayList<FunctionalNVar> functions = new ArrayList<FunctionalNVar>();

		int n = c.getArgumentNumber();
		int vars = varName.length;
		for (int i = 0; i < n - 1; i += 2) {
			replaceVariables(c.getArgument(i), varName, fv);
			FunctionalNVar current = resolveFunction(c, i, fv, vars);
			if (current.isBooleanFunction()) {
				conditions.add(current);
			} else {
				throw argErr(app, c.getName(), current);
			}
			replaceVariables(c.getArgument(i + 1), varName, fv);
			checkAdd(c, functions,
					(GeoElement) resolveFunction(c, i + 1, fv, vars), vars);
		}
		if (n % 2 == 1) {
			replaceVariables(c.getArgument(n - 1), varName, fv);
			checkAdd(c, functions,
					(GeoElement) resolveFunction(c, n - 1, fv, vars), vars);
		}

		kernelA.getConstruction().setSuppressLabelCreation(oldFlag);
		return new GeoElement[] { If(c.getLabel(), conditions, functions, vars) };
	}

	private FunctionalNVar resolveFunction(Command c, int i,
			FunctionVariable[] fv, int vars) {
		c.getArgument(i).resolveVariables();
		// If we have a ready function rather than expression, just use it #4674
		if (c.getArgument(i).unwrap() instanceof GeoFunction) {
			return (GeoFunction) c.getArgument(i).unwrap();
		}
		if (vars < 2) {
			return (GeoFunction) kernelA.getAlgebraProcessor().processFunction(
					new Function(c.getArgument(i), fv[0]))[0];
		}
		return (GeoFunctionNVar) kernelA.getAlgebraProcessor()
				.processFunctionNVar(new FunctionNVar(c.getArgument(i), fv))[0];
	}

	/**
	 * If-then-else construct for functions. example: If[ x < 2, x^2, x + 2 ]
	 */
	final private GeoElement If(String label,
			ArrayList<FunctionalNVar> conditions,
			ArrayList<FunctionalNVar> functions, int vars) {
		FunctionVariable[] fv = conditions.get(0).getFunctionVariables();
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
			expr = new ExpressionNode(kernelA, wrap(conditions.get(0), fv,
					mayUseIndependent), Operation.IF, wrap(functions.get(0),
					fv, mayUseIndependent));
		} else if (functions.size() == 2 && conditions.size() == 1) {
			expr = new ExpressionNode(kernelA, new MyNumberPair(kernelA, wrap(
					conditions.get(0), fv, mayUseIndependent), wrap(
					functions.get(0), fv, mayUseIndependent)),
					Operation.IF_ELSE, wrap(functions.get(1), fv,
							mayUseIndependent));
		} else {
			MyList cond = new MyList(kernelA), funs = new MyList(kernelA);
			for (FunctionalNVar f : conditions) {
				cond.addListElement(wrap(f, fv, mayUseIndependent));
			}
			for (FunctionalNVar f : functions) {
				funs.addListElement(wrap(f, fv, mayUseIndependent));
			}
			expr = new ExpressionNode(kernelA, cond, Operation.IF_LIST, funs);
		}
		if (vars < 2) {
			Function fun = new Function(expr, fv[0]);
			if (mayUseIndependent) {
				return new GeoFunction(cons, label, fun);
			}
			AlgoDependentFunction algo = new AlgoDependentFunction(cons, label,
					fun);
			return algo.getFunction();
		}
		FunctionNVar fun = new FunctionNVar(expr, fv);
		if (mayUseIndependent) {
			return new GeoFunctionNVar(cons, label, fun);
		}
		AlgoDependentFunctionNVar algo = new AlgoDependentFunctionNVar(cons,
				label, fun);
		return algo.getFunction();
	}

	private ExpressionNode wrap(FunctionalNVar boolFun, FunctionVariable[] fv,
			boolean mayUseIndependent) {
		if (!mayUseIndependent) {
			if (fv.length == 1) {
				return new ExpressionNode(kernelA, boolFun, Operation.FUNCTION,
						fv[0]);
			}
			MyList arg = new MyList(kernelA);
			for (int i = 0; i < fv.length; i++) {
				arg.addListElement(fv[i]);
			}
			return new ExpressionNode(kernelA, boolFun,
					Operation.FUNCTION_NVAR, arg);
		}
		ExpressionValue exp = boolFun.getFunctionExpression().deepCopy(kernelA);
		for (int i = 0; i < fv.length; i++) {
			exp = exp.traverse(VariablePolyReplacer.getReplacer(fv[i]));
		}
		return exp.wrap();
	}
}
