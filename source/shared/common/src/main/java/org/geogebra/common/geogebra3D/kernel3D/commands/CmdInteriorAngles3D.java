package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdInteriorAngles;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;

/**
 * 3D processor for InteriorAngles
 *
 */
public class CmdInteriorAngles3D extends CmdInteriorAngles {

	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdInteriorAngles3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] process(String[] labels, GeoPolygon poly) {
		if (poly.isGeoElement3D()) {
			return kernel.getManager3D().angles3D(labels, poly, true);
		}
		return super.process(labels, poly);
	}
}
