package geogebra.common.kernel.cas;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;

/**
 *SurdText
 */
public class CmdSurdText extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSurdText(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			if (arg[0].isGeoNumeric()) {
				
				AlgoSurdText algo = new AlgoSurdText(cons, c.getLabel(),
						(GeoNumeric) arg[0], null);
				
				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if (arg[0].isGeoPoint()) {
				
				AlgoSurdTextPoint algo = new AlgoSurdTextPoint(cons, c.getLabel(),
						(GeoPoint) arg[0]);
				
				GeoElement[] ret = { algo.getResult() };
				return ret;
			} 

			throw argErr(app, c.getName(), arg[0]);

		case 2:

			boolean ok0;
			if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoList()) {
				
				AlgoSurdText algo = new AlgoSurdText(cons, c.getLabel(),
						(GeoNumeric) arg[0], (GeoList) arg[1]);
				
				GeoElement[] ret = { algo.getResult() };
				return ret;
			} 

			throw argErr(app, c.getName(), arg[ok0 ? 0 : 1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
