package org.geogebra.common.jre.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.AdvancedCommandProcessorFactory;
import org.geogebra.common.kernel.commands.CASCommandProcessorFactory;
import org.geogebra.common.kernel.commands.DiscreteCommandDispatcherFactory;
import org.geogebra.common.kernel.commands.CommandProcessorFactory;
import org.geogebra.common.kernel.commands.ProverCommandProcessorFactory;
import org.geogebra.common.kernel.commands.ScriptingCommandProcessorFactory;
import org.geogebra.common.kernel.commands.StatsCommandProcessorFactory;

/**
 * Command dispatcher that creates command processors (via factories)
 * synchronously. Does not support 3D specific commands.
 */
public class CommandDispatcherJre extends CommandDispatcher {

	public CommandDispatcherJre(Kernel kernel) {
		super(kernel);
	}

	@Override
	public CommandProcessorFactory getStatsDispatcher() {
		if (statsDispatcher == null) {
			statsDispatcher = new StatsCommandProcessorFactory();
		}
		return statsDispatcher;
	}

	@Override
	public CommandProcessorFactory getDiscreteDispatcher() {
		if (discreteDispatcher == null) {
			discreteDispatcher = new DiscreteCommandDispatcherFactory();
		}
		return discreteDispatcher;
	}

	@Override
	public CommandProcessorFactory getCASDispatcher() {
		if (casDispatcher == null) {
			casDispatcher = new CASCommandProcessorFactory();
		}
		return casDispatcher;
	}

	@Override
	public CommandProcessorFactory getScriptingDispatcher() {
		if (scriptingDispatcher == null) {
			scriptingDispatcher = new ScriptingCommandProcessorFactory();
		}
		return scriptingDispatcher;
	}

	@Override
	public CommandProcessorFactory getAdvancedDispatcher() {
		if (advancedDispatcher == null) {
			advancedDispatcher = new AdvancedCommandProcessorFactory();
		}
		return advancedDispatcher;
	}

	@Override
	public CommandProcessorFactory getProverDispatcher() {
		if (proverDispatcher == null) {
			proverDispatcher = new ProverCommandProcessorFactory();
		}
		return proverDispatcher;
	}
}
