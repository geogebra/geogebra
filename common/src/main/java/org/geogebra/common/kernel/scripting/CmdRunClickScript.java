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
