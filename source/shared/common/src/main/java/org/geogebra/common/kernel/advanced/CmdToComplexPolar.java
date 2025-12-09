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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.main.MyError;

/**
 * ToComplex[Vector] ToComplex[List]
 *
 */
public class CmdToComplexPolar extends CommandProcessor {
	private int coordStyle;

	/**
	 * @param kernel
	 *            kernel
	 * @param coordStyle
	 *            Kernel.COORD_*
	 */
	public CmdToComplexPolar(Kernel kernel, int coordStyle) {
		super(kernel);
		this.coordStyle = coordStyle;
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		if (c.getArgumentNumber() != 1) {
			throw argNumErr(c);
		}
		GeoElement[] arg = resArgs(c, info);
		AlgoToComplexPolar algo = null;
		if (arg[0] instanceof GeoPoint) {
			algo = new AlgoToComplexPolar(cons, (GeoPoint) arg[0], coordStyle);
		}
		if (arg[0] instanceof GeoVector) {
			algo = new AlgoToComplexPolar(cons, (GeoVector) arg[0], coordStyle);
		}
		if (arg[0] instanceof GeoList) {
			algo = new AlgoToComplexPolar(cons, (GeoList) arg[0], coordStyle);
		}
		if (arg[0] instanceof GeoNumberValue) {
			algo = new AlgoToComplexPolar(cons,
					(GeoNumberValue) arg[0], coordStyle);
		}
		if (algo == null) {
			throw argErr(arg[0], c);
		}
		algo.getResult().setLabel(c.getLabel());
		return new GeoElement[] { algo.getResult() };
	}

}
