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
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/**
 * Orthogonal[ &lt;GeoPoint3D&gt;, &lt;GeoCoordSys&gt; ]
 */
public class CmdOrthogonalPlane extends CommandProcessor {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdOrthogonalPlane(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c, info);
			if (arg[0].isGeoPoint()) {
				if (arg[1] instanceof GeoLineND) {
					return new GeoElement[] { (GeoElement) kernel
							.getManager3D().orthogonalPlane3D(c.getLabel(),
									(GeoPointND) arg[0], (GeoLineND) arg[1]) };
				} else if (arg[1] instanceof GeoVectorND) {
					return new GeoElement[] { (GeoElement) kernel
							.getManager3D().orthogonalPlane3D(c.getLabel(),
									(GeoPointND) arg[0],
									(GeoVectorND) arg[1]) };
				} else {
					throw argErr(c, arg[1]);
				}
			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}

	}

}
