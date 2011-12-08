package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoConic;
import geogebra.kernel.geos.GeoLine;
import geogebra.kernel.geos.GeoPoint2;
import geogebra.kernel.geos.GeoPolyLineInterface;
import geogebra.kernel.implicit.GeoImplicitPoly;

/**
 * (2nd argument is the mirror) Mirror[ <GeoPoint>, <GeoPoint> ]
 * Mirror[<GeoLine>, <GeoPoint> ] Mirror[ <GeoConic>, <GeoPoint> ]
 * Mirror[<GeoPolygon>, <GeoPoint> ]
 * 
 * Mirror[ <GeoPoint>, <GeoLine> ] Mirror[ <GeoLine>, <GeoLine> ] Mirror[
 * <GeoConic>, <GeoLine> ] Mirror[ <GeoPolygon>, <GeoLine> ]
 */
class CmdMirror extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMirror(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		GeoElement[] ret = new GeoElement[1];

		switch (n) {
		case 2:
			arg = resArgs(c);

			if (arg[1].isGeoConic()) { // mirror point in circle Michael
										// Borcherds 2008-02-10
				GeoConic conic1 = (GeoConic) arg[1];

				if (conic1.getType() == GeoConic.CONIC_CIRCLE
						&& arg[0].isGeoConic() || arg[0].isGeoPoint()
						|| arg[0] instanceof GeoCurveCartesian
						|| arg[0] instanceof GeoLine
						|| arg[0] instanceof GeoPolyLineInterface
						|| arg[0] instanceof GeoFunction
						|| arg[0] instanceof GeoList
						|| arg[0] instanceof GeoImplicitPoly) {
					ret = kernel.Mirror(label, arg[0], conic1);
					return ret;
				}

			}

			// mirror object
			if (ok[0] = true) {

				// GeoElement geo = p.toGeoElement();

				// mirror at point
				if (ok[1] = (arg[1].isGeoPoint())) {
					GeoPoint2 Q = (GeoPoint2) arg[1];

					ret = kernel.Mirror(label, arg[0], Q);
					return ret;
				}
				// mirror is line
				else if (ok[1] = (arg[1].isGeoLine())) {
					GeoLine line = (GeoLine) arg[1];

					ret = kernel.Mirror(label, arg[0], line);
					return ret;
				}
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, "Mirror", n);
		}
	}
}
