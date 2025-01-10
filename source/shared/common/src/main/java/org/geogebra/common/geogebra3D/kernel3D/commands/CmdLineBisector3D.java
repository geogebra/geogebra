package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdLineBisector;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.MyError;

/**
 * Line bisector
 *
 */
public class CmdLineBisector3D extends CmdLineBisector {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdLineBisector3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] process2(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		// line through point orthogonal to vector
		if ((ok[0] = (arg[0].isGeoSegment()))
				&& (ok[1] = (arg[1] instanceof GeoDirectionND))) {
			GeoElement[] ret = {
					kernel.getManager3D().lineBisector3D(c.getLabel(),
							(GeoSegmentND) arg[0], (GeoDirectionND) arg[1]) };
			return ret;
		}

		return super.process2(c, arg, ok);
	}

	@Override
	protected GeoElement lineBisector(String label, GeoSegmentND segment) {

		GeoDirectionND orientation = CommandProcessor3D
				.getCurrentViewOrientationNoSpace(kernel, app);
		if (orientation == null) {
			if (segment.isGeoElement3D()) {
				orientation = kernel.getXOYPlane();
			} else {
				// use 2D algo
				return super.lineBisector(label, segment);
			}
		}

		return kernel.getManager3D().lineBisector3D(label, segment,
				orientation);
	}

	@Override
	protected GeoElement lineBisector(String label, GeoPointND a,
			GeoPointND b) {

		GeoDirectionND orientation = CommandProcessor3D
				.getCurrentViewOrientationNoSpace(kernel, app);
		if (orientation == null) {
			if (a.isGeoElement3D() || b.isGeoElement3D()) {
				orientation = kernel.getXOYPlane();
			} else {
				// use 2D algo
				return super.lineBisector(label, a, b);
			}
		}

		return kernel.getManager3D().lineBisector3D(label, a, b, orientation);
	}

	@Override
	protected GeoElement[] process3(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {

		if ((ok[0] = (arg[0].isGeoPoint())) && (ok[1] = (arg[1].isGeoPoint()))
				&& (ok[2] = (arg[2] instanceof GeoDirectionND))) {

			GeoElement[] ret = { kernel.getManager3D().lineBisector3D(
					c.getLabel(), (GeoPointND) arg[0], (GeoPointND) arg[1],
					(GeoDirectionND) arg[2]) };
			return ret;
		}

		return null;
	}

}
