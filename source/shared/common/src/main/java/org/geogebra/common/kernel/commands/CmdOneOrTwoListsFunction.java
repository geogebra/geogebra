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
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * abstract class for Commands with one list argument eg Mean[ &lt;List&gt; ]
 * 
 * if more than one argument, then they are put into a list
 * 
 * Michael Borcherds 2008-04-12
 */
public abstract class CmdOneOrTwoListsFunction extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdOneOrTwoListsFunction(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);
		switch (n) {
		case 0:
			throw argNumErr(c);
		case 1:
			arg = resArgs(c, info);
			if (arg[0].isGeoList()) {
				GeoElement[] ret = {
						doCommand(c.getLabel(), (GeoList) arg[0]) };
				return ret;
			}
			throw argErr(c, arg[0]);

		case 2:
			arg = resArgs(c, info);
			if (arg[0].isGeoList() && arg[1].isGeoList()) {
				GeoElement[] ret = { doCommand(c.getLabel(), (GeoList) arg[0],
						(GeoList) arg[1]) };
				return ret;

			} else if (!(arg[0] instanceof VectorValue
					&& arg[1] instanceof VectorValue)) {
				throw argErr(c, arg[0]);
			}

		default:

			if (arg[0] instanceof VectorValue) {
				// try to create list of points (eg FitExp[])
				GeoList list = wrapInList(kernel, arg, arg.length,
						GeoClass.POINT);
				if (list != null) {
					GeoElement[] ret = { doCommand(c.getLabel(), list) };
					return ret;
				}
			}
			throw argNumErr(c);
		}
	}

	/**
	 * Perform the actual command
	 * 
	 * @param label
	 *            label for output
	 * @param list
	 *            input list
	 * @return resulting element
	 */
	abstract protected GeoElement doCommand(String label, GeoList list);

	/**
	 * Perform the actual command
	 * 
	 * @param label
	 *            label for output
	 * @param listX
	 *            first input list
	 * @param listY
	 *            second input list
	 * @return resulting element
	 */
	abstract protected GeoElement doCommand(String label, GeoList listX,
			GeoList listY);
}
