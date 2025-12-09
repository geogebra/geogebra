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
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;

/**
 * 
 * @author Giuliano Bellucci
 * @since 19/03/2013
 * 
 */

public class CmdRunClickScript extends CmdScripting {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdRunClickScript(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] perform(Command c) {

		int n = c.getArgumentNumber();
		GeoElement[] args;

		switch (n) {

		case 1:
			args = resArgs(c);
			if (args[0].getScript(EventType.CLICK) == null) {
				return args;
			}
			if (args[0].isGeoInputBox()) {
				((GeoInputBox) args[0]).textSubmitted();
			} else {
				app.dispatchEvent(
						new Event(EventType.CLICK, args[0], args[0].getLabelSimple())
								.setAlwaysDispatched(true));
			}
			return args;

		default:
			throw argNumErr(c);
		}

	}
}
