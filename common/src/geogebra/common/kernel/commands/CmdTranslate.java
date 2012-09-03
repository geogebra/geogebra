package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoTranslateVector;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.geos.Translateable;
import geogebra.common.main.MyError;

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

	@Override
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
				GeoPoint P = (GeoPoint) arg[1];
				
				AlgoTranslateVector algo = new AlgoTranslateVector(cons, label, v, P);

				ret[0] = algo.getTranslatedVector();

				return ret;
			} else if ((ok[0] = (arg[0] instanceof Translateable
					|| arg[0] instanceof GeoPolygon || arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoVector() || arg[1].isGeoPoint()))) {
				GeoVec3D v = (GeoVec3D) arg[1];
				ret = getAlgoDispatcher().Translate(label, arg[0], v);
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
}