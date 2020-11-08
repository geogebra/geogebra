package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Curvature[&lt;Point>,&lt;Curve>], Curvature[&lt;Point>,&lt;Function>]
 *
 * @author Victor Franco Espino
 */
public class CmdTorsion extends CommandProcessor {
	/**
	 * Create new command processor
	 *
	 * @param kernel
	 *            kernel
	 */
	public CmdTorsion(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1] instanceof GeoCurveCartesianND))) {

				AlgoTorsion algo = new AlgoTorsion(cons,
						c.getLabel(), (GeoPointND) arg[0],
						(GeoCurveCartesianND) arg[1]);

				GeoElement[] ret = {algo.getResult()};
				return ret;
			}

			if (!ok[0]) {
				throw argErr(c, arg[0]);
			}
			throw argErr(c, arg[1]);

		default:
			throw argNumErr(c);
		}
	}
}
