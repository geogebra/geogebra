package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/**
 * Stretch[<Object>,<Line>,<Ratio>]
 */
class CmdStretch extends CommandProcessorDesktop {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdStretch(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		GeoElement[] ret;

		switch (n) {
		case 2:
			arg = resArgs(c);

			if (arg[1] instanceof GeoVector) {

				if (arg[0].isMatrixTransformable() || arg[0].isGeoFunction()
						|| arg[0].isGeoPolygon() || arg[0].isGeoList()) {

					ret = kernel.Stretch(label, arg[0], (GeoVec3D) arg[1],
							null);
					return ret;

				} else
					throw argErr(app, c.getName(), arg[0]);
			}
			else
				throw argErr(app, c.getName(), arg[1]);
		case 3:
			arg = resArgs(c);

			if ((arg[1] instanceof GeoLine) && arg[2].isGeoNumeric()) {

				if (arg[0].isMatrixTransformable() || arg[0].isGeoFunction()
						|| arg[0].isGeoPolygon() || arg[0].isGeoList()) {

					ret = kernel.Stretch(label, arg[0], (GeoVec3D) arg[1],
							(GeoNumeric) arg[2]);
					return ret;

				} else
					throw argErr(app, c.getName(), arg[0]);
			} else {
				if (!(arg[1] instanceof GeoVec3D))
					throw argErr(app, c.getName(), arg[1]);
				throw argErr(app, c.getName(), arg[2]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
