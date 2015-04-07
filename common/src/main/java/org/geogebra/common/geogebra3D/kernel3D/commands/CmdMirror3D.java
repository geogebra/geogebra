package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdMirror;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Mirror at 3D point or 3D line
 * 
 * @author mathieu
 *
 */
public class CmdMirror3D extends CmdMirror {

	/**
	 * constructor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMirror3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] process2(String label, GeoElement[] arg, boolean[] ok) {

		GeoElement[] ret = new GeoElement[1];

		if (arg[1] instanceof GeoCoordSys2D && !arg[1].isGeoConic()) { // no
																		// override
																		// for
																		// mirror
																		// at
																		// circle
			ret = kernelA.getManager3D().Mirror3D(label, arg[0],
					(GeoCoordSys2D) arg[1]);
			return ret;

		} else if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()) { // check
																			// if
																			// there
																			// is
																			// a
																			// 3D
																			// geo

			if (arg[0] instanceof Transformable) {
				if (arg[1].isGeoPoint()) {
					ret = kernelA.getManager3D().Mirror3D(label, arg[0],
							(GeoPointND) arg[1]);
					return ret;
				}
				if (arg[1].isGeoLine()) {
					ret = kernelA.getManager3D().Mirror3D(label, arg[0],
							(GeoLineND) arg[1]);
					return ret;
				}

				ok[1] = false;
			} else {
				ok[0] = false;
			}
		}

		return super.process2(label, arg, ok);
	}

}
