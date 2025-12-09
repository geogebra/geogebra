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
import org.geogebra.common.kernel.algos.AlgoConicFromCoeffList;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * Conic[ &lt;List&gt; ]
 * 
 * Conic[ five GeoPoints ]
 */
public class CmdConic extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdConic(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c, info);
		switch (n) {
		case 1:
			if (arg[0].isGeoList()) {
				return conic(c.getLabel(), (GeoList) arg[0]);
			}
		case 5:
			for (int i = 0; i < 5; i++) {
				if (!arg[i].isGeoPoint()) {
					throw argErr(c, arg[i]);
				}
			}
			GeoElement[] ret = { conic(c.getLabel(), arg) };
			return ret;
		default:
			if (n > 0 && arg[0] instanceof GeoNumberValue) {
				// try to create list of numbers
				GeoList list = wrapInList(arg, arg.length,
						GeoClass.NUMERIC, c);
				if (list != null) {
					ret = conic(c.getLabel(), list);
					return ret;
				}
			}
			if (n == 6) {
				for (GeoElement input : arg) {
					if (!input.isNumberValue()) {
						throw argErr(c, input);
					}
				}
			}
			throw argNumErr(c);
		}
	}

	/**
	 * conic from coefficients
	 * 
	 * @param coeffList
	 *            coefficients
	 * @return conic
	 */
	final private GeoElement[] conic(String label, GeoList coeffList) {
		AlgoConicFromCoeffList algo = new AlgoConicFromCoeffList(cons, label,
				coeffList);

		return new GeoElement[] { algo.getConic() };
	}

	/**
	 * @param label
	 *            label
	 * @param arg
	 *            points
	 * @return conic 5 points
	 */
	protected GeoElement conic(String label, GeoElement[] arg) {
		GeoPoint[] points = { (GeoPoint) arg[0], (GeoPoint) arg[1],
				(GeoPoint) arg[2], (GeoPoint) arg[3], (GeoPoint) arg[4] };
		return getAlgoDispatcher().conic(label, points);
	}
}
