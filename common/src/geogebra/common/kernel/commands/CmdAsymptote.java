package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.implicit.AlgoAsymptoteImplicitPoly;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.main.MyError;

/**
 * Asymptote[ <GeoConic> ]
 */
public class CmdAsymptote extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAsymptote(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic())
				return kernelA.Asymptote(c.getLabels(), (GeoConic) arg[0]);
			else if (arg[0].isGeoFunction())
			{
				GeoElement[] ret = { kernelA.AsymptoteFunction(c.getLabel(),
						(GeoFunction) arg[0]) };
				return ret;
			}
			else if (arg[0].isGeoImplicitPoly()) {
				
				AlgoAsymptoteImplicitPoly algo = new AlgoAsymptoteImplicitPoly(cons,
						c.getLabel(),
						(GeoImplicitPoly) arg[0]);

				GeoElement[] ret =  { algo.getResult() } ;
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
