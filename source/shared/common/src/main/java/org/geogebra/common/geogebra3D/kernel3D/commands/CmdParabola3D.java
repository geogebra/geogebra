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
import org.geogebra.common.kernel.commands.CmdParabola;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Parabola command
 *
 */
public class CmdParabola3D extends CmdParabola {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdParabola3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement parabola(String label, GeoPointND a, GeoLineND d) {
		if (a.isGeoElement3D() || d.isGeoElement3D()) {
			return kernel.getManager3D().parabola3D(label, a, d);
		}

		return super.parabola(label, a, d);
	}

}
