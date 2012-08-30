package geogebra.common.kernel.commands;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoIntervalMidpoint;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoInterval;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.main.MyError;


/**
 * Midpoint[ <GeoConic> ] Midpoint[ <GeoPoint>, <GeoPoint> ]
 */
public class CmdMidpoint extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMidpoint(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoConic()) {
				GeoElement[] ret = { kernelA.Center(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			} else if (arg[0].isGeoSegment()) {
				GeoElement[] ret = { kernelA.Midpoint(c.getLabel(),
						(GeoSegment) arg[0]) };
				return ret;
			} else if (arg[0].isGeoInterval()) {
				AlgoIntervalMidpoint algo = new AlgoIntervalMidpoint(cons, c.getLabel(),
						(GeoInterval) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { kernelA.Midpoint(c.getLabel(),
						(GeoPoint) arg[0], (GeoPoint) arg[1]) };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok,arg));
			
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}