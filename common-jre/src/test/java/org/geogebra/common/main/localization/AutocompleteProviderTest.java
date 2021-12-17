package org.geogebra.common.main.localization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.BaseUnitTest;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Before;
import org.junit.Test;

public class AutocompleteProviderTest extends BaseUnitTest {

	private AutocompleteProvider provider;

	@Before
	public void setupProvider() {
		provider = new AutocompleteProvider(getApp(), false);
	}

	@Test
	public void functionSuggestionTest() {
		List<AutocompleteProvider.Completion> completionList = getCompletions("sin");
		assertEquals("sin", completionList.get(0).command);
		assertEquals(Collections.singletonList("sin( <x> )"), completionList.get(0).syntaxes);
	}

	@Test
	public void functionSuggestionShouldBeCaseSensitive() {
		List<AutocompleteProvider.Completion> completionList = getCompletions("Sin");
		assertThat(completionList, IsEmptyCollection.empty());
	}

	@Test
	public void commandSuggestionTest() {
		List<AutocompleteProvider.Completion> completionList = getCompletions("int");
		assertEquals("Integral", completionList.get(0).command);
		assertEquals(Arrays.asList("Integral( <Function> )", "Integral( <Function>, <Variable> )"),
				completionList.get(0).syntaxes.subList(0, 2));
	}

	private List<AutocompleteProvider.Completion> getCompletions(String sin) {
		return provider.getCompletions(sin).collect(Collectors.toList());
	}
}
