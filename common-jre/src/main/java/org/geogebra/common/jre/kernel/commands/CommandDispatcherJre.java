package org.geogebra.common.jre.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.CommandDispatcherAdvanced;
import org.geogebra.common.kernel.commands.CommandDispatcherCAS;
import org.geogebra.common.kernel.commands.CommandDispatcherDiscrete;
import org.geogebra.common.kernel.commands.CommandDispatcherInterface;
import org.geogebra.common.kernel.commands.CommandDispatcherProver;
import org.geogebra.common.kernel.commands.CommandDispatcherScripting;
import org.geogebra.common.kernel.commands.CommandDispatcherStats;

public class CommandDispatcherJre extends CommandDispatcher {

	public CommandDispatcherJre(Kernel kernel) {
		super(kernel);
	}

	@Override
	public CommandDispatcherInterface getStatsDispatcher() {
		if (statsDispatcher == null) {
			statsDispatcher = new CommandDispatcherStats();
		}
		return statsDispatcher;
	}

	@Override
	public CommandDispatcherInterface getDiscreteDispatcher() {
		if (discreteDispatcher == null) {
			discreteDispatcher = new CommandDispatcherDiscrete();
		}
		return discreteDispatcher;
	}

	@Override
	public CommandDispatcherInterface getCASDispatcher() {
		if (casDispatcher == null) {
			casDispatcher = new CommandDispatcherCAS();
		}
		return casDispatcher;
	}

	@Override
	public CommandDispatcherInterface getScriptingDispatcher() {
		if (scriptingDispatcher == null) {
			scriptingDispatcher = new CommandDispatcherScripting();
		}
		return scriptingDispatcher;
	}

	@Override
	public CommandDispatcherInterface getAdvancedDispatcher() {
		if (advancedDispatcher == null) {
			advancedDispatcher = new CommandDispatcherAdvanced();
		}
		return advancedDispatcher;
	}

	@Override
	public CommandDispatcherInterface getProverDispatcher() {
		if (proverDispatcher == null) {
			proverDispatcher = new CommandDispatcherProver();
		}
		return proverDispatcher;
	}
}
