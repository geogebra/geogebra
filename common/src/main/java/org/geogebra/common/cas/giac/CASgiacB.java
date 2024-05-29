package org.geogebra.common.cas.giac;

import java.util.ArrayList;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.error.TimeoutException;
import org.geogebra.common.cas.giac.binding.CASGiacBinding;
import org.geogebra.common.cas.giac.binding.Context;
import org.geogebra.common.cas.giac.binding.Gen;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.debug.crashlytics.CrashlyticsLogger;

/**
 * Giac connector using C++ or JNI binding
 */
public abstract class CASgiacB extends CASgiac {

	/**
	 * Giac's context.
	 */
	private Context context;
	/** result from thread */
	protected volatile String threadResult;

	/**
	 * @param casParser parser
	 */
	public CASgiacB(CASparser casParser) {
		super(casParser);
		createContext();
	}

	/**
	 * @return binding
	 */
	protected abstract CASGiacBinding createBinding();

	/**
	 * Create context instance
	 */
	protected void createContext() {
		try {
			CASGiacBinding binding = createBinding();
			context = binding.createContext();
		} catch (Throwable e) {
			Log.error("CAS not available: " + e.getMessage());
		}
	}

	@Override
	final public void clearResult() {
		threadResult = null;
	}

	/**
	 * @param exp0 String to send to Giac
	 * @param timeoutMilliseconds timeout in milliseconds
	 * @return String from Giac
	 */
	final String evalRaw(String exp0, long timeoutMilliseconds) {
		CASGiacBinding binding = createBinding();
		// #5439
		// reset Giac before each call
		init(exp0, timeoutMilliseconds);

		String exp = wrapInevalfa(exp0);

		debug("giac evalRaw input: ", exp);

		String cachedResult = getResultFromCache(exp);

		if (cachedResult != null && !cachedResult.isEmpty()) {
			return cachedResult;
		}
		String casInput = "caseval(" + exp + ")";

		CrashlyticsLogger.log("Giac Input: " + casInput);

		Gen g = binding.createGen(casInput, context);
		g = g.eval(1, context);
		String ret = g.print(context);

		debug("giac evalRaw output: ", ret);

		if (ret != null && ret.startsWith("\"") && ret.endsWith("\"")) {
			ret = ret.substring(1, ret.length() - 1);
		}

		addResultToCache(exp, ret);

		return ret;
	}

	/**
	 * @param prefix debug prefix
	 * @param giacString giac input / output
	 */
	protected void debug(String prefix, String giacString) {
		Log.debug(prefix + giacString);
	}

	private void init(String exp, long timeoutMilliseconds) {
		CASGiacBinding binding = createBinding();
		Gen g = binding.createGen(initString, context);
		g.eval(1, context);

		CustomFunctions[] init = CustomFunctions.values();
		CustomFunctions.setDependencies();

		for (int i = 0; i < init.length; i++) {
			CustomFunctions function = init[i];

			// send only necessary init commands
			boolean foundInInput = false;
			/* This is very hacky here. If the input expression as string
			 * contains an internal GeoGebra CAS command, then that command will be executed
			 * in Giac. TODO: find a better a way.
			 */
			if (function.functionName == null
					|| (foundInInput = (exp
					.indexOf(function.functionName) > -1))) {
				g = binding.createGen(function.definitionString, context);
				g.eval(1, context);
				/* Some commands may require additional commands to load. */
				if (foundInInput) {
					ArrayList<CustomFunctions> dependencies = CustomFunctions
							.prereqs(function);
					for (CustomFunctions dep : dependencies) {
						Log.debug(function + " implicitly loads " + dep);
						g = binding.createGen(dep.definitionString, context);
						g.eval(1, context);
					}
				}
			}
		}

		long timeout = timeoutMilliseconds / 1000;
		binding.createGen("caseval(\"timeout " + timeout + "\")", context)
				.eval(1, context);
		binding.createGen("caseval(\"ckevery 20\")", context)
				.eval(1, context);

		// make sure we don't always get the same value!
		int seed = getSeed(exp);
		g = binding.createGen("srand(" + seed + ")", context);
		g.eval(1, context);
	}

	@Override
	public String evaluateCAS(String input) {
		// don't need to replace Unicode when sending to JNI
		String exp = casParser.replaceIndices(input, false);

		try {
			return evaluate(exp, timeoutMillis);
		} catch (TimeoutException te) {
			throw te;
		} catch (Throwable e) {
			Log.debug(e);
		}

		return null;
	}

	@Override
	protected String evaluate(final String exp, final long timeoutMillis0)
			throws Throwable {
		Runnable evalFunction = () -> threadResult = evalRaw(exp, timeoutMillis0);

		threadResult = null;

		callEvaluateFunction(evalFunction);

		String ret = postProcess(threadResult);

		// Log.debug("giac output: " + ret);
		if (ret.contains("user interruption")) {
			Log.debug("Standard timeout from Giac");
			throw new TimeoutException("Standard timeout from Giac");
		}

		return ret;
	}

	/**
	 * @param evaluateFunction function
	 * @throws Throwable exception
	 */
	protected abstract void callEvaluateFunction(Runnable evaluateFunction) throws Throwable;

	@Override
	public boolean externalCAS() {
		return true;
	}
}
