package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoFunction;
import geogebra.kernel.geos.GeoFunctionable;

/**
 * If[ <GeoBoolean>, <GeoElement> ] If[ <GeoBoolean>, <GeoElement>, <GeoElement>
 * ]
 */
class CmdIf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIf(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2: // if - then
		case 3: // if - then - else
			arg = resArgs(c);
			GeoElement geoElse = n == 3 ? arg[2] : null;

			// standard case: simple boolean condition
			if (ok[0] = arg[0].isGeoBoolean()) {
				GeoElement[] ret = { kernel.If(c.getLabel(),
						(GeoBoolean) arg[0], arg[1], geoElse) };
				return ret;
			}

			// SPECIAL CASE for functions:
			// boolean function in x as condition
			// example: If[ x < 2, x^2, x + 2 ]
			// DO NOT change instanceof here (see
			// GeoFunction.isGeoFunctionable())
			else if (ok[0] = (arg[0] instanceof GeoFunction)) {
				GeoFunction booleanFun = (GeoFunction) arg[0];
				if ((ok[0] = booleanFun.isBooleanFunction())
						// now that lines are functionable, need to disallow eg if[x<=40, y=20]
						&& (ok[1] = (arg[1].isGeoFunctionable() && !arg[1].isGeoLine()))
						&& (geoElse == null || geoElse.isGeoFunctionable())) {
					GeoFunction elseFun = geoElse == null ? null
							: ((GeoFunctionable) geoElse).getGeoFunction();

					GeoElement[] ret = { kernel.If(c.getLabel(),
							(GeoFunction) booleanFun,
							((GeoFunctionable) arg[1]).getGeoFunction(),
							elseFun) };
					return ret;
				}
			}

			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (n == 2 || !ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
