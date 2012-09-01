package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoFlatten;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

	/**
	 * Flatten[ <GeoList> ]
	 */
public class CmdFlatten extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFlatten(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		
		if (n!=1) {
			throw argNumErr(app, c.getName(), n);
		}
		
		GeoElement arg;
		arg = resArgs(c)[0];

		if (arg.isGeoList()) {
			
			AlgoFlatten algo = new AlgoFlatten(cons, c.getLabel(), (GeoList) arg);

			GeoElement[] ret = { algo.getResult() };
			return ret;
		}
		throw argErr(app, c.getName(), arg);
	}
}
