package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.advanced.AlgoFunctionInterval;
import geogebra.common.kernel.algos.AlgoFunctionFreehand;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

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
		switch (n) {
		case 1:
			GeoElement[] arg = resArgs(c);
			if (arg[0].isGeoList()) {
				
				AlgoFunctionFreehand algo = new AlgoFunctionFreehand(cons, c.getLabel(),
						(GeoList) arg[0]);

				GeoElement[] ret = { algo.getFunction() };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
		case 4:
			varName = c.getArgument(1).toString(StringTemplate.defaultTemplate);
			c.setArgument(1, c.getArgument(2));
			c.setArgument(2, c.getArgument(3));
			//fall through
		case 3:
			if (varName != null
					|| kernelA.getConstruction().getRegistredFunctionVariable() != null) {
				if (varName == null)
					varName = kernelA.getConstruction()
							.getRegistredFunctionVariable();
				FunctionVariable fv = new FunctionVariable(kernelA, varName);
				int r = c.getArgument(0).replaceVariables(varName, fv);
				c.getArgument(0).replaceVariables(varName, fv);
				if (r > 0) {
					boolean oldFlag = kernelA.getConstruction()
							.isSuppressLabelsActive();
					kernelA.getConstruction().setSuppressLabelCreation(true);
					c.getArgument(0).resolveVariables(false);
					c.getArgument(1).resolveVariables(false);
					c.getArgument(2).resolveVariables(false);
					GeoFunction condFun = (GeoFunction) kernelA
							.getAlgebraProcessor().processFunction(
									new Function(c.getArgument(0), fv))[0];
					GeoElement low = kernelA.getAlgebraProcessor()
							.processExpressionNode(c.getArgument(1))[0];
					GeoElement high = kernelA.getAlgebraProcessor()
							.processExpressionNode(c.getArgument(2))[0];
					if (!(low instanceof NumberValue))
						throw argErr(app, c.getName(), low);
					if (!(high instanceof NumberValue))
						throw argErr(app, c.getName(), high);
					c.getArgument(1).replaceVariables(varName, fv);
					c.getArgument(0).resolveVariables(false);

					kernelA.getConstruction().setSuppressLabelCreation(oldFlag);
					return new GeoElement[] { Function(c.getLabel(),
							condFun, (NumberValue) low, (NumberValue) high) };
				}
			}
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = { Function(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2]) };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
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