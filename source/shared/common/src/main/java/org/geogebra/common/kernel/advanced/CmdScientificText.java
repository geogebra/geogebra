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

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * ScientificText(number)
 */
public class CmdScientificText extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdScientificText(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 1:

			if (arg[0].isGeoNumeric()) {
				GeoElement[] ret = { scientificText(c.getLabel(),
						(GeoNumeric) arg[0], null) };
				return ret;
			}

			throw argErr(c, arg[arg[0].isGeoNumeric() ? 1 : 0]);
		case 2:

			if (arg[0].isGeoNumeric() && arg[1].isGeoNumeric()) {
				GeoElement[] ret = { scientificText(c.getLabel(),
						(GeoNumeric) arg[0], (GeoNumeric) arg[1]) };
				return ret;
			}

			throw argErr(c, arg[arg[0].isGeoNumeric() ? 1 : 0]);

		default:
			throw argNumErr(c);
		}
	}

	final private GeoText scientificText(String label, GeoNumeric num,
			GeoNumeric prec) {
		AlgoScientificText algo = new AlgoScientificText(cons, num,
				prec);
		GeoText text = algo.getResult();
		text.setLabel(label);
		return text;
	}
}
