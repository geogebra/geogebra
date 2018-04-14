package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdDilate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Mirror at 3D point or 3D line
 * 
 * @author mathieu
 *
 */
public class CmdDilate3D extends CmdDilate {

	/**
	 * constructor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDilate3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] dilate(String label, GeoElement geoDil,
			GeoNumberValue r, GeoElement point) {

		if (geoDil.isGeoElement3D() || point.isGeoElement3D()) {
			return kernel.getManager3D().dilate3D(label, geoDil, r,
					(GeoPointND) point);
		}

		return super.dilate(label, geoDil, r, point);

	}

}
