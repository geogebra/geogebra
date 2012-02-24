package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

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
		case 0:
		case 1:
		case 2:
			throw argNumErr(app, c.getName(), n);

		default:

			// polygon for given points
			GeoPoint2[] points = new GeoPoint2[n];
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint()))
					throw argErr(app, c.getName(), arg[i]);
				points[i] = (GeoPoint2) arg[i];
			}

			// everything ok
			return kernelA.RigidPolygon(c.getLabels(), points);
		}
	}
}
