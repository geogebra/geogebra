package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.plugin.EventType;

/**
 * 
 * @author Giuliano Bellucci
 * @date 19/03/2013
 * 
 */

public class CmdRunClickScript extends CmdScripting {

	public CmdRunClickScript(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void perform(Command c) {

		int n = c.getArgumentNumber();
		GeoElement[] args;

		switch (n) {
		
		case 1:
			args = resArgs(c);
			if (args[0].getScript(EventType.CLICK) == null) {
				return;
			}
			args[0].runClickScripts(null);
			break;
			
		default:
			throw argNumErr(app, c.getName(), n);
		}

	}
}

