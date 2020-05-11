package org.geogebra.common.kernel.commands;

import org.geogebra.common.BaseUnitTest;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class ProductParserTest extends BaseUnitTest {
	@Before
	public void setUp() throws Exception {
		ParserTest.setupCas();
	}

	@Test
	public void testPiRSquare() {
		add("r = 2");
		shouldReparseAs("pir^(2)", Unicode.PI_STRING + " r" + Unicode.SUPERSCRIPT_2);
	}

	@Test
	public void testABCD() {
		add("a=1");
		add("b=2");
		add("c=2");
		add("d=2");
		shouldReparseAs("abcd", "a b c d");
	}

	@Test
	public void testAvarb() {
		add("a=1");
		add("f(var)=?");
		add("b=2");
		shouldReparseAs("avarb", "a var b");
	}

	@Test
	public void testVarVar() {
		add("f(var)=?");
		shouldReparseAs("varvar", "var var");
	}

	@Test
	public void testPix() {
		shouldReparseAs("pix^(2)", Unicode.PI_STRING + " x" + Unicode.SUPERSCRIPT_2);
	}

	private void shouldReparseAs(String original, String parsed) {
		ParserTest.shouldReparseAs(getApp(), original, parsed);
	}
}
