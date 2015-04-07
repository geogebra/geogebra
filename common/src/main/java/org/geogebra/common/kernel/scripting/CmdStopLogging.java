package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.plugin.SensorLogger;

public class CmdStopLogging extends CmdScripting {
	public CmdStopLogging(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void perform(Command c) {

		SensorLogger logger = app.getSensorLogger();
		if (logger != null) {
			logger.stopLogging();
		} else {
			// no need for error
		}

	}

}
