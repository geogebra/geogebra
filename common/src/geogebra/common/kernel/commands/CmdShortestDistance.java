package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.discrete.AlgoShortestDistance;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

/**
 * ShortestDistance[ <List of Segments>, <Start Point>, <End Point>, <Boolean
 * Weighted> ] Michael Borcherds 2008-03-04
 */
public class CmdShortestDistance extends CommandProcessor {

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdShortestDistance(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 4:

			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoPoint())
					&& (ok[2] = arg[2].isGeoPoint())
					&& (ok[3] = arg[3].isGeoBoolean())) {
				
				GeoElement[] ret = {  new AlgoShortestDistance(cons, c.getLabel(),
						(GeoList) arg[0], (GeoPointND) arg[1],
						(GeoPointND) arg[2], (GeoBoolean) arg[3]).getResult() };

				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
