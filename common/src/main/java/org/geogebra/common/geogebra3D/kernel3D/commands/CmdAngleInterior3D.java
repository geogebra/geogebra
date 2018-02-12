package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdAngleInterior;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;

/**
 * 3D processor for AngleInterior
 *
 */
public class CmdAngleInterior3D extends CmdAngleInterior {

	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdAngleInterior3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] process(String[] labels, GeoPolygon poly) {
		if (poly.isGeoElement3D()) {
			return kernel.getManager3D().Angles3D(labels, poly, true);
		}
		return super.process(labels, poly);
	}
}
