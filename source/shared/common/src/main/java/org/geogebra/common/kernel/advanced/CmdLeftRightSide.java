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
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * LeftSide[equation] RightSide[equation]
 * 
 * @author Zbynek Konecny
 *
 */
public class CmdLeftRightSide extends CommandProcessor {
	private boolean left;

	/**
	 * 
	 * @param kernel
	 *            kernel
	 * @param left
	 *            true for LeftSide, false for RightSide
	 */
	public CmdLeftRightSide(Kernel kernel, boolean left) {
		super(kernel);
		this.left = left;
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		GeoElement[] args = resArgs(c, info);
		if (args.length != 1) {
			throw argNumErr(c);
		}
		if (!(args[0] instanceof EquationValue)
				|| (args[0].isGeoImplicitCurve() && args[0].isGeoElement3D())) {
			throw argErr(c, args[0]);
		}

		AlgoLeftRightSide algo = new AlgoLeftRightSide(cons, c.getLabel(),
				args[0], left);

		return new GeoElement[] { algo.getResult() };
	}

}
