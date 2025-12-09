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
