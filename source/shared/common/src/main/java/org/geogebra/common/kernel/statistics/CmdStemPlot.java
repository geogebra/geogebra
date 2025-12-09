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

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * StemPlot
 * 
 * @author Michael Borcherds
 */
public class CmdStemPlot extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdStemPlot(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {

		case 1:
			if (arg[0].isGeoList()) {
				GeoList list = (GeoList) arg[0];

				GeoElement[] ret = { stemPlot(c.getLabel(), list, null) };
				return ret;
			}
			throw argErr(c, arg[0]);

		case 2:
			if (!arg[0].isGeoList()) {
				throw argErr(c, arg[0]);
			}
			if (!arg[1].isGeoNumeric()) {
				throw argErr(c, arg[1]);
			}

			GeoElement[] ret = { stemPlot(c.getLabel(), (GeoList) arg[0],
					(GeoNumeric) arg[1]) };
			return ret;

		case 0:
			throw argNumErr(c);

		default:

			GeoList list = wrapInList(kernel, arg, arg.length,
					GeoClass.DEFAULT);
			if (list != null) {
				GeoElement[] ret2 = { stemPlot(c.getLabel(), list, null) };
				return ret2;
			}

			throw argErr(c, arg[0]);
		}
	}

	/**
	 * StemPlot[list, number]
	 * 
	 * @param label
	 *            output label
	 * @param list
	 *            list
	 * @param num
	 *            scale adjustment
	 */
	private GeoText stemPlot(String label, GeoList list, GeoNumeric num) {
		AlgoStemPlot algo = new AlgoStemPlot(cons, label, list, num);
		GeoText text = algo.getResult();
		return text;
	}
}
