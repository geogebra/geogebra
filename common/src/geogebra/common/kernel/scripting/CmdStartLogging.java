package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.plugin.UDPLogger;

public class CmdStartLogging extends CmdScripting {
	public CmdStartLogging(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void perform(Command c) {

		UDPLogger logger = app.getUDPLogger();
		if (logger != null) {
			logger.startLogging();
		} else {
			// no logging available
		}

	}



}
