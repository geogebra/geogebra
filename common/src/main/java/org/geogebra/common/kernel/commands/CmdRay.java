package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/**
 * Ray[ <GeoPoint>, <GeoPoint> ] Ray[ <GeoPoint>, <GeoVector> ]
 */
public class CmdRay extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRay(Kernel kernel) {
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

			// line through two points
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { ray(c.getLabel(), (GeoPointND) arg[0],
						(GeoPointND) arg[1]) };
				return ret;
			}

			// line through point with direction vector
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoVector()))) {
				GeoElement[] ret = { ray(c.getLabel(), (GeoPointND) arg[0],
						(GeoVectorND) arg[1]) };
				return ret;
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * @param label
	 *            label
	 * @param a
	 *            first point
	 * @param b
	 *            second point
	 * @return [ab)
	 */
	protected GeoElement ray(String label, GeoPointND a, GeoPointND b) {
		return getAlgoDispatcher().Ray(label, (GeoPoint) a, (GeoPoint) b);
	}

	/**
	 * @param label
	 *            label
	 * @param a
	 *            first point
	 * @param v
	 *            vector direction
	 * @return [av)
	 */
	protected GeoElement ray(String label, GeoPointND a, GeoVectorND v) {
		return getAlgoDispatcher().Ray(label, (GeoPoint) a, (GeoVector) v);
	}
}