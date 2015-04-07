package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.MyError;

public class CmdPolyhedronNet extends CommandProcessor {

	public CmdPolyhedronNet(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPolyhedron()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				return kernelA.getManager3D().PolyhedronNet(c.getLabels(),
						(GeoPolyhedron) arg[0], (NumberValue) arg[1], null,
						null);
			}

			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			throw argErr(app, c.getName(), arg[1]);

			/*
			 * case 3 : arg = resArgs(c); if ( (ok[0] = (arg[0]
			 * .isGeoPolyhedron() ) ) && (ok[1] = (arg[1].isNumberValue() )) &&
			 * (ok[2] = (arg[2].isGeoPolygon() )) ) { return
			 * kernelA.getManager3D().PolyhedronNet( c.getLabels(),
			 * (GeoPolyhedron) arg[0], (NumberValue) arg[1], (GeoPolygon)
			 * arg[2], null); }
			 * 
			 * 
			 * if (!ok[0]) throw argErr(app, c.getName(), arg[0]); if (!ok[1])
			 * throw argErr(app, c.getName(), arg[1]); throw argErr(app,
			 * c.getName(), arg[2]);
			 */

		case 0:
		case 1:
			throw argNumErr(app, c.getName(), n);

		default:
			// throw argNumErr(app, c.getName(), n);

			arg = resArgs(c);

			if ((ok[0] = (arg[0].isGeoPolyhedron()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isGeoPolygon()))) {
				GeoSegmentND[] segments = new GeoSegmentND[n - 3];
				for (int i = 3; i < n; i++) {
					if (arg[i].isGeoSegment()) {
						segments[i - 3] = (GeoSegmentND) arg[i];
					} else {
						throw argErr(app, c.getName(), arg[i]);
					}

				}
				return kernelA.getManager3D().PolyhedronNet(c.getLabels(),
						(GeoPolyhedron) arg[0], (NumberValue) arg[1],
						(GeoPolygon) arg[2], segments);
			}

			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			throw argErr(app, c.getName(), arg[2]);

		}

	}

}
