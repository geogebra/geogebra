package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoDependentFunction;
import geogebra.common.kernel.algos.AlgoIf;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.Inspecting;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.MyNumberPair;
import geogebra.common.kernel.arithmetic.Traversing.VariablePolyReplacer;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.main.MyError;
import geogebra.common.plugin.Operation;

import java.util.ArrayList;

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

		if (kernelA.getConstruction().getRegistredFunctionVariable() != null) {
			String varName = kernelA.getConstruction()
					.getRegistredFunctionVariable();
			FunctionVariable fv = new FunctionVariable(kernelA, varName);
			int r = c.getArgument(0).replaceVariables(varName, fv);
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

		ArrayList<GeoFunction> conditions = new ArrayList<GeoFunction>();
		ArrayList<GeoFunction> functions = new ArrayList<GeoFunction>();
		for (int i = 0; i < n - 1; i += 2) {
			if (arg[i] instanceof GeoFunction
					&& ((GeoFunction) arg[i]).isBooleanFunction()) {
				conditions.add((GeoFunction) arg[i]);
			} else {
				throw argErr(app, c.getName(), arg[i]);
			}
			checkAdd(c, functions, arg[i + 1]);
		}
		if (n % 2 == 1) {
			checkAdd(c, functions, arg[n - 1]);
		}
		return new GeoElement[] { If(c.getLabel(), conditions, functions) };

	}

	private void checkAdd(Command c, ArrayList<GeoFunction> functions,
			GeoElement fn) {
		if (fn.isGeoFunctionable() && !(fn instanceof GeoLine)) {
			functions.add(((GeoFunctionable) fn).getGeoFunction());
		} else {
			throw argErr(app, c.getName(), fn);
		}

	}

	private GeoElement[] specialFunction(Command c, String varName,
			FunctionVariable fv) {

		boolean oldFlag = kernelA.getConstruction().isSuppressLabelsActive();
		kernelA.getConstruction().setSuppressLabelCreation(true);
		ArrayList<GeoFunction> conditions = new ArrayList<GeoFunction>();
		ArrayList<GeoFunction> functions = new ArrayList<GeoFunction>();
		
		
		int n = c.getArgumentNumber();
		
		for (int i = 0; i < n - 1; i += 2) {
			c.getArgument(i).replaceVariables(varName, fv);
			GeoFunction current = resolveFunction(c, i, fv);
			if (current.isBooleanFunction()) {
				conditions.add(current);
			} else {
				throw argErr(app, c.getName(), current);
			}
			c.getArgument(i+1).replaceVariables(varName, fv);
			checkAdd(c, functions, resolveFunction(c, i+1, fv));
		}
		if (n % 2 == 1) {
			c.getArgument(n-1).replaceVariables(varName, fv);
			checkAdd(c, functions, resolveFunction(c, n - 1, fv));
		}

		kernelA.getConstruction().setSuppressLabelCreation(oldFlag);
		return new GeoElement[] { If(c.getLabel(), conditions, functions) };
	}

	private GeoFunction resolveFunction(Command c, int i, FunctionVariable fv) {
		c.getArgument(i).resolveVariables();
		return (GeoFunction) kernelA.getAlgebraProcessor().processFunction(
				new Function(c.getArgument(i), fv))[0];
	}

	/**
	 * If-then-else construct for functions. example: If[ x < 2, x^2, x + 2 ]
	 */
	final private GeoFunction If(String label,
			ArrayList<GeoFunction> conditions, ArrayList<GeoFunction> functions) {
		FunctionVariable fv = conditions.get(0).getFunctionVariables()[0];
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
			for (GeoFunction f : conditions) {
				cond.addListElement(wrap(f, fv, mayUseIndependent));
			}
			for (GeoFunction f : functions) {
				funs.addListElement(wrap(f, fv, mayUseIndependent));
			}
			expr = new ExpressionNode(kernelA, cond, Operation.IF_LIST, funs);
		}
		Function fun = new Function(expr, fv);
		if (mayUseIndependent) {
			return new GeoFunction(cons, label, fun);
		}
		AlgoDependentFunction algo = new AlgoDependentFunction(cons, label, fun);
		return algo.getFunction();
	}

	private ExpressionNode wrap(GeoFunction boolFun, FunctionVariable fv,
			boolean mayUseIndependent) {
		if (!mayUseIndependent) {
			return new ExpressionNode(kernelA, boolFun, Operation.FUNCTION, fv);
		}
		return boolFun.getFunctionExpression().deepCopy(kernelA)
				.traverse(VariablePolyReplacer.getReplacer(fv)).wrap();
	}
}
