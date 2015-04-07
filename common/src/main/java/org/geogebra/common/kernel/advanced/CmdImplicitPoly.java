package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.implicit.AlgoImplicitPolyThroughPoints;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * 
 * ImplicitPoly[ &lt;Function> ]
 *
 */
public class CmdImplicitPoly extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdImplicitPoly(Kernel kernel) {
		super(kernel);
	}

	private GeoElement doCommand(String a, GeoList b, Command c) {

		AlgoImplicitPolyThroughPoints algo = new AlgoImplicitPolyThroughPoints(
				cons, a, b);
		GeoElement ret = algo.getImplicitPoly();

		return ret;
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 0:
			throw argNumErr(app, c.getName(), n);
		case 1:
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { doCommand(c.getLabel(), (GeoList) arg[0],
						c) };
				return ret;
			} else if (arg[0] instanceof GeoFunctionNVar) {
				FunctionNVar f = ((GeoFunctionNVar) arg[0]).getFunction();
				FunctionVariable[] fvars = f.getFunctionVariables();
				if (fvars.length != 2) {
					throw new MyError(app.getLocalization(), "InvalidEquation");
				}
				GeoElement[] ret = { getAlgoDispatcher().ImplicitPoly(
						c.getLabel(), ((GeoFunctionNVar) arg[0])) };
				return ret;
			} else {
				App.debug(arg[0] + ": " + arg[0].getGeoClassType() + "; "
						+ arg[0].getClass());
				throw argErr(app, c.getName(), arg[0]);
			}

			// more than one argument
		default:
			if ((int) Math.sqrt(9 + (8 * n)) != Math.sqrt(9 + (8 * n))) {
				throw argNumErr(app, c.getName(), n);
			}

			for (int i = 0; i < n; i++) {
				if (!arg[i].isGeoPoint()) {
					throw argErr(app, c.getName(), arg[i]);
				}
			}

			GeoList list = wrapInList(kernelA, arg, arg.length, GeoClass.POINT);
			if (list != null) {
				GeoElement[] ret = { doCommand(c.getLabel(), list, c) };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
		}
	}
}