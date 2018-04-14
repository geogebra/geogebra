package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdMidpoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.MyError;

/**
 * Midpoint command
 *
 */
public class CmdMidpoint3D extends CmdMidpoint {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdMidpoint3D(Kernel kernel) {
		super(kernel);

	}

	@Override
	protected GeoElement[] process1(Command c, GeoElement arg) throws MyError {
		if ((arg instanceof GeoQuadric3D)
				&& !(arg instanceof GeoQuadric3DPart)) {
			GeoElement[] ret = { (GeoElement) kernel.getManager3D()
					.centerQuadric(c.getLabel(), (GeoQuadricND) arg) };
			return ret;
		}

		return super.process1(c, arg);

	}

	@Override
	protected GeoElement[] segment(String label, GeoSegmentND segment) {

		if (segment.isGeoElement3D()) {
			GeoElement[] ret = { (GeoElement) kernel.getManager3D()
					.midpoint(label, segment) };
			return ret;
		}

		return super.segment(label, segment);
	}

	@Override
	protected GeoElement[] conic(String label, GeoConicND conic) {

		if (conic.isGeoElement3D()) {
			GeoElement[] ret = {
					(GeoElement) kernel.getManager3D().center(label, conic) };
			return ret;
		}

		return super.conic(label, conic);

	}

	@Override
	protected GeoElement[] twoPoints(String label, GeoPointND p1,
			GeoPointND p2) {

		if (p1.isGeoElement3D() || p2.isGeoElement3D()) {
			GeoElement[] ret = { (GeoElement) kernel.getManager3D()
					.midpoint(label, p1, p2) };
			return ret;
		}

		return super.twoPoints(label, p1, p2);

	}
}
