package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoConicFociLength3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoEllipseFociLength3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoHyperbolaFociLength3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdEllipseHyperbola;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

public class CmdEllipseHyperbola3D extends CmdEllipseHyperbola {

	public CmdEllipseHyperbola3D(Kernel kernel, final int type) {
		super(kernel, type);
	}

	@Override
	protected GeoElement ellipse(String label, GeoPointND a, GeoPointND b,
			GeoPointND c) {

		if (a.isGeoElement3D() || b.isGeoElement3D() || c.isGeoElement3D()) {
			return kernelA.getManager3D().EllipseHyperbola3D(label, a, b, c,
					type);
		}

		return super.ellipse(label, a, b, c);
	}

	@Override
	protected GeoElement[] process4(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		if ((ok[0] = (arg[0].isGeoPoint())) && (ok[1] = (arg[1].isGeoPoint()))
				&& (ok[2] = (arg[2].isGeoPoint()))
				&& (ok[3] = (arg[3] instanceof GeoDirectionND))) {

			GeoElement[] ret = { kernelA.getManager3D().EllipseHyperbola3D(
					c.getLabel(), (GeoPointND) arg[0], (GeoPointND) arg[1],
					(GeoPointND) arg[2], (GeoDirectionND) arg[3], type) };
			return ret;
		}

		if ((ok[0] = (arg[0].isGeoPoint())) && (ok[1] = (arg[1].isGeoPoint()))
				&& (ok[2] = (arg[2] instanceof GeoNumberValue))
				&& (ok[3] = (arg[3] instanceof GeoDirectionND))) {

			GeoElement[] ret = { ellipseHyperbola(c.getLabel(),
					(GeoPointND) arg[0], (GeoPointND) arg[1],
					(GeoNumberValue) arg[2], (GeoDirectionND) arg[3]) };
			return ret;
		}

		return null;
	}

	private final GeoElement ellipseHyperbola(String label, GeoPointND a,
			GeoPointND b, GeoNumberValue v, GeoDirectionND orientation) {

		// check if all 2D
		if (!a.isGeoElement3D() && !b.isGeoElement3D()
				&& orientation == kernelA.getXOYPlane()) {
			return super.ellipseHyperbola(label, a, b, v);
		}

		// use 3D algo with orientation
		AlgoConicFociLength3D algo;
		if (type == GeoConicNDConstants.CONIC_HYPERBOLA) {
			algo = new AlgoHyperbolaFociLength3D(kernelA.getConstruction(),
					label, a, b, v, orientation);
		} else { // ellipse
			algo = new AlgoEllipseFociLength3D(kernelA.getConstruction(),
					label, a, b, v, orientation);
		}
		return algo.getConic();
	}

	@Override
	protected GeoElement ellipseHyperbola(String label, GeoPointND a,
			GeoPointND b, GeoNumberValue v) {

		GeoDirectionND orientation = CommandProcessor3D
				.getCurrentViewOrientation(kernelA, app);
		if (orientation == null) {
			if (a.isGeoElement3D() || b.isGeoElement3D()) {
				orientation = kernelA.getXOYPlane();
			} else {
				// use 2D algo
				return super.ellipseHyperbola(label, a, b, v);
			}
		}

		return ellipseHyperbola(label, a, b, v, orientation);
	}
}
