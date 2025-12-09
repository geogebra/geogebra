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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Cube[ &lt;GeoPoint3D&gt;, &lt;GeoPoint3D&gt;, &lt;GeoDirectionND&gt; ]
 * 
 * or Icosahedron etc.
 */
public class CmdArchimedeanSolid extends CommandProcessor {

	private Commands name;

	/**
	 * @param kernel
	 *            Kernel
	 * @param name
	 *            command name
	 */
	public CmdArchimedeanSolid(Kernel kernel, Commands name) {
		super(kernel);
		this.name = name;
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {

		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);
			ok[0] = arg[0].isGeoPolygon();
			if (ok[0]) {
				GeoElement[] ret = kernel.getManager3D().archimedeanSolid(
						c.getLabels(), (GeoPolygon) arg[0],
						new GeoBoolean(kernel.getConstruction(), true), name);
				return ret;
			}
			throw argErr(c, arg[0]);
		case 2:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isGeoPoint())) {
				GeoElement[] ret = kernel.getManager3D().archimedeanSolid(
						c.getLabels(), (GeoPointND) arg[0], (GeoPointND) arg[1],
						name);
				return ret;

			}
			if ((ok[0] = arg[0].isGeoPolygon())
					&& (ok[1] = arg[1].isGeoBoolean())) {
				GeoElement[] ret = kernel.getManager3D().archimedeanSolid(
						c.getLabels(), (GeoPolygon) arg[0], (GeoBoolean) arg[1],
						name);
				return ret;
			}
			for (int i = 0; i < 2; i++) {
				if (!ok[i]) {
					throw argErr(c, arg[i]);
				}
			}
			break;
		case 3:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isGeoPoint())) {

				if (arg[2] instanceof GeoDirectionND) {

					GeoElement[] ret = kernel.getManager3D().archimedeanSolid(
							c.getLabels(), (GeoPointND) arg[0],
							(GeoPointND) arg[1], (GeoDirectionND) arg[2], name);
					return ret;

				}

				if (arg[2] instanceof GeoPointND) {

					GeoElement[] ret = kernel.getManager3D().archimedeanSolid(
							c.getLabels(), (GeoPointND) arg[0],
							(GeoPointND) arg[1], (GeoPointND) arg[2], name);
					return ret;

				}

				ok[2] = false;
			}

			for (int i = 0; i < 3; i++) {
				if (!ok[i]) {
					throw argErr(c, arg[i]);
				}
			}
			break;
		}

		throw argNumErr(c);

	}

}
