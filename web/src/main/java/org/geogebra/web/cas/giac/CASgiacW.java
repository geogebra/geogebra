package org.geogebra.web.cas.giac;

import java.util.ArrayList;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.Evaluate;
import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.JavaScriptInjector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

/**
 * Web implementation of Giac CAS
 * 
 * @author Michael Borcherds, based on Reduce version
 *
 */
public class CASgiacW extends CASgiac {

	/** kernel */
	Kernel kernel;
	private Evaluate giac;

	/**
	 * Creates new CAS
	 * 
	 * @param casParser
	 *            parser
	 * @param kernel
	 *            kernel
	 */
	public CASgiacW(CASparser casParser,
	        Kernel kernel) {
		super(casParser);
		this.kernel = kernel;

		App.setCASVersionString("Giac/JS");
		Log.debug("starting CAS");
		if (Browser.externalCAS()) {
			Log.debug("switching to external");
			// CASgiacW.this.kernel.getApplication().getGgbApi().initCAS();
		} else if (Browser.supportsJsCas()) {
			initialize(Browser.webAssemblySupported()
					&& kernel.getApplication().has(Feature.GGB_WEB_ASSEMBLY));
		} else {
			Log.debug("CAS not possible");
		}

	}

	@Override
	public String evaluateCAS(String exp) {
		if (!casLoaded()) {
			return "?";
		}
		try {
			// replace Unicode when sending to JavaScript
			// (encoding problem)
			String processedExp = casParser.replaceIndices(exp, true);
			String ret = evaluateRaw(processedExp);

			return postProcess(ret);

			// } catch (TimeoutException toe) {
			// throw new Error(toe.getMessage());
		} catch (Throwable e) {
			Log.debug("evaluateGiac: " + e.getMessage());
			return "?";
		}
	}

	@Override
	protected synchronized String evaluate(String exp, long timeoutMilliseconds) {
		if (!casLoaded()) {
			return "?";
		}
		boolean external = Browser.externalCAS();
		if (external) {
			// native Giac so need same initString as desktop
			nativeEvaluateRaw(initString, false, external);

		} else {
			// #5439
			// restart Giac before each call
			nativeEvaluateRaw(initStringWeb, false, external);
		}
		
		// GGB-850
		CustomFunctions[] init = CustomFunctions.values();
		CustomFunctions.setDependencies();

		// Log.debug("exp = " + exp);

		for (int i = 0; i < init.length; i++) {
			CustomFunctions function = init[i];

			// send only necessary init commands
			boolean foundInInput = false;
			/* This is very hacky here. If the input expression as string
			 * contains an internal GeoGebra CAS command, then that command will be executed
			 * in Giac. TODO: find a better a way.
			 */
			if (function.functionName == null || (foundInInput = (exp
				.indexOf(function.functionName) > -1))) {
				nativeEvaluateRaw(function.definitionString, false, external);
				/* Some commands may require additional commands to load. */
				if (foundInInput) {
					ArrayList<CustomFunctions> dependencies = CustomFunctions
							.prereqs(function);
					for (CustomFunctions dep : dependencies) {
						Log.debug(function + " implicitly loads " + dep);
						nativeEvaluateRaw(dep.definitionString, false,
								external);
					}
				}
			}
		}

		nativeEvaluateRaw("timeout " + (timeoutMilliseconds / 1000), false,
				external);

		// make sure we don't always get the same value!
		int seed = rand.nextInt(Integer.MAX_VALUE);
		nativeEvaluateRaw("srand(" + seed + ")", false, external);

		// set to radians mode
		nativeEvaluateRaw("angle_radian:=1", false, external);

		// show logging in tube-beta only
		String ret = nativeEvaluateRaw(wrapInevalfa(exp), kernel
				.getApplication().has(Feature.TUBE_BETA), external);

		return ret;
	}

	private native String setUpInitCAS(String ggbApplet) /*-{
		if (!$wnd.__ggb__giac)
			$wnd.__ggb__giac = (typeof $wnd.__ggb__giac !== "undefined" ? $wnd.__ggb__giac
					: null)
					|| {};

		$wnd.__ggb__giac.postRun = [ $wnd[ggbApplet].initCAS ];
	}-*/;

	private native String nativeEvaluateRaw(String s, boolean showOutput,
			boolean useExternal) /*-{
		if (useExternal && @org.geogebra.web.html5.Browser::externalCAS()()) {
			return $wnd.evalGeoGebraCASExternal(s);
		}
		if (typeof Float64Array === 'undefined') {
			$wnd.console.log("Typed arrays not supported, Giac won't work");
			return "?";
		}

		if (showOutput) {
			$wnd.console.log("js giac  input:" + s);
		}

		caseval = $wnd.__ggb__giac.cwrap('caseval', 'string', [ 'string' ]);

		var ret = caseval(s);

		if (showOutput) {
			$wnd.console.log("js giac output:" + ret);
		}

		return ret
	}-*/;

	private boolean casLoaded() {
		return nativeCASloaded() || externalCAS();

	}

	private native boolean nativeCASloaded() /*-{
		return !!$wnd.__ggb__giac && !!$wnd.__ggb__giac.cwrap;
	}-*/;

	/**
	 * Make sure an instance of giac.js is loaded
	 * 
	 * @param wasm
	 *            whether to use WebAssembly or JavaScript
	 */
	public void initialize(final boolean wasm) {
		final String versionString = wasm ? "giac.wasm" : "giac.js";

		if (casLoaded()) {
			Log.debug(versionString + " is already loaded!");
			return;
		}

		if (nativeCASloaded()) {

			kernel.getApplication().invokeLater(new Runnable() {
				@Override
				public void run() {
					Log.debug(versionString + " is already loaded");
					CASgiacW.this.kernel.getApplication().getGgbApi().initCAS();
				}
			});

			return;

		}

		Log.debug("Loading " + versionString);

		if (wasm) {

			// make sure CAS cells etc re-evaluated after CAS loaded
			setUpInitCAS(((AppW) kernel.getApplication()).getDataParamId());

			GWT.runAsync(new RunAsyncCallback() {
				@Override
				public void onSuccess() {
					JavaScriptInjector.inject(CASResources.INSTANCE.giacWasm());
					// don't call this here
					// needs to be called once WebAssembly is actually working
					// (compiled?)
					// see setUpInitCAS()
					// initCAS(versionString);

				}

				@Override
				public void onFailure(Throwable reason) {
					Log.debug(versionString + " loading failure");
				}
			});

		} else {
			GWT.runAsync(new RunAsyncCallback() {
				@Override
				public void onSuccess() {
					JavaScriptInjector.inject(CASResources.INSTANCE.giacJs());
					// make sure CAS cells etc re-evaluated after CAS loaded
					initCAS(versionString);
				}

				@Override
				public void onFailure(Throwable reason) {
					Log.debug(versionString + " loading failure");
				}
			});
		}
	}

	/**
	 * @param versionString
	 *            version (for debugging)
	 */
	protected void initCAS(String versionString) {
		Log.debug(versionString + " loading success");
		this.kernel.getApplication().getGgbApi().initCAS();
	}

	@Override
	public void clearResult() {
		// not needed
	}

	@Override
	public boolean isLoaded() {
		return casLoaded();
	}

	@Override
	public boolean externalCAS() {
		return Browser.externalCAS();
	}
}
