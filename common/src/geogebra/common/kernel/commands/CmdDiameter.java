package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.main.MyError;

/**
 * Diameter[ <GeoVector>, <GeoConic> ] Diameter[ <GeoLine>, <GeoConic> ]
 */
public class CmdDiameter extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDiameter(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// diameter line conjugate to vector relative to conic
			if ((ok[0] = (arg[0].isGeoVector()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = { getAlgoDispatcher().DiameterLine(c.getLabel(),
						(GeoVector) arg[0], (GeoConic) arg[1]) };
				return ret;
			}

			// diameter line conjugate to line relative to conic
			if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = { getAlgoDispatcher().DiameterLine(c.getLabel(),
						(GeoLine) arg[0], (GeoConic) arg[1]) };
				return ret;
			}
			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
