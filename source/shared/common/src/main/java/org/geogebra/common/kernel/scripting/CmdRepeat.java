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
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoScriptAction;
import org.geogebra.common.main.MyError;

/**
 * Repeat[ &lt;Number&gt;, &lt;Scripting Command&gt;, &lt;Scripting Command&gt;, ... ]
 */
public class CmdRepeat extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRepeat(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		if (n < 2) {
			throw argNumErr(c);
		}

		GeoElement[] arg = resArgs(c);
		if (arg[0] instanceof GeoNumberValue) {

			int loopMax = (int) arg[0].evaluateDouble();
			if (loopMax < 1) {
				throw argErr(c, arg[0]);
			}

			for (int loop = 0; loop < loopMax; loop++) {

				for (int object = 1; object < n; object++) {

					if (arg[object] instanceof GeoScriptAction) {
						GeoScriptAction script = (GeoScriptAction) arg[object];
						script.perform();
					} else {
						throw argErr(c, arg[object]);
					}
				}
			}

			return arg;
		}
		throw argErr(c, arg[0]);

	}
}
