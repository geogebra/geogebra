package geogebra.common.kernel.advanced;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.implicit.AlgoImplicitPolyThroughPoints;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;
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
		int n = b.size();
		if ((n == 0)
				|| ((int) Math.sqrt(9 + (8 * n)) != Math.sqrt(9 + (8 * n)))) {
			throw argNumErr(app, c.getName(), n);
		}

		for (int i = 0; i < n; i++) {
			if (!b.get(i).isGeoPoint()) {
				throw argErr(app, c.getName(), b.get(i));
			}
		}

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
					throw new MyError(app, "InvalidEquation");
				}
				GeoElement[] ret = { kernelA.ImplicitPoly(c.getLabel(),
						((GeoFunctionNVar) arg[0])) };
				return ret;
			} else {
				App.debug(arg[0] + ": " + arg[0].getGeoClassType()
						+ "; " + arg[0].getClass());
				throw argErr(app, c.getName(), arg[0]);
			}

			// more than one argument
		default:
			if (arg[0].isNumberValue()) {
				// try to create list of numbers
				GeoList list = wrapInList(kernelA, arg, arg.length,
						GeoClass.NUMERIC);
				if (list != null) {
					GeoElement[] ret = { doCommand(c.getLabel(), list, c) };
					return ret;
				}
			} else if (arg[0].isVectorValue()) {
				// try to create list of points (eg FitExp[])
				GeoList list = wrapInList(kernelA, arg, arg.length,
						GeoClass.POINT);
				if (list != null) {
					GeoElement[] ret = { doCommand(c.getLabel(), list, c) };
					return ret;
				}
			}
			throw argNumErr(app, c.getName(), n);
		}
	}
}