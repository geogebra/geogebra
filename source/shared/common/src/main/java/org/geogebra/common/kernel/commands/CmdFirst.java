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
import org.geogebra.common.kernel.algos.AlgoFirst;
import org.geogebra.common.kernel.algos.AlgoFirstLocus;
import org.geogebra.common.kernel.algos.AlgoFirstString;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocusable;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * First[ &lt;List&gt;, n ]
 * 
 * @author Michael Borcherds
 */
public class CmdFirst extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFirst(Kernel kernel) {
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
				GeoElement[] ret = {
						first(c.getLabel(), (GeoList) arg[0], null) };
				return ret;
			} else if (arg[0].isGeoText()) {
				GeoElement[] ret = {
						first(c.getLabel(), (GeoText) arg[0], null) };
				return ret;
			} else {
				throw argErr(c, arg[0]);
			}
		case 2:
			boolean list = arg[0].isGeoList();
			boolean string = arg[0].isGeoText();
			boolean locus = arg[0].isGeoLocusable();
			if (list && arg[1].isGeoNumeric()) {
				GeoElement[] ret = { first(c.getLabel(), (GeoList) arg[0],
						(GeoNumeric) arg[1]) };
				return ret;
			} else if (string && arg[1].isGeoNumeric()) {
				GeoElement[] ret = { first(c.getLabel(), (GeoText) arg[0],
						(GeoNumeric) arg[1]) };
				return ret;
			} else if (locus && arg[1].isGeoNumeric()) {

				AlgoFirstLocus algo = new AlgoFirstLocus(cons, c.getLabel(),
						(GeoLocusable) arg[0], (GeoNumeric) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else {
				throw argErr(c,
						(list && string) ? arg[1] : arg[0]);
			}

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * First[string,n] Michael Borcherds
	 */
	final private GeoText first(String label, GeoText list, GeoNumeric n) {
		AlgoFirstString algo = new AlgoFirstString(cons, label, list, n);
		GeoText list2 = algo.getResult();
		return list2;
	}

	/**
	 * First[list,n] Michael Borcherds
	 */
	final private GeoList first(String label, GeoList list, GeoNumeric n) {
		AlgoFirst algo = new AlgoFirst(cons, label, list, n);
		GeoList list2 = algo.getResult();
		return list2;
	}

}
