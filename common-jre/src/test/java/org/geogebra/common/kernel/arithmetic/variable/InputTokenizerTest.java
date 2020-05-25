package org.geogebra.common.kernel.arithmetic.variable;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class InputTokenizerTest extends TokenizerBaseTest {

	@Test
	public void testAB() {
		withGeos("a", "b");
		shouldBeSplitTo("ab", "a", "b");
	}

	@Test
	public void testAIndexedB() {
		withGeos("a_{1}", "b");
		shouldBeSplitTo("a_{1}b", "a_{1}", "b");
	}

	@Test
	public void testAIndexedBIndexed() {
		withGeos("a_{1}", "b_{242}");
		shouldBeSplitTo("a_{1}b_{242}", "a_{1}", "b_{242}");
	}

	@Test
	public void testConstantAndVariable() {
		withGeos("a", "b");
		shouldBeSplitTo("21ab", "21", "a", "b");
	}

	@Test
	public void testMoreVariables() {
		shouldBeSplitTo("a_{1}bcd_{3}4fdx", "a_{1}", "b", "c", "d_{3}", "4", "f", "d", "x");
	}

	@Test
	public void testPi() {
		shouldBeSplitTo("api", "a", "pi");
	}

	@Test
	public void testFunctionVarPlus() {
		withGeos("f(var)", "a", "b");
		shouldBeSplitTo("var+ab", "var", "+", "a", "b");
	}

	@Test
	public void testFunctionVar() {
		withGeos("f(var)");
		shouldBeSplitTo("avarb", "a", "var", "b");
	}

	@Test
	public void testAkakakaaa() {
		withGeos("a", "k", "aa(x)");
		shouldBeSplitTo("akakakaaa" , "a", "k", "a", "k", "a",
				"k", "a", "a", "a");
	}

	@Test
	public void testAakkaa() {
		withGeos("aa(x)", "k", "a");
		shouldBeSplitTo("aakkaaa", "a", "a", "k", "k", "a", "a", "a");
	}

	@Test
	public void testImaginary() {
		shouldBeSplitTo("i1", String.valueOf(Unicode.IMAGINARY), "1");
	}

	@Test
	public void textX4() {
		shouldBeSplitTo("x4", "x", "4");
	}

	@Test
	public void textK4() {
		shouldBeSplitTo("k4", "k", "4");
	}

	@Test
	public void testMutliFunctionVars() {
		withGeos("t(mul, var)");
		shouldBeSplitTo("amulvarb", "a", "mul", "var", "b");
	}

	private void shouldBeSplitTo(String input, String... tokens) {
		InputTokenizer tokenizer = new InputTokenizer(getKernel(), input);
		assertEquals(Arrays.asList(tokens), tokenizer.getTokens());
	}
}
