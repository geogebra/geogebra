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
