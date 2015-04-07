package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

public class CmdPlane extends CommandProcessor {

	public CmdPlane(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0] instanceof GeoCoordSys2D) {
				GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
						.Plane3D(c.getLabel(), (GeoCoordSys2D) arg[0]) };
				return ret;
			}

			throw argErr(app, c.getName(), arg[0]);
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1] instanceof GeoLineND))) {
				GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
						.Plane3D(c.getLabel(), (GeoPointND) arg[0],
								(GeoLineND) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1] instanceof GeoCoordSys2D))) {
				GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
						.Plane3D(c.getLabel(), (GeoPointND) arg[0],
								(GeoCoordSys2D) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoLine()))) {
				GeoElement[] ret = { kernelA.getManager3D().Plane3D(
						c.getLabel(), (GeoLineND) arg[0], (GeoLineND) arg[1]) };
				return ret;

			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
			}

		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { kernelA.getManager3D().Plane3D(
						c.getLabel(), (GeoPointND) arg[0], (GeoPointND) arg[1],
						(GeoPointND) arg[2]) };
				return ret;
			}

			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}

	}

}
