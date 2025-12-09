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
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * SetConstructionStep[ &lt;Number&gt; ]
 * 
 * @author Michael Borcherds
 */
public class CmdSetConstructionStep extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetConstructionStep(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 1:
			double newStep = Math.round(arg[0].evaluateDouble() - 1);
			int maxStep = cons.steps();
			// eg SetConstructionStep[infinity] to set to end
			if (newStep >= maxStep) {
				newStep = maxStep - 1;
			}

			cons.setStep((int) newStep);

			if (app.getGuiManager() != null) {
				app.getGuiManager().updateNavBars();
			}

			return arg;

		default:
			throw argNumErr(c);
		}
	}

}
