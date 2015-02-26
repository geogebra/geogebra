package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.advanced.CmdClosestPoint;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

/**
 * Intersect[ <GeoPlane3D>, <GeoConicND> ] Intersect[ <GeoLineND>,
 * <GeoQuadric3D> ] Intersect[ <GeoConicND>, <GeoConicND> ] Intersect[
 * <GeoLineND>, <GeoPolygon> ] Intersect[ <GeoLineND>, <GeoCoordSys2D> ]
 * Intersect[ <GeoLineND>, <GeoLineND> ] Intersect[ <GeoLineND>, <GeoConicND>,
 * <GeoNumeric> ] Intersect[ <GeoLineND>, <GeoQuadric3D>, <GeoNumeric> ]
 */
public class CmdClosestPoint3D extends CmdClosestPoint {

	public CmdClosestPoint3D(Kernel kernel) {
		super(kernel);

	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			if (!arg[0].isGeoElement3D() && !arg[1].isGeoElement3D()) {
				return super.process(c);
			}

			if (arg[0].isPath() && arg[1].isGeoPoint()) {
				return new GeoElement[] { kernelA.getManager3D().ClosestPoint(
						c.getLabel(), (Path) arg[0], (GeoPointND) arg[1]) };
			}

			if (arg[1].isPath() && arg[0].isGeoPoint()) {
				return new GeoElement[] { kernelA.getManager3D().ClosestPoint(
						c.getLabel(), (Path) arg[1], (GeoPointND) arg[0]) };
			}

			if (arg[0].isGeoLine() || arg[1].isGeoLine()) {

				return new GeoElement[] { kernelA.getManager3D().ClosestPoint(
						c.getLabel(), (GeoLineND) arg[0], (GeoLineND) arg[1]) };
			}

			if ((ok[0] = arg[0].isRegion()) && (ok[1] = arg[1].isGeoPoint())) {
				return new GeoElement[] { (GeoElement) kernelA.getManager3D()
						.ClosestPoint(c.getLabel(), (Region) arg[0],
								(GeoPointND) arg[1]) };
			}
			
			if ((ok[1] = arg[1].isRegion()) && (ok[0] = arg[0].isGeoPoint())) {
				return new GeoElement[] { (GeoElement) kernelA.getManager3D()
						.ClosestPoint(c.getLabel(), (Region) arg[1],
								(GeoPointND) arg[0]) };
			}

			
			if (!ok[0]){
				throw argErr(app, c.getName(), arg[0]);
			}
			throw argErr(app, c.getName(), arg[1]);

		default:
			// return super.process(c);
			throw argNumErr(app, c.getName(), n);
		}
	}
}