package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoPolarLine;
import org.geogebra.common.kernel.algos.AlgoPolarPoint;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Polar[ <GeoPoint>, <GeoConic> ]
 */
public class CmdPolar extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPolar(Kernel kernel) {
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

			// polar line to point relative to conic
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = { PolarLine(c.getLabel(),
						(GeoPointND) arg[0], (GeoConicND) arg[1]) };
				return ret;
			}
			// pole of a line relative to conic
			if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = { PolarPoint(c.getLabel(),
						(GeoLineND) arg[0], (GeoConicND) arg[1]) };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * polar line to P relative to c
	 */
	protected GeoElement PolarLine(String label, GeoPointND P, GeoConicND c) {
		AlgoPolarLine algo = new AlgoPolarLine(cons, label, c, P);
		return (GeoElement) algo.getLine();
	}

	/**
	 * pole of line relative to c
	 */
	protected GeoElement PolarPoint(String label, GeoLineND line, GeoConicND c) {
		AlgoPolarPoint algo = new AlgoPolarPoint(cons, label, c, line);
		return (GeoElement) algo.getPoint();
	}

}
