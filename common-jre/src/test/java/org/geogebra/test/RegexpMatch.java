package org.geogebra.test;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public final class RegexpMatch extends TypeSafeMatcher<String> {
	private String regexp;

	private RegexpMatch(String regexp) {
		this.regexp = regexp;
	}
	public static RegexpMatch matches(String regexp) {
		return new RegexpMatch(regexp);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("text matching " + regexp);
	}

	@Override
	public boolean matchesSafely(String item) {
		return item.replace("\n", " ").replace("\r", " ").matches(regexp);
	}
}
