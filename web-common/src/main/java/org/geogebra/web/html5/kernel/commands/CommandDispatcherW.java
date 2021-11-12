package org.geogebra.web.html5.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.CommandDispatcherAdvanced;
import org.geogebra.common.kernel.commands.CommandDispatcherCAS;
import org.geogebra.common.kernel.commands.CommandDispatcherDiscrete;
import org.geogebra.common.kernel.commands.CommandDispatcherInterface;
import org.geogebra.common.kernel.commands.CommandDispatcherProver;
import org.geogebra.common.kernel.commands.CommandDispatcherScripting;
import org.geogebra.common.kernel.commands.CommandDispatcherStats;
import org.geogebra.common.kernel.commands.CommandDispatcherSteps;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.debug.LoggerW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

/**
 * For deferred loading of advanced algos
 *
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
	public CommandDispatcherInterface getDiscreteDispatcher() {
		if (discreteDispatcher == null) {
			GWT.runAsync(CommandDispatcherDiscrete.class,
					new RunAsyncCallback() {
						@Override
						public void onFailure(Throwable reason) {
							Log.error("Loading failed for discrete commands");
						}

						@Override
						public void onSuccess() {
							LoggerW.loaded("discrete commands");
							discreteDispatcher = new CommandDispatcherDiscrete();
							initCmdTable();
							((AppW) app).commandsLoaded();
						}
					});
			throw new CommandNotLoadedError("Discrete commands not loaded yet");
		}

		return discreteDispatcher;
	}

	@Override
	public CommandDispatcherInterface getScriptingDispatcher() {
		if (scriptingDispatcher == null) {
			GWT.runAsync(CommandDispatcherScripting.class,
					new RunAsyncCallback() {
						@Override
						public void onFailure(Throwable reason) {
							Log.error("Loading failed for scripting commands");
						}

						@Override
						public void onSuccess() {
							LoggerW.loaded("scripting commands");
							scriptingDispatcher = new CommandDispatcherScripting();
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
	public CommandDispatcherInterface getAdvancedDispatcher() {
		if (advancedDispatcher == null) {
			GWT.runAsync(CommandDispatcherAdvanced.class,
					new RunAsyncCallback() {
						@Override
						public void onFailure(Throwable reason) {
							Log.error("Loading failed for advanced commands");
						}

						@Override
						public void onSuccess() {
							LoggerW.loaded("advanced commands");
							advancedDispatcher = new CommandDispatcherAdvanced();
							initCmdTable();
							((AppW) app).commandsLoaded();
						}
					});
			throw new CommandNotLoadedError("Advanced commands not loaded yet");
		}

		return advancedDispatcher;
	}

	@Override
	public CommandDispatcherInterface getCASDispatcher() {
		if (casDispatcher == null) {
			GWT.runAsync(CommandDispatcherCAS.class, new RunAsyncCallback() {
				@Override
				public void onFailure(Throwable reason) {
					Log.error("Loading failed for CAS commands");
				}

				@Override
				public void onSuccess() {
					LoggerW.loaded("CAS commands");
					casDispatcher = new CommandDispatcherCAS();
					initCmdTable();
					((AppW) app).commandsLoaded();
				}
			});
			throw new CommandNotLoadedError("CAS commands not loaded yet");
		}

		return casDispatcher;
	}

	@Override
	public CommandDispatcherInterface getStatsDispatcher() {
		if (statsDispatcher == null) {
			GWT.runAsync(CommandDispatcherStats.class, new RunAsyncCallback() {
				@Override
				public void onFailure(Throwable reason) {
					Log.error("Loading failed for stats commands");
				}

				@Override
				public void onSuccess() {
					LoggerW.loaded("stats commands");
					statsDispatcher = new CommandDispatcherStats();
					initCmdTable();
					((AppW) app).commandsLoaded();
				}
			});
			throw new CommandNotLoadedError("Stats commands not loaded yet");
		}

		return statsDispatcher;
	}

	@Override
	public CommandDispatcherInterface getStepsDispatcher() {
		if (stepsDispatcher == null) {
			GWT.runAsync(CommandDispatcherSteps.class, new RunAsyncCallback() {
				@Override
				public void onFailure(Throwable reason) {
					Log.error("Loading failed for steps commands");
				}

				@Override
				public void onSuccess() {
					LoggerW.loaded("steps commands");
					stepsDispatcher = new CommandDispatcherSteps();
					initCmdTable();
					((AppW) app).commandsLoaded();
				}
			});
			throw new CommandNotLoadedError("Steps commands not loaded yet");
		}

		return stepsDispatcher;
	}

	@Override
	public CommandDispatcherInterface getProverDispatcher() {
		if (proverDispatcher == null) {
			GWT.runAsync(Prover.class, new RunAsyncCallback() {
				@Override
				public void onFailure(Throwable reason) {
					Log.error("Loading failed for prover commands");
				}

				@Override
				public void onSuccess() {
					LoggerW.loaded("prover");
					proverDispatcher = new CommandDispatcherProver();
					initCmdTable();
					((AppW) app).commandsLoaded();
				}
			});
			throw new CommandNotLoadedError("Prover commands not loaded yet");
		}

		return proverDispatcher;
	}
}
