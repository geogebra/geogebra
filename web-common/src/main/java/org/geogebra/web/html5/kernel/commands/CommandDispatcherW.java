package org.geogebra.web.html5.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.AdvancedCommandProcessorFactory;
import org.geogebra.common.kernel.commands.CASCommandProcessorFactory;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.kernel.commands.CommandProcessorFactory;
import org.geogebra.common.kernel.commands.DiscreteCommandDispatcherFactory;
import org.geogebra.common.kernel.commands.ProverCommandProcessorFactory;
import org.geogebra.common.kernel.commands.ScriptingCommandProcessorFactory;
import org.geogebra.common.kernel.commands.StatsCommandProcessorFactory;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.debug.LoggerW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

/**
 * Command dispatcher with deferred loading of advanced commands.
 */
public class CommandDispatcherW extends CommandDispatcher {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CommandDispatcherW(Kernel kernel) {
		super(kernel);
	}

	@Override
	public CommandProcessorFactory getDiscreteDispatcher() {
		if (discreteDispatcher == null) {
			GWT.runAsync(DiscreteCommandDispatcherFactory.class,
					new RunAsyncCallback() {
						@Override
						public void onFailure(Throwable reason) {
							Log.error("Loading failed for discrete commands");
						}

						@Override
						public void onSuccess() {
							LoggerW.loaded("discrete commands");
							discreteDispatcher = new DiscreteCommandDispatcherFactory();
							initCmdTable();
							((AppW) app).commandsLoaded();
						}
					});
			throw new CommandNotLoadedError("Discrete commands not loaded yet");
		}

		return discreteDispatcher;
	}

	@Override
	public CommandProcessorFactory getScriptingCmdFactory() {
		if (scriptingDispatcher == null) {
			GWT.runAsync(ScriptingCommandProcessorFactory.class,
					new RunAsyncCallback() {
						@Override
						public void onFailure(Throwable reason) {
							Log.error("Loading failed for scripting commands");
						}

						@Override
						public void onSuccess() {
							LoggerW.loaded("scripting commands");
							scriptingDispatcher = new ScriptingCommandProcessorFactory();
							initCmdTable();
							((AppW) app).commandsLoaded();
						}
					});
			throw new CommandNotLoadedError(
					"Scripting commands not loaded yet");
		}

		return scriptingDispatcher;
	}

	@Override
	public CommandProcessorFactory getAdvancedCmdFactory() {
		if (advancedDispatcher == null) {
			GWT.runAsync(AdvancedCommandProcessorFactory.class,
					new RunAsyncCallback() {
						@Override
						public void onFailure(Throwable reason) {
							Log.error("Loading failed for advanced commands");
						}

						@Override
						public void onSuccess() {
							LoggerW.loaded("advanced commands");
							advancedDispatcher = new AdvancedCommandProcessorFactory();
							initCmdTable();
							((AppW) app).commandsLoaded();
						}
					});
			throw new CommandNotLoadedError("Advanced commands not loaded yet");
		}

		return advancedDispatcher;
	}

	@Override
	public CommandProcessorFactory getCASCmdFactory() {
		if (casDispatcher == null) {
			GWT.runAsync(CASCommandProcessorFactory.class, new RunAsyncCallback() {
				@Override
				public void onFailure(Throwable reason) {
					Log.error("Loading failed for CAS commands");
				}

				@Override
				public void onSuccess() {
					LoggerW.loaded("CAS commands");
					casDispatcher = new CASCommandProcessorFactory();
					initCmdTable();
					((AppW) app).commandsLoaded();
				}
			});
			throw new CommandNotLoadedError("CAS commands not loaded yet");
		}

		return casDispatcher;
	}

	@Override
	public CommandProcessorFactory getStatsDispatcher() {
		if (statsDispatcher == null) {
			GWT.runAsync(StatsCommandProcessorFactory.class, new RunAsyncCallback() {
				@Override
				public void onFailure(Throwable reason) {
					Log.error("Loading failed for stats commands");
				}

				@Override
				public void onSuccess() {
					LoggerW.loaded("stats commands");
					statsDispatcher = new StatsCommandProcessorFactory();
					initCmdTable();
					((AppW) app).commandsLoaded();
				}
			});
			throw new CommandNotLoadedError("Stats commands not loaded yet");
		}

		return statsDispatcher;
	}

	@Override
	public CommandProcessorFactory getProverCmdFactory() {
		if (proverDispatcher == null) {
			GWT.runAsync(Prover.class, new RunAsyncCallback() {
				@Override
				public void onFailure(Throwable reason) {
					Log.error("Loading failed for prover commands");
				}

				@Override
				public void onSuccess() {
					LoggerW.loaded("prover");
					proverDispatcher = new ProverCommandProcessorFactory();
					initCmdTable();
					((AppW) app).commandsLoaded();
				}
			});
			throw new CommandNotLoadedError("Prover commands not loaded yet");
		}

		return proverDispatcher;
	}
}
