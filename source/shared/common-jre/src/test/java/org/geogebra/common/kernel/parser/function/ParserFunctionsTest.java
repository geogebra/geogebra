/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */
 
package org.geogebra.common.kernel.parser.function;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.geogebra.common.gui.util.TableSymbols;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.test.LocalizationCommonUTF;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

class ParserFunctionsTest {

	private static final String GAMMA_PREFIX = "gam"; // NO-TYPO
	private ParserFunctions parserFunctions;

	@BeforeEach
	void setUp() {
		parserFunctions = ParserFunctionsFactory.createParserFunctionsFactory()
				.createParserFunctions();
	}

	@Test
	void testGetString() {
		assertNotNull(parserFunctions.get("sin", 1));
		assertNotNull(parserFunctions.get("cos", 1));
		assertNull(parserFunctions.get("", 1));
		assertNull(parserFunctions.get(null, 1));
	}

	@Test
	void testGetSize() {
		assertNull(parserFunctions.get("sin", 100));
		assertNull(parserFunctions.get("sin", 0));
		assertNull(parserFunctions.get("sin", 2));
		assertNotNull(parserFunctions.get("sin", 1));
	}

	@Test
	void testReserved() {
		assertTrue(parserFunctions.isReserved("sin"));
		assertTrue(parserFunctions.isReserved("sin"));
		assertTrue(parserFunctions.isReserved(Unicode.IMAGINARY + ""));
		assertTrue(parserFunctions.isReserved(Unicode.EULER_STRING));
		assertFalse(parserFunctions.isReserved("a"));
		assertFalse(parserFunctions.isReserved("b"));
		assertFalse(parserFunctions.isReserved(null));
	}

	@Test
	void testGetCompletions() {
		List<String> completions = parserFunctions.getCompletions("si");
		assertThat(completions, hasItem("sin( <x> )"));
		assertThat(completions, hasItem("sinIntegral( <x> )"));
		assertThat(completions, not(hasItem("cos( <x> )")));

		completions = parserFunctions.getCompletions("Si");
		assertEquals(0, completions.size());
	}

	@Test
	void testGetInternal() {
		Localization loc = Mockito.mock(Localization.class);
		Mockito.when(loc.getFunction(Mockito.anyString()))
				.then(AdditionalAnswers.returnsFirstArg());
		assertEquals("sin", parserFunctions.getInternal(loc, "sin"));
		assertEquals("cos", parserFunctions.getInternal(loc, "cos"));
		assertNull(parserFunctions.getInternal(loc, "NO-SUCH-FUNCTION"));
		assertEquals("nroot", parserFunctions.getInternal(loc, "nroot"));
	}

	@Test
	void testIsTranslatableFunction() {
		assertTrue(parserFunctions.isTranslatableFunction("sin"));
		assertTrue(parserFunctions.isTranslatableFunction("cos"));
		assertFalse(parserFunctions.isTranslatableFunction("e"));
		assertFalse(parserFunctions.isTranslatableFunction(""));
		assertFalse(parserFunctions.isTranslatableFunction(null));
	}

	@Test
	void testReverseTrig() {
		parserFunctions.setInverseTrig(true);
		assertEquals(parserFunctions.get("arcsin", 1), Operation.ARCSIND);
		assertEquals(parserFunctions.get("arccos", 1), Operation.ARCCOSD);
		parserFunctions.setInverseTrig(false);
		assertEquals(parserFunctions.get("arcsin", 1), Operation.ARCSIN);
		assertEquals(parserFunctions.get("arccos", 1), Operation.ARCCOS);
	}

	@Test
	void testUpdateLocale() {
		Localization loc = Mockito.mock(Localization.class);
		parserFunctions.updateLocale(loc);
		Mockito.verify(loc).getFunction("sin", false);
		Mockito.verify(loc).getFunction("sin", false);
		Mockito.verify(loc).getFunction("nroot", true);
	}

	@Test
	void testCompletions() {
		List<String> completions = parserFunctions.getCompletions(GAMMA_PREFIX);
		assertEquals(3, completions.size());
	}

	@Test
	void testCompletionsWithFilteredOperations() {
		List<String> completions = parserFunctions.getCompletions(GAMMA_PREFIX,
				operation -> !Set.of(Operation.GAMMA,
						Operation.GAMMA_INCOMPLETE,
						Operation.GAMMA_INCOMPLETE_REGULARIZED).contains(operation));
		assertEquals(0, completions.size());
	}

	@Test
	@Issue("APPS-5454")
	void testUnaryFlagConsistent() {
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
