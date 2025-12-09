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

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoTranslateVector;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Translate[ &lt;GeoPoint&gt;, &lt;GeoVector&gt; ] Translate[ &lt;GeoLine&gt;,
 * &lt;GeoVector&gt; ] Translate[ &lt;GeoConic&gt;, &lt;GeoVector&gt; ] Translate[
 * &lt;GeoFunction&gt;, &lt;GeoVector&gt; ] Translate[ &lt;GeoVector&gt;, &lt;GeoPoint&gt; ]
 * // set start point Translate[ &lt;GeoPolygon&gt;, &lt;GeoVector&gt; ]
 * 
 */
public class CmdTranslate extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTranslate(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		String label = c.getLabel();
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		GeoElement[] ret = new GeoElement[1];

		switch (n) {
		case 2:
			arg = resArgs(c, info);

			// translate object

			if ((ok[0] = arg[0].isGeoVector())
					&& (ok[1] = arg[1].isGeoPoint())) {

				AlgoTranslateVector algo = getAlgoTranslateVector(label, arg[0],
						arg[1]);

				ret[0] = (GeoElement) algo.getTranslatedVector();

				return ret;
			} else if ((ok[0] = arg[0] instanceof Translateable
					|| arg[0] instanceof GeoPolygon || arg[0].isGeoList())
					&& (ok[1] = arg[1] instanceof GeoVec3D)) {

				// 2D Vectors, Points
				GeoVec3D v = (GeoVec3D) arg[1];

				ret = getAlgoDispatcher().translate(label, arg[0], v);
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
	 * 
	 * @param label
	 *            label
	 * @param v
	 *            input vector
	 * @param P
	 *            starting point
	 * @return new algo translate vector
	 */
	protected AlgoTranslateVector getAlgoTranslateVector(String label,
			GeoElement v, GeoElement P) {
		return new AlgoTranslateVector(cons, label, (GeoVector) v,
				(GeoPointND) P);
	}
}