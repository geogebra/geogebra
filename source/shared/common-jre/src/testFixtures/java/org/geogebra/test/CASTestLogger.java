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

package org.geogebra.test;

import java.util.ArrayList;

import org.geogebra.common.util.debug.Log;

/**
 * Used for logging all test results of the GeoGebraCasIntegrationTest
 * which are not the expected but valid results
 * 
 * @author Johannes Renner
 */
public class CASTestLogger {
	private final ArrayList<String> logs;

	/**
	 * does the needed initialization
	 */
	public CASTestLogger() {
		logs = new ArrayList<>();
	}

	/**
	 * Adds an entry to the logs
	 * 
	 * @param input
	 *            the input which has been executed in the CAS
	 * @param gotResult
	 *            the result got from executing the input in the CAS
	 * @param expectedResult
	 *            the expected result
	 */
	public void addLog(String input, String gotResult, String expectedResult) {
		String entry = "WARNING\tTest input:\t " + input
				+ " returned not the expected result but another valid result";
		entry += "\n\tExpected result: " + expectedResult;
		entry += "\n\tActual result:\t " + gotResult;
		entry += "\n";
		logs.add(entry);
	}

	/**
	 * Prints the logs to the shell output
	 */
	public void handleLogs() {
		int nrOfWarnings = logs.size();
		Log.debug("\nTests finished with " + nrOfWarnings
				+ " warnings" + (nrOfWarnings > 0 ? ":" : "."));
		if (nrOfWarnings > 0) {
			for (String log : logs) {
				Log.debug(log);
			}
		}
	}
}
