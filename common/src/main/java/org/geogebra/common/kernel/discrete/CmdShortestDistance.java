package org.geogebra.common.kernel.discrete;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * ShortestDistance[ &lt;List of Segments>, &lt;Start Point>, &lt;End Point>,
 * &lt;Boolean Weighted> ]
 * 
 * @author Michael Borcherds
 */
public class CmdShortestDistance extends CommandProcessor {

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
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

		if (n == 4) {
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoPoint())
					&& (ok[2] = arg[2].isGeoPoint())
					&& (ok[3] = arg[3].isGeoBoolean())) {

				GeoElement[] ret = {new AlgoShortestDistance(cons,
						(GeoList) arg[0], (GeoPointND) arg[1],
						(GeoPointND) arg[2], (GeoBoolean) arg[3]).getResult()};
				ret[0].setLabel(c.getLabel());
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));
		}
		throw argNumErr(c);
	}

}
