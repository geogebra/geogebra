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
	public void testFunctionalVarVar() {
		add("f(var)=?");
		shouldReparseAs("varvar", "var var");
	}

	@Test
	public void testNFunctionalUV() {
		add("f(u, v)=?");
		shouldReparseAs("uv", "u v");
		shouldReparseAs("vu", "v u");
	}

	@Test
	public void testPir() {
		add("r=2");
		shouldReparseAs("pir^(2)", Unicode.PI_STRING + " r" + Unicode.SUPERSCRIPT_2);
	}

	@Test
	public void testXPlusBs() {
		add("b=1");
		shouldReparseAs("x+bb", "x + b b");
		shouldReparseAs("x+bbb", "x + b b b");
		shouldReparseAs("x+bbbb", "x + b b b b");
//		shouldReparseAs("x+bbbbbx", "x + b b b b b x");
	}

	@Test
	public void testABX() {
		add("a=1");
		add("b=1");
		shouldReparseAs("xab", "x a b");
		shouldReparseAs("x + ab", "x + a b");
		shouldReparseAs("xxxxxxxxxx", "x" + Unicode.SUPERSCRIPT_1 + Unicode.SUPERSCRIPT_0);
		shouldReparseAs("axxxxxxxxxx", "a x" + Unicode.SUPERSCRIPT_1 + Unicode.SUPERSCRIPT_0);
		shouldReparseAs("axaxaxaxax", "a x a x a x a x a x");
	}

	@Test
	public void testAkka() {
		add("a=1");
		add("k=1");
		shouldReparseAs("kk", "k k");
		shouldReparseAs("kkk", "k k k");
		shouldReparseAs("kkkk", "k k k k");
//		shouldReparseAs("akakak", "a k a k a k");
		shouldReparseAs("akka", "a k k a");
//		shouldReparseAs("aakkaa", "a a k k a a");
	}

	private void shouldReparseAs(String original, String parsed) {
		ParserTest.shouldReparseAs(getApp(), original, parsed);
	}
}
