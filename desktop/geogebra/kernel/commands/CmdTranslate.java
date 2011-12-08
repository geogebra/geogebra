package geogebra.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.Translateable;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoPolygon;
import geogebra.kernel.geos.GeoVector;

/**
 * Translate[ <GeoPoint>, <GeoVector> ] Translate[ <GeoLine>, <GeoVector> ]
 * Translate[ <GeoConic>, <GeoVector> ] Translate[ <GeoFunction>, <GeoVector> ]
 * Translate[ <GeoVector>, <GeoPoint> ] // set start point Translate[
 * <GeoPolygon>, <GeoVector> ]
 * 
 */
public class CmdTranslate extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTranslate(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		String label = c.getLabel();
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		GeoElement[] ret = new GeoElement[1];

		switch (n) {
		case 2:
			arg = resArgs(c);

			// translate object

			if ((ok[0] = (arg[0].isGeoVector()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoVector v = (GeoVector) arg[0];
				GeoPoint2 P = (GeoPoint2) arg[1];

				ret[0] = kernel.Translate(label, v, P);

				return ret;
			} else if ((ok[0] = (arg[0] instanceof Translateable
					|| arg[0] instanceof GeoPolygon || arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoVector() || arg[1].isGeoPoint()))) {
				GeoVec3D v = (GeoVec3D) arg[1];
				ret = kernel.Translate(label, arg[0], v);
				return ret;
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, "Translate", arg[0]);
				else
					throw argErr(app, "Translate", arg[1]);
			}

		default:
			throw argNumErr(app, "Translate", n);
		}
	}
}