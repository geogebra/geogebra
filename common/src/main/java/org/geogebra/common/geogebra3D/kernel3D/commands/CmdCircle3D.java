package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdCircle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

public class CmdCircle3D extends CmdCircle {

	public CmdCircle3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] process2(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		if ((ok[0] = (arg[0] instanceof GeoLineND))
				&& (ok[1] = (arg[1].isGeoPoint()))) {
			GeoElement[] ret = { kernelA.getManager3D().Circle3D(c.getLabel(),
					(GeoLineND) arg[0], (GeoPointND) arg[1]) };
			return ret;
		}

		return super.process2(c, arg, ok);
	}

	@Override
	protected GeoElement circle(String label, GeoPointND a, GeoNumberValue v) {

		GeoDirectionND orientation = CommandProcessor3D
				.getCurrentViewOrientation(kernelA, app);
		if (orientation == null) {
			if (a.isGeoElement3D()) {
				orientation = kernelA.getXOYPlane();
			} else {
				// use 2D algo
				return super.circle(label, a, v);
			}
		}

		return kernelA.getManager3D().Circle3D(label, a, v, orientation);
	}

	@Override
	protected GeoElement circle(String label, GeoPointND a, GeoPointND b) {

		GeoDirectionND orientation = CommandProcessor3D
				.getCurrentViewOrientation(kernelA, app);
		if (orientation == null) {
			if (a.isGeoElement3D() || b.isGeoElement3D()) {
				orientation = kernelA.getXOYPlane();
			} else {
				// use 2D algo
				return super.circle(label, a, b);
			}
		}

		return kernelA.getManager3D().Circle3D(label, a, b, orientation);
	}

	@Override
	protected GeoElement[] process3(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		if ((ok[0] = (arg[0].isGeoPoint()))
				&& (ok[2] = (arg[2] instanceof GeoDirectionND))) {
			if (ok[1] = (arg[1] instanceof GeoNumberValue)) {
				GeoElement[] ret = { kernelA.getManager3D().Circle3D(
						c.getLabel(), (GeoPointND) arg[0],
						(GeoNumberValue) arg[1], (GeoDirectionND) arg[2]) };
				return ret;
			} else if ((ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { kernelA.getManager3D().Circle3D(
						c.getLabel(), (GeoPointND) arg[0], (GeoPointND) arg[1],
						(GeoDirectionND) arg[2]) };
				return ret;
			}
		}

		return super.process3(c, arg, ok);
	}

	@Override
	protected GeoElement circle(String label, GeoPointND a, GeoPointND b,
			GeoPointND c) {

		if (a.isGeoElement3D() || b.isGeoElement3D() || c.isGeoElement3D()) {
			return kernelA.getManager3D().Circle3D(label, a, b, c);
		}

		return super.circle(label, a, b, c);
	}

}
