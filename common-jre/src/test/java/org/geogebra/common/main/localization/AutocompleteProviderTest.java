package org.geogebra.common.main.localization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.main.syntax.suggestionfilter.GraphingSyntaxFilter;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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

	@Test
	public void testCommandWithoutSyntaxIsNotReturned() {
		AppConfig config = Mockito.spy(new AppConfigGraphing());
		SyntaxFilter commandSyntax = Mockito.spy(new GraphingSyntaxFilter());
		// Filter every syntax for InverseBinomial
		when(commandSyntax.getFilteredSyntax(
				eq(Commands.InverseBinomial.name()), anyString())).thenReturn("");
		when(config.newCommandSyntaxFilter()).thenReturn(commandSyntax);

		App app = AppCommonFactory.create(config);
		AutocompleteProvider provider = new AutocompleteProvider(app, false);
		assertEquals(0, provider.getCompletions(Commands.InverseBinomial.name()).count());
	}

	private List<AutocompleteProvider.Completion> getCompletions(String sin) {
		return provider.getCompletions(sin).collect(Collectors.toList());
	}
}
