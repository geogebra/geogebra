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

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoTranslateVector3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoVectorPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoTranslateVector;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdTranslate;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/**
 * Translate command
 *
 */
public class CmdTranslate3D extends CmdTranslate {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdTranslate3D(Kernel kernel) {
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

		if (n == 2) {
			arg = resArgs(c, info);

			// check if there is a 3D geo
			if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()) {
				if (arg[0].isGeoVector() && arg[1].isGeoPoint()) {
					AlgoTranslateVector algo = getAlgoTranslateVector(label,
							arg[0], arg[1]);

					ret[0] = (GeoElement) algo.getTranslatedVector();
					return ret;
				}
				ok[0] = arg[0] instanceof Translateable
						|| arg[0] instanceof GeoPolygon || arg[0].isGeoList();
				// translate object
				if (ok[0] && (ok[1] = arg[1].isGeoVector())) {
					ret = kernel.getManager3D().translate3D(label, arg[0],
							(GeoVectorND) arg[1]);
					return ret;
				} else if (ok[0] && (ok[1] = arg[1] instanceof GeoPointND)) {

					// wrap (1,2,3) as Vector[(1,2,3)]
					AlgoVectorPoint3D algoVP = new AlgoVectorPoint3D(cons,
							(GeoPointND) arg[1]);
					cons.removeFromConstructionList(algoVP);

					ret = kernel.getManager3D().translate3D(label, arg[0],
							algoVP.getVector());
					return ret;
				}
				throw argErr(c, getBadArg(ok, arg));
			}
		}

		return super.process(c, info);
	}

	@Override
	protected AlgoTranslateVector getAlgoTranslateVector(String label,
			GeoElement v, GeoElement P) {

		if (v.isGeoElement3D()) {
			return new AlgoTranslateVector3D(cons, label, (GeoVector3D) v,
					(GeoPointND) P);
		}

		return super.getAlgoTranslateVector(label, v, P);
	}

}
