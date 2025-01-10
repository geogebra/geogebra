package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.MyError;

/**
 * Net[polyhedron, number]
 * 
 * Net[polyhedron, number, side]
 *
 */
public class CmdPolyhedronNet extends CommandProcessor {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdPolyhedronNet(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPolyhedron()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				return kernel.getManager3D().polyhedronNet(c.getLabels(),
						(GeoPolyhedron) arg[0], (NumberValue) arg[1], null,
						null);
			}

			if (!ok[0]) {
				throw argErr(c, arg[0]);
			}
			throw argErr(c, arg[1]);

			/*
			 * case 3 : arg = resArgs(c); if ( (ok[0] = (arg[0]
			 * .isGeoPolyhedron() ) ) && (ok[1] = (arg[1].isNumberValue() )) &&
			 * (ok[2] = (arg[2].isGeoPolygon() )) ) { return
			 * kernelA.getManager3D().polyhedronNet( c.getLabels(),
			 * (GeoPolyhedron) arg[0], (NumberValue) arg[1], (GeoPolygon)
			 * arg[2], null); }
			 * 
			 * 
			 * if (!ok[0]) throw argErr(app, c, arg[0]); if (!ok[1]) throw
			 * argErr(app, c, arg[1]); throw argErr(app, c.getName(), arg[2]);
			 */

		case 0:
		case 1:
			throw argNumErr(c);

		default:
			// throw argNumErr(c);

			arg = resArgs(c);

			if ((ok[0] = (arg[0].isGeoPolyhedron()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isGeoPolygon()))) {
				GeoSegmentND[] segments = new GeoSegmentND[n - 3];
				for (int i = 3; i < n; i++) {
					if (arg[i].isGeoSegment()) {
						segments[i - 3] = (GeoSegmentND) arg[i];
					} else {
						throw argErr(c, arg[i]);
					}

				}
				return kernel.getManager3D().polyhedronNet(c.getLabels(),
						(GeoPolyhedron) arg[0], (NumberValue) arg[1],
						(GeoPolygon) arg[2], segments);
			}

			if (!ok[0]) {
				throw argErr(c, arg[0]);
			}
			if (!ok[1]) {
				throw argErr(c, arg[1]);
			}
			throw argErr(c, arg[2]);

		}

	}

}
