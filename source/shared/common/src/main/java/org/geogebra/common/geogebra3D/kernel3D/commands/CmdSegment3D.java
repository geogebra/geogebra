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
import org.geogebra.common.kernel.commands.CmdSegment;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Segment[ &lt;GeoPoint3D&gt;, &lt;GeoPoint3D&gt; ] or CmdSegment
 */
public class CmdSegment3D extends CmdSegment {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdSegment3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement segment(String label, GeoPointND a, GeoPointND b) {
		if (a.isGeoElement3D() || b.isGeoElement3D()) {
			return (GeoElement) kernel.getManager3D().segment3D(label, a, b);
		}

		return super.segment(label, a, b);
	}

}
