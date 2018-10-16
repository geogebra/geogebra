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

	public AsyncManager(AppW app) {
		this.app = app;
		callbacks = new ArrayList<>();
	}

	public void scheduleCallback(Runnable r) {
		callbacks.add(r);
		onResourceLoaded();
	}

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
					}
				}
			}
		};

		callbacks.add(0, r);
	}

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
