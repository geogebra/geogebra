package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * AngularBisector[ <GeoPoint>, <GeoPoint>, <GeoPoint> ] AngularBisector[
 * <GeoLine>, <GeoLine> ]
 */
public class CmdAngularBisector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAngularBisector(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];



		GeoElement[] arg = resArgs(c);

		switch (n) {
		case 2:

			// angular bisector of 2 lines
			if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoLine()))) {
				return angularBisector(c.getLabels(), (GeoLineND) arg[0],
						(GeoLineND) arg[1]);
			}
			if (!ok[0]) {
				throw argErr(app, c.getName(), arg[0]);
			}
			throw argErr(app, c.getName(), arg[1]);

		case 3:

			// angular bisector of three points
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { angularBisector(c.getLabel(),
						(GeoPointND) arg[0], (GeoPointND) arg[1],
						(GeoPointND) arg[2]) };
				return ret;
			}
			if (!ok[0]) {
				throw argErr(app, c.getName(), arg[0]);
			} else if (!ok[1]) {
				throw argErr(app, c.getName(), arg[1]);
			} else {
				throw argErr(app, c.getName(), arg[2]);
			}
		case 4:
			return process4(arg, ok, c);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * @param arg
	 *            processed arguments
	 * @param ok
	 *            helper array for validation
	 * @param c
	 *            command
	 * @return bisector
	 */
	protected GeoElement[] process4(GeoElement[] arg, boolean[] ok, Command c) {
		throw argNumErr(app, c.getName(), 4);
	}

	/**
	 * 
	 * @param labels
	 *            labels
	 * @param g
	 *            first line
	 * @param h
	 *            second line
	 * @return angular bisector for two lines
	 */
	protected GeoElement[] angularBisector(String[] labels, GeoLineND g,
			GeoLineND h) {

		return getAlgoDispatcher().AngularBisector(labels, (GeoLine) g,
				(GeoLine) h);
	}

	/**
	 * @param label
	 *            label
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 * @param C
	 *            third point
	 * @return angular bisector for three points
	 */
	protected GeoElement angularBisector(String label, GeoPointND A,
			GeoPointND B, GeoPointND C) {

		return getAlgoDispatcher().AngularBisector(label, (GeoPoint) A,
				(GeoPoint) B, (GeoPoint) C);
	}
}
