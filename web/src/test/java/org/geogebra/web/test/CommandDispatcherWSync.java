package org.geogebra.web.test;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcherAdvanced;
import org.geogebra.common.kernel.commands.CommandDispatcherCAS;
import org.geogebra.common.kernel.commands.CommandDispatcherDiscrete;
import org.geogebra.common.kernel.commands.CommandDispatcherInterface;
import org.geogebra.common.kernel.commands.CommandDispatcherProver;
import org.geogebra.common.kernel.commands.CommandDispatcherScripting;
import org.geogebra.common.kernel.commands.CommandDispatcherStats;
import org.geogebra.web.html5.kernel.commands.CommandDispatcherW;

/**
 * Synchronized version of Command Dispatcher
 */
class CommandDispatcherWSync extends CommandDispatcherW {

	public CommandDispatcherWSync(Kernel cmdKernel) {
		super(cmdKernel);
	}

	@Override
	public CommandDispatcherInterface getStatsDispatcher() {
		return new CommandDispatcherStats();
	}

	@Override
	public CommandDispatcherInterface getDiscreteDispatcher() {
		return new CommandDispatcherDiscrete();
	}

	@Override
	public CommandDispatcherInterface getCASDispatcher() {
		return new CommandDispatcherCAS();
	}

	@Override
	public CommandDispatcherInterface getScriptingDispatcher() {
		return new CommandDispatcherScripting();
	}

	@Override
	public CommandDispatcherInterface getAdvancedDispatcher() {
		return new CommandDispatcherAdvanced();
	}

	@Override
	public CommandDispatcherInterface getProverDispatcher() {
		return new CommandDispatcherProver();
	}
}
