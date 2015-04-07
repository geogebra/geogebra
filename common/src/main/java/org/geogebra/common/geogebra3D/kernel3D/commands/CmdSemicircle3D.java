package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdSemicircle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

public class CmdSemicircle3D extends CmdSemicircle {

	public CmdSemicircle3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement semicircle(String label, GeoPointND A, GeoPointND B) {

		GeoDirectionND orientation = CommandProcessor3D
				.getCurrentViewOrientation(kernelA, app);
		if (orientation != null) {
			return (GeoElement) kernelA.getManager3D().Semicircle3D(label, A,
					B, orientation);
		}

		if (A.isGeoElement3D() || B.isGeoElement3D()) {
			return (GeoElement) kernelA.getManager3D().Semicircle3D(label, A,
					B, kernelA.getXOYPlane());
		}

		return super.semicircle(label, A, B);
	}

	@Override
	protected GeoElement[] process3(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		// semicircle joining two points, oriented
		if ((ok[0] = (arg[0].isGeoPoint())) && (ok[1] = (arg[1].isGeoPoint()))
				&& (ok[2] = (arg[2] instanceof GeoDirectionND))) {

			GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
					.Semicircle3D(c.getLabel(), (GeoPointND) arg[0],
							(GeoPointND) arg[1], (GeoDirectionND) arg[2]) };
			return ret;
		}

		return null;
	}
}
