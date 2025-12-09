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
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

public class CmdSetLineOpacity extends CmdScripting {

	/**
	 * Create new command processor
	 * @param kernel kernel
	 */
	public CmdSetLineOpacity(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		switch (n) {
		case 2:
			GeoElement[] arg = resArgs(c);
			if (arg[1].isNumberValue()) {

				int percentage = (int) (Math.max(0, Math.min(1, arg[1].evaluateDouble())) * 100);
				int opacity = Math.round(percentage / 100f * 255);

				arg[0].setLineOpacity(opacity);
				arg[0].updateVisualStyleRepaint(GProperty.LINE_STYLE);

				return arg;
			}
			throw argErr(c, arg[1]);
		default:
			throw argNumErr(c);
		}
	}
}
