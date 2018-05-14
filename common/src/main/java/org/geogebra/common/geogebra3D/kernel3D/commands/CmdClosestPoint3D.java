package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.advanced.CmdClosestPoint;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * ClosestPoint(Path or Region, Point)
 * 
 * ClosestPoint(Point, Path or Region)
 */
public class CmdClosestPoint3D extends CmdClosestPoint {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdClosestPoint3D(Kernel kernel) {
		super(kernel);

	}

	@Override
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
				return new GeoElement[] { kernel.getManager3D().closestPoint(
						c.getLabel(), (Path) arg[0], (GeoPointND) arg[1]) };
			}

			if (arg[1].isPath() && arg[0].isGeoPoint()) {
				return new GeoElement[] { kernel.getManager3D().closestPoint(
						c.getLabel(), (Path) arg[1], (GeoPointND) arg[0]) };
			}

			if (arg[0].isGeoLine() || arg[1].isGeoLine()) {

				return new GeoElement[] { kernel.getManager3D().closestPoint(
						c.getLabel(), (GeoLineND) arg[0], (GeoLineND) arg[1]) };
			}

			if ((ok[0] = arg[0].isRegion()) && (ok[1] = arg[1].isGeoPoint())) {
				return new GeoElement[] { (GeoElement) kernel.getManager3D()
						.closestPoint(c.getLabel(), (Region) arg[0],
								(GeoPointND) arg[1]) };
			}

			if ((ok[1] = arg[1].isRegion()) && (ok[0] = arg[0].isGeoPoint())) {
				return new GeoElement[] { (GeoElement) kernel.getManager3D()
						.closestPoint(c.getLabel(), (Region) arg[1],
								(GeoPointND) arg[0]) };
			}

			if (!ok[0]) {
				throw argErr(c, arg[0]);
			}
			throw argErr(c, arg[1]);

		default:
			// return super.process(c);
			throw argNumErr(c);
		}
	}
}