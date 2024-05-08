package org.geogebra.web.html5.main;

import java.util.Locale;

import org.geogebra.common.geogebra3D.kernel3D.commands.SpatialCommandProcessorFactory;
import org.geogebra.common.kernel.commands.AdvancedCommandProcessorFactory;
import org.geogebra.common.kernel.commands.CASCommandProcessorFactory;
import org.geogebra.common.kernel.commands.DiscreteCommandProcessorFactory;
import org.geogebra.common.kernel.commands.ScriptingCommandProcessorFactory;
import org.geogebra.common.kernel.commands.StatsCommandProcessorFactory;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.cas.giac.CASgiacW;

import com.google.gwt.core.client.prefetch.RunAsyncCode;

import elemental2.dom.DomGlobal;

/**
 * Enumeration of all modules (fragments) that can be prefetched
 */
public enum AsyncModule {
	STATS(RunAsyncCode.runAsyncCode(StatsCommandProcessorFactory.class)),

	ADVANCED(RunAsyncCode.runAsyncCode(AdvancedCommandProcessorFactory.class)),

	CAS(RunAsyncCode.runAsyncCode(CASCommandProcessorFactory.class)),

	SPATIAL(RunAsyncCode.runAsyncCode(SpatialCommandProcessorFactory.class)),

	PROVER(RunAsyncCode.runAsyncCode(Prover.class)),

	SCRIPTING(RunAsyncCode.runAsyncCode(ScriptingCommandProcessorFactory.class)),

	DISCRETE(RunAsyncCode.runAsyncCode(DiscreteCommandProcessorFactory.class)),

	GIAC(RunAsyncCode.runAsyncCode(CASgiacW.class));

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
				&& DomGlobal.location.protocol.startsWith("http")) {
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