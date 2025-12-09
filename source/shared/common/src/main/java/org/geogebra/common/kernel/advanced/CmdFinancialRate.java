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
import org.geogebra.common.main.MyError;

/**
 * Rate[ &lt;Number of Periods&gt;, &lt;Payment&gt;, &lt;Present Value&gt;, &lt;Future
 * Value (optional)&gt;, &lt;Type (optional)&gt; ] Like the Excel Rate function
 */
public class CmdFinancialRate extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFinancialRate(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c, info);

			if ((ok[0] = arg[0].isGeoNumeric())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())) {

				AlgoFinancial algo = new AlgoFinancial(cons, c.getLabel(), null,
						(GeoNumeric) arg[0], (GeoNumeric) arg[1],
						(GeoNumeric) arg[2], null, null,
						AlgoFinancial.CalculationType.RATE);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}

			else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			} else {
				throw argErr(c, arg[3]);
			}

		case 4:
			arg = resArgs(c, info);

			if ((ok[0] = arg[0].isGeoNumeric())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())) {

				AlgoFinancial algo = new AlgoFinancial(cons, c.getLabel(), null,
						(GeoNumeric) arg[0], (GeoNumeric) arg[1],
						(GeoNumeric) arg[2], (GeoNumeric) arg[3], null,
						AlgoFinancial.CalculationType.RATE);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}

			else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			} else if (!ok[2]) {
				throw argErr(c, arg[2]);
			} else {
				throw argErr(c, arg[3]);
			}

		case 5:
			arg = resArgs(c, info);

			if ((ok[0] = arg[0].isGeoNumeric())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())) {

				AlgoFinancial algo = new AlgoFinancial(cons, c.getLabel(), null,
						(GeoNumeric) arg[0], (GeoNumeric) arg[1],
						(GeoNumeric) arg[2], (GeoNumeric) arg[3],
						(GeoNumeric) arg[4],
						AlgoFinancial.CalculationType.RATE);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}

			else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			} else if (!ok[2]) {
				throw argErr(c, arg[2]);
			} else if (!ok[3]) {
				throw argErr(c, arg[3]);
			} else {
				throw argErr(c, arg[4]);
			}

		case 6: // include guess
			arg = resArgs(c, info);

			if ((ok[0] = arg[0].isGeoNumeric())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())
					&& (ok[5] = arg[5].isGeoNumeric())) {

				AlgoFinancial algo = new AlgoFinancial(cons, c.getLabel(), null,
						(GeoNumeric) arg[0], (GeoNumeric) arg[1],
						(GeoNumeric) arg[2], (GeoNumeric) arg[3],
						(GeoNumeric) arg[4], (GeoNumeric) arg[5],
						AlgoFinancial.CalculationType.RATE);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}

			else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			} else if (!ok[2]) {
				throw argErr(c, arg[2]);
			} else if (!ok[3]) {
				throw argErr(c, arg[3]);
			} else if (!ok[4]) {
				throw argErr(c, arg[4]);
			} else {
				throw argErr(c, arg[5]);
			}

		default:
			throw argNumErr(c);
		}
	}
}
