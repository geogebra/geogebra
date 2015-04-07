package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Circle[ <GeoPoint>, <GeoNumeric> ] Circle[ <GeoPoint>, <GeoPoint> ] Circle[
 * <GeoPoint>, <GeoPoint>, <GeoPoint> ]
 */
public class CmdCircle extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCircle(Kernel kernel) {
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
			return process2(c, arg, ok);

		case 3:
			arg = resArgs(c);
			return process3(c, arg, ok);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * process when 2 arguments
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            arguments
	 * @param ok
	 *            ok array
	 * @return result (if one)
	 * @throws MyError
	 *             arg error
	 */
	protected GeoElement[] process2(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		if ((ok[0] = (arg[0].isGeoPoint()))
				&& (ok[1] = (arg[1] instanceof GeoNumberValue))) {
			GeoElement[] ret = { circle(c.getLabel(), (GeoPointND) arg[0],
					(GeoNumberValue) arg[1]) };
			return ret;
		} else if ((ok[0] = (arg[0].isGeoPoint()))
				&& (ok[1] = (arg[1].isGeoPoint()))) {
			GeoElement[] ret = { circle(c.getLabel(), (GeoPointND) arg[0],
					(GeoPointND) arg[1]) };
			return ret;
		} else {
			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			throw argErr(app, c.getName(), arg[1]);
		}

	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param a
	 *            center point
	 * @param v
	 *            radius
	 * @return circle (center, radius)
	 */
	protected GeoElement circle(String label, GeoPointND a, GeoNumberValue v) {
		return getAlgoDispatcher().Circle(label, (GeoPoint) a, v);
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param a
	 *            center point
	 * @param b
	 *            point on circle
	 * @return circle (center, point)
	 */
	protected GeoElement circle(String label, GeoPointND a, GeoPointND b) {
		return getAlgoDispatcher().Circle(label, (GeoPoint) a, (GeoPoint) b);
	}

	/**
	 * process when 3 arguments
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            arguments
	 * @param ok
	 *            ok array
	 * @return result (if one)
	 * @throws MyError
	 *             arg error
	 */
	protected GeoElement[] process3(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		if ((ok[0] = (arg[0].isGeoPoint())) && (ok[1] = (arg[1].isGeoPoint()))
				&& (ok[2] = (arg[2].isGeoPoint()))) {
			GeoElement[] ret = { circle(c.getLabel(), (GeoPointND) arg[0],
					(GeoPointND) arg[1], (GeoPointND) arg[2]) };
			return ret;
		}
		throw argErr(app, c.getName(), getBadArg(ok, arg));

	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param a
	 *            point on circle
	 * @param b
	 *            point on circle
	 * @param c
	 *            point on circle
	 * @return circle three points
	 */
	protected GeoElement circle(String label, GeoPointND a, GeoPointND b,
			GeoPointND c) {
		return getAlgoDispatcher().Circle(label, (GeoPoint) a, (GeoPoint) b,
				(GeoPoint) c);
	}

}