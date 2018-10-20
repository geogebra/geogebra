package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.util.debug.Log;

import com.google.gwt.core.client.JavaScriptObject;

public class AsyncManager {

	/**
	 * Preload all but discrete and steps
	 */
	private static final String[] defaultPreload = {
			"advanced",
			"prover",
			"scripting",
			"stats",
			"CAS"
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
		final String[] preload = modules == null ? defaultPreload : modules;

		Runnable r = new Runnable() {
			@Override
			public void run() {
				for (String module : preload) {
					switch (module) {
					case "discrete":
						cmdDispatcher.getDiscreteDispatcher();
						break;
					case "scripting":
						cmdDispatcher.getScriptingDispatcher();
						break;
					case "advanced":
						cmdDispatcher.getAdvancedDispatcher();
						break;
					case "stats":
						cmdDispatcher.getStatsDispatcher();
						break;
					case "steps":
						cmdDispatcher.getStepsDispatcher();
						break;
					case "prover":
						cmdDispatcher.getProverDispatcher();
						break;
					case "CAS":
						cmdDispatcher.getCASDispatcher();
						break;
					default:
						Log.debug("Tring to preload nonexistent module: " + module);
					}
				}
			}
		};

		callbacks.add(0, r);
	}

	/**
	 * Asynchronously evaluate a command
	 * @param command command to evaluate
	 * @param onSuccess function to be called when the execution succeeds
	 * @param onFailure function to be called when the execution fails
	 */
	public void asyncEvalCommand(final String command, final JavaScriptObject onSuccess,
			final JavaScriptObject onFailure) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					app.getGgbApi().evalCommand(command);
					call(onSuccess, null);
				} catch (Exception e) {
					call(onFailure, e);
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
	public void asyncEvalCommandGetLabels(final String command, final JavaScriptObject onSuccess,
			final JavaScriptObject onFailure) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					call(onSuccess, app.getGgbApi().evalCommandGetLabels(command));
				} catch (Exception e) {
					call(onFailure, e);
				}
			}
		};

		scheduleCallback(r);
	}

	private native void call(JavaScriptObject callback, Object o) /*-{
		if (typeof callback === 'function') {
			callback(o);
		}
	}-*/;

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
				callbacks.remove(0);
			} catch (CommandNotLoadedError e) {
				break;
			}
		}
	}
}
