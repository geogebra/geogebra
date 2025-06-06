package org.geogebra.web.cas.giac;

import java.util.ArrayList;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.JavaScriptInjector;
import org.geogebra.gwtutil.JsRunnable;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.GiacNative;
import org.geogebra.web.html5.util.debug.LoggerW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

import elemental2.core.Function;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import fr.grenoble.ujf.giac.CASResourcesImpl;
import jsinterop.base.Js;

/**
 * Web implementation of Giac CAS
 * 
 * @author Michael Borcherds, based on Reduce version
 *
 */
public class CASgiacW extends CASgiac {

	/** kernel */
	Kernel kernel;
	private static final boolean externalCAS = Browser.externalCAS();
	Function caseval;

	/**
	 * Creates new CAS
	 * 
	 * @param casParser
	 *            parser
	 * @param kernel
	 *            kernel
	 */
	public CASgiacW(CASparser casParser, Kernel kernel) {
		super(casParser);
		this.kernel = kernel;

		Log.debug("starting CAS");
		if (externalCAS) {
			Log.debug("switching to external");
			// CASgiacW.this.kernel.getApplication().getGgbApi().initCAS();
		} else {
			initialize(Browser.webAssemblySupported());
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
		evaluateRaw(externalCAS ? initString : initStringWeb, externalCAS);
		
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
				evaluateRaw(function.definitionString, externalCAS);
				/* Some commands may require additional commands to load. */
				if (foundInInput) {
					ArrayList<CustomFunctions> dependencies = CustomFunctions
							.prereqs(function);
					for (CustomFunctions dep : dependencies) {
						Log.debug(function + " implicitly loads " + dep);
						evaluateRaw(dep.definitionString, externalCAS);
					}
				}
			}
		}

		long timeout = timeoutMilliseconds / 1000;
		String timeoutCommand = "caseval(\"timeout " + timeout + "\")";

		// Giac's default is 15s unless specified
		evaluateRaw(timeoutCommand, externalCAS);
		evaluateRaw("caseval(\"ckevery 20\")", externalCAS);

		// make sure we don't always get the same value!
		int seed = getSeed(exp);
		evaluateRaw("srand(" + seed + ")", externalCAS);

		// set to radians mode
		evaluateRaw("angle_radian:=1", externalCAS);

		return evaluateRaw(wrapInevalfa(exp), externalCAS);
	}

	private String evaluateRaw(String giacCommand, boolean external) {
		if (external) {
			return nativeEvaluateRawExternal(giacCommand);
		}

		return nativeEvaluateRaw(giacCommand);
	}

	private void setUpInitCAS() {
		if (!"object".equals(Js.typeof(GeoGebraGlobal.__ggb__giac))) {
			GeoGebraGlobal.__ggb__giac = new GiacNative();
		}

		JsRunnable oldPostRun = GeoGebraGlobal.__ggb__giac.postRun;
		GeoGebraGlobal.__ggb__giac.postRun = () -> {
			if (oldPostRun != null) {
				oldPostRun.run();
			}
			kernel.getApplication().getGgbApi().initCAS();
		};
	}

	private String nativeEvaluateRawExternal(String s) {
		return Js.asString(GeoGebraGlobal.evalGeoGebraCASExternal.call(DomGlobal.window, s));
	}

	private String nativeEvaluateRaw(String s) {
		if (!Browser.hasGlobal("Float64Array")) {
			Log.error("Typed arrays not supported, Giac won't work");
			return "?";
		}
		updateCaseval();
		return (String) caseval.call(DomGlobal.window, s);
	}

	private void updateCaseval() {
		if (caseval == null && Js.isTruthy(GeoGebraGlobal.__ggb__giac)) {
			caseval = GeoGebraGlobal.__ggb__giac.cwrap("caseval", "string",
					JsArray.of("string"));
		}
	}

	private boolean casLoaded() {
		return nativeCASloaded() || externalCAS();
	}

	private boolean nativeCASloaded() {
		return GeoGebraGlobal.__ggb__giac != null && GeoGebraGlobal.__ggb__giac.getCwrap() != null;
	}

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

			kernel.getApplication().invokeLater(() -> {
				Log.debug(versionString + " is already loaded");
				kernel.getApplication().getGgbApi().initCAS();
			});

			return;

		}

		Log.debug("Loading " + versionString);

		if (wasm) {

			// make sure CAS cells etc re-evaluated after CAS loaded
			setUpInitCAS();

			GWT.runAsync(CASgiacW.class, new RunAsyncCallback() {
				@Override
				public void onSuccess() {
					LoggerW.loaded("GIAC webAssembly");
					JavaScriptInjector.inject(new CASResourcesImpl().giacWasm());
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
			Log.error("Browser does not support WebAssembly");
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
