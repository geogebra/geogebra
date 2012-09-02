package geogebra.common.kernel.scripting;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.main.MyError;

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

		case 1: if (arg[0].isGeoPolygon()) {
			
			EuclidianViewInterfaceCommon view = kernelA.getApplication().getActiveEuclidianView();
			
			double offset = view.toRealWorldCoordX(view.getWidth()) / 15;

			GeoElement[] ret = kernelA.RigidPolygon((GeoPolygon) arg[0], offset, -offset);
			
			return ret;
		}
		
		// else fall through
		
		case 0:
		case 2:
			throw argNumErr(app, c.getName(), n);

		case 3: if (arg[0].isGeoPolygon() && arg[1].isNumberValue() && arg[2].isNumberValue()) {
			
			GeoElement[] ret = kernelA.RigidPolygon((GeoPolygon) arg[0], ((NumberValue)arg[1]).getDouble(), ((NumberValue)arg[2]).getDouble());
			
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
