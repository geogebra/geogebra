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
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Semicircle[ &lt;GeoPoint&gt;, &lt;GeoPoint&gt; ]
 */
public class CmdSemicircle extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSemicircle(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isGeoPoint())) {
				GeoElement[] ret = { semicircle(c.getLabel(),
						(GeoPointND) arg[0], (GeoPointND) arg[1]) };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		case 3:
			arg = resArgs(c, info);

			GeoElement[] ret = process3(c, arg, ok);

			return ret;

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 * @return semicircle joining A and B
	 */
	protected GeoElement semicircle(String label, GeoPointND A, GeoPointND B) {
		return getAlgoDispatcher().semicircle(label, (GeoPoint) A,
				(GeoPoint) B);
	}

	/**
	 * process semicircle when 3 arguments
	 * 
	 * @param c
	 *            command
	 * @param arg
	 *            arguments
	 * @param ok
	 *            ok array
	 * @return result (if one)
	 * @throws MyError
	 *             in 2D, not possible with 3 args
	 */
	protected GeoElement[] process3(Command c, GeoElement[] arg, boolean[] ok)
			throws MyError {
		throw argNumErr(c);
	}
}
