package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.implicit.GeoImplicitPoly;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.main.MyError;

/**
 * (2nd argument is the mirror) Mirror[ <GeoPoint>, <GeoPoint> ]
 * Mirror[<GeoLine>, <GeoPoint> ] Mirror[ <GeoConic>, <GeoPoint> ]
 * Mirror[<GeoPolygon>, <GeoPoint> ]
 * 
 * Mirror[ <GeoPoint>, <GeoLine> ] Mirror[ <GeoLine>, <GeoLine> ] Mirror[
 * <GeoConic>, <GeoLine> ] Mirror[ <GeoPolygon>, <GeoLine> ]
 */
public class CmdMirror extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMirror(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			GeoElement[] ret = process2(label, arg, ok);

			if (ret != null) {
				return ret;
			}

			// syntax error

			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * process angle when 2 arguments
	 * 
	 * @param label
	 *            for the result
	 * @param arg
	 *            arguments
	 * @param ok
	 *            ok array
	 * @return result (if one)
	 */
	protected GeoElement[] process2(String label, GeoElement[] arg, boolean[] ok) {

		GeoElement[] ret = new GeoElement[1];

		if (arg[1].isGeoConic()) { // mirror point in circle Michael
			// Borcherds 2008-02-10
			GeoConic conic1 = (GeoConic) arg[1];

			if (conic1.getType() == GeoConicNDConstants.CONIC_CIRCLE
					&& arg[0].isGeoConic() || arg[0].isGeoPoint()
					|| arg[0] instanceof GeoCurveCartesian
					|| arg[0] instanceof GeoLine || arg[0] instanceof GeoPoly
					|| arg[0] instanceof GeoFunction
					|| arg[0] instanceof GeoList
					|| arg[0] instanceof GeoImplicitPoly) {
				ret = getAlgoDispatcher().Mirror(label, arg[0], conic1);
				return ret;
			}

		}

		// mirror object
		if (arg[0] instanceof Transformable) {
			ok[0] = true;
			// GeoElement geo = p.toGeoElement();

			// mirror at point
			if (arg[1].isGeoPoint()) {

				GeoPoint Q = (GeoPoint) arg[1];

				ret = getAlgoDispatcher().Mirror(label, arg[0], Q);
				return ret;
			}
			// mirror is line
			else if (arg[1].isGeoLine()) {
				GeoLine line = (GeoLine) arg[1];

				ret = getAlgoDispatcher().Mirror(label, arg[0], line);
				return ret;
			}
		}

		return null;
	}
}
