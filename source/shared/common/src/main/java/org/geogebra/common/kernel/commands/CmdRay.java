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

package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/**
 * Ray[ &lt;GeoPoint&gt;, &lt;GeoPoint&gt; ]
 * 
 * Ray[ &lt;GeoPoint&gt;, &lt;GeoVector&gt; ]
 */
public class CmdRay extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRay(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c, info);

			// line through two points
			if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isGeoPoint())) {
				GeoElement[] ret = { ray(c.getLabel(), (GeoPointND) arg[0],
						(GeoPointND) arg[1]) };
				return ret;
			}

			// line through point with direction vector
			else if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isGeoVector())) {
				GeoElement[] ret = { ray(c.getLabel(), (GeoPointND) arg[0],
						(GeoVectorND) arg[1]) };
				return ret;
			}

			// syntax error
			else {
				if (!ok[0]) {
					throw argErr(c, arg[0]);
				}
				throw argErr(c, arg[1]);
			}

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @param label
	 *            label
	 * @param a
	 *            first point
	 * @param b
	 *            second point
	 * @return [ab)
	 */
	protected GeoElement ray(String label, GeoPointND a, GeoPointND b) {
		return getAlgoDispatcher().ray(label, (GeoPoint) a, (GeoPoint) b);
	}

	/**
	 * @param label
	 *            label
	 * @param a
	 *            first point
	 * @param v
	 *            vector direction
	 * @return [av)
	 */
	protected GeoElement ray(String label, GeoPointND a, GeoVectorND v) {
		return getAlgoDispatcher().ray(label, (GeoPoint) a, (GeoVector) v);
	}
}