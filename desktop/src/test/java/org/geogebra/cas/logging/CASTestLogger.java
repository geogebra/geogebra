package org.geogebra.cas.logging;

import org.geogebra.cas.GeoGebraCasIntegrationTest;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Used for logging all test results of the {@link GeoGebraCasIntegrationTest}
 * which are not the expected but valid results
 * 
 * @author Johannes Renner
 */
public class CASTestLogger {
	private ArrayList<String> logs;

	/**
	 * does the needed initialization
	 */
	public CASTestLogger() {
		logs = new ArrayList<String>();
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
		entry += "\n\tGotten Result:\t " + gotResult;
		entry += "\n";
		logs.add(entry);
	}

	/**
	 * Prints the logs to the shell output
	 */
	public void handleLogs() {
		int nrOfWarnings = logs.size();
		System.out.println("\nTests finished with " + nrOfWarnings
				+ " warnings" + (nrOfWarnings > 0 ? ":" : "."));
		if (nrOfWarnings > 0) {
			Iterator<String> it = logs.iterator();
			while (it.hasNext()) {
				System.out.println(it.next());
			}
		}
	}
}
