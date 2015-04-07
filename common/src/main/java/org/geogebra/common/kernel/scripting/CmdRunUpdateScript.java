package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;

/**
 * 
 * @author Giuliano Bellucci
 * @date 19/03/2013
 * 
 */

public class CmdRunUpdateScript extends CmdScripting {

	public CmdRunUpdateScript(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void perform(Command c) {
		int n = c.getArgumentNumber();
		GeoElement[] args;

		switch (n) {

		case 1:
			args = resArgs(c);
			if (args[0].getScript(EventType.UPDATE) == null) {
				return;
			}

			kernelA.getApplication().dispatchEvent(
					new Event(EventType.UPDATE, args[0], true));
			break;

		default:
			throw argNumErr(app, c.getName(), n);
		}

	}
}
