package geogebra.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

	/**
	 * Flatten[ <GeoList> ]
	 */
class CmdFlatten extends CommandProcessorDesktop {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFlatten(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		
		if (n!=1)
			throw argNumErr(app, c.getName(), n);
		
		boolean ok;
		GeoElement arg;
		arg = resArgs(c)[0];

		ok = arg.isGeoList();

		if (ok) {
			GeoElement[] ret = { kernel
					.Flatten(c.getLabel(), (GeoList) arg) };
			return ret;
		} else
			throw argErr(app, c.getName(), arg);
	}
}
