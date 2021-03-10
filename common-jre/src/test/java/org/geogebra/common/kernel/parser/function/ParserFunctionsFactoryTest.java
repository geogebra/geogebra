package org.geogebra.common.kernel.parser.function;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.plugin.Operation;
import org.junit.Assert;
import org.junit.Test;

public class ParserFunctionsFactoryTest {
	private final LocalizationCommon loc = new LocalizationCommon(3);

	@Test
	public void testGraphingParserFunctions() {
		ParserFunctions functions = ParserFunctionsFactory
				.createGraphingParserFunctionsFactory().createParserFunctions();
		assertEquals(functions.get("sin", 1), Operation.SIN);
		Assert.assertNull(functions.get("alt", 1));
		Assert.assertNull(functions.get("arg", 1));
	}

	@Test
	public void testDefaultParserFunctions() {
		ParserFunctions functions = ParserFunctionsFactory
				.createParserFunctionsFactory().createParserFunctions();
		assertEquals(functions.get("sin", 1), Operation.SIN);
		assertEquals(functions.get("alt", 1), Operation.ALT);
		assertEquals(functions.get("arg", 1), Operation.ARG);
	}

	@Test
	public void suggestionsShouldBeUnique() {
		ParserFunctions functions = ParserFunctionsFactory
				.createParserFunctionsFactory().createParserFunctions();
		functions.updateLocale(new LocalizationCommon(3));
		List<String> suggestions = functions.getCompletions("nroot");
		assertEquals(1, suggestions.size());
		suggestions = functions.getCompletions("tan");
		assertEquals(2, suggestions.size()); //tan, tanh
	}

	@Test
	public void suggestionsShouldShowLocalAndDefault() {
		ParserFunctions functions = ParserFunctionsFactory
				.createParserFunctionsFactory().createParserFunctions();
		setLanguage(functions, new Locale("es"));
		List<String> suggestions = functions.getCompletions("sen");
		assertEquals(suggestions, Arrays.asList("sen( <x> )", "senh( <x> )"));
		suggestions = functions.getCompletions("sin");
		assertEquals(suggestions.size(), 3); // sin, sinh, sinIntegral
		suggestions = functions.getCompletions("nro");
		assertEquals(suggestions.size(), 1);
		suggestions = functions.getCompletions(loc.getFunction("nroot"));
		assertEquals(suggestions.size(), 1);
		setLanguage(functions, new Locale("nn"));
		suggestions = functions.getCompletions("nro");
		assertEquals(suggestions, Arrays.asList("nroot( <x>, <n> )",
				"nrot( <x>, <n> )"));
	}

	@Test
	public void testParserFunctionsForSpanish() {
		ParserFunctions functions = ParserFunctionsFactory
				.createParserFunctionsFactory().createParserFunctions();
		setLanguage(functions, new Locale("es"));
		assertEquals(functions.get("sen", 1), Operation.SIN);
		assertEquals(functions.get("arcsen", 1), Operation.ARCSIN);
		assertEquals(functions.get("senh", 1), Operation.SINH);
		assertEquals(functions.get("arcsenh", 1), Operation.ASINH);
	}

	private void setLanguage(ParserFunctions functions, Locale es) {
		loc.setLocale(es);
		functions.updateLocale(loc);
	}
}
