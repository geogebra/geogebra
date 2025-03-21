package org.geogebra.common.kernel.parser.function;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.geogebra.common.gui.util.TableSymbols;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;
import org.geogebra.test.LocalizationCommonUTF;
import org.geogebra.test.annotation.Issue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import com.himamis.retex.editor.share.util.Unicode;

public class ParserFunctionsTest {

	private static final String GAMMA_PREFIX = "gam"; // NO-TYPO
	private ParserFunctions parserFunctions;

	@Before
	public void setUp() {
		parserFunctions = ParserFunctionsFactory.createParserFunctionsFactory()
				.createParserFunctions();
	}

	@Test
	public void testGetString() {
		assertNotNull(parserFunctions.get("sin", 1));
		assertNotNull(parserFunctions.get("cos", 1));
		assertNull(parserFunctions.get("", 1));
		assertNull(parserFunctions.get(null, 1));
	}

	@Test
	public void testGetSize() {
		assertNull(parserFunctions.get("sin", 100));
		assertNull(parserFunctions.get("sin", 0));
		assertNull(parserFunctions.get("sin", 2));
		assertNotNull(parserFunctions.get("sin", 1));
	}

	@Test
	public void testReserved() {
		assertTrue(parserFunctions.isReserved("sin"));
		assertTrue(parserFunctions.isReserved("sin"));
		assertTrue(parserFunctions.isReserved(Unicode.IMAGINARY + ""));
		assertTrue(parserFunctions.isReserved(Unicode.EULER_STRING));
		assertFalse(parserFunctions.isReserved("a"));
		assertFalse(parserFunctions.isReserved("b"));
		assertFalse(parserFunctions.isReserved(null));
	}

	@Test
	public void testGetCompletions() {
		List<String> completions = parserFunctions.getCompletions("si");
		assertThat(completions, hasItem("sin( <x> )"));
		assertThat(completions, hasItem("sinIntegral( <x> )"));
		assertThat(completions, not(hasItem("cos( <x> )")));

		completions = parserFunctions.getCompletions("Si");
		assertEquals(0, completions.size());
	}

	@Test
	public void testGetInternal() {
		Localization loc = Mockito.mock(Localization.class);
		Mockito.when(loc.getFunction(Mockito.anyString())).then(AdditionalAnswers.returnsFirstArg());
		assertEquals("sin", parserFunctions.getInternal(loc, "sin"));
		assertEquals("cos", parserFunctions.getInternal(loc, "cos"));
		assertNull(parserFunctions.getInternal(loc, "NO-SUCH-FUNCTION"));
		assertEquals("nroot", parserFunctions.getInternal(loc, "nroot"));
	}

	@Test
	public void testIsTranslatableFunction() {
		assertTrue(parserFunctions.isTranslatableFunction("sin"));
		assertTrue(parserFunctions.isTranslatableFunction("cos"));
		assertFalse(parserFunctions.isTranslatableFunction("e"));
		assertFalse(parserFunctions.isTranslatableFunction(""));
		assertFalse(parserFunctions.isTranslatableFunction(null));
	}

	@Test
	public void testReverseTrig() {
		parserFunctions.setInverseTrig(true);
		assertEquals(parserFunctions.get("arcsin", 1), Operation.ARCSIND);
		assertEquals(parserFunctions.get("arccos", 1), Operation.ARCCOSD);
		parserFunctions.setInverseTrig(false);
		assertEquals(parserFunctions.get("arcsin", 1), Operation.ARCSIN);
		assertEquals(parserFunctions.get("arccos", 1), Operation.ARCCOS);
	}

	@Test
	public void testUpdateLocale() {
		Localization loc = Mockito.mock(Localization.class);
		parserFunctions.updateLocale(loc);
		Mockito.verify(loc).getFunction("sin", false);
		Mockito.verify(loc).getFunction("sin", false);
		Mockito.verify(loc).getFunction("nroot", true);
	}

	@Test
	public void testCompletions() {
		List<String> completions = parserFunctions.getCompletions(GAMMA_PREFIX);
		assertEquals(3, completions.size());
	}

	@Test
	public void testCompletionsWithFilteredOperations() {
		List<String> completions = parserFunctions.getCompletions(GAMMA_PREFIX,
				operation -> !Set.of(Operation.GAMMA,
						Operation.GAMMA_INCOMPLETE,
						Operation.GAMMA_INCOMPLETE_REGULARIZED).contains(operation));
		assertEquals(0, completions.size());
	}

	@Test
	@Issue("APPS-5454")
	public void testUnaryFlagConsistent() {
		Localization loc = new LocalizationCommonUTF(3);
		Set<Operation> fromHelp = Arrays.stream(TableSymbols
						.getTranslatedFunctions(loc, parserFunctions))
				.map(s -> s.split("\\(")[0].trim())
				.map(s -> parserFunctions.get(s, 1))
				.filter(Objects::nonNull)
				.collect(Collectors.toCollection(TreeSet::new));
		Set<Operation> fromEnum = Arrays.stream(Operation.values())
				.filter(Operation::isUnary)
				.collect(Collectors.toCollection(TreeSet::new));
		fromEnum.remove(Operation.NO_OPERATION);
		fromEnum.remove(Operation.FACTORIAL);
		assertEquals(fromHelp, fromEnum);
	}
}
