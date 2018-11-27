package org.geogebra.common.kernel.scripting;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.PolygonFactory;
import org.geogebra.common.main.MyError;

/**
 * RigidPolygon[ &lt;GeoPoint>, ..., &lt;GeoPoint> ]
 * 
 * CmdScripting -> disable preview (otherwise it doesn't work)
 */
public class CmdRigidPolygon extends CmdScripting {
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
	public GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		arg = resArgs(c);
		switch (n) {

		case 1:
			if (arg[0].isGeoPolygon()) {

				EuclidianViewInterfaceCommon view = kernel.getApplication()
						.getActiveEuclidianView();

				double offset = view.toRealWorldCoordX(view.getWidth()) / 15;

				GeoElement[] ret = new PolygonFactory(kernel).rigidPolygon((GeoPolygon) arg[0],
						offset, -offset, c.getLabels());

				return ret;
			}

			//$FALL-THROUGH$
		case 0:
		case 2:
			throw argNumErr(c);

		case 3:
			if (arg[0].isGeoPolygon() && arg[1] instanceof GeoNumberValue
					&& arg[2] instanceof GeoNumberValue) {

				GeoElement[] ret = new PolygonFactory(kernel).rigidPolygon((GeoPolygon) arg[0],
						((GeoNumberValue) arg[1]).getDouble(),
						((GeoNumberValue) arg[2]).getDouble(), c.getLabels());

				return ret;
			}

			//$FALL-THROUGH$
		default:

			// polygon for given points
			GeoPoint[] points = new GeoPoint[n];
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint())) {
					throw argErr(c, arg[i]);
				}
				points[i] = (GeoPoint) arg[i];
			}

			// everything ok
			return new PolygonFactory(kernel).rigidPolygon(c.getLabels(),
					points);
		}
	}

}
