package org.geogebra.test.util;

import org.geogebra.cas.logging.CASTestLogger;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

public class IsEqualStringIgnoreWhitespaces extends TypeSafeMatcher<String> {

	/**
	 * the expected test result
	 */
	protected final String expected;
	/**
	 * not the expected but also valid result(s)
	 */
	protected final String[] valid;

	private final CASTestLogger logger;
	private final String input;

	public IsEqualStringIgnoreWhitespaces(CASTestLogger logger, String input,
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

	protected String trimAll(final String input) {
		return input.replaceAll(" ", "");
	}

	protected String[] trimAll(final String[] input) {
		String[] trimmed = new String[input.length];
		for (int i = 0; i < input.length; i++) {
			trimmed[i] = input[i].replaceAll(" ", "");
		}
		return trimmed;
	}

	public void describeTo(Description description) {
		description.appendValue(expected);
	}

	@Factory
	public static <T> Matcher<String> equalToIgnoreWhitespaces(
			CASTestLogger logger, String input, String expectedResult,
			String... validResults) {
		return new IsEqualStringIgnoreWhitespaces(logger, input,
				expectedResult, validResults);
	}
}
