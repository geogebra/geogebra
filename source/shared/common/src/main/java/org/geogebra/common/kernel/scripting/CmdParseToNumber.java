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

package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.AlgoParseToNumberOrFunction;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * ParseToNumber
 */
public class CmdParseToNumber extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdParseToNumber(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		if (!info.isScripting() && n == 2) {
			return new GeoElement[0];
		}
		GeoElement[] arg = resArgs(c, info);
		boolean ok;
		switch (n) {
		case 1:
			if (arg[0] instanceof GeoText) {
				AlgoParseToNumberOrFunction
						algo = new AlgoParseToNumberOrFunction(cons, (GeoText) arg[0], null,
						Commands.ParseToNumber, c.getLabel());
				algo.getOutput(0).setLabel(c.getLabel());
				return algo.getOutput();
			}
			throw argErr(arg[0], c);
		case 2:
			if ((ok = arg[0].isGeoNumeric()) && arg[1].isGeoText()) {

				GeoNumeric num = (GeoNumeric) arg[0];
				String str = ((GeoText) arg[1]).getTextStringSafe();

				try {
					kernel.getAlgebraProcessor().evaluateToDouble(str, true,
							num);
					num.updateCascade();
				} catch (Exception e) {
					num.setUndefined();
					num.updateCascade();
				}

				return num.asArray();
			} else if (!ok) {
				throw argErr(c, arg[0]);
			} else {
				throw argErr(c, arg[1]);
			}

		default:
			throw argNumErr(c);
		}
	}
}
