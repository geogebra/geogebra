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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * Sets perspective
 *
 */
public class CmdPerspective extends CmdScripting {
	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdPerspective(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] perform(Command c) {
		GeoElement[] args = resArgs(c);
		if (args.length != 1) {
			throw this.argNumErr(c);
		}
		if (args[0] instanceof GeoText || args[0] instanceof GeoNumberValue) {
			String code = args[0].toValueString(StringTemplate.defaultTemplate);
			app.getGgbApi().setPerspective(code);
			return args;
		}

		throw this.argErr(c, args[0]);
	}

}
