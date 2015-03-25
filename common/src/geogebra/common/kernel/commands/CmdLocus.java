package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoIntegralODE;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

/**
 * Locus[ <GeoPoint Q>, <GeoPoint P> ] or Locus[ <GeoPoint Q>, <GeoNumeric P> ]
 */
public class CmdLocus extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLocus(Kernel kernel) {
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
			if ((ok[0] = (arg[0] instanceof FunctionalNVar)
					|| arg[0].isGeoLocus())
					&& (ok[1] = arg[1].isGeoPoint())) {

				AlgoIntegralODE algo = new AlgoIntegralODE(cons, c.getLabel(),
						arg[0], (GeoPoint) arg[1]);

				GeoElement[] ret = { algo.getResult() }; // var
				return ret;
			}

			// second argument has to be point on path
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {

				GeoPointND p1 = (GeoPointND) arg[0];
				GeoPointND p2 = (GeoPointND) arg[1];

				if (p2.isPointOnPath()) {

					GeoElement[] ret = { locus(c.getLabel(), p1, p2) };
					return ret;
				}
				GeoElement[] ret = { locus(c.getLabel(), p2, p1) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoNumeric()))) {
				GeoPointND p1 = (GeoPointND) arg[0];
				GeoNumeric p2 = (GeoNumeric) arg[1];

				GeoElement[] ret = { locus(c.getLabel(),
						p1, p2) };
				return ret;
			} else {
				throw argErr(app, c.getName(), getBadArg(ok, arg));

			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * @param label
	 *            label
	 * @param p1
	 *            dependent point
	 * @param p2
	 *            point on path
	 * @return locus
	 */
	protected GeoElement locus(String label, GeoPointND p1, GeoPointND p2) {
		return getAlgoDispatcher().Locus(label, p1, p2);
	}
	protected GeoElement locus(String label, GeoPointND p, GeoNumeric slider){
		return getAlgoDispatcher().Locus(label, (GeoPoint)p, slider);
	}

}
