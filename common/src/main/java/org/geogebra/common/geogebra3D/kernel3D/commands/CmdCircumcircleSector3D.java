package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdCircumcircleSector;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Circumcircle sector
 *
 */
public class CmdCircumcircleSector3D extends CmdCircumcircleSector {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdCircumcircleSector3D(Kernel kernel) {
		super(kernel);

	}

	@Override
	protected GeoElement getSector(String label, GeoElement A, GeoElement B,
			GeoElement C) {

		if (A.isGeoElement3D() || B.isGeoElement3D() || C.isGeoElement3D()) {
			return (GeoElement) kernel.getManager3D().circumcircleSector3D(
					label, (GeoPointND) A, (GeoPointND) B, (GeoPointND) C);
		}

		return super.getSector(label, A, B, C);
	}

}
