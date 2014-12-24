package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdCircleArcSector;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

public class CmdCircleArcSector3D extends CmdCircleArcSector {

	public CmdCircleArcSector3D(Kernel kernel, int type) {
		super(kernel, type);
	}

	@Override
	protected GeoElement circleArcSector(String label, GeoPointND center,
			GeoPointND startPoint, GeoPointND endPoint) {

		GeoDirectionND orientation = CommandProcessor3D
				.getCurrentViewOrientation(kernelA, app);
		if (orientation != null) {
			return (GeoElement) kernelA.getManager3D().CircleArcSector3D(label,
					center, startPoint, endPoint, orientation, type);
		}

		if (center.isGeoElement3D() || startPoint.isGeoElement3D()
				|| endPoint.isGeoElement3D()) {
			return (GeoElement) kernelA.getManager3D().CircleArcSector3D(label,
					center, startPoint, endPoint, type);
		}

		return super.circleArcSector(label, center, startPoint, endPoint);
	}

	@Override
	protected GeoElement[] process4(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		// arc center-two points, oriented
		if ((ok[0] = (arg[0].isGeoPoint())) && (ok[1] = (arg[1].isGeoPoint()))
				&& (ok[2] = (arg[2].isGeoPoint()))
				&& (ok[3] = (arg[3] instanceof GeoDirectionND))) {

			GeoElement[] ret = { (GeoElement) kernelA.getManager3D()
					.CircleArcSector3D(c.getLabel(), (GeoPointND) arg[0],
							(GeoPointND) arg[1], (GeoPointND) arg[2],
							(GeoDirectionND) arg[3], type) };
			return ret;
		}

		return null;
	}
}
