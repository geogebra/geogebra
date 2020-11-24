package org.geogebra.common.kernel.parser.function;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import com.himamis.retex.editor.share.util.Unicode;

public class ParserFunctionsTest {

	private ParserFunctions parserFunctions;

	@Before
	public void setUp() {
		parserFunctions = ParserFunctionsFactory.createParserFunctionsFactory()
				.createParserFunctions();
	}

	@Test
	public void testGetString() {
		Assert.assertNotNull(parserFunctions.get("sin", 1));
		Assert.assertNotNull(parserFunctions.get("cos", 1));
		Assert.assertNull(parserFunctions.get("", 1));
		Assert.assertNull(parserFunctions.get(null, 1));
	}

	@Test
	public void testGetSize() {
		Assert.assertNull(parserFunctions.get("sin", 100));
		Assert.assertNull(parserFunctions.get("sin", 0));
		Assert.assertNull(parserFunctions.get("sin", 2));
		Assert.assertNotNull(parserFunctions.get("sin", 1));
	}

	@Test
	public void testReserved() {
		Assert.assertTrue(parserFunctions.isReserved("sin"));
		Assert.assertTrue(parserFunctions.isReserved("sin"));
		Assert.assertTrue(parserFunctions.isReserved(Unicode.IMAGINARY + ""));
		Assert.assertTrue(parserFunctions.isReserved(Unicode.EULER_STRING));
		Assert.assertFalse(parserFunctions.isReserved("a"));
		Assert.assertFalse(parserFunctions.isReserved("b"));
		Assert.assertFalse(parserFunctions.isReserved(null));
	}

	@Test
	public void testGetCompletions() {
		List<String> completions = parserFunctions.getCompletions("si");
		assertThat(completions, hasItem("sin( <x> )"));
		assertThat(completions, hasItem("sinIntegral( <x> )"));
		assertThat(completions, not(hasItem("cos( <x> )")));

		completions = parserFunctions.getCompletions("Si");
		Assert.assertEquals(completions.size(), 0);
	}

	@Test
	public void testGetInternal() {
		Localization loc = Mockito.mock(Localization.class);
		Mockito.when(loc.getFunction(Mockito.anyString())).then(AdditionalAnswers.returnsFirstArg());
		Assert.assertEquals(parserFunctions.getInternal(loc, "sin"), "sin");
		Assert.assertEquals(parserFunctions.getInternal(loc, "cos"), "cos");
		Assert.assertNull(parserFunctions.getInternal(loc, "NO-SUCH-FUNCTION"));
		Assert.assertEquals(parserFunctions.getInternal(loc, "nroot"), "nroot");
	}

	@Test
	public void testIsTranslatableFunction() {
		Assert.assertTrue(parserFunctions.isTranslatableFunction("sin"));
		Assert.assertTrue(parserFunctions.isTranslatableFunction("cos"));
		Assert.assertFalse(parserFunctions.isTranslatableFunction("e"));
		Assert.assertFalse(parserFunctions.isTranslatableFunction(""));
		Assert.assertFalse(parserFunctions.isTranslatableFunction(null));
	}

	@Test
	public void testReverseTrig() {
		parserFunctions.setInverseTrig(true);
		Assert.assertEquals(parserFunctions.get("arcsin", 1), Operation.ARCSIND);
		Assert.assertEquals(parserFunctions.get("arccos", 1), Operation.ARCCOSD);
		parserFunctions.setInverseTrig(false);
		Assert.assertEquals(parserFunctions.get("arcsin", 1), Operation.ARCSIN);
		Assert.assertEquals(parserFunctions.get("arccos", 1), Operation.ARCCOS);
	}

	@Test
	public void testUpdateLocale() {
		Localization loc = Mockito.mock(Localization.class);
		parserFunctions.updateLocale(loc);
		Mockito.verify(loc).getFunction("sin", false);
		Mockito.verify(loc).getFunction("sin", false);
		Mockito.verify(loc).getFunction("nroot", true);
	}
}
