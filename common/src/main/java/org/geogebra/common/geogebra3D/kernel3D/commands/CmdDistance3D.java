package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdDistance;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Distance[ <GeoLineND>, <GeoLineND> ]
 */
public class CmdDistance3D extends CmdDistance {

	public CmdDistance3D(Kernel kernel) {
		super(kernel);

	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()) {

				if (arg[0].isGeoLine() && arg[1].isGeoLine()) {

					return new GeoElement[] { kernelA.getManager3D().Distance(
							c.getLabel(), (GeoLineND) arg[0],
							(GeoLineND) arg[1]) };
				}

				if (arg[0].isGeoPoint() && arg[1].isGeoPlane()) {

					return new GeoElement[] { kernelA.getManager3D().Distance(
							c.getLabel(), (GeoPointND) arg[0],
							(GeoPlaneND) arg[1]) };
				}

				if (arg[1].isGeoPoint() && arg[0].isGeoPlane()) {

					return new GeoElement[] { kernelA.getManager3D().Distance(
							c.getLabel(), (GeoPointND) arg[1],
							(GeoPlaneND) arg[0]) };
				}
			}

			return super.process(c);

		default:
			// return super.process(c);
			throw argNumErr(app, c.getName(), n);
		}
	}
}