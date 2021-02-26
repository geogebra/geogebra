package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.implicit.AlgoImplicitPolyThroughPoints;
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

	private GeoElement doCommand(String a, GeoList b) {

		AlgoImplicitPolyThroughPoints algo = new AlgoImplicitPolyThroughPoints(
				cons, a, b);
		GeoElement ret = algo.getImplicitPoly().toGeoElement();

		return ret;
	}

	@Override
	public GeoElement[] process(Command c)
			throws MyError, CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 0:
			throw argNumErr(c);
		case 1:
			if (arg[0].isGeoList()) {
				GeoElement[] ret = {
						doCommand(c.getLabel(), (GeoList) arg[0]) };
				return ret;
			} else if (arg[0] instanceof GeoFunctionNVar) {
				GeoFunctionNVar geoFunctionNVar = (GeoFunctionNVar) arg[0];
				FunctionVariable[] fvars = geoFunctionNVar.getFunctionVariables();
				if (fvars.length != 2) {
					throw new MyError(app.getLocalization(), "InvalidEquation");
				}
				GeoElement[] ret = { getAlgoDispatcher()
						.implicitPoly(c.getLabel(), geoFunctionNVar)
						.toGeoElement() };
				return ret;
			} else {
				throw argErr(c, arg[0]);
			}

			// more than one argument
		default:
			if ((int) Math.sqrt(9 + (8 * n)) != Math.sqrt(9 + (8 * n))) {
				throw argNumErr(c);
			}

			for (int i = 0; i < n; i++) {
				if (!arg[i].isGeoPoint()) {
					throw argErr(c, arg[i]);
				}
			}

			GeoList list = wrapInList(kernel, arg, arg.length, GeoClass.POINT);
			if (list != null) {
				GeoElement[] ret = { doCommand(c.getLabel(), list) };
				return ret;
			}
			throw argErr(c, arg[0]);
		}
	}
}