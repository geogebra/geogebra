package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.util.debug.Log;

import elemental2.promise.Promise.PromiseExecutorCallbackFn.RejectCallbackFn;
import elemental2.promise.Promise.PromiseExecutorCallbackFn.ResolveCallbackFn;

/**
 * Async modules manager
 * 
 * @author Agoston
 */
public class AsyncManager {

	/**
	 * Preload all but discrete and steps
	 */
	private static final AsyncModule[] defaultPreload = { AsyncModule.ADVANCED,
			AsyncModule.PROVER, AsyncModule.SCRIPTING, AsyncModule.STATS, AsyncModule.CAS,
			AsyncModule.SPATIAL
	};

	private AppW app;

	private List<Runnable> callbacks;

	/**
	 * @param app Application
	 */
	public AsyncManager(AppW app) {
		this.app = app;
		callbacks = new ArrayList<>();
	}

	/**
	 * Try executing r until it succeeds
	 * @param r code that requires modules that might've not been loaded yet
	 */
	public void scheduleCallback(Runnable r) {
		callbacks.add(r);
		onResourceLoaded();
	}

	/**
	 * Check if all modules are loaded. If not, throw error and
	 * ensure they will be before the next async callback is evaluated
	 */
	public void loadAllCommands() {
		try {
			final CommandDispatcher cmdDispatcher = app.getKernel()
				.getAlgebraProcessor().getCmdDispatcher();

			cmdDispatcher.getScriptingDispatcher();
			cmdDispatcher.getAdvancedDispatcher();
			cmdDispatcher.getStatsDispatcher();
			cmdDispatcher.getProverDispatcher();
			cmdDispatcher.getCASDispatcher();
			cmdDispatcher.get3DDispatcher();
		} catch (CommandNotLoadedError e) {
			ensureModulesLoaded(null);
			throw e;
		}
	}

	/**
	 * Ensure that all the specified modules are loaded before
	 * any other code inside async callback is run
	 * @param modules modules to preload
	 *                   (null -> preload all specified in defaultPreload)
	 */
	public void ensureModulesLoaded(String[] modules) {
		final CommandDispatcher cmdDispatcher = app.getKernel()
				.getAlgebraProcessor().getCmdDispatcher();
		final AsyncModule[] preload = modules == null ? defaultPreload
				: parse(modules);
		for (AsyncModule module : preload) {
			module.prefetch();
		}
		Runnable r = new Runnable() {
			@Override
			public void run() {
				for (AsyncModule module : preload) {
					switch (module) {
					case DISCRETE:
						cmdDispatcher.getDiscreteDispatcher();
						break;
					case SCRIPTING:
						cmdDispatcher.getScriptingDispatcher();
						break;
					case ADVANCED:
						cmdDispatcher.getAdvancedDispatcher();
						break;
					case STATS:
						cmdDispatcher.getStatsDispatcher();
						break;
					case STEPS:
						cmdDispatcher.getStepsDispatcher();
						break;
					case PROVER:
						cmdDispatcher.getProverDispatcher();
						break;
					case CAS:
						cmdDispatcher.getCASDispatcher();
						break;
					case SPATIAL:
						cmdDispatcher.get3DDispatcher();
						break;
					default:
						Log.debug("Tring to preload nonexistent module: " + module);
					}
				}
			}
		};

		callbacks.add(0, r);
	}

	private static AsyncModule[] parse(String[] modules) {
		ArrayList<AsyncModule> parsed = new ArrayList<>();
		for (String name : modules) {
			AsyncModule module = AsyncModule.parseOrNull(name);
			if (module != null) {
				parsed.add(module);
			}
		}
		return parsed.toArray(new AsyncModule[0]);
	}

	/**
	 * Asynchronously evaluate a command
	 * 
	 * @param command
	 *            command to evaluate
	 * @param onSuccess
	 *            function to be called when the execution succeeds
	 * @param onFailure
	 *            function to be called when the execution fails
	 */
	public void asyncEvalCommand(final String command, ResolveCallbackFn<String> onSuccess,
			RejectCallbackFn onFailure) {
		Runnable r = () -> {
			try {
				getGgbApi().evalCommand(command);
				if (onSuccess != null) {
					onSuccess.onInvoke("");
				}
			} catch (Exception e) {
				if (onFailure != null) {
					onFailure.onInvoke(e);
				}
			}
		};

		scheduleCallback(r);
	}

	/**
	 * Asynchronously evaluate a command
	 * @param command command to evaluate
	 * @param onSuccess function to be called when the execution succeeds
	 *                     (with the labels of the created Geos)
	 * @param onFailure function to be called if the execution fails
	 */
	public void asyncEvalCommandGetLabels(final String command, ResolveCallbackFn<String> onSuccess,
			RejectCallbackFn onFailure) {
		Runnable r = () -> {
			try {
				onSuccess.onInvoke(getGgbApi().evalCommandGetLabels(command));
			} catch (Exception e) {
				onFailure.onInvoke(e);
			}
		};

		scheduleCallback(r);
	}

	/**
	 * @return API object
	 */
	protected GgbAPIW getGgbApi() {
		return app.getGgbApi();
	}

	/**
	 * Split module has finished loading: try to run scheduled
	 * callbacks, until all of them have succeeded, or one requires
	 * additional modules
	 */
	public void onResourceLoaded() {
		Log.debug("resource loaded called");
		while (callbacks.size() > 0) {
			try {
				callbacks.get(0).run();
				if (!callbacks.isEmpty()) {
					callbacks.remove(0);
				}
			} catch (CommandNotLoadedError e) {
				break;
			}
		}
	}

}
