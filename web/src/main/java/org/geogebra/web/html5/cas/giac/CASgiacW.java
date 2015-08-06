package org.geogebra.web.html5.cas.giac;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.CasParserTools;
import org.geogebra.common.cas.Evaluate;
import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.AsynchronousCommand;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.js.JavaScriptInjector;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

/**
 * Web implementation of Giac CAS
 * 
 * @author Michael Borcherds, based on Reduce version
 *
 */
public class CASgiacW extends CASgiac implements org.geogebra.common.cas.Evaluate {

	/** kernel */
	Kernel kernel;
	/** flag indicating that JS file was loaded */
	boolean jsLoaded = false;
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
	public CASgiacW(CASparser casParser, CasParserTools parserTools,
	        Kernel kernel) {
		super(casParser);
		this.parserTools = parserTools;
		this.kernel = kernel;

		App.setCASVersionString("Giac/JS");
		App.debug("starting CAS");
		if (Browser.externalCAS()) {
			App.debug("switching to external");
			// CASgiacW.this.kernel.getApplication().getGgbApi().initCAS();
			this.jsLoaded = true;
		} else
		// asynchronous initialization, runs update as callback
		// try NaCl first
		if (org.geogebra.web.html5.cas.giac.PNaCl.isEnabled()) {
			org.geogebra.web.html5.cas.giac.PNaCl.get().initialize();
		} else if (Browser.supportsJsCas()) {
			initialize();
		}

	}

	@Override
	public String evaluateCAS(String exp) {
		if (!jsLoaded) {
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
			App.debug("evaluateGiac: " + e.getMessage());
			return "?";
		}
	}

	/*
	 * called from JavaScript when the CAS is loaded (non-Javadoc)
	 * 
	 * @see geogebra.common.kernel.cas.CASGenericInterface#initCAS()
	 */
	public void initCAS() {
		// not called?
	}



	public synchronized String evaluate(String s) {
		if (!jsLoaded) {
			return "?";
		}

		// #5439
		// restart Giac before each call
		nativeEvaluateRaw(initString, Log.logger != null);
		nativeEvaluateRaw("timeout " + (timeoutMillis / 1000), false);
		nativeEvaluateRaw(specialFunctions, false);

		// make sure we don't always get the same value!
		int seed = rand.nextInt(Integer.MAX_VALUE);
		nativeEvaluateRaw("srand(" + seed + ")", false);

		String exp;
		GLookAndFeelI laf = ((AppW) kernel.getApplication()).getLAF();
		if (laf != null && !laf.isSmart()) {
			// evalfa makes sure rootof() converted to decimal
			// eg @rootof({{-4,10,-440,2025},{1,0,10,-200,375}})
			exp = wrapInevalfa(s);
		} else {
			exp = s;
		}

		App.debug("giac  input:" + exp);
		String ret = nativeEvaluateRaw(exp, true);
		App.debug("giac output:" + ret);

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

		caseval = $wnd.__ggb__giac.cwrap('_ZN4giac7casevalEPKc', 'string',
				[ 'string' ]);

		var ret = caseval(s);

		if (showOutput) {
			$wnd.console.log("js giac output:" + ret);
		}

		return ret
	}-*/;

	public void initialize() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				App.debug("giac.js loading success");
				JavaScriptInjector.inject(CASResources.INSTANCE.giacJs());
				CASgiacW.this.jsLoaded = true;
				CASgiacW.this.kernel.getApplication().getGgbApi().initCAS();
			}

			public void onFailure(Throwable reason) {
				App.debug("giac.js loading failure");
			}
		});
	}

	public void evaluateGeoGebraCASAsync(AsynchronousCommand c) {
		// TODO Auto-generated method stub

	}

	@Override
	public String evaluate(String exp, long timeoutMilliseconds) {
		return evaluate(exp);
	}

	public void clearResult() {
		// not needed

	}

}
