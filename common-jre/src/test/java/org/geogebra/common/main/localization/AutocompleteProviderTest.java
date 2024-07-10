package org.geogebra.common.main.localization;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.main.settings.config.AppConfigUnrestrictedGraphing;
import org.geogebra.common.main.syntax.suggestionfilter.GraphingSyntaxFilter;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
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
		assertEquals("sin", completionList.get(0).getCommand());
		assertEquals(Collections.singletonList("sin( <x> )"), completionList.get(0).syntaxes);
	}

	@Test
	public void functionSuggestionShouldBeCaseSensitive() {
		List<String> completionList = getStringCompletions("Sin");
		assertThat(completionList, equalTo(Arrays.asList("FitSin", "IsInRegion", "IsInteger")));
	}

	@Test
	public void initialMatchesShouldComeFirst() {
		List<String> completionList = getStringCompletions("Row");
		assertThat(completionList, equalTo(Arrays.asList("Row", "FillRow", "FitGrowth",
				"ReducedRowEchelonForm")));
	}

	@Test
	public void commandSuggestionTest() {
		List<AutocompleteProvider.Completion> completionList = getCompletions("int");
		assertEquals("Integral", completionList.get(0).getCommand());
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

		assertEquals(0, getExactSyntaxMatchOf(config, Commands.InverseBinomial.name())
						.count());
	}

	@Test
	public void shouldShowCasSpecific() {
		AutocompleteProvider casProvider = new AutocompleteProvider(getApp(), true);
		assertEquals(3, casProvider.getCompletions("Groebner").count());
		assertEquals(0, casProvider.getCompletions("ExpSimplify").count());
	}

	private Stream<AutocompleteProvider.Completion> getExactSyntaxMatchOf(AppConfig config,
			String name) {
		App app = AppCommonFactory.create(config);
		AutocompleteProvider provider = new AutocompleteProvider(app, false);
		return provider.getCompletions(name).filter(c -> Objects.equals(c.match, name));
	}

	@Test
	public void shouldUpdateOnAppSwitch() {
		shouldUpdateOnAppSwitch("en", "Curve");
	}

	@Test
	public void shouldUpdateOnAppSwitchDE() {
		shouldUpdateOnAppSwitch("de", "Kurve");
	}

	private void shouldUpdateOnAppSwitch(String lang, String curveCommand) {
		getApp().setLocale(new Locale(lang));
		AutocompleteProvider provider = new AutocompleteProvider(getApp(), false);
		AutocompleteProvider casProvider = new AutocompleteProvider(getApp(), true);

		AppConfigCas casConfig = new AppConfigCas();
		swapConfig(casConfig);
		assertEquals(0, provider.getCompletions(curveCommand).count());
		assertEquals(0, casProvider.getCompletions(curveCommand).count());

		swapConfig(new AppConfigGraphing());
		assertEquals(2, provider.getCompletions(curveCommand).count());
		assertEquals(2, casProvider.getCompletions(curveCommand).count());

		swapConfig(new AppConfigCas());
		assertEquals(0, provider.getCompletions(curveCommand).count());
		assertEquals(0, casProvider.getCompletions(curveCommand).count());
	}

	private void swapConfig(AppConfig config) {
		CommandDispatcher commandDispatcher =
				getKernel().getAlgebraProcessor().getCommandDispatcher();
		CommandFilter commandFilter = getApp().getConfig().getCommandFilter();
		if (commandFilter != null) {
			commandDispatcher.removeCommandFilter(commandFilter);
		}
		getApp().setConfig(config);
		commandFilter = getApp().getConfig().getCommandFilter();
		if (commandFilter != null) {
			commandDispatcher.addCommandFilter(commandFilter);
		}
		getApp().resetCommandDict();
	}

	@Test
	public void graphingSuiteShouldHaveCasCommands() {
		AutocompleteProvider provider = new AutocompleteProvider(getApp(), false);
		AppConfigUnrestrictedGraphing graphingSuiteConfig = new AppConfigUnrestrictedGraphing();

		swapConfig(graphingSuiteConfig);
		assertEquals(5, provider.getCompletions("Solve").count());

		swapConfig(new AppConfigGraphing());
		assertEquals(1, provider.getCompletions("Solve").count());

		swapConfig(new AppConfigUnrestrictedGraphing());
		assertEquals(5, provider.getCompletions("Solve").count());
	}

	private List<AutocompleteProvider.Completion> getCompletions(String sin) {
		return provider.getCompletions(sin).collect(Collectors.toList());
	}

	private List<String> getStringCompletions(String sin) {
		return provider.getCompletions(sin)
				.map(AutocompleteProvider.Completion::getCommand).collect(Collectors.toList());
	}
}
