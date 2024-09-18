package org.geogebra.common.io;

import java.text.Normalizer;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.test.TestStringUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.event.KeyEvent;
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
				"((x)/(sqrt(x^(2)+4)))");
		checker.checkEditorInsert("x/(" + Unicode.EULER_STRING + "^x+1)",
				"((x)/(" + Unicode.EULER_STRING + "^(x)+1))");

		checker.checkEditorInsert("3*x", "3*x");
	}

	@Test
	public void testEditor() {
		checker.checkEditorInsert("sqrt(x/2)", "sqrt(((x)/(2)))");

		checker.checkEditorInsert("1+2+3-4", "1+2+3-4");
		checker.checkEditorInsert("12345", "12345");
		checker.checkEditorInsert("1/2/3/4", "((((((1)/(2)))/(3)))/(4))");
		checker.checkEditorInsert("Segment[(1,2),(3,4)]", "Segment[(1,2),(3,4)]");
	}

	@Test
	public void insertNrootShouldMaintainArgumentsOrder() {
		checker.checkEditorInsert("nroot(x,3)", "nroot(x,3)");
	}

	@Test
	public void unicodeShouldMerge() {
		checker.type("\uD835\uDC65"
						+ "\uD83D\uDD96\uD83C\uDFFD"
						+ "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66")
				.checkLength(3);
	}

	@Test
	public void absShouldBePrefixedBySpace() {
		// typing second | starts another abs() clause
		checker.type("3|x").checkAsciiMath("3 abs(x)");
		checker.type("3 |x").checkAsciiMath("3 abs(x)");
		checker.type("3*|x").checkAsciiMath("3*abs(x)");
		checker.type("x|xx").checkAsciiMath("x abs(xx)");
		checker.type("x |x x").checkAsciiMath("x abs(x x)");
		checker.type("x*|x*x").checkAsciiMath("x*abs(x*x)");
		checker.type("x sqrt(x)").checkAsciiMath("x sqrt(x)");
		checker.type("x" + Unicode.SQUARE_ROOT + "x+1").checkAsciiMath("x sqrt(x+1)");
		checker.type("ln|x+6").checkAsciiMath("ln abs(x+6)");
		checker.type("ln|x+6").checkAsciiMath("ln abs(x+6)");
	}

	@Test
	public void emptyAbsShouldBecomeOr() {
		checker.type("true||false").checkAsciiMath("true" + Unicode.OR + "false");
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
				+ Unicode.SUPERSCRIPT_2 + Unicode.SUPERSCRIPT_3 + "(x)");
	}

	@Test
	public void testTrig5() {
		checker.type("sin^2").right(1).type("(x)")
				.checkGGBMath("sin" + Unicode.SUPERSCRIPT_2 + "(x)");
	}

	@Test
	public void testTrig6() {
		checker.type("sin^-1").right(1).type("(x)")
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

		// tests from https://github.com/clee704/hangul-js/blob/master/spec/hangul-dubeol.spec.js (MIT)
		checker.checkEditorInsert(convertKoreanTyping("gksrmfdl dks Tjwudy!"),
				"\uD55C\uAE00\uC774 \uC548 \uC368\uC838\uC694!");
		checker.checkEditorInsert(
				convertKoreanTyping("dkswdk dlTwl dksgdmaus wjdtlsdmf dlfgdmf tneh dlTdjdy."),
				"\uC549\uC544 \uC788\uC9C0 \uC54A\uC73C\uBA74 \uC815\uC2E0\uC744 "
						+ "\uC783\uC744 \uC218\uB3C4 \uC788\uC5B4\uC694.");
		checker.checkEditorInsert(convertKoreanTyping("10dnjf 7dlf dhgn 2tl rhkdhl"),
				"10\uC6D4 7\uC77C \uC624\uD6C4 2\uC2DC \uACFC\uC678");
		checker.checkEditorInsert(convertKoreanTyping("rhkfqrnlswwlQkqdlswlrrtdddkzzzl"),
				"\uAD07\uADC5\uC9C0\uBE71\uC778\uC9C1\u3133\u3147\u3147\uC55C\u314B\uD0A4");
		checker.checkEditorInsert(convertKoreanTyping("RhkRRnswEnpsgdnprtkt lhkd kdl s"),
				"\uAF4A\uAFBD\uB6DA\uC6E9\uC0BF \u3163\u3158\u3147 \u314F\uC774 \u3134");
		checker.checkEditorInsert(
				convertKoreanTyping("quf qkdqjqdms ek Tjqhkeh dksehldy nn wpqkf ehdhkwntpdy"),
				"\uBCC4 \uBC29\uBC95\uC740 \uB2E4 \uC368\uBD10\uB3C4 \uC548\uB418\uC694 "
						+ "\u315C\u315C \uC81C\uBC1C \uB3C4\uC640\uC8FC\uC138\uC694");
		checker.checkEditorInsert(convertKoreanTyping("wltlrdlsdptj goqhfksmsrj ekgoeh dksTjwlrh"),
				"\uC9C0\uC2DD\uC778\uC5D0\uC11C \uD574\uBCF4\uB77C\uB294\uAC70 "
						+ "\uB2E4\uD574\uB3C4 \uC548\uC368\uC9C0\uACE0");

		// https://github.com/e-/Hangul.js/blob/master/test/index.js (MIT)
		checker.checkEditorInsert("\u3131\u314F\u3134\u314F\u3137\u314F", "\uAC00\uB098\uB2E4");

		checker.checkEditorInsert("\u3131\u314F\u3134\u314F\u3137\u314F", "\uAC00\uB098\uB2E4");
		checker.checkEditorInsert("\u3142\u3163\u314E\u3150\u3147", "\uBE44\uD589");
		checker.checkEditorInsert("\u3146\u3161\u3139\u3137\u314F", "\uC4F8\uB2E4");
		checker.checkEditorInsert("\u3147\u3161\u3163\u3145\u314F", "\uC758\uC0AC");
		checker.checkEditorInsert("\u3149\u314F\u3139\u3142\u3147\u3161\u3134", "\uC9E7\uC740");
		checker.checkEditorInsert("\u3137\u314F\u3139\u3131\u3131\u3157\u3131\u3163",
				"\uB2ED\uACE0\uAE30");
		// original, wrong
		//checker.checkEditorInsertReverse("\uC63D\u314F", "\u3147\u3157\u314C\u314F");
		// fixed
		checker.checkEditorInsert("\u3147\u3157\u314C\u314F", "\uC624\uD0C0");
		checker.checkEditorInsert(
				"AB\u3145\u314F\u3139\u3131e$@#24sdf\u3132\u3163\u3139\u314B\u314F\u314B"
						+ "\u314B\u314B\u314B\u314B",
				"AB\uC0B5e$@#24sdf\uB084\uCE8C\u314B\u314B\u314B\u314B");
		checker.checkEditorInsert(
				"\u3142\u315C\u3154\u3139\u3131\u3131\u315C\u3154\u3139\u3139\u3161\u3163"
						+ "\u314D\u3149\u3161\u3163\u3139\u3142\u314C\u315C\u3163\u3139\u3142",
				"\uBDC1\uADAC\uB9AA\uCBFB\uD28B");
		checker.checkEditorInsert("\u3131\u3145", "\u3133");
		checker.checkEditorInsert("\u3157\u3150", "\u3159");
		checker.checkEditorInsert("\u3131\u314F\u3134\u314F\u3137\u314F", "\uAC00\uB098\uB2E4");
		checker.checkEditorInsert("\u3142\u3163\u314E\u3150\u3147", "\uBE44\uD589");
		checker.checkEditorInsert("\u3146\u3161\u3139\u3137\u314F", "\uC4F8\uB2E4");
		checker.checkEditorInsert("\u3147\u3161\u3163\u3145\u314F", "\uC758\uC0AC");
		checker.checkEditorInsert("\u3149\u314F\u3139\u3142\u3147\u3161\u3134", "\uC9E7\uC740");
		checker.checkEditorInsert("\u3137\u314F\u3139\u3131\u3131\u3157\u3131\u3163",
				"\uB2ED\uACE0\uAE30");
		checker.checkEditorInsert("\u3147\u3157\u314C\u314F", "\uC624\uD0C0");
		checker.checkEditorInsert(
				"AB\u3145\u314F\u3139\u3131e$@#24sdf\u3132\u3163\u3139\u314B\u314F\u314B"
						+ "\u314B\u314B\u314B\u314B",
				"AB\uC0B5e$@#24sdf\uB084\uCE8C\u314B\u314B\u314B\u314B");
		checker.checkEditorInsert(
				"\u3142\u315C\u3154\u3139\u3131\u3131\u315C\u3154\u3139\u3139\u3161\u3163"
						+ "\u314D\u3149\u3161\u3163\u3139\u3142\u314C\u315C\u3163\u3139\u3142",
				"\uBDC1\uADAC\uB9AA\uCBFB\uD28B");
		checker.checkEditorInsert("\u3131\u3145", "\u3133");
		checker.checkEditorInsert("\u3157\u3150", "\u3159");
		checker.checkEditorInsert("\u3148\u3145\u314F", "\u3148\uC0AC");
		checker.checkEditorInsert("\u3131\u3145\u3131\u3145", "\u3133\u3133");
		checker.checkEditorInsert("\u3157\u3150\u3157\u3150", "\u3159\u3159");
		checker.checkEditorInsert("\u3148\u3157\u3157\u3150", "\uC870\u3159");
		checker.checkEditorInsert("\u3163\u3157\u3150", "\u3163\u3159");
		checker.checkEditorInsert("\u3143\u3149\u314F\u3138", "\u3143\uC9DC\u3138");
		checker.checkEditorInsert("\u3152\u3157\u3152", "\u3152\u3157\u3152");
		checker.checkEditorInsert("\u3143\u315E\u3139\u3131\u3145", "\uC00D\u3145");
		checker.checkEditorInsert("\u3143\u315E\u3139\u3131\u314F", "\uC00C\uAC00");
		checker.checkEditorInsert(
				"\u3143\u315E\u3139\u3131\u315E\u3139\u3131\u315E\u3139\u3131\u3142",
				"\uC00C\uADAC\uADAD\u3142");

		String in = "\u3147\u315E\u11B9"; // \u3147\u315E\u11B9
		String out = Normalizer.normalize(in, Normalizer.Form.NFKC);
		checker.checkEditorInsert(in, out);

		checker.checkEditorInsert(
				"\u3147\u314F\u3134\u3134\u3155\u3147\u314E\u314F\u3145\u3154\u3147\u315B",
				"\uC548\uB155\uD558\uC138\uC694");

		// doubled vowel characters
		checker.checkEditorInsert("\u3131\u3145", "\u3133");
		checker.checkEditorInsert("\u3134\u3148", "\u3135");
		checker.checkEditorInsert("\u3134\u314E", "\u3136");
		checker.checkEditorInsert("\u3139\u3131", "\u313A");
		checker.checkEditorInsert("\u3139\u3142", "\u313C");
		checker.checkEditorInsert("\u3139\u3145", "\u313D");
		checker.checkEditorInsert("\u3139\u314C", "\u313E");
		checker.checkEditorInsert("\u3139\u314D", "\u313F");
		checker.checkEditorInsert("\u3139\u314E", "\u3140");
		checker.checkEditorInsert("\u3142\u3145", "\u3144");

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

	private String convertKoreanTyping(String s) {
		String ret = s;

		ret = ret.replace('a', '\u3141');
		ret = ret.replace('b', '\u3160');
		ret = ret.replace('c', '\u314A');
		ret = ret.replace('d', '\u3147');
		ret = ret.replace('e', '\u3137');
		ret = ret.replace('f', '\u3139');
		ret = ret.replace('g', '\u314E');
		ret = ret.replace('h', '\u3157');
		ret = ret.replace('i', '\u3151');
		ret = ret.replace('j', '\u3153');
		ret = ret.replace('k', '\u314F');
		ret = ret.replace('l', '\u3163');
		ret = ret.replace('m', '\u3161');
		ret = ret.replace('n', '\u315C');
		ret = ret.replace('o', '\u3150');
		ret = ret.replace('p', '\u3154');
		ret = ret.replace('q', '\u3142');
		ret = ret.replace('r', '\u3131');
		ret = ret.replace('s', '\u3134');
		ret = ret.replace('t', '\u3145');
		ret = ret.replace('u', '\u3155');
		ret = ret.replace('v', '\u314D');
		ret = ret.replace('w', '\u3148');
		ret = ret.replace('x', '\u314C');
		ret = ret.replace('y', '\u315B');
		ret = ret.replace('z', '\u314B');

		ret = ret.replace('A', '\u3141');
		ret = ret.replace('B', '\u3160');
		ret = ret.replace('C', '\u314A');
		ret = ret.replace('D', '\u3147');
		ret = ret.replace('E', '\u3138');
		ret = ret.replace('F', '\u3139');
		ret = ret.replace('G', '\u314E');
		ret = ret.replace('H', '\u3157');
		ret = ret.replace('I', '\u3151');
		ret = ret.replace('J', '\u3153');
		ret = ret.replace('K', '\u314F');
		ret = ret.replace('L', '\u3163');
		ret = ret.replace('M', '\u3161');
		ret = ret.replace('N', '\u315C');
		ret = ret.replace('O', '\u3152');
		ret = ret.replace('P', '\u3156');
		ret = ret.replace('Q', '\u3143');
		ret = ret.replace('R', '\u3132');
		ret = ret.replace('S', '\u3134');
		ret = ret.replace('T', '\u3146');
		ret = ret.replace('U', '\u3155');
		ret = ret.replace('V', '\u314D');
		ret = ret.replace('W', '\u3149');
		ret = ret.replace('X', '\u314C');
		ret = ret.replace('Y', '\u315B');
		return ret.replace('Z', '\u314B');
	}

	@Test
	public void testInverseTrigEditor() {
		checker.type("cos" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(1)/2").checkRaw(
				"MathSequence[FnFRAC[MathSequence[FnAPPLY[MathSequence[c, o, s, "
						+ "FnSUPERSCRIPT[MathSequence[-, 1]]], MathSequence[1]]], "
						+ "MathSequence[2]]]");
	}

	@Test
	public void testLogBase() {
		checker.type("log_2").right(1).type("(4)").checkRaw(
				"MathSequence[FnLOG[MathSequence[2], MathSequence[4]]]");
	}

	@Test
	public void testSlash1() {
		checker.type("/1").right(1).type("2")
				.checkAsciiMath("((1)/(2))");
	}

	@Test
	public void testSlash2() {
		checker.type("1/2").checkAsciiMath("((1)/(2))");
	}

	@Test
	public void testSlash3() {
		checker.type("12").left(1).type("/")
				.checkAsciiMath("((1)/())2");
	}

	@Test
	public void testDivision1() {
		checker.type(Unicode.DIVIDE + "1").right(1).type("2")
				.checkAsciiMath("((1)/(2))");
	}

	@Test
	public void testDivision2() {
		checker.type("1" + Unicode.DIVIDE + "2").checkAsciiMath("((1)/(2))");
	}

	@Test
	public void testDivision3() {
		checker.type("12").left(1).type(Unicode.DIVIDE + "")
				.checkAsciiMath("((1)/())2");
	}

	@Test
	public void testBracketsAroundFunction() {
		checker.type("ln(x").left(4).type("(")
				.checkAsciiMath("(ln(x))");
	}

	@Test
	public void testBracketsAfterEquals() {
		checker.type("f(p").right(1)
				.type("=ln(p*2.72").right(1)
				.type("+3)").checkAsciiMath("f(p)=(ln(p*2.72)+3)");
	}

	@Test
	public void testBackspace() {
		checker.type("8" + Unicode.DIVIDE).typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.type(Unicode.DIVIDE + "2")
				.checkAsciiMath("((8)/(2))");
	}

	@Test
	public void testBackspaceAfterBrackets() {
		checker.type("x(x+1)").typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkAsciiMath("x(x+)");
	}

	@Test
	public void testBackspaceAfterFraction() {
		checker.type("12/34").typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkAsciiMath("((12)/(3))");
	}

	@Test
	public void spaceAfterFunctionShouldAddBrackets() {
		checker.setFormatConverter(new SyntaxAdapterImpl(AppCommonFactory.create().getKernel()));
		checker.type("sin 9x").checkAsciiMath("sin(9x)");
	}

	@Test
	public void characterAfterFunctionShouldAddBrackets() {
		AppCommon app = AppCommonFactory.create();

		MetaModel model = new MetaModel();
		model.setForceBracketAfterFunction(true);
		EditorChecker inputBoxChecker = new EditorChecker(app, model);
		inputBoxChecker.setFormatConverter(new SyntaxAdapterImpl(app.getKernel()));

		inputBoxChecker.type("sin9x").checkAsciiMath("sin(9x)");
		inputBoxChecker.fromParser("");

		inputBoxChecker.type("sinhb").checkAsciiMath("sinh(b)");
		inputBoxChecker.fromParser("");

		inputBoxChecker.type("xsinxcosx").checkAsciiMath("xsin(xcos(x))");
		inputBoxChecker.fromParser("");

		inputBoxChecker.type("sin^2").right(1).type("a")
				.checkAsciiMath("sin^(2)(a)");
		inputBoxChecker.fromParser("");

		inputBoxChecker.type("log_3").right(1).type("x")
				.checkAsciiMath("log(3,x)");
	}

	@Test
	public void testBackspaceWithBrackets() {
		checker.type("8/").typeKey(JavaKeyCodes.VK_BACK_SPACE).type("/2")
				.checkAsciiMath("((8)/(2))");
	}

	@Test
	public void typingPiShouldProduceUnicodeInInputBox() {
		MetaModel model = new MetaModel();
		model.enableSubstitutions();
		EditorChecker inputBoxChecker = new EditorChecker(AppCommonFactory.create(), model);
		inputBoxChecker.type("sin(pix)").checkAsciiMath("sin(" + Unicode.PI_STRING + "x)");
	}

	@Test
	public void typingEpsilonShouldProduceUnicodeInInputBox() {
		MetaModel model = new MetaModel();
		model.enableSubstitutions();
		EditorChecker inputBoxChecker = new EditorChecker(AppCommonFactory.create(), model);
		inputBoxChecker.type("1+epsilon").checkAsciiMath("1+" + Unicode.epsilon);
	}

	@Test
	public void typingEpsilonShouldNotProduceUnicodeByDefault() {
		MetaModel model = new MetaModel();
		EditorChecker inputBoxChecker = new EditorChecker(AppCommonFactory.create(), model);
		inputBoxChecker.type("1+epsilon").checkAsciiMath("1+epsilon");
	}

	@Test
	public void typingOperatorsShouldProduceUnicode() {
		MetaModel model = new MetaModel();
		model.enableSubstitutions();
		EditorChecker inputBoxChecker = new EditorChecker(AppCommonFactory.create(), model);
		inputBoxChecker.type("x<=y").checkAsciiMath("x" + Unicode.LESS_EQUAL + "y");
		inputBoxChecker.type("x&&y").checkAsciiMath("x" + Unicode.AND + "y");
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
	public void shouldRecognizeDelta() {
		checker.type("Δy(1+y)").checkGGBMath("Δy(1 + y)");
	}

	@Test
	public void shouldRecognizeDeltaAtEnd() {
		checker.type("yΔ(1+y)").checkGGBMath("yΔ(1 + y)");
	}

	@Test
	public void gammaShouldBeRecognizedAsFunction() {
		checker.add(Unicode.Gamma + "(x)=gamma(x)");
		checker.type(Unicode.Gamma + "(1+y)").checkGGBMath(Unicode.Gamma + "(1 + y)");
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

	@Test
	public void testBracketsForSelection() {
		checker.type("x^2").right(1).type("+1")
				.setModifiers(KeyEvent.SHIFT_MASK)
				.left(5)
				.setModifiers(0).type("(").checkAsciiMath("(x^(2)+1)");
	}

	@Test
	public void testBracketsForSelectionSin() {
		checker.type("sinx^2").right(1).type("+1")
				.setModifiers(KeyEvent.SHIFT_MASK)
				.left(5)
				.setModifiers(0).type("(").checkAsciiMath("sin(x^(2)+1)");
	}

	@Test
	public void testBracketsForSelectionSingleChar() {
		checker.type("x^2").right(1).type("+1")
				.setModifiers(KeyEvent.SHIFT_MASK)
				.left(1)
				.setModifiers(0).type("(").checkAsciiMath("x^(2)+(1)");
	}

	@Test
	public void testBracketsForSelectionAfterScript() {
		checker.type("x^2").right(1).type("+1")
				.setModifiers(KeyEvent.SHIFT_MASK)
				.left(2)
				.setModifiers(0).type("(").checkAsciiMath("x^(2)(+1)");
	}

	@Test
	public void testCommaInPointEditorWithSelection() {
		checker.insert("(123,456)").protect()
				.left(42).setModifiers(KeyEvent.SHIFT_MASK).right(42) // select as far as possible
				.type("7")
				.checkAsciiMath("(7,456)");
	}

	@Test
	public void testCommaInPointEditor() {
		checker.insert("(2,1)").protect().left(42) // go to the left of protected editor
				.type("3").right(1) // cursor in front of comma
				.type(",4")
				.checkAsciiMath("(32,41)");
	}

	@Test
	public void testPipeInPointEditor() {
		checker.setAllowAbs(false);
		checker.insert("(2,1)").protect().left(42) // go to the left of protected editor
				.type("3").right(1) // cursor in front of comma
				.type("|4")
				.checkAsciiMath("(32,41)");
	}

	@Test
	public void testPipeInPointEditorAustrian() {
		checker.setAllowAbs(false);
		checker.insert("(2" + Unicode.verticalLine + "1)").protect()
				.left(42) // go to the left of protected editor
				.type("3").right(1) // cursor in front of comma
				.type("|4")
				.checkAsciiMath("(32" + Unicode.verticalLine + "41)");
	}

	@Test
	public void testCommaInMatrixEditor() {
		checker.insert("{{1,2},{3,4}}").protect().left(42) // go to the left of protected editor
				.type(",").type(",")
				.checkAsciiMath("{{1,2},{3,4}}");
	}

	@Test
	public void testSqrtInPointEditor() {
		checker.setFormatConverter(new SyntaxAdapterImpl(AppCommonFactory.create().getKernel()));
		checker.setForceBracketsAfterFunction();
		checker.insert("(2,1,0)").protect()
				.left(5)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.type("sqrt9")
				.right(3)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.type("sin3")
				.checkAsciiMath("(sqrt(9),sin(3),0)");
	}

	@Test
	public void testBracketsInPointEditor() {
		checker.insert("(2,3,3+4)")
				.left(8)
				.type("(")
				.checkAsciiMath("((2),3,3+4)");

		checker.insert("(2,3,3+4)")
				.left(7)
				.type(")")
				.checkAsciiMath("(2),3,3+4");

		checker.insert("(2,3,3+4)").protect()
				.left(7)
				.type(")")
				.checkAsciiMath("((2),3,3+4)");

		checker.insert("(2,3,3+4)")
				.left(6)
				.type("(")
				.checkAsciiMath("(2,(3),3+4)");

		checker.insert("(2,3,3+4)")
				.left(5)
				.type(")")
				.checkAsciiMath("(2,3),3+4");

		checker.insert("(2,3,3+4)").protect()
				.left(5)
				.type(")")
				.checkAsciiMath("(2,(3),3+4)");

		checker.insert("(2,3,3+4)")
				.left(4)
				.type("(")
				.checkAsciiMath("(2,3,(3+4))");

		checker.insert("(2,3,3+4)")
				.left(1)
				.type(")")
				.checkAsciiMath("(2,3,3+4)");

		checker.insert("(2,3,3+4)").protect()
				.left(1)
				.type(")")
				.checkAsciiMath("(2,3,(3+4))");

		checker.insert("(2,3,3+4)")
				.left(8)
				.type("[")
				.checkAsciiMath("([2],3,3+4)");

		checker.insert("(2,3,3+4)")
				.left(5)
				.type("]")
				.checkAsciiMath("(2,[3],3+4)");
	}

	@Test
	public void testEditorBackspace() {
		checker.type("ab cd(")
				.left(3)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkAsciiMath("abcd()");

		checker.type("a b(")
				.left(3)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkAsciiMath("ab()");

		checker.type("1 + N Solve(")
				.left(6)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkAsciiMath("1 + NSolve()");
	}

	@Test
	public void testBackspaceLeavesIndexUnchanged() {
		checker.type("x^3/2")
				.left(5)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.type("y")
				.checkAsciiMath("y^(((3)/(2)))");
	}

	@Test
	public void testBackspaceLeavesFraction() {
		checker.type("1-1/4")
				.left(4)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.type("+")
				.checkAsciiMath("1+((1)/(4))");
	}

	@Test
	public void testBackspaceLeavesPower() {
		checker.type("12^34")
				.left(3)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkAsciiMath("1^(34)");
	}

	@Test
	public void testBackspaceLeavesPowerOfLetters() {
		checker.type("e^t")
				.left(2)
				.type("-")
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkAsciiMath("e^(t)");

		checker.type("ee^t")
				.left(2)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkAsciiMath("e^(t)");
	}

	@Test
	public void testBackspaceLeavesUnderscoreWithLetters() {
		checker.type("e_t")
				.left(2)
				.type("-")
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkAsciiMath("e_{t}");

		checker.type("ee_t")
				.left(2)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkAsciiMath("e_{t}");
	}

	@Test
	public void testBackspaceLeavesSqrt() {
		checker.type("12-sqrt(45")
				.left(3)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.type("+")
				.checkAsciiMath("12+sqrt(45)");
	}

	@Test
	public void noCommasPastedInProtectedField() {
		checker.insert("(2,3,3+4)").protect()
				.left(3)
				.insert("5,6")
				.checkAsciiMath("(2,3,356+4)");
	}

	@Test
	public void noCurlyBracesPastedInProtectedField() {
		checker.insert("(2,3,3+4)").protect()
				.left(3)
				.insert("{}")
				.checkAsciiMath("(2,3,3+4)");
	}

	@Test
	public void closeBracketShouldMoveCharactersOut() {
		checker.insert("123456789")
				.left(6)
				.type("(")
				.right(2)
				.type(")")
				.checkAsciiMath("123(45)6789");
	}

	@Test
	public void noMathFunctionFuseForFractions() {
		checker.type("x+π/2")
				.left(4)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.type("-")
				.checkAsciiMath("x-((π)/(2))");
	}

	@Test
	public void cursorInNoScript() {
		checker.type("x + 1/2").checkCursorNotInScript();
	}

	@Test
	public void cursorInNoScriptXSquared() {
		checker.type("x^2").left(2).checkCursorNotInScript();
	}

	@Test
	public void cursorInSuperscript() {
		checker.type("x^2^345").checkCursorInScript();
	}

	@Test
	public void cursorInSubscript() {
		checker.type("x_2").checkCursorInScript();
	}

	@Test
	public void cursorInNoSubscript() {
		checker.type("x_2").left(2).checkCursorNotInScript();
	}

	@Test
	public void collapseSelectionOnArrowLeft() {
		checker.insert("1+2+3+4").select(3, 5).left(1).type("x")
				.checkAsciiMath("1+2x+3+4");
	}

	@Test
	public void collapseSelectionOnArrowRight() {
		checker.insert("1+2+3+4").select(3, 5).right(1).type("x")
				.checkAsciiMath("1+2+3+x4");
	}

	@Test
	public void shouldNotSerializeEmptyIntPartOfMixedNumber() {
		checker.setModifiers(KeyEvent.CTRL_MASK).typeKey(JavaKeyCodes.VK_M).setModifiers(0)
				.right(1).type("2").right(1).type("3")
				.checkAsciiMath("((2)/(3))");
		checker.type("1+")
				.setModifiers(KeyEvent.CTRL_MASK).typeKey(JavaKeyCodes.VK_M).setModifiers(0)
				.right(1).type("2").right(1).type("3")
				.checkGGBMath("1 + 2 / 3");
	}

	@Test
	public void shouldNotSerializeInvalidIntPartOfMixedNumber() {
		checker.setModifiers(KeyEvent.CTRL_MASK).typeKey(JavaKeyCodes.VK_M).setModifiers(0)
				.type("1+")
				.right(1).type("2").right(1).type("3")
				.checkGGBMath("1 + 2 / 3");
	}

	@Test
	public void shouldSerializeValidMixedNumber() {
		checker.setModifiers(KeyEvent.CTRL_MASK).typeKey(JavaKeyCodes.VK_M).setModifiers(0)
				.type("1")
				.right(1).type("2").right(1).type("3")
				.checkGGBMath("1" + Unicode.INVISIBLE_PLUS + "2 / 3");
		checker.type("(")
				.setModifiers(KeyEvent.CTRL_MASK).typeKey(JavaKeyCodes.VK_M).setModifiers(0)
				.type("1")
				.right(1).type("2").right(1).type("3")
				.right(1).type(",1")
				.setModifiers(KeyEvent.CTRL_MASK).typeKey(JavaKeyCodes.VK_M).setModifiers(0)
				.type("2").right(1).type("3")
				.checkGGBMath("(1" + Unicode.INVISIBLE_PLUS
						+ "2 / 3, 1" + Unicode.INVISIBLE_PLUS + "2 / 3)");
	}

	@Test
	public void shouldSerializeAssignment() {
		checker.type("a=")
				.setModifiers(KeyEvent.CTRL_MASK).typeKey(JavaKeyCodes.VK_M).setModifiers(0)
				.type("1")
				.right(1).type("2").right(1).type("3")
				.checkGGBMath("1" + Unicode.INVISIBLE_PLUS + "2 / 3");
	}

	@Test
	public void operatorShouldBeFollowedByZeroSpace() {
		checker.type("1+").checkPlaceholders("1+\u200b");
	}
}
