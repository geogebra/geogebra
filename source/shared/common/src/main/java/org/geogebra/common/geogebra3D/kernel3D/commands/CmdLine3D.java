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
import org.geogebra.common.kernel.commands.CmdLine;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/**
 * Line[ &lt;GeoPoint3D&gt;, &lt;GeoPoint3D&gt; ] or CmdLine
 */
public class CmdLine3D extends CmdLine {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdLine3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {

		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		if (n == 2) {
			arg = resArgs(c, info);
			if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()) {

				GeoElement geo0 = arg[0];
				GeoElement geo1 = arg[1];

				if ((ok[0] = geo0.isGeoPoint())
						&& (ok[1] = geo1.isGeoPoint())) { // line between two
															// 3D points
					GeoElement[] ret = {
							kernel.getManager3D().line3D(c.getLabel(),
									(GeoPointND) geo0, (GeoPointND) geo1) };
					return ret;
				} else if ((ok[0] = geo0.isGeoPoint())
						&& (ok[1] = geo1.isGeoVector())) { // line directed
					GeoElement[] ret = { (GeoElement) kernel.getManager3D()
							.line3D(c.getLabel(), (GeoPointND) geo0,
									(GeoVectorND) geo1) };
					return ret;

				} else if ((ok[0] = geo0.isGeoPoint())
						&& (ok[1] = geo1 instanceof GeoLineND)) { // line
																	// parallel
					GeoElement[] ret = { (GeoElement) kernel.getManager3D()
							.line3D(c.getLabel(), (GeoPointND) geo0,
									(GeoLineND) geo1) };
					return ret;
				}
			}
		}

		return super.process(c, info);
	}

}
