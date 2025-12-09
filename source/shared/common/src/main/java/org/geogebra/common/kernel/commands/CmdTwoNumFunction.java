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
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * Abstract class for Commands with two numerical arguments eg Binomial[
 * &lt;Number&gt;, &lt;Number&gt; ].
 * 
 * @author Michael Borcherds
 */
public abstract class CmdTwoNumFunction extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTwoNumFunction(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {

		case 2:
			arg = resArgs(c, info);
			if ((arg[0] instanceof GeoNumberValue)
					&& (arg[1] instanceof GeoNumberValue)) {
				GeoElement[] ret = { doCommand(c.getLabel(),
						(GeoNumberValue) arg[0], (GeoNumberValue) arg[1]) };
				return ret;

			}
			throw argErr(c, arg[0]);

		case 3: // return list of results
			arg = resArgs(c, info);
			if ((arg[0] instanceof GeoNumberValue)
					&& (arg[1] instanceof GeoNumberValue)
					&& (arg[2] instanceof GeoNumberValue)) {
				GeoElement[] ret = { doCommand2(c, (GeoNumberValue) arg[0],
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2]) };
				return ret;

			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * Perform the actual command
	 * 
	 * @param b
	 *            first number
	 * @param c
	 *            second number
	 * @param a
	 *            label
	 * @return resulting element
	 */
	abstract protected GeoElement doCommand(String a, GeoNumberValue b,
			GeoNumberValue c);

	/**
	 * Perform the actual command
	 * 
	 * @param c
	 *            command
	 * @param a
	 *            first arg
	 * @param b
	 *            second arg
	 * @param d
	 *            third arg
	 * @return resulting element
	 */
	protected GeoElement doCommand2(Command c, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue d) {
		throw argNumErr(c);
	}
}
