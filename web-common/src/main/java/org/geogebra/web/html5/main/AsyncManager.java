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
	private boolean callbackRunning;

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

			cmdDispatcher.getScriptingCommandProcessorFactory();
			cmdDispatcher.getAdvancedCommandProcessorFactory();
			cmdDispatcher.getStatsCommandProcessorFactory();
			cmdDispatcher.getProverCommandProcessorFactory();
			cmdDispatcher.getCASCommandProcessorFactory();
			cmdDispatcher.getSpatialCommandProcessorFactory();
		} catch (CommandNotLoadedError e) {
			ensureModulesLoaded(null);
			throw e;
		}
	}

	/**
	 * Ensure that all the specified modules are loaded before
	 * any other code inside async callback is run
	 * @param modules modules to preload
	 *                   (null -&gt; preload all specified in defaultPreload)
	 */
	public void ensureModulesLoaded(String[] modules) {
		final AsyncModule[] preload = modules == null ? defaultPreload
				: parse(modules);
		for (AsyncModule module : preload) {
			module.prefetch();
		}
		Runnable r = () -> ensureAvailable(preload, null);

		callbacks.add(0, r);
	}

	private void ensureAvailable(AsyncModule[] modules, Runnable callback) {
		for (AsyncModule module: modules) {
			final CommandDispatcher cmdDispatcher = app.getKernel()
					.getAlgebraProcessor().getCmdDispatcher();
			switch (module) {
			case DISCRETE:
				cmdDispatcher.getDiscreteCommandProcessorFactory();
				break;
			case SCRIPTING:
				cmdDispatcher.getScriptingCommandProcessorFactory();
				break;
			case ADVANCED:
				cmdDispatcher.getAdvancedCommandProcessorFactory();
				break;
			case STATS:
				cmdDispatcher.getStatsCommandProcessorFactory();
				break;
			case PROVER:
				cmdDispatcher.getProverCommandProcessorFactory();
				break;
			case CAS:
				cmdDispatcher.getCASCommandProcessorFactory();
				break;
			case SPATIAL:
				cmdDispatcher.getSpatialCommandProcessorFactory();
				break;
			case GIAC:
				app.getKernel().getGeoGebraCAS().initCurrentCAS();
			default:
				Log.debug("Trying to preload nonexistent module: " + module);
			}
		}
		if (callback != null) {
			callback.run();
		}
	}

	/**
	 * @param callback runs after all chunks are loaded
	 * @param modules list of chunk names, see {@link AsyncModule}
	 */
	public void prefetch(Runnable callback, String... modules) {
		final AsyncModule[] preload = parse(modules);
		for (AsyncModule module : preload) {
			module.prefetch();
		}
		runOrSchedule(() -> ensureAvailable(preload, callback));
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
				callbackRunning = true;
				callbacks.get(0).run();
				if (!callbacks.isEmpty()) {
					callbacks.remove(0);
				}
			} catch (CommandNotLoadedError e) {
				break;
			} finally {
				callbackRunning = false;
			}
		}
	}

	/**
	 * If we're already inside a callback, just run it, otherwise schedule it.
	 * @param callback callback
	 */
	public void runOrSchedule(Runnable callback) {
		if (callbackRunning) {
			callback.run();
		} else {
			scheduleCallback(callback);
		}
	}
}
