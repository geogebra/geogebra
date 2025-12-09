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
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.commands.CmdPointIn;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * PointIn[ &lt;Region&gt; ]
 */
public class CmdPointIn3D extends CmdPointIn {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdPointIn3D(Kernel kernel) {
		super(kernel);

	}

	@Override
	protected GeoElement[] pointIn(String label, Region region) {
		if (region.isRegion3D()) {
			GeoElement[] ret = { (GeoElement) kernel.getManager3D()
					.point3DIn(label, region, false) };
			return ret;
		}

		return super.pointIn(label, region);
	}
}