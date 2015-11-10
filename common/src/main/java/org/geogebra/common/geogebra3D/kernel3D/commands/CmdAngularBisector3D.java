package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdAngularBisector;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * 3D processor for AngularBisector
 */
public class CmdAngularBisector3D extends CmdAngularBisector {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdAngularBisector3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] process4(GeoElement[] arg, boolean[] ok, Command c)
			throws MyError {


			// angular bisector of three points
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))
					&& (ok[3] = (arg[3] instanceof GeoDirectionND))) {
				GeoElement[] ret = { kernelA.getManager3D().AngularBisector3D(
						c.getLabel(), (GeoPointND) arg[0], (GeoPointND) arg[1],
						(GeoPointND) arg[2], (GeoDirectionND) arg[3]) };
				return ret;
			}

			throw argErr(app, c.getName(), getBadArg(ok, arg));


	}

	@Override
	protected GeoElement[] angularBisector(String[] labels, GeoLineND g,
			GeoLineND h) {

		if (g.isGeoElement3D() || h.isGeoElement3D()) {
			GeoElement[] ret = kernelA.getManager3D().AngularBisector3D(labels,
					g, h);
			return ret;
		}

		return super.angularBisector(labels, g, h);
	}

	@Override
	protected GeoElement angularBisector(String label, GeoPointND A,
			GeoPointND B, GeoPointND C) {

		if (A.isGeoElement3D() || B.isGeoElement3D() || C.isGeoElement3D()) {
			return kernelA.getManager3D().AngularBisector3D(label, A, B, C);
		}

		return super.angularBisector(label, A, B, C);
	}

}
