package org.geogebra.common.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class LowerCaseDictionaryTest {

	private LowerCaseDictionary dict;

	@Before
	public void setup() {
		dict = new LowerCaseDictionary(new NormalizerMinimal());
	}

	@Test
	public void testCompletions() {
		dict.addEntry("b\u00C6c");
		assertThat(completionOf("ae"), equalTo("b[\u00C6]c"));
		assertThat(completionOf("bae"), equalTo("[b\u00C6]c"));
		assertThat(completionOf("aec"), equalTo("b[\u00C6c]"));
		assertThat(completionOf("baec"), equalTo("[b\u00C6c]"));
	}

	private String completionOf(String content) {
		MatchedString match = dict.getCompletions(content).get(0);
		return String.format("%s[%s]%s", (Object[]) match.getParts());
	}

}
