package org.geogebra.common.kernel.scripting;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.main.MyError;

/**
 * RigidPolygon[ <GeoPoint>, ..., <GeoPoint> ]
 */
public class CmdRigidPolygon extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRigidPolygon(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		arg = resArgs(c);
		switch (n) {

		case 1:
			if (arg[0].isGeoPolygon()) {

				EuclidianViewInterfaceCommon view = kernelA.getApplication()
						.getActiveEuclidianView();

				double offset = view.toRealWorldCoordX(view.getWidth()) / 15;

				GeoElement[] ret = kernelA.RigidPolygon((GeoPolygon) arg[0],
						offset, -offset);

				return ret;
			}

			// else fall through

		case 0:
		case 2:
			throw argNumErr(app, c.getName(), n);

		case 3:
			if (arg[0].isGeoPolygon() && arg[1] instanceof GeoNumberValue
					&& arg[2] instanceof GeoNumberValue) {

				GeoElement[] ret = kernelA.RigidPolygon((GeoPolygon) arg[0],
						((GeoNumberValue) arg[1]).getDouble(),
						((GeoNumberValue) arg[2]).getDouble());

				return ret;
			}

			// else fall through
		default:

			// polygon for given points
			GeoPoint[] points = new GeoPoint[n];
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint()))
					throw argErr(app, c.getName(), arg[i]);
				points[i] = (GeoPoint) arg[i];
			}

			// everything ok
			return kernelA.RigidPolygon(c.getLabels(), points);
		}
	}
}
