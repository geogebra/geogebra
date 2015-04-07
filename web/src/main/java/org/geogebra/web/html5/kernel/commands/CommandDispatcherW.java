package org.geogebra.web.html5.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.CommandDispatcherAdvanced;
import org.geogebra.common.kernel.commands.CommandDispatcherCAS;
import org.geogebra.common.kernel.commands.CommandDispatcherDiscrete;
import org.geogebra.common.kernel.commands.CommandDispatcherInterface;
import org.geogebra.common.kernel.commands.CommandDispatcherScripting;

public class CommandDispatcherW extends CommandDispatcher {

	public CommandDispatcherW(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected CommandDispatcherInterface getDiscreteDispatcher() {
		if (discreteDispatcher == null) {
			discreteDispatcher = new CommandDispatcherDiscrete();
			initCmdTable();
		}
		return discreteDispatcher;
	}

	@Override
	protected CommandDispatcherInterface getScriptingDispatcher() {
		if (scriptingDispatcher == null) {
			scriptingDispatcher = new CommandDispatcherScripting();
			initCmdTable();
			// kernel.getApplication().getActiveEuclidianView().repaintView();
		}
		return scriptingDispatcher;
	}

	@Override
	protected CommandDispatcherInterface getAdvancedDispatcher() {
		if (advancedDispatcher == null) {
			advancedDispatcher = new CommandDispatcherAdvanced();
			initCmdTable();
			// kernel.getApplication().getActiveEuclidianView().repaintView();
		}
		return advancedDispatcher;
	}

	@Override
	protected CommandDispatcherInterface getCASDispatcher() {
		if (casDispatcher == null) {
			casDispatcher = new CommandDispatcherCAS();
			initCmdTable();
			// kernel.getApplication().getActiveEuclidianView().repaintView();
		}
		return casDispatcher;
	}

}
