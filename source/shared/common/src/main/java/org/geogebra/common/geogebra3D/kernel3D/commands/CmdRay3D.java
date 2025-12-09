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

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoRayPointVector3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdRay;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Ray[ &lt;GeoPoint3D&gt;, &lt;GeoPoint3D&gt; ] or CmdRay
 */
public class CmdRay3D extends CmdRay {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdRay3D(Kernel kernel) {
		super(kernel);

	}

	@Override
	protected GeoElement ray(String label, GeoPointND a, GeoPointND b) {
		if (a.isGeoElement3D() || b.isGeoElement3D()) {
			return (GeoElement) kernel.getManager3D().ray3D(label, a, b);
		}

		return super.ray(label, a, b);
	}

	@Override
	protected GeoElement ray(String label, GeoPointND a, GeoVectorND v) {

		if (a.isGeoElement3D() || v.isGeoElement3D()) {
			AlgoRayPointVector3D algo = new AlgoRayPointVector3D(
					kernel.getConstruction(),  a, v);
			algo.getLine().setLabel(label);
			return algo.getLine();
		}

		return super.ray(label, a, v);
	}

}
