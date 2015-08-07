package org.geogebra.common.kernel.commands;

import java.util.ArrayList;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.algos.AlgoFunctionFreehand;
import org.geogebra.common.kernel.algos.AlgoFunctionInterval;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.Traversing.VariablePolyReplacer;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * Function[ <GeoFunction>, <NumberValue>, <NumberValue> ]
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
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];

		String varName = null;
		ExpressionNode expr;
		boolean mayUseIndependent;
		String label;
		FunctionVariable fv;
		switch (n) {
		case 0:
			fv = new FunctionVariable(kernelA);
			ExpressionValue en = CmdDataFunction.getDataFunction(kernelA,
					c.getLabel(),
 new MyList(app.getKernel()),
					new MyList(app.getKernel()), null, fv);
			GeoFunction geo = new GeoFunction(en.wrap(), fv);
			geo.setLabel(c.getLabel());
			return new GeoElement[] { geo };
		case 1:
			GeoElement[] arg = resArgs(c);
			if (arg[0].isGeoList()) {

				AlgoFunctionFreehand algo = new AlgoFunctionFreehand(cons,
						c.getLabel(), (GeoList) arg[0]);

				GeoElement[] ret = { algo.getFunction() };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
		case 4:
			varName = c.getArgument(1).toString(StringTemplate.defaultTemplate);
			c.setArgument(1, c.getArgument(2));
			c.setArgument(2, c.getArgument(3));
			// fall through
		case 3:

			// file might be saved with old Function[sin(x),1,2]
			if (!cons.isFileLoading()) {
				fv = null;
				if (varName != null
						|| kernelA.getConstruction()
								.getRegisteredFunctionVariable() != null) {
					if (varName == null)
						varName = kernelA.getConstruction()
								.getRegisteredFunctionVariable();
					fv = new FunctionVariable(kernelA, varName);
					int r = c.getArgument(0).replaceVariables(varName, fv);
					c.getArgument(0).replaceVariables(varName, fv);
					if (r > 0) {
						c.getArgument(1).replaceVariables(varName, fv);
					}
				}
				// new code: convert Function[sin(x),1,2] to If[1<=x<=2, sin(x)]

				arg = resArgs(c);
				if ((ok[0] = (arg[0].isGeoFunctionable()))
						&& (ok[1] = (arg[1] instanceof GeoNumberValue))
						&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {

					label = c.getLabel();

					GeoFunction geoFun = ((GeoFunctionable) arg[0])
							.getGeoFunction();
					GeoNumberValue low = (GeoNumberValue) arg[1];
					GeoNumberValue high = (GeoNumberValue) arg[2];

					if (fv == null) {
						fv = new FunctionVariable(kernelA);
					}

					// construct the equivalent of parsing a<=x<=b
					ExpressionNode left = new ExpressionNode(kernelA, low,
							Operation.LESS_EQUAL, fv);
					ExpressionNode right = new ExpressionNode(kernelA, fv,
							Operation.LESS_EQUAL, high);
					ExpressionNode interval = new ExpressionNode(kernelA, left,
							Operation.AND_INTERVAL, right);
					Function intervalFun = new Function(interval, fv);
					AlgoDependentFunction intervalAlgo = new AlgoDependentFunction(
							cons, intervalFun);
					cons.removeFromConstructionList(intervalAlgo);
					GeoFunction intervalGeo = intervalAlgo.getFunction();

					ArrayList<GeoFunction> conditions = new ArrayList<GeoFunction>();
					conditions.add(intervalGeo);
					mayUseIndependent = false;

					// copied from CmdIf from here

					expr = new ExpressionNode(kernelA, wrap(conditions.get(0),
							fv, mayUseIndependent), Operation.IF, wrap(geoFun,
							fv, mayUseIndependent));
					Log.debug(expr);
					Log.debug(arg[0]);
					Function fun = new Function(expr, fv);
					if (mayUseIndependent) {
						return new GeoElement[] { new GeoFunction(cons, label,
								fun) };
					}
					AlgoDependentFunction algo = new AlgoDependentFunction(
							cons, label, fun);
					return new GeoElement[] { algo.getFunction() };

				}
				throw argErr(app, c.getName(), getBadArg(ok, arg));

			}

			// old code, just for when file loading

			if (varName != null
					|| kernelA.getConstruction()
							.getRegisteredFunctionVariable() != null) {
				if (varName == null)
					varName = kernelA.getConstruction()
							.getRegisteredFunctionVariable();
				fv = new FunctionVariable(kernelA, varName);
				int r = c.getArgument(0).replaceVariables(varName, fv);
				c.getArgument(0).replaceVariables(varName, fv);
				if (r > 0) {
					boolean oldFlag = kernelA.getConstruction()
							.isSuppressLabelsActive();
					kernelA.getConstruction().setSuppressLabelCreation(true);

					c.getArgument(1).resolveVariables();
					c.getArgument(2).resolveVariables();

					GeoFunction condFun;
					if (c.getArgument(0).unwrap() instanceof Command) {
						condFun = (GeoFunction) kernelA.getAlgebraProcessor()
								.processCommand(
										(Command) c.getArgument(0).unwrap(),
										true)[0];
					} else {
						c.getArgument(0).resolveVariables();
						condFun = (GeoFunction) kernelA.getAlgebraProcessor()
								.processFunction(
										new Function(c.getArgument(0), fv))[0];
					}
					GeoElement low = kernelA.getAlgebraProcessor()
							.processExpressionNode(c.getArgument(1))[0];
					GeoElement high = kernelA.getAlgebraProcessor()
							.processExpressionNode(c.getArgument(2))[0];
					if (!(low instanceof NumberValue))
						throw argErr(app, c.getName(), low);
					if (!(high instanceof NumberValue))
						throw argErr(app, c.getName(), high);
					c.getArgument(1).replaceVariables(varName, fv);
					c.getArgument(0).resolveVariables();

					kernelA.getConstruction().setSuppressLabelCreation(oldFlag);
					return new GeoElement[] { Function(c.getLabel(), condFun,
							(NumberValue) low, (NumberValue) high) };
				}
			}
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))
					&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
				GeoElement[] ret = { Function(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2]) };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	private ExpressionNode wrap(GeoFunction boolFun, FunctionVariable fv,
			boolean mayUseIndependent) {
		if (!mayUseIndependent) {
			return new ExpressionNode(kernelA, boolFun, Operation.FUNCTION, fv);
		}
		return boolFun.getFunctionExpression().deepCopy(kernelA)
				.traverse(VariablePolyReplacer.getReplacer(fv)).wrap();
	}

	/**
	 * function limited to interval [a, b]
	 */
	final private GeoFunction Function(String label, GeoFunction f,
			NumberValue a, NumberValue b) {
		AlgoFunctionInterval algo = new AlgoFunctionInterval(cons, label, f, a,
				b);
		GeoFunction g = algo.getFunction();
		return g;
	}
}