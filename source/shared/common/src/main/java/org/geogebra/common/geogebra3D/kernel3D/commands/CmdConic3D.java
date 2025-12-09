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
