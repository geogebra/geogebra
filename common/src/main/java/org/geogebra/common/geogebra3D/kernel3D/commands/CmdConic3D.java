package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Conic through five points
 *
 */
public class CmdConic3D extends CmdConic {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdConic3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement conic(String label, GeoElement[] arg) {
		GeoPointND[] points = new GeoPointND[5];

		boolean is3D = false;
		for (int i = 0; i < 5; i++) {
			points[i] = (GeoPointND) arg[i];
			if (!is3D && points[i].isGeoElement3D()) {
				is3D = true;
			}
		}

		if (is3D) {
			return kernel.getManager3D().conic3D(label, points);
		}

		return super.conic(label, arg);
	}

}
