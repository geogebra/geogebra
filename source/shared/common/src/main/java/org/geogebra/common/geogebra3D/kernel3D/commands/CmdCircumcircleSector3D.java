/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
