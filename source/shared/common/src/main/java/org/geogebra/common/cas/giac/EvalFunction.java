/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.cas.giac;

import org.geogebra.common.cas.giac.binding.CASGiacBinding;
import org.geogebra.common.cas.giac.binding.Gen;
import org.geogebra.common.util.debug.crashlytics.CrashlyticsLogger;

public class EvalFunction implements Runnable {
	private final CASgiacB casGiac;
	protected boolean canceled;
	private final String exp;
	private final long timeoutMillis;

	public EvalFunction(CASgiacB casGiac, String exp, long timeoutMillis) {
		this.casGiac = casGiac;
		this.exp = exp;
		this.timeoutMillis = timeoutMillis;
	}

	public void cancel() {
		canceled = true;
	}

	@Override
	public void run() {
		String result = evalRaw(exp, timeoutMillis);
		if (!canceled) {
			casGiac.threadResult = result;
		}
	}

	/**
	 * @param exp0 String to send to Giac
	 * @param timeoutMilliseconds timeout in milliseconds
	 * @return String from Giac
	 */
	String evalRaw(String exp0, long timeoutMilliseconds) {
		CASGiacBinding binding = casGiac.createBinding();
		// #5439
		// reset Giac before each call
		casGiac.init(exp0, timeoutMilliseconds);

		String exp = casGiac.wrapInevalfa(exp0);

		casGiac.debug("giac evalRaw input: ", exp);

		String cachedResult = casGiac.getResultFromCache(exp);

		if (cachedResult != null && !cachedResult.isEmpty()) {
			return cachedResult;
		}
		String casInput = "caseval(" + exp + ")";

		CrashlyticsLogger.log("Giac Input: " + casInput);

		Gen g = binding.createGen(casInput, casGiac.context);
		g = g.eval(1, casGiac.context);
		String ret = g.print(casGiac.context);
		if (canceled) {
			casGiac.debug("giac evalRaw canceled", ret);
		} else {
			casGiac.debug("giac evalRaw output: ", ret);
		}
		if (ret != null && ret.startsWith("\"") && ret.endsWith("\"")) {
			ret = ret.substring(1, ret.length() - 1);
		}

		casGiac.addResultToCache(exp, ret);

		return ret;
	}
}
