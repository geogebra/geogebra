package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.implicit.GeoImplicitPoly;

/**
 * Asymptote[ <GeoConic> ]
 */
class CmdAsymptote extends CommandProcessorDesktop {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAsymptote(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic())
				return kernel.Asymptote(c.getLabels(), (GeoConic) arg[0]);
			else if (arg[0].isGeoFunction())
			{
				GeoElement[] ret = { kernel.AsymptoteFunction(c.getLabel(),
						(GeoFunction) arg[0]) };
				return ret;
			}
			else if (arg[0].isGeoImplicitPoly()) {
				GeoElement[] ret =  {kernel.AsymptoteImplicitpoly(c.getLabel(),
						(GeoImplicitPoly) arg[0])} ;
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
