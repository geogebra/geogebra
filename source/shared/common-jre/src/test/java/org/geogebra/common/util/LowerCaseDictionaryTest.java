package org.geogebra.common.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class LowerCaseDictionaryTest {

	private LowerCaseDictionary dict;

	@Before
	public void setup() {
		dict = new LowerCaseDictionary();
	}

	@Test
	public void testCompletions() {
		dict.addEntry("b\u00e4cd");
		assertThat(completionOf("ac"), equalTo("b[\u00e4c]d"));
		assertThat(completionOf("bac"), equalTo("[b\u00e4c]d"));
		assertThat(completionOf("ba"), equalTo("[b\u00e4]cd"));
	}

	private String completionOf(String content) {
		MatchedString match = dict.getCompletions(content).get(0);
		return String.format("%s[%s]%s", (Object[]) match.getParts());
	}

}
