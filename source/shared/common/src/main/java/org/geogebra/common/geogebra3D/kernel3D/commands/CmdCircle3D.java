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

/**
 * Circle command
 *
 */
public class CmdCircle3D extends CmdCircle {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdCircle3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] process2(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		if ((ok[0] = (arg[0] instanceof GeoLineND))
				&& (ok[1] = (arg[1].isGeoPoint()))) {
			GeoElement[] ret = { kernel.getManager3D().circle3D(c.getLabel(),
					(GeoLineND) arg[0], (GeoPointND) arg[1]) };
			return ret;
		}

		return super.process2(c, arg, ok);
	}

	@Override
	protected GeoElement circle(String label, GeoPointND a, GeoNumberValue v) {

		GeoDirectionND orientation = CommandProcessor3D
				.getCurrentViewOrientationNoSpace(kernel, app);
		if (orientation == null) {
			if (a.isGeoElement3D()) {
				orientation = kernel.getXOYPlane();
			} else {
				// use 2D algo
				return super.circle(label, a, v);
			}
		}

		return kernel.getManager3D().circle3D(label, a, v, orientation);
	}

	@Override
	protected GeoElement circle(String label, GeoPointND a, GeoPointND b) {

		GeoDirectionND orientation = CommandProcessor3D
				.getCurrentViewOrientationNoSpace(kernel, app);
		if (orientation == null) {
			if (a.isGeoElement3D() || b.isGeoElement3D()) {
				orientation = kernel.getXOYPlane();
			} else {
				// use 2D algo
				return super.circle(label, a, b);
			}
		}

		return kernel.getManager3D().circle3D(label, a, b, orientation);
	}

	@Override
	protected GeoElement[] process3(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		if ((ok[0] = (arg[0].isGeoPoint()))
				&& (ok[2] = (arg[2] instanceof GeoDirectionND))) {

			if (arg[1] instanceof GeoNumberValue) {
				GeoElement[] ret = { kernel.getManager3D().circle3D(
						c.getLabel(), (GeoPointND) arg[0],
						(GeoNumberValue) arg[1], (GeoDirectionND) arg[2]) };
				return ret;
			} else if (arg[1].isGeoPoint()) {
				GeoElement[] ret = { kernel.getManager3D().circle3D(
						c.getLabel(), (GeoPointND) arg[0], (GeoPointND) arg[1],
						(GeoDirectionND) arg[2]) };
				return ret;
			}
			ok[1] = false;
		}

		return super.process3(c, arg, ok);
	}

	@Override
	protected GeoElement circle(String label, GeoPointND a, GeoPointND b,
			GeoPointND c) {

		if (a.isGeoElement3D() || b.isGeoElement3D() || c.isGeoElement3D()) {
			return kernel.getManager3D().circle3D(label, a, b, c);
		}

		return super.circle(label, a, b, c);
	}

}
