package org.geogebra.web.cas.giac;

import java.util.ArrayList;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.util.JsRunnable;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.resources.JavaScriptInjector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

import fr.grenoble.ujf.giac.CASResources;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Web implementation of Giac CAS
 * 
 * @author Michael Borcherds, based on Reduce version
 *
 */
public class CASgiacW extends CASgiac {

	/** kernel */
	Kernel kernel;
	private static boolean externalCAS = Browser.externalCAS();

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
		if (externalCAS) {
			Log.debug("switching to external");
			// CASgiacW.this.kernel.getApplication().getGgbApi().initCAS();
		} else if (Browser.supportsJsCas()) {
			initialize(Browser.webAssemblySupported());
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

		// #5439
		// restart Giac before each call
		// native Giac needs same initString as desktop
		evaluateRaw(externalCAS ? initString : initStringWeb, false,
				externalCAS);
		
		// GGB-850
		CustomFunctions[] init = CustomFunctions.values();
		CustomFunctions.setDependencies();

		// Log.debug("exp = " + exp);

		for (int i = 0; i < init.length; i++) {
			CustomFunctions function = init[i];

			// send only necessary init commands
			boolean foundInInput = false;
			/*
			 * Check if the command uses any "extra" custom functions for
			 * example ggbisPolynomial()
			 * 
			 * (using a string compare which is robust)
			 * 
			 * It's just possible that we will load commands that aren't needed
			 * in rare circumstances
			 */
			if (function.functionName == null || (foundInInput = (exp
				.indexOf(function.functionName) > -1))) {
				evaluateRaw(function.definitionString, false, externalCAS);
				/* Some commands may require additional commands to load. */
				if (foundInInput) {
					ArrayList<CustomFunctions> dependencies = CustomFunctions
							.prereqs(function);
					for (CustomFunctions dep : dependencies) {
						Log.debug(function + " implicitly loads " + dep);
						evaluateRaw(dep.definitionString, false,
								externalCAS);
					}
				}
			}
		}

		// timeout doesn't work for giac.js / webassembly so use 999
		// @solve(((x^2*((-2*sqrt(10))-6))+(((x*(3*sqrt(10)+9))-6*sqrt(10)-18)*sqrt((x^2*(-2*sqrt(10)-6))+(x*(8*sqrt(10)+24))-(2*sqrt(10))-5))+(x*(8*sqrt(10)+24))-2*sqrt(10)-5)/((x^2*(-12*sqrt(10)-38))+(x*(48*sqrt(10)+152))-11*sqrt(10)-35))
		String timeoutCommand = "timeout "
				+ (externalCAS ? "" + (timeoutMilliseconds / 1000)
						: "999");

		// Giac's default is 15s unless specified
		evaluateRaw(timeoutCommand, false, externalCAS);

		// make sure we don't always get the same value!
		int seed = rand.nextInt(Integer.MAX_VALUE);
		evaluateRaw("srand(" + seed + ")", false, externalCAS);

		// set to radians mode
		evaluateRaw("angle_radian:=1", false, externalCAS);

		// show logging in tube-beta only
		String ret = evaluateRaw(wrapInevalfa(exp), kernel
				.getApplication().has(Feature.TUBE_BETA), externalCAS);

		return ret;
	}

	private String evaluateRaw(String giacCommand, boolean showOutput,
			boolean external) {
		if (external) {
			return nativeEvaluateRawExternal(giacCommand, showOutput);
		}

		return nativeEvaluateRaw(giacCommand, showOutput);
	}

	private void setUpInitCAS() {
		if (Js.isFalsy(GeoGebraGlobal.__ggb__giac)) {
			GeoGebraGlobal.__ggb__giac = JsPropertyMap.of();
		}

		GeoGebraGlobal.__ggb__giac.set("postRun", (JsRunnable) () -> {
			kernel.getApplication().getGgbApi().initCAS();
		});
	}

	private native String nativeEvaluateRawExternal(String s,
			boolean showOutput) /*-{
		return $wnd.evalGeoGebraCASExternal(s);
	}-*/;

	private native String nativeEvaluateRaw(String s, boolean showOutput) /*-{
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
			setUpInitCAS();

			GWT.runAsync(new RunAsyncCallback() {
				@Override
				public void onSuccess() {
					LoggerW.loaded("GIAC webAssembly");
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
					LoggerW.loaded("GIAC emscripten");
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
		return externalCAS;
	}
}
