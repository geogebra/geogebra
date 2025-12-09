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

package org.geogebra.test.matcher;

import org.geogebra.test.CASTestLogger;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public final class IsEqualStringIgnoreWhitespaces extends TypeSafeMatcher<String> {

	/**
	 * the expected test result
	 */
	private final String expected;
	/**
	 * not the expected but also valid result(s)
	 */
	private final String[] valid;

	private final CASTestLogger logger;
	private final String input;

	private IsEqualStringIgnoreWhitespaces(CASTestLogger logger, String input,
			String expectedResult, String... validResults) {
		this.logger = logger;
		this.input = input;
		this.expected = expectedResult;
		this.valid = validResults;
	}

	@Override
	public boolean matchesSafely(String string) {
		String trimmedExpected = trimAll(expected);
		String trimmedString = trimAll(string);

		if (trimmedExpected.equals(trimmedString)) {
			return true;
		}

		String[] trimmedValid = trimAll(valid);

		for (int i = 0; i < trimmedValid.length; i++) {
			if (trimmedValid[i].equals(trimmedString)) {
				logger.addLog(input, string, expected);
				return true;
			}
		}
		return false;
	}

	private String trimAll(final String input) {
		return input.replaceAll(" ", "");
	}

	private String[] trimAll(final String[] input) {
		String[] trimmed = new String[input.length];
		for (int i = 0; i < input.length; i++) {
			trimmed[i] = input[i].replaceAll(" ", "");
		}
		return trimmed;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(expected);
	}

	/**
	 * @param logger logger
	 * @param input CAS input
	 * @param expectedResult preferred output
	 * @param validResults alternative outputs
	 * @return matcher
	 */
	@Factory
	public static Matcher<String> equalToIgnoreWhitespaces(
			CASTestLogger logger, String input, String expectedResult,
			String... validResults) {
		return new IsEqualStringIgnoreWhitespaces(logger, input,
				expectedResult, validResults);
	}
}
