package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Semicircle[ <GeoPoint>, <GeoPoint> ]
 */
public class CmdSemicircle extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSemicircle(Kernel kernel) {
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
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { semicircle(c.getLabel(),
						(GeoPointND) arg[0], (GeoPointND) arg[1]) };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		case 3:
			arg = resArgs(c);

			GeoElement[] ret = process3(c, arg, ok);

			if (ret != null) {
				return ret;
			}

			// syntax error
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 * @return semicircle joining A and B
	 */
	protected GeoElement semicircle(String label, GeoPointND A, GeoPointND B) {
		return getAlgoDispatcher()
				.Semicircle(label, (GeoPoint) A, (GeoPoint) B);
	}

	/**
	 * process semicircle when 3 arguments
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            arguments
	 * @param ok
	 *            ok array
	 * @return result (if one)
	 * @throws MyError
	 *             in 2D, not possible with 3 args
	 */
	protected GeoElement[] process3(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {
		throw argNumErr(app, c.getName(), 3);
	}
}
