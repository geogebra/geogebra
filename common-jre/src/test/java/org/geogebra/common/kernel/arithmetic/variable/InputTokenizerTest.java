package org.geogebra.common.kernel.arithmetic.variable;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class InputTokenizerTest extends BaseUnitTest {


	@Test
	public void testAB() {
		withGeos("a", "b");
		shouldBeSplitTo("ab","a", "b");
	}

	@Test
	public void testAIndexedB() {
		withGeos("a_{1}", "b");
		shouldBeSplitTo("a_{1}b","a_{1}", "b");
	}

	@Test
	public void testAIndexedBIndexed() {
		withGeos("a_{1}", "b_{242}");
		shouldBeSplitTo("a_{1}b_{242}","a_{1}", "b_{242}");
	}

	@Test
	public void testConstantAndVariable() {
		withGeos("a", "b");
		shouldBeSplitTo("21ab", "21", "a", "b");
	}

	@Test
	public void testMoreVariables() {
		shouldBeSplitTo("a_{1}bcd_{3}4fdx","a_{1}", "b", "c", "d_{3}", "4", "f", "d", "x");
	}

	@Test
	public void testPi() {
		shouldBeSplitTo("api","a", "pi");
	}

	@Test
	public void testFunctionVarPlus() {
		withGeos("f(var)", "a", "b");
		shouldBeSplitTo("var+ab","var", "+", "a", "b");
	}

	@Test
	public void testFunctionVar() {
		add("f(var)=?");
		shouldBeSplitTo("avarb","a", "var", "b");
	}

	@Test
	public void testC_2e() {
		shouldBeSplitTo("c_2e^(7x)", "c_2", "e^(7x)");
	}

	@Test
	public void testAkakakaaa() {
		withGeos("a", "k", "vv(x)");
		shouldBeSplitTo("akakakvva" ,"a", "k", "a", "k", "a",
				"k", "vv", "a");
	}


	private void withGeos(String... labels) {
		for (String label: labels) {
			add(label + "=?");
		}
	}

	@Test
	public void testMutliFunctionVars() {
		add("t(mul, var)=?");
		shouldBeSplitTo("amulvarb","a", "mul", "var", "b");
	}

	private void shouldBeSplitTo(String input, String... tokens) {
		InputTokenizer tokenizer = new InputTokenizer(getKernel(), input);
		assertEquals(Arrays.asList(tokens), tokenizer.getTokens());
	}
}
