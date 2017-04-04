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
	/** flag indicating that JS file was loaded */
	boolean casLoaded = false;
	private Evaluate giac;

	/**
	 * Creates new CAS
	 * 
	 * @param casParser
	 *            parser
	 * @param parserTools
	 *            scientific notation convertor
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
			this.casLoaded = true;
		} else if (Browser.supportsJsCas()) {
			initialize();
		}

	}

	@Override
	public String evaluateCAS(String exp) {
		if (!casLoaded) {
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
		if (!casLoaded) {
			return "?";
		}
		
		if (Browser.externalCAS()) {
			// native Giac so need same initString as desktop
			nativeEvaluateRaw(initString, false);

		} else {
			// #5439
			// restart Giac before each call
			nativeEvaluateRaw(initStringWeb, false);
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
            	nativeEvaluateRaw(function.definitionString, false);
				/* Some commands may require additional commands to load. */
				if (foundInInput) {
					ArrayList<CustomFunctions> dependencies = CustomFunctions
							.prereqs(function);
					for (CustomFunctions dep : dependencies) {
						Log.debug(function + " implicitly loads " + dep);
						nativeEvaluateRaw(dep.definitionString, false);
					}
				}

				// Log.debug("sending " + function);
			} else {
				// Log.error("not sending " + function + " "
				// + function.functionName);
			}

			// Log.error(function.functionName + " " +
			// function.definitionString);
		}


		nativeEvaluateRaw("timeout " + (timeoutMilliseconds / 1000), false);

		// make sure we don't always get the same value!
		int seed = rand.nextInt(Integer.MAX_VALUE);
		nativeEvaluateRaw("srand(" + seed + ")", false);

		// show logging in tube-beta only
		String ret = nativeEvaluateRaw(wrapInevalfa(exp), kernel
				.getApplication().has(Feature.TUBE_BETA));

		return ret;
	}

	private native String nativeEvaluateRaw(String s, boolean showOutput) /*-{
		if (typeof $wnd.evalGeoGebraCASExternal === 'function') {
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

	public void initialize() {
		GWT.runAsync(new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				Log.debug("giac.js loading success");
				JavaScriptInjector.inject(CASResources.INSTANCE.giacJs());
				CASgiacW.this.casLoaded = true;
				CASgiacW.this.kernel.getApplication().getGgbApi().initCAS();
			}

			@Override
			public void onFailure(Throwable reason) {
				Log.debug("giac.js loading failure");
			}
		});
	}



	@Override
	public void clearResult() {
		// not needed

	}

	@Override
	public boolean isLoaded() {
		return casLoaded;
	}

	public boolean externalCAS() {
		return Browser.externalCAS();
	}

}
