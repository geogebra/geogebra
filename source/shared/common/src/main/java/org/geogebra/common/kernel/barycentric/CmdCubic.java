package org.geogebra.common.kernel.barycentric;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.MyError;

/**
 * TriangleCumic[point,point,point,index]
 * 
 * @author Darko
 *
 */
public class CmdCubic extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCubic(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 4:
			arg = resArgs(c);

			if ((ok[0] = arg[0] instanceof GeoPoint)
					&& (ok[1] = arg[1] instanceof GeoPoint)
					&& (ok[2] = arg[2] instanceof GeoPoint)
					&& (ok[3] = arg[3] instanceof GeoNumberValue)) {

				AlgoCubic algo = new AlgoCubic(cons, c.getLabel(),
						(GeoPoint) arg[0], (GeoPoint) arg[1], (GeoPoint) arg[2],
						(GeoNumberValue) arg[3]);

				GeoElement[] ret = { algo.getResult().toGeoElement() };
				return ret;

			}

			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}
}
