package org.geogebra.common.kernel.arithmetic.variable;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	public void testRhoIndexedB() {
		String rhoW = Unicode.rho + "_{w}";
		withGeos(Unicode.rho + "", rhoW, "h");
		shouldBeSplitTo(rhoW + "h", rhoW, "h");
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
	public void testVariableConstant() {
		withGeos("a");
		shouldBeSplitTo("aa21", "a", "a", "21");
		shouldBeSplitTo("aa2", "a", "a", "2");
		shouldBeSplitTo("a2", "a",  "2");
	}

	@Test
	public void testMultiFunctionVars() {
		withGeos("t(mul, var)");
		shouldBeSplitTo("amulvarb", "a", "mul", "var", "b");
	}

	@Test
	public void testAmbiguousTokenization() {
		withGeos("a", "a1");
		shouldBeSplitTo("a1b", "a", "1", "b");
	}

	private void shouldBeSplitTo(String input, String... tokens) {
		InputTokenizer tokenizer = new InputTokenizer(getKernel(),
				getApp().getParserFunctions(), input);
		assertEquals(Arrays.asList(tokens), getTokens(tokenizer));
	}

	/**
	 *
	 * @return all the tokens input was split to.
	 */
	public List<String> getTokens(InputTokenizer tokenizer) {
		ArrayList<String> tokens = new ArrayList<>();
		while (tokenizer.hasToken()) {
			tokens.add(tokenizer.next());
		}

		return tokens;
	}
}
