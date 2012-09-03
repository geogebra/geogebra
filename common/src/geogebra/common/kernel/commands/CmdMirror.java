package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPoly;
import geogebra.common.kernel.geos.Transformable;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.main.MyError;

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

				if (conic1.getType() == GeoConicNDConstants.CONIC_CIRCLE
						&& arg[0].isGeoConic() || arg[0].isGeoPoint()
						|| arg[0] instanceof GeoCurveCartesian
						|| arg[0] instanceof GeoLine
						|| arg[0] instanceof GeoPoly
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

			// syntax error

			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
