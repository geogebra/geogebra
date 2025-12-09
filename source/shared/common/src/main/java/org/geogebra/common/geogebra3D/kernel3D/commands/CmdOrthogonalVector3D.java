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
import org.geogebra.common.kernel.commands.CmdOrthogonalVector;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.main.MyError;

/**
 * OrthogonalVector[ &lt;GeoPlane3D&gt; ]
 */
public class CmdOrthogonalVector3D extends CmdOrthogonalVector {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdOrthogonalVector3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);
			if (arg[0] instanceof GeoCoordSys2D) {
				GeoElement[] ret = {
						(GeoElement) kernel.getManager3D().orthogonalVector3D(
								c.getLabel(), (GeoCoordSys2D) arg[0]) };
				return ret;
			}
			break;

		case 2:
			arg = resArgs(c, info);
			if (arg[0] instanceof GeoLineND
					&& arg[1] instanceof GeoDirectionND) {
				GeoElement[] ret = { (GeoElement) kernel.getManager3D()
						.orthogonalVector3D(c.getLabel(), (GeoLineND) arg[0],
								(GeoDirectionND) arg[1]) };
				return ret;
			}
			break;

		}

		return super.process(c, info);
	}

}
