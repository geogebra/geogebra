package org.geogebra.common.io;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.test.TestStringUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.Korean;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class EditorTypingTest {
	private static EditorChecker checker;

	/**
	 * Reset LaTeX factory
	 */
	@BeforeClass
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@Before
	public void setUp() {
		checker = new EditorChecker(AppCommonFactory.create());
	}

	@Test
	public void testEditorUnicode() {
		checker.checkEditorInsert(TestStringUtil.unicode("x/sqrt(x^2+4)"),
				"(x)/(sqrt(x^(2)+4))");
		checker.checkEditorInsert("x/(" + Unicode.EULER_STRING + "^x+1)",
				"(x)/(" + Unicode.EULER_STRING + "^(x)+1)");

		checker.checkEditorInsert("3*x", "3*x");
	}

	@Test
	public void testEditor() {
		checker.checkEditorInsert("sqrt(x/2)", "sqrt((x)/(2))");

		checker.checkEditorInsert("1+2+3-4", "1+2+3-4");
		checker.checkEditorInsert("12345", "12345");
		checker.checkEditorInsert("1/2/3/4", "(((1)/(2))/(3))/(4)");
		checker.checkEditorInsert("Segment[(1,2),(3,4)]", "Segment[(1,2),(3,4)]");
	}

	@Test
	public void insertNrootShouldMaintainArgumentsOrder() {
		checker.checkEditorInsert("nroot(x,3)", "nroot(x,3)");
	}

	@Test
	public void absShouldBePrefixedBySpace() {
		// typing second | starts another abs() clause
		checker.checkEditorInsert("3|x", "3 abs(x)");
		checker.checkEditorInsert("3 |x", "3 abs(x)");
		checker.checkEditorInsert("3*|x", "3*abs(x)");
		checker.checkEditorInsert("x|xx", "x abs(xx)");
		checker.checkEditorInsert("x |x x", "x abs(x x)");
		checker.checkEditorInsert("x*|x*x", "x*abs(x*x)");
		checker.checkEditorInsert("x sqrt(x)", "x sqrt(x)");
		checker.checkEditorInsert("x" + Unicode.SQUARE_ROOT + "x+1", "x sqrt(x+1)");
		checker.checkEditorInsert("ln|x+6", "ln abs(x+6)");
		checker.checkEditorInsert("ln|x+6", "ln abs(x+6)");
	}

	@Test
	public void testLnAbs() {
		checker.type("ln|x+6").checkGGBMath("ln(abs(x + 6))");
	}

	@Test
	public void testTrig1() {
		checker.type("sin(x)").checkGGBMath("sin(x)");
	}

	@Test
	public void testTrig2() {
		checker.type("sin(x)^2").checkGGBMath("sin" + Unicode.SUPERSCRIPT_2 + "(x)");
	}

	@Test
	public void testTrig3() {
		checker.type("sin(x)^3").checkGGBMath("sin" + Unicode.SUPERSCRIPT_3 + "(x)");
	}

	@Test
	public void testTrig4() {
		checker.type("sin(x)^123").checkGGBMath("sin" + Unicode.SUPERSCRIPT_1
				+ Unicode.SUPERSCRIPT_2	+ Unicode.SUPERSCRIPT_3 + "(x)");
	}

	@Test
	public void testTrig5() {
		checker.type("sin^2").typeKey(JavaKeyCodes.VK_RIGHT).type("(x)")
				.checkGGBMath("sin" + Unicode.SUPERSCRIPT_2 + "(x)");
	}

	@Test
	public void testTrig6() {
		checker.type("sin^-1").typeKey(JavaKeyCodes.VK_RIGHT).type("(x)")
				.checkGGBMath("sin" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(x)");
	}

	@Test
	public void testFloor() {
		checker.insert("2 floor(x)")
				.checkRaw("MathSequence[2,  , FnFLOOR[MathSequence[x]]]");
	}

	@Test
	public void testCeil() {
		checker.insert("2 ceil(x)")
				.checkRaw("MathSequence[2,  , FnCEIL[MathSequence[x]]]");
	}

	@Test
	public void testKorean() {
		checker.checkEditorInsert("\u3141", "\u3141");
		checker.checkEditorInsert("\u3141\u3157", "\uBAA8");
		checker.checkEditorInsert("\u3141\u3157\u3131", "\uBAA9");
		checker.checkEditorInsert("\u3141\u3157\u3131\u3145", "\uBAAB");

		checker.checkEditorInsert("\u3147\u314F\u3139\u314D\u314F", "\uC54C\uD30C");
		checker.checkEditorInsert("\u314A\u315C\u3139\u3131\u314F", "\uCD9C\uAC00");

		checker.checkEditorInsert("\u3142", "\u3142");
		checker.checkEditorInsert("\u3142\u315C", "\uBD80");
		checker.checkEditorInsert("\u3142\u315C\u3134", "\uBD84");
		checker.checkEditorInsert("\u3142\u315C\u3134\u314E", "\uBD86");

		checker.checkEditorInsert("\u3142\u315C\u3134\u314E\u3150", "\uBD84\uD574");
		checker.checkEditorInsert("\u3142\u315C\u3134\u314E\u3155", "\uBD84\uD600");
		checker.checkEditorInsert("\u3142\u315C\u3134\u314E\u315B", "\uBD84\uD6A8");

		checker.checkEditorInsert("\u314D\u3157\u314E", "\uD407");
		checker.checkEditorInsert("\u3141\u3163\u3142\u315C\u3134", "\uBBF8\uBD84");

		checker.checkEditorInsert("\u3147\u315F", "\uC704");

		checker.checkEditorInsert("\u3131\u314F\u3144", "\uAC12");

		checker.checkEditorInsert("\u314E\u314F\u3134\u3146\u314F\u3147",
				"\uD55C\uC30D");

		checker.checkEditorInsert("\u314E\u314F\u3145\u3145\u314F\u3147",
				"\uD56B\uC0C1");

		// small steps
		checker.checkEditorInsert("\u314E\u314F", "\uD558");
		checker.checkEditorInsert("\u314E\u314F\u3145", "\uD56B");
		checker.checkEditorInsert("\u314E\u314F\u3145\u3145", "\uD56B\u3145");
		checker.checkEditorInsert("\u314E\u314F\u3145\u3145\u314F", "\uD56B\uC0AC");

		checker.checkEditorInsert("\u3131\u3161", "\uADF8");
		checker.checkEditorInsert("\u3131\u3161\u3131", "\uADF9");
		checker.checkEditorInsert("\u3131\u3161\u3132", "\uADFA");
		checker.checkEditorInsert("\u3131\u3161\u3131\u3131\u314F", "\uADF9\uAC00");
		checker.checkEditorInsert("\u3131\u3161\u3131\u3131\u314F\u3142",
				"\uADF9\uAC11");
		checker.checkEditorInsert("\u3131\u3161\u3131\u3131\u314F\u3144",
				"\uADF9\uAC12");

		checker.checkEditorInsert("\u314E\u314F\u3134\u3146\u314F\u3147",
				"\uD55C\uC30D");

		checker.checkEditorInsert("\u314E\u314F\u3146\u314F\u3147", "\uD558\uC30D");

		checker.checkEditorInsert("\u3134\u3153\u313C\u3147\u3163", "\uB113\uC774");
		checker.checkEditorInsert("\u3147\u314F\u3136\u3137\u314F", "\uC54A\uB2E4");
		checker.checkEditorInsert("\u3131\u314F\u3144\u3147\u3161\u3134",
				"\uAC12\uC740");

		checker.checkEditorInsert("\u3131\u314F\u3144\u3145\u314F\u3134",
				"\uAC12\uC0B0");

		checker.checkEditorInsert(Korean.flattenKorean("\uB098"), "\uB098");
		checker.checkEditorInsert(Korean.flattenKorean("\uB108"), "\uB108");
		checker.checkEditorInsert(Korean.flattenKorean("\uC6B0\uB9AC"), "\uC6B0\uB9AC");
		checker.checkEditorInsert(Korean.flattenKorean("\uBBF8\uBD84"), "\uBBF8\uBD84");
		checker.checkEditorInsert(Korean.flattenKorean("\uBCA1\uD130"), "\uBCA1\uD130");
		checker.checkEditorInsert(Korean.flattenKorean("\uC0C1\uC218"), "\uC0C1\uC218");
		checker.checkEditorInsert(Korean.flattenKorean("\uB2ED\uBA39\uC5B4"),
				"\uB2ED\uBA39\uC5B4");
		checker.checkEditorInsert(Korean.flattenKorean("\uC6EC\uC77C"), "\uC6EC\uC77C");
		checker.checkEditorInsert(Korean.flattenKorean("\uC801\uBD84"), "\uC801\uBD84");
		checker.checkEditorInsert(Korean.flattenKorean("\uC288\uD37C\uB9E8"),
				"\uC288\uD37C\uB9E8");
		checker.checkEditorInsert(Korean.flattenKorean("\u3138"), "\u1104");
		checker.checkEditorInsert(Korean.flattenKorean("\uC778\uD14C\uADF8\uB784"),
				"\uC778\uD14C\uADF8\uB784");
		checker.checkEditorInsert(Korean.flattenKorean("\u3137"), "\u1103");
		checker.checkEditorInsert(Korean.flattenKorean("\u3131"), "\u1100");
		checker.checkEditorInsert(Korean.flattenKorean("\u3134"), "\u1102");
		checker.checkEditorInsert(Korean.flattenKorean("\uC8FC\uC778\uC7A5"),
				"\uC8FC\uC778\uC7A5");
		checker.checkEditorInsert(
				Korean.flattenKorean("\uC774\uC81C\uC880\uC790\uC790"),
				"\uC774\uC81C\uC880\uC790\uC790");
		checker.checkEditorInsert(
				Korean.flattenKorean("\uC544\uBAA8\uB974\uACA0\uB2E4"),
				"\uC544\uBAA8\uB974\uACA0\uB2E4");

		checker.checkEditorInsert("\u3146\u1161\u11BC", "\uC30D");
		checker.checkEditorInsert("\u110A\u1161\u11BC", "\uC30D");

		checker.checkEditorInsert("\u3142\u315C", "\uBD80");
		checker.checkEditorInsert("\u3142\u315E", "\uBDB8");
		checker.checkEditorInsert("\u3142\u315E\u3139", "\uBDC0");
		checker.checkEditorInsert("\u3142\u315E\u313A", "\uBDC1");

		// testEditor("\u3132", "\u1101");
		checker.checkEditorInsert("\u3132\u314F", "\uAE4C");

		checker.checkEditorInsert("\u3131\u3157\u3142\u3131\u3161\u3134",
				"\uACF1\uADFC");
		checker.checkEditorInsert("\u3147\u3163\u3142\u3139\u3155\u3131",
				"\uC785\uB825");

		checker.checkEditorInsert("\u3147\u3157\u314F\u3134\u3139\u315B",
				"\uC644\uB8CC");
		checker.checkEditorInsert("\u3131\u3157\u3142\u314E\u314F\u3131\u3163",
				"\uACF1\uD558\uAE30");

		// some middle (vowel) characters need doubling (no other way to enter
		// them)
		// eg \u315c \u3153 = \u116f
		checker.checkEditorInsert("\u3147\u315c", "\uc6b0");
		checker.checkEditorInsert("\u3147\u315c\u3153", "\uc6cc");
		checker.checkEditorInsert("\u3147\u315c\u3153\u3134", "\uc6d0");
		checker.checkEditorInsert("\u3147\u3157\u314F", "\uC640");
		// ... and same for tail
		checker.checkEditorInsert("\u3137\u314F\u3139\u3131", "\uB2ED");
	}

	@Test
	public void testInverseTrigEditor() {
		checker.type("cos" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(1)/2").checkRaw(
				"MathSequence[FnFRAC[MathSequence[FnAPPLY[MathSequence[c, o, s, "
						+ Unicode.SUPERSCRIPT_MINUS + ", "
						+ Unicode.SUPERSCRIPT_1
						+ "], MathSequence[1]]], MathSequence[2]]]");
	}

	@Test
	public void testLogBase() {
		checker.type("log_2").typeKey(JavaKeyCodes.VK_RIGHT).type("(4)").checkRaw(
				"MathSequence[FnLOG[MathSequence[2], MathSequence[4]]]");
	}

	@Test
	public void testSlash1() {
		checker.type("/1").typeKey(JavaKeyCodes.VK_RIGHT).type("2")
				.checkAsciiMath("(1)/(2)");
	}

	@Test
	public void testSlash2() {
		checker.type("1/2").checkAsciiMath("(1)/(2)");
	}

	@Test
	public void testSlash3() {
		checker.type("12").typeKey(JavaKeyCodes.VK_LEFT).type("/")
				.checkAsciiMath("(1)/()2");
	}

	@Test
	public void testDivision1() {
		checker.type(Unicode.DIVIDE + "1").typeKey(JavaKeyCodes.VK_RIGHT).type("2")
				.checkAsciiMath("1/2");
	}

	@Test
	public void testDivision2() {
		checker.type("1" + Unicode.DIVIDE + "2").checkAsciiMath("1/2");
	}

	@Test
	public void testDivision3() {
		checker.type("12").typeKey(JavaKeyCodes.VK_LEFT).type(Unicode.DIVIDE + "")
			.checkAsciiMath("1/2");
}

	@Test
	public void testBracketsAroundFunction() {
		checker.type("ln(x").typeKey(JavaKeyCodes.VK_LEFT)
				.typeKey(JavaKeyCodes.VK_LEFT).typeKey(JavaKeyCodes.VK_LEFT)
				.typeKey(JavaKeyCodes.VK_LEFT).type("(")
				.checkAsciiMath("(ln(x))");
	}

	@Test
	public void testBracketsAfterEquals() {
		checker.type("f(p").typeKey(JavaKeyCodes.VK_RIGHT)
				.type("=ln(p*2.72").typeKey(JavaKeyCodes.VK_RIGHT)
				.type("+3)").checkAsciiMath("f(p)=(ln(p*2.72)+3)");
	}

	@Test
	public void testBackspace() {
		checker.type("8" + Unicode.DIVIDE).typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.type(Unicode.DIVIDE + "2")
				.checkAsciiMath("8/2");
	}

	@Test
	public void spaceAfterTrigShouldAddBrackets() {
		checker.setFormatConverter(new SyntaxAdapterImpl(AppCommonFactory.create().getKernel()));
		checker.type("sin 9x").checkAsciiMath("sin(9x)");
	}

	@Test
	public void testBackspaceWithBrackets() {
		checker.type("8/").typeKey(JavaKeyCodes.VK_BACK_SPACE).type("/2")
				.checkAsciiMath("(8)/(2)");
	}

	@Test
	public void typingPiShouldProduceUnicode() {
		MetaModel model = new MetaModel();
		model.enableSubstitutions();
		EditorChecker inputBoxChecker = new EditorChecker(AppCommonFactory.create(), model);
		inputBoxChecker.type("sin(pix)").checkAsciiMath("sin(" + Unicode.PI_STRING + "x)");
	}

	@Test
	public void shouldRecognizeAbsAsSuffix() {
		checker.type("xabs(x").checkGGBMath("x abs(x)");
	}

	@Test
	public void shouldRecognizeSqrtAsSuffix() {
		checker.type("xsqrt(x").checkGGBMath("x sqrt(x)");
	}

	@Test
	public void shouldRecognizeSqrtAsSuffixWithConst() {
		// for constant no multiplication space added => we have to check the raw string
		checker.type("8sqrt(x").checkRaw("MathSequence[8, FnSQRT[MathSequence[x]]]");
	}

	@Test
	public void testTypingPiWithComplex() {
		MetaModel model = new MetaModel();
		model.enableSubstitutions();
		EditorChecker inputBoxChecker = new EditorChecker(AppCommonFactory.create(), model);
		inputBoxChecker.type("3pi + 4i").checkAsciiMath("3" + Unicode.PI_STRING + " + 4i");
	}

	@Test
	public void testTypingPiiWithComplex() {
		MetaModel model = new MetaModel();
		model.enableSubstitutions();
		EditorChecker inputBoxChecker = new EditorChecker(AppCommonFactory.create(), model);
		inputBoxChecker.type("3pii").checkAsciiMath("3" + Unicode.PI_STRING + "i");
	}
}
