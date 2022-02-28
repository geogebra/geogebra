package org.geogebra.web.html5.main;

import java.util.Locale;

import org.geogebra.common.geogebra3D.kernel3D.commands.CommandDispatcher3D;
import org.geogebra.common.kernel.commands.CommandDispatcherAdvanced;
import org.geogebra.common.kernel.commands.CommandDispatcherCAS;
import org.geogebra.common.kernel.commands.CommandDispatcherDiscrete;
import org.geogebra.common.kernel.commands.CommandDispatcherScripting;
import org.geogebra.common.kernel.commands.CommandDispatcherStats;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.google.gwt.core.client.prefetch.RunAsyncCode;
import com.google.gwt.user.client.Window.Location;

/**
 * Enumeration of all modules (fragments) that can be prefetched
 */
public enum AsyncModule {
	STATS(RunAsyncCode.runAsyncCode(CommandDispatcherStats.class)),

	ADVANCED(RunAsyncCode.runAsyncCode(CommandDispatcherAdvanced.class)),

	CAS(RunAsyncCode.runAsyncCode(CommandDispatcherCAS.class)),

	SPATIAL(RunAsyncCode.runAsyncCode(CommandDispatcher3D.class)),

	PROVER(RunAsyncCode.runAsyncCode(Prover.class)),

	SCRIPTING(RunAsyncCode.runAsyncCode(CommandDispatcherScripting.class)),

	DISCRETE(RunAsyncCode.runAsyncCode(CommandDispatcherDiscrete.class));

	private final RunAsyncCode asyncCode;

	AsyncModule(RunAsyncCode splitPoint) {
		this.asyncCode = splitPoint;
	}

	/**
	 * Prefetch the module so that actual fetch can load it from memory
	 */
	public void prefetch() {
		// in dev mode split point is -1; avoid bogus network requests
		if (!asyncCode.isLoaded() && asyncCode.getSplitPoint() > 0
				&& Location.getProtocol().startsWith("http")) {
			FragmentPrefetcher.prefetch(asyncCode.getSplitPoint());
		}
	}

	/**
	 * @param name
	 *            module name
	 * @return module with given name or null if not found
	 */
	public static AsyncModule parseOrNull(String name) {
		if (!StringUtil.empty(name)) {
			try {
				return valueOf(name.toUpperCase(Locale.US));
			} catch (Exception e) {
				Log.warn("Invalid module " + name);
			}
		}
		return null;
	}

}