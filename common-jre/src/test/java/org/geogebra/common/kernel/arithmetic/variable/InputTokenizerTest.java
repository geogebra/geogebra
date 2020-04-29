package org.geogebra.common.kernel.arithmetic.variable;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class InputTokenizerTest {

	@Test
	public void testAB() {
		InputTokenizer tokenizer = new InputTokenizer("ab");
		assertEquals(Arrays.asList("a", "b"), tokenizer.getTokens());
	}

	@Test
	public void testAIndexedB() {
		InputTokenizer tokenizer = new InputTokenizer("a_{1}b");
		assertEquals(Arrays.asList("a_{1}", "b"), tokenizer.getTokens());
	}

	@Test
	public void testAIndexedBIndexed() {
		InputTokenizer tokenizer = new InputTokenizer("a_{1}b_{242}");
		assertEquals(Arrays.asList("a_{1}", "b_{242}"), tokenizer.getTokens());
	}

	@Test
	public void testMoreVariables() {
		InputTokenizer tokenizer = new InputTokenizer("a_{1}bcd_{3}4fdx");
		assertEquals(Arrays.asList("a_{1}", "b", "c", "d_{3}", "4", "f", "d", "x"), tokenizer.getTokens());
	}
}
