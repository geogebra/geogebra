package org.geogebra.web.test;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.AdvancedCommandProcessorFactory;
import org.geogebra.common.kernel.commands.CASCommandProcessorFactory;
import org.geogebra.common.kernel.commands.CommandProcessorFactory;
import org.geogebra.common.kernel.commands.DiscreteCommandDispatcherFactory;
import org.geogebra.common.kernel.commands.ProverCommandProcessorFactory;
import org.geogebra.common.kernel.commands.ScriptingCommandProcessorFactory;
import org.geogebra.common.kernel.commands.StatsCommandProcessorFactory;
import org.geogebra.web.html5.kernel.commands.CommandDispatcherW;

/**
 * Synchronized version of Command Dispatcher.
 * Functionally equivalent to the {@code common-jre} implementation which cannot be
 * simply imported to web because of code splitting.
 */
class CommandDispatcherWSync extends CommandDispatcherW {

	public CommandDispatcherWSync(Kernel cmdKernel) {
		super(cmdKernel);
	}

	@Override
	public CommandProcessorFactory getStatsDispatcher() {
		return new StatsCommandProcessorFactory();
	}

	@Override
	public CommandProcessorFactory getDiscreteDispatcher() {
		return new DiscreteCommandDispatcherFactory();
	}

	@Override
	public CommandProcessorFactory getCASCmdFactory() {
		return new CASCommandProcessorFactory();
	}

	@Override
	public CommandProcessorFactory getScriptingCmdFactory() {
		return new ScriptingCommandProcessorFactory();
	}

	@Override
	public CommandProcessorFactory getAdvancedCmdFactory() {
		return new AdvancedCommandProcessorFactory();
	}

	@Override
	public CommandProcessorFactory getProverCmdFactory() {
		return new ProverCommandProcessorFactory();
	}
}
