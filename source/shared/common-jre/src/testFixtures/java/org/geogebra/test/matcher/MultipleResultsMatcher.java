package org.geogebra.test.matcher;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;
import org.hamcrest.TypeSafeMatcher;

public class MultipleResultsMatcher extends TypeSafeMatcher<String> {

	private List<ValidResult> validResults;

	public MultipleResultsMatcher(String... validResults) {
		this.validResults = removeSpaces(validResults);
	}

	private List<ValidResult> removeSpaces(String... strings) {
		List<ValidResult> validResultsWithoutSpaces = new ArrayList<>();
		for (String string : strings) {
			ValidResult validResult = new ValidResult(string);
			validResultsWithoutSpaces.add(validResult);
		}
		return validResultsWithoutSpaces;
	}

	@Override
	protected boolean matchesSafely(String item) {
		String actualResultWithoutSpaces = removeSpaces(item);
		for (ValidResult validResult : validResults) {
			if (actualResultWithoutSpaces.equals(validResult.toString())) {
				return true;
			}
		}
		return false;
	}

	private String removeSpaces(String string) {
		return string.replaceAll(" ", "");
	}

	@Override
	public void describeTo(Description description) {
		description.appendList(" Any of: ", ", ", "", validResults);
	}

	private class ValidResult implements SelfDescribing {

		private String resultString;

		private ValidResult(String resultString) {
			this.resultString = resultString;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText(resultString);
		}

		@Override
		public String toString() {
			return removeSpaces(resultString);
		}
	}
}
