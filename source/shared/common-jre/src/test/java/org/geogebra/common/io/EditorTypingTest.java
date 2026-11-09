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

package org.geogebra.common.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.text.Normalizer;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.BaseExamTestSetup;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.controller.EditorState;
import org.geogebra.editor.share.editor.EditorFeatures;
import org.geogebra.editor.share.event.KeyEvent;
import org.geogebra.editor.share.serializer.GeoGebraSerializer;
import org.geogebra.editor.share.tree.CharPlaceholderNode;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.Korean;
import org.geogebra.editor.share.tree.PlaceholderNode;
import org.geogebra.editor.share.util.JavaKeyCodes;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.test.TestStringUtil;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

class EditorTypingTest extends BaseExamTestSetup {
	private EditorChecker checker;

	/**
	 * Reset LaTeX factory
	 */
	@BeforeAll
	static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@BeforeEach
	void setUp() {
		setupApp(SuiteSubApp.G3D);
		checker = new EditorChecker(getApp());
	}

	@Test
	void testEditorUnicode() {
		checker.checkEditorInsert(TestStringUtil.unicode("x/sqrt(x^2+4)"), "((x)/(sqrt(x^(2)+4)))");
		checker.checkEditorInsert(TestStringUtil.unicode("x^-1"), "x^(-1)");
		checker.checkEditorInsert(
				"x/(" + Unicode.EULER_STRING + "^x+1)", "((x)/(" + Unicode.EULER_STRING + "^(x)+1))");

		checker.checkEditorInsert("3*x", "3*x");
	}

	@Test
	void testEditor() {
		checker.checkEditorInsert("sqrt(x/2)", "sqrt(((x)/(2)))");

		checker.checkEditorInsert("1+2+3-4", "1+2+3-4");
		checker.checkEditorInsert("12345", "12345");
		checker.checkEditorInsert("1/2/3/4", "((((((1)/(2)))/(3)))/(4))");
		checker.checkEditorInsert("Segment[(1,2),(3,4)]", "Segment[(1,2),(3,4)]");
	}

	@Test
	void insertNrootShouldMaintainArgumentsOrder() {
		checker.checkEditorInsert("nroot(x,3)", "nroot(x,3)");
	}

	@Test
	void unicodeShouldMerge() {
		checker
				.type("\uD835\uDC65"
						+ "\uD83D\uDD96\uD83C\uDFFD"
						+ "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66")
				.checkLength(3);
	}

	@Test
	void absShouldBePrefixedBySpace() {
		// typing second | starts another abs() clause
		checker.setFormatConverter(new SyntaxAdapterImpl(AppCommonFactory.create().getKernel()));
		checker.type("3|x").checkAsciiMath("3 abs(x)");
		checker.type("3 |x").checkAsciiMath("3*abs(x)");
		checker.type("3*|x").checkAsciiMath("3*abs(x)");
		checker.type("x|xx").checkAsciiMath("x abs(xx)");
		checker.type("x |x x").checkAsciiMath("x*abs(x*x)");
		checker.type("x*|x*x").checkAsciiMath("x*abs(x*x)");
		checker.type("x sqrt(x)").checkAsciiMath("x*sqrt(x)");
		checker.type("x" + Unicode.SQUARE_ROOT + "x+1").checkAsciiMath("x sqrt(x+1)");
		checker.type("ln|x+6").checkAsciiMath("ln abs(x+6)");
		checker.type("ln|x+6").checkAsciiMath("ln abs(x+6)");
	}

	@Test
	void emptyAbsShouldBecomeOr() {
		checker.type("true||false").checkAsciiMath("true" + Unicode.OR + "false");
	}

	@Test
	void testLnAbs() {
		checker.type("ln|x+6").checkGGBMath("ln(abs(x + 6))");
	}

	@Test
	void testTrig1() {
		checker.type("sin(x)").checkGGBMath("sin(x)");
	}

	@Test
	void testTrig2() {
		checker.type("sin(x)^2").checkGGBMath("sin" + Unicode.SUPERSCRIPT_2 + "(x)");
	}

	@Test
	void testTrig3() {
		checker.type("sin(x)^3").checkGGBMath("sin" + Unicode.SUPERSCRIPT_3 + "(x)");
	}

	@Test
	void testTrig4() {
		checker
				.type("sin(x)^123")
				.checkGGBMath(
						"sin" + Unicode.SUPERSCRIPT_1 + Unicode.SUPERSCRIPT_2 + Unicode.SUPERSCRIPT_3 + "(x)");
	}

	@Test
	void testTrig5() {
		checker.type("sin^2").right(1).type("(x)").checkGGBMath("sin" + Unicode.SUPERSCRIPT_2 + "(x)");
	}

	@Test
	void testTrig6() {
		checker
				.type("sin^-1")
				.right(1)
				.type("(x)")
				.checkGGBMath("sin" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(x)");
	}

	@Test
	void testFloor() {
		checker.insert("2 floor(x)").checkRaw("SequenceNode[2,  , FnFLOOR[SequenceNode[x]]]");
	}

	@Test
	void testCeil() {
		checker.insert("2 ceil(x)").checkRaw("SequenceNode[2,  , FnCEIL[SequenceNode[x]]]");
	}

	@Test
	void testKorean() {

		// tests from https://github.com/clee704/hangul-js/blob/master/spec/hangul-dubeol.spec.js (MIT)
		checker.checkEditorInsert(
				convertKoreanTyping("gksrmfdl dks Tjwudy!"),
				"\uD55C\uAE00\uC774 \uC548 \uC368\uC838\uC694!");
		checker.checkEditorInsert(
				convertKoreanTyping("dkswdk dlTwl dksgdmaus wjdtlsdmf dlfgdmf tneh dlTdjdy."),
				"\uC549\uC544 \uC788\uC9C0 \uC54A\uC73C\uBA74 \uC815\uC2E0\uC744 "
						+ "\uC783\uC744 \uC218\uB3C4 \uC788\uC5B4\uC694.");
		checker.checkEditorInsert(
				convertKoreanTyping("10dnjf 7dlf dhgn 2tl rhkdhl"),
				"10\uC6D4 7\uC77C \uC624\uD6C4 2\uC2DC \uACFC\uC678");
		checker.checkEditorInsert(
				convertKoreanTyping("rhkfqrnlswwlQkqdlswlrrtdddkzzzl"),
				"\uAD07\uADC5\uC9C0\uBE71\uC778\uC9C1\u3133\u3147\u3147\uC55C\u314B\uD0A4");
		checker.checkEditorInsert(
				convertKoreanTyping("RhkRRnswEnpsgdnprtkt lhkd kdl s"),
				"\uAF4A\uAFBD\uB6DA\uC6E9\uC0BF \u3163\u3158\u3147 \u314F\uC774 \u3134");
		checker.checkEditorInsert(
				convertKoreanTyping("quf qkdqjqdms ek Tjqhkeh dksehldy nn wpqkf ehdhkwntpdy"),
				"\uBCC4 \uBC29\uBC95\uC740 \uB2E4 \uC368\uBD10\uB3C4 \uC548\uB418\uC694 "
						+ "\u315C\u315C \uC81C\uBC1C \uB3C4\uC640\uC8FC\uC138\uC694");
		checker.checkEditorInsert(
				convertKoreanTyping("wltlrdlsdptj goqhfksmsrj ekgoeh dksTjwlrh"),
				"\uC9C0\uC2DD\uC778\uC5D0\uC11C \uD574\uBCF4\uB77C\uB294\uAC70 "
						+ "\uB2E4\uD574\uB3C4 \uC548\uC368\uC9C0\uACE0");

		// https://github.com/e-/Hangul.js/blob/master/test/index.js (MIT)
		checker.checkEditorInsert("\u3131\u314F\u3134\u314F\u3137\u314F", "\uAC00\uB098\uB2E4");

		checker.checkEditorInsert("\u3131\u314F\u3134\u314F\u3137\u314F", "\uAC00\uB098\uB2E4");
		checker.checkEditorInsert("\u3142\u3163\u314E\u3150\u3147", "\uBE44\uD589");
		checker.checkEditorInsert("\u3146\u3161\u3139\u3137\u314F", "\uC4F8\uB2E4");
		checker.checkEditorInsert("\u3147\u3161\u3163\u3145\u314F", "\uC758\uC0AC");
		checker.checkEditorInsert("\u3149\u314F\u3139\u3142\u3147\u3161\u3134", "\uC9E7\uC740");
		checker.checkEditorInsert(
				"\u3137\u314F\u3139\u3131\u3131\u3157\u3131\u3163", "\uB2ED\uACE0\uAE30");
		// original, wrong
		// checker.checkEditorInsertReverse("\uC63D\u314F", "\u3147\u3157\u314C\u314F");
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
		checker.checkEditorInsert(
				"\u3137\u314F\u3139\u3131\u3131\u3157\u3131\u3163", "\uB2ED\uACE0\uAE30");
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

		checker.checkEditorInsert("\u314E\u314F\u3134\u3146\u314F\u3147", "\uD55C\uC30D");

		checker.checkEditorInsert("\u314E\u314F\u3145\u3145\u314F\u3147", "\uD56B\uC0C1");

		// small steps
		checker.checkEditorInsert("\u314E\u314F", "\uD558");
		checker.checkEditorInsert("\u314E\u314F\u3145", "\uD56B");
		checker.checkEditorInsert("\u314E\u314F\u3145\u3145", "\uD56B\u3145");
		checker.checkEditorInsert("\u314E\u314F\u3145\u3145\u314F", "\uD56B\uC0AC");

		checker.checkEditorInsert("\u3131\u3161", "\uADF8");
		checker.checkEditorInsert("\u3131\u3161\u3131", "\uADF9");
		checker.checkEditorInsert("\u3131\u3161\u3132", "\uADFA");
		checker.checkEditorInsert("\u3131\u3161\u3131\u3131\u314F", "\uADF9\uAC00");
		checker.checkEditorInsert("\u3131\u3161\u3131\u3131\u314F\u3142", "\uADF9\uAC11");
		checker.checkEditorInsert("\u3131\u3161\u3131\u3131\u314F\u3144", "\uADF9\uAC12");

		checker.checkEditorInsert("\u314E\u314F\u3134\u3146\u314F\u3147", "\uD55C\uC30D");

		checker.checkEditorInsert("\u314E\u314F\u3146\u314F\u3147", "\uD558\uC30D");

		checker.checkEditorInsert("\u3134\u3153\u313C\u3147\u3163", "\uB113\uC774");
		checker.checkEditorInsert("\u3147\u314F\u3136\u3137\u314F", "\uC54A\uB2E4");
		checker.checkEditorInsert("\u3131\u314F\u3144\u3147\u3161\u3134", "\uAC12\uC740");

		checker.checkEditorInsert("\u3131\u314F\u3144\u3145\u314F\u3134", "\uAC12\uC0B0");

		checker.checkEditorInsert(Korean.flattenKorean("\uB098"), "\uB098");
		checker.checkEditorInsert(Korean.flattenKorean("\uB108"), "\uB108");
		checker.checkEditorInsert(Korean.flattenKorean("\uC6B0\uB9AC"), "\uC6B0\uB9AC");
		checker.checkEditorInsert(Korean.flattenKorean("\uBBF8\uBD84"), "\uBBF8\uBD84");
		checker.checkEditorInsert(Korean.flattenKorean("\uBCA1\uD130"), "\uBCA1\uD130");
		checker.checkEditorInsert(Korean.flattenKorean("\uC0C1\uC218"), "\uC0C1\uC218");
		checker.checkEditorInsert(Korean.flattenKorean("\uB2ED\uBA39\uC5B4"), "\uB2ED\uBA39\uC5B4");
		checker.checkEditorInsert(Korean.flattenKorean("\uC6EC\uC77C"), "\uC6EC\uC77C");
		checker.checkEditorInsert(Korean.flattenKorean("\uC801\uBD84"), "\uC801\uBD84");
		checker.checkEditorInsert(Korean.flattenKorean("\uC288\uD37C\uB9E8"), "\uC288\uD37C\uB9E8");
		checker.checkEditorInsert(Korean.flattenKorean("\u3138"), "\u1104");
		checker.checkEditorInsert(
				Korean.flattenKorean("\uC778\uD14C\uADF8\uB784"), "\uC778\uD14C\uADF8\uB784");
		checker.checkEditorInsert(Korean.flattenKorean("\u3137"), "\u1103");
		checker.checkEditorInsert(Korean.flattenKorean("\u3131"), "\u1100");
		checker.checkEditorInsert(Korean.flattenKorean("\u3134"), "\u1102");
		checker.checkEditorInsert(Korean.flattenKorean("\uC8FC\uC778\uC7A5"), "\uC8FC\uC778\uC7A5");
		checker.checkEditorInsert(
				Korean.flattenKorean("\uC774\uC81C\uC880\uC790\uC790"), "\uC774\uC81C\uC880\uC790\uC790");
		checker.checkEditorInsert(
				Korean.flattenKorean("\uC544\uBAA8\uB974\uACA0\uB2E4"), "\uC544\uBAA8\uB974\uACA0\uB2E4");

		checker.checkEditorInsert("\u3146\u1161\u11BC", "\uC30D");
		checker.checkEditorInsert("\u110A\u1161\u11BC", "\uC30D");

		checker.checkEditorInsert("\u3142\u315C", "\uBD80");
		checker.checkEditorInsert("\u3142\u315E", "\uBDB8");
		checker.checkEditorInsert("\u3142\u315E\u3139", "\uBDC0");
		checker.checkEditorInsert("\u3142\u315E\u313A", "\uBDC1");

		// testEditor("\u3132", "\u1101");
		checker.checkEditorInsert("\u3132\u314F", "\uAE4C");

		checker.checkEditorInsert("\u3131\u3157\u3142\u3131\u3161\u3134", "\uACF1\uADFC");
		checker.checkEditorInsert("\u3147\u3163\u3142\u3139\u3155\u3131", "\uC785\uB825");

		checker.checkEditorInsert("\u3147\u3157\u314F\u3134\u3139\u315B", "\uC644\uB8CC");
		checker.checkEditorInsert("\u3131\u3157\u3142\u314E\u314F\u3131\u3163", "\uACF1\uD558\uAE30");

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
	void testInverseTrigEditor() {
		checker
				.type("cos" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(1)/2")
				.checkRaw("SequenceNode[FnFRAC[SequenceNode[FnAPPLY[SequenceNode[c, o, s, "
						+ "FnSUPERSCRIPT[SequenceNode[-, 1]]], SequenceNode[1]]], "
						+ "SequenceNode[2]]]");
	}

	@Test
	void testLogBase() {
		checker
				.type("log_2")
				.right(1)
				.type("(4)")
				.checkRaw("SequenceNode[FnLOG[SequenceNode[2], SequenceNode[4]]]");
	}

	@Test
	void testLogPower() {
		checker
				.type("log_2")
				.right(1)
				.type("^3")
				.right(1)
				.type("(4")
				.checkRaw("SequenceNode[FnLOG_POWER[SequenceNode[2], SequenceNode[3], SequenceNode[4]]]");
		checker.checkAsciiMath("log(2,4)^(3)");
		checker
				.type("log_2")
				.right(1)
				.type("^3")
				.right(1)
				.type("(4")
				.checkLaTeX("\\log_{2}^{3}\\left(4\\right)");
	}

	@Test
	void testSlash1() {
		checker.type("/1").right(1).type("2").checkAsciiMath("((1)/(2))");
	}

	@Test
	void testSlash2() {
		checker.type("1/2").checkAsciiMath("((1)/(2))");
	}

	@Test
	void testSlash3() {
		checker.type("12").left(1).type("/").checkAsciiMath("((1)/())2");
	}

	@Test
	void testDivision1() {
		checker.type(Unicode.DIVIDE + "1").right(1).type("2").checkAsciiMath("((1)/(2))");
	}

	@Test
	void testDivision2() {
		checker.type("1" + Unicode.DIVIDE + "2").checkAsciiMath("((1)/(2))");
	}

	@Test
	void testDivision3() {
		checker.type("12").left(1).type(Unicode.DIVIDE + "").checkAsciiMath("((1)/())2");
	}

	@Test
	void testBracketsAroundFunction() {
		checker.type("ln(x").left(4).type("(").checkAsciiMath("(ln(x))");
	}

	@Test
	void testBracketsAfterEquals() {
		checker
				.type("f(p")
				.right(1)
				.type("=ln(p*2.72")
				.right(1)
				.type("+3)")
				.checkAsciiMath("f(p)=(ln(p*2.72)+3)");
	}

	@Test
	void testBackspace() {
		checker
				.type("8" + Unicode.DIVIDE)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.type(Unicode.DIVIDE + "2")
				.checkAsciiMath("((8)/(2))");
	}

	@Test
	@Issue("APPS-7021")
	void testBackspaceAfterBrackets() {
		checker.type("x(x+1)");
		// x(x+1)|
		checkEditorState("x(x+1)", 1);
		checker.backspace(1);
		// x(x+1|)
		checkEditorState("x(x+1)", 3, 1, 0);
	}

	@Test
	@Issue("APPS-7021")
	void backspaceFunctionDefinition() {
		String rhs = "=SolveODE(y'+2 y=4)";
		insertAtRoot("f(y)" + rhs, 1);
		// f(y)|=SolveODE(y'+2 y=4)
		checker.backspace(1);
		// f(y|)=SolveODE(y'+2 y=4)
		checkEditorState("f(y)" + rhs, 1, 1, 0);
		checker.backspace(1);
		// f(|)=SolveODE(y'+2 y=4)
		checkEditorState("f()" + rhs, 0, 1, 0);
		checker.backspace(1);
		// f|=SolveODE(y'+2 y=4)
		checkEditorState("f" + rhs, 1);
		checker.backspace(1);
		// |=SolveODE(y'+2 y=4)
		checkEditorState(rhs, 0);
	}

	@Test
	@Issue("APPS-7021")
	void forwardDeleteFunctionDefinition() {
		String rhs = "=SolveODE(y'+2 y=4)";
		insertAtRoot("f(y)" + rhs, 0);
		// |f(y)=SolveODE(y'+2 y=4)
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// |(y)=SolveODE(y'+2 y=4)
		checkEditorState("(y)" + rhs, 0, 0, 0);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// |y=SolveODE(y'+2 y=4)
		checkEditorState("y" + rhs, 0);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// |=SolveODE(y'+2 y=4)
		checkEditorState(rhs, 0);
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(
			strings = {
				"f(x,g(y))=1",
				"f[x,g(y)]=1",
			})
	@Issue("APPS-7021")
	void backspaceUnwrapsFunctionWithoutLosingArguments(String input) {
		insertAtRoot(input, 1);
		// f(x,g(y))|=1, f[x,g(y)]|=1
		checker.left(1);
		// f(x,g(y)|)=1, f[x,g(y)|]=1
		// Offset 0 moves to the start of the arguments, just after the opening bracket.
		checker.getMathField().getInternal().getEditorState().setCurrentOffset(0);
		// f(|x,g(y))=1, f[|x,g(y)]=1
		checker.backspace(1);
		// f|x,g(y)=1
		checkEditorState("fx,g(y)=1", 1);
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(
			strings = {
				"f(x,g(y))=1",
				"f[x,g(y)]=1",
			})
	@Issue("APPS-7021")
	void forwardDeleteUnwrapsFunctionWithoutLosingArguments(String input) {
		insertAtRoot(input, 1);
		// f(x,g(y))|=1, f[x,g(y)]|=1
		EditorState state = checker.getMathField().getInternal().getEditorState();
		FunctionNode function = (FunctionNode) state.getRootNode().getChild(0);
		// Child 0 is the function name; offset 1 is just after f, before its bracket.
		state.setCurrentNode(function.getChild(0));
		state.setCurrentOffset(1);
		// f|(x,g(y))=1, f|[x,g(y)]=1
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// f|x,g(y)=1
		checkEditorState("fx,g(y)=1", 1);
	}

	@ParameterizedTest(name = "{0}")
	@CsvSource({
		"'(x)+2', 'x+2', 1",
		"'[x]+2', 'x+2', 1",
		"'{x}+2', 'x+2', 1",
		"'((x))+2', '(x)+2', 1",
		"'()+2', '+2', 0"
	})
	@Issue("APPS-7021")
	void backspaceEntersAndUnwrapsGrouping(String input, String unwrapped, int contentSize) {
		insertAtRoot(input, 1);
		// (x)|+2, [x]|+2, {x}|+2, ((x))|+2, ()|+2
		checker.backspace(1);
		// (x|)+2, [x|]+2, {x|}+2, ((x)|)+2, (|)+2
		checkEditorState(input, contentSize, 0, 0);
		EditorState state = checker.getMathField().getInternal().getEditorState();
		// Offset 0 moves to the start of the group, just after its opening bracket.
		state.setCurrentOffset(0);
		// (|x)+2, [|x]+2, {|x}+2, (|(x))+2, (|)+2
		checker.backspace(1);
		// |x+2, |x+2, |x+2, |(x)+2, |+2
		checkEditorState(unwrapped, 0);
	}

	@ParameterizedTest(name = "{0}")
	@CsvSource({
		"'(x)+2', 'x+2'",
		"'[x]+2', 'x+2'",
		"'{x}+2', 'x+2'",
		"'((x))+2', '(x)+2'",
		"'()+2', '+2'"
	})
	@Issue("APPS-7021")
	void forwardDeleteUnwrapsGrouping(String input, String unwrapped) {
		insertAtRoot(input, 0);
		// |(x)+2, |[x]+2, |{x}+2, |((x))+2, |()+2
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// |x+2, |x+2, |x+2, |(x)+2, |+2
		checkEditorState(unwrapped, 0);
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(
			strings = {
				"f(x)=x^2",
				"f[x]=x^2",
				"(x)+2",
				"[x]+2",
				"{x}+2",
				"((x))+2",
				"()+2",
			})
	@Issue("APPS-7021")
	void forwardDeleteCrossesClosingBrackets(String input) {
		String expected = input.replace("x^2", "x^(2)");
		insertAtRoot(input, 1);
		// f(x)|=x^2, f[x]|=x^2, (x)|+2, [x]|+2, {x}|+2, ((x))|+2, ()|+2
		checker.left(1);
		// f(x|)=x^2, f[x|]=x^2, (x|)+2, [x|]+2, {x|}+2, ((x)|)+2, (|)+2
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// f(x)|=x^2, f[x]|=x^2, (x)|+2, [x]|+2, {x}|+2, ((x))|+2, ()|+2
		checkEditorState(expected, 1);
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(
			strings = {
				"floor", "abs", "ceil", "sqrt", "log_10",
			})
	@Issue("APPS-7021")
	void backspacePreservesBuiltInFunctionArguments(String name) {
		String expression = name.equals("log_10") ? "log(10,x)+1" : name + "(x)+1";
		checker.parse("");
		checker.type(name + "(x)+1");
		// floor(x)+1|, abs(x)+1|, ceil(x)+1|, sqrt(x)+1|, log_10(x)+1|
		EditorState state = checker.getMathField().getInternal().getEditorState();
		// Root offset 1 is immediately after the function, before +1.
		state.setCurrentNode(state.getRootNode());
		state.setCurrentOffset(1);
		// floor(x)|+1, abs(x)|+1, ceil(x)|+1, sqrt(x)|+1, log_10(x)|+1
		checkEditorState(expression, 1);
		int argumentIndex = name.equals("log_10") ? 1 : 0;
		checker.backspace(1);
		// floor(x|)+1, abs(x|)+1, ceil(x|)+1, sqrt(x|)+1, log_10(x|)+1
		checkEditorState(expression, 1, argumentIndex, 0);
		checker.backspace(1);
		// floor(|)+1, abs(|)+1, ceil(|)+1, sqrt(|)+1, log_10(|)+1
		checkEditorState(expression.replace("x", ""), 0, argumentIndex, 0);
		// Return to root offset 1, immediately after the function with its empty argument.
		state.setCurrentNode(state.getRootNode());
		state.setCurrentOffset(1);
		// floor()|+1, abs()|+1, ceil()|+1, sqrt()|+1, log_10()|+1
		checker.backspace(1);
		// floor(|)+1, abs(|)+1, ceil(|)+1, sqrt(|)+1, log_10(|)+1
		checkEditorState(expression.replace("x", ""), 0, argumentIndex, 0);
	}

	@ParameterizedTest(name = "{0}")
	@CsvSource({
		"'cbrt(x)+1', 0",
		"'nroot(x,3)+1', 1",
	})
	@Issue("APPS-7021")
	void deletionPreservesCubeAndNthRootClosingBoundaries(String input, int argumentIndex) {
		insertAtRoot(input, 1);
		// cbrt(x)|+1, nroot(x,3)|+1
		checkEditorState(input, 1);
		checker.backspace(1);
		// cbrt(x|)+1, nroot(x|,3)+1
		checkEditorState(input, 1, argumentIndex, 0);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// cbrt(x)|+1, nroot(x,3)|+1
		checkEditorState(input, 1);
		checker.backspace(1);
		// cbrt(x|)+1, nroot(x|,3)+1
		checker.backspace(1);
		// cbrt(|)+1, nroot(|,3)+1
		checkEditorState(input.replace("x", ""), 0, argumentIndex, 0);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// cbrt()|+1, nroot(,3)|+1
		checkEditorState(input.replace("x", ""), 1);
	}

	@Test
	@Issue("APPS-7021")
	void deletionPreservesPoweredLogClosingBoundary() {
		checker.type("log_2");
		// log_{2|}
		checker.right(1);
		// log_2|
		checker.type("^3");
		// log_2^{3|}
		checker.right(1);
		// log_2^3|
		checker.type("(x)+1");
		// log_2^3(x)+1|
		EditorState state = checker.getMathField().getInternal().getEditorState();
		// Root offset 1 is immediately after the log function, before +1.
		state.setCurrentNode(state.getRootNode());
		state.setCurrentOffset(1);
		// log_2^3(x)|+1
		checkEditorState("log(2,x)^(3)+1", 1);
		checker.backspace(1);
		// log_2^3(x|)+1
		checkEditorState("log(2,x)^(3)+1", 1, 2, 0);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// log_2^3(x)|+1
		checkEditorState("log(2,x)^(3)+1", 1);
		checker.backspace(1);
		// log_2^3(x|)+1
		checker.backspace(1);
		// log_2^3(|)+1
		checkEditorState("log(2,)^(3)+1", 0, 2, 0);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// log_2^3()|+1
		checkEditorState("log(2,)^(3)+1", 1);
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(
			strings = {
				"floor", "abs", "ceil", "sqrt", "log_10",
			})
	@Issue("APPS-7021")
	void forwardDeleteCrossesBuiltInFunctionClosingBoundaries(String name) {
		String expression = name.equals("log_10") ? "log(10,x)+1" : name + "(x)+1";
		int argumentIndex = name.equals("log_10") ? 1 : 0;
		checker.parse("");
		checker.type(name + "(x)+1");
		// floor(x)+1|, abs(x)+1|, ceil(x)+1|, sqrt(x)+1|, log_10(x)+1|
		EditorState state = checker.getMathField().getInternal().getEditorState();
		// Root offset 1 is immediately after the function, before +1.
		state.setCurrentNode(state.getRootNode());
		state.setCurrentOffset(1);
		// floor(x)|+1, abs(x)|+1, ceil(x)|+1, sqrt(x)|+1, log_10(x)|+1
		checker.left(1);
		// floor(x|)+1, abs(x|)+1, ceil(x|)+1, sqrt(x|)+1, log_10(x|)+1
		checkEditorState(expression, 1, argumentIndex, 0);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// floor(x)|+1, abs(x)|+1, ceil(x)|+1, sqrt(x)|+1, log_10(x)|+1
		checkEditorState(expression, 1);
	}

	@ParameterizedTest(name = "backspace={0}")
	@ValueSource(
			booleans = {
				true, false,
			})
	@Issue("APPS-7021")
	void openingBracketDeletionRemovesUnusedCommandPlaceholders(boolean backspace) {
		checker.pressSingleKey("Circle( <Point>, <Radius Number> )");
		// Circle(|<Point>, <Radius Number>)
		EditorState state = checker.getMathField().getInternal().getEditorState();
		FunctionNode command = (FunctionNode) state.getCurrentNode().getParent();
		assertInstanceOf(PlaceholderNode.class, command.getChild(1).getChild(0));
		assertInstanceOf(PlaceholderNode.class, command.getChild(1).getChild(2));
		if (!backspace) {
			// Child 0 is the command name; offset 6 is after Circle, before its bracket.
			state.setCurrentNode(command.getChild(0));
			state.setCurrentOffset(6);
			// Circle|(<Point>, <Radius Number>)
		}
		checker.typeKey(backspace ? JavaKeyCodes.VK_BACK_SPACE : JavaKeyCodes.VK_DELETE);
		// Circle|
		checkEditorState("Circle", 6);
		// Placeholders serialize as empty text, so also check that no hidden nodes remain.
		checker.checkLength(6);
		checker.type("A");
		// CircleA|
		checkEditorState("CircleA", 7);
		checker.checkLength(7);
	}

	@ParameterizedTest(name = "placeholder={0}, value={1}, backspace={2}")
	@CsvSource({
		"0, A, true, CircleA",
		"0, A, false, CircleA",
		"2, r, true, Circler",
		"2, r, false, Circler",
	})
	@Issue("APPS-7021")
	void openingBracketDeletionPreservesEnteredCommandArguments(
			int placeholderOffset, String value, boolean backspace, String expected) {
		checker.pressSingleKey("Circle( <Point>, <Radius Number> )");
		// Circle(|<Point>, <Radius Number>)
		EditorState state = checker.getMathField().getInternal().getEditorState();
		FunctionNode command = (FunctionNode) state.getCurrentNode().getParent();
		// Argument offsets 0 and 2 are the first and second syntax placeholders.
		state.setCurrentOffset(placeholderOffset);
		// Circle(|<Point>, <Radius Number>), Circle(<Point>, |<Radius Number>)
		checker.type(value);
		// Circle(A|, <Radius Number>), Circle(<Point>, r|)
		// Backspace targets the argument's start; Delete targets the command name's end.
		state.setCurrentNode(command.getChild(backspace ? 1 : 0));
		state.setCurrentOffset(backspace ? 0 : 6);
		// Backspace: Circle(|A, <Radius Number>), Circle(|<Point>, r)
		// Delete: Circle|(A, <Radius Number>), Circle|(<Point>, r)
		checker.typeKey(backspace ? JavaKeyCodes.VK_BACK_SPACE : JavaKeyCodes.VK_DELETE);
		// Circle|A, Circle|r
		checkEditorState(expected, 6);
		checker.checkLength(7);
	}

	@ParameterizedTest(name = "backspace={0}")
	@ValueSource(
			booleans = {
				true, false,
			})
	@Issue("APPS-7021")
	void openingBracketDeletionPreservesSeparatorsBetweenEnteredArguments(boolean backspace) {
		checker.pressSingleKey("Circle( <Point>, <Point>, <Point> )");
		// Circle(|<Point>, <Point>, <Point>)
		EditorState state = checker.getMathField().getInternal().getEditorState();
		FunctionNode command = (FunctionNode) state.getCurrentNode().getParent();
		checker.type("A");
		// Circle(A|, <Point>, <Point>)
		// Offset 4 is the third argument's placeholder, after A, comma, placeholder, comma.
		state.setCurrentOffset(4);
		// Circle(A, <Point>, |<Point>)
		checker.type("B");
		// Circle(A, <Point>, B|)
		// Backspace targets the argument's start; Delete targets the command name's end.
		state.setCurrentNode(command.getChild(backspace ? 1 : 0));
		state.setCurrentOffset(backspace ? 0 : 6);
		// Backspace: Circle(|A, <Point>, B); Delete: Circle|(A, <Point>, B)
		checker.typeKey(backspace ? JavaKeyCodes.VK_BACK_SPACE : JavaKeyCodes.VK_DELETE);
		// Circle|A,B
		checkEditorState("CircleA,B", 6);
		checker.checkLength(9);
	}

	@Test
	@Issue("APPS-7021")
	void forwardDeletePreservesFunctionCommaInProtectedPoint() {
		checker.convertFormula("(1,2)");
		// (|1,2)
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// (|,2)
		checker.type("f(a,b)");
		// (f(a,b)|,2)
		checker.left(1);
		// (f(a,b|),2)
		checkEditorState("(f(a,b),2)", 3, 1, 0, 0, 0);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// (f(a,b)|,2)
		checkEditorState("(f(a,b),2)", 1, 0, 0);
		checker.backspace(1);
		// (f(a,b|),2)
		checkEditorState("(f(a,b),2)", 3, 1, 0, 0, 0);
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(
			strings = {
				"f(a,b)", "f[a,b]",
			})
	@Issue("APPS-7021")
	void backspacePreservesOpeningBracketAndFunctionCommaInProtectedPoint(String input) {
		String expression = "(" + input + ",2)";
		checker.parse("");
		checker.convertFormula("(1,2)");
		// (|1,2)
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// (|,2)
		checker.type(input);
		// (f(a,b)|,2), (f[a,b]|,2)
		checker.left(1);
		// (f(a,b|),2), (f[a,b|],2)
		// Offset 0 moves to the start of the argument, just after its opening bracket.
		checker.getMathField().getInternal().getEditorState().setCurrentOffset(0);
		// (f(|a,b),2), (f[|a,b],2)
		checkEditorState(expression, 0, 1, 0, 0, 0);
		checker.backspace(1);
		// (f(|a,b),2), (f[|a,b],2)
		checkEditorState(expression, 0, 1, 0, 0, 0);
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(
			strings = {
				"f(a,b)", "f[a,b]",
			})
	@Issue("APPS-7021")
	void forwardDeletePreservesOpeningBracketAndFunctionCommaInProtectedPoint(String input) {
		checker.parse("");
		checker.convertFormula("(1,2)");
		// (|1,2)
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// (|,2)
		checker.type(input);
		// (f(a,b)|,2), (f[a,b]|,2)
		checker.left(1);
		// (f(a,b|),2), (f[a,b|],2)
		EditorState state = checker.getMathField().getInternal().getEditorState();
		FunctionNode function = (FunctionNode) state.getCurrentNode().getParent();
		String expression = "(" + input + ",2)";
		// Child 0 is the function name; offset 1 is just after f, before its bracket.
		state.setCurrentNode(function.getChild(0));
		state.setCurrentOffset(1);
		// (f|(a,b),2), (f|[a,b],2)
		checkEditorState(expression, 1, 0, 0, 0, 0);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// (f|(a,b),2), (f|[a,b],2)
		checkEditorState(expression, 1, 0, 0, 0, 0);
	}

	@ParameterizedTest(name = "backspace={0}")
	@ValueSource(
			booleans = {
				true, false,
			})
	@Issue("APPS-7021")
	void openingBracketDeletionStillUnwrapsSingleArgumentInProtectedPoint(boolean backspace) {
		checker.parse("");
		checker.convertFormula("(1,2)");
		// (|1,2)
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// (|,2)
		checker.type("f(a)");
		// (f(a)|,2)
		checker.left(1);
		// (f(a|),2)
		EditorState state = checker.getMathField().getInternal().getEditorState();
		FunctionNode function = (FunctionNode) state.getCurrentNode().getParent();
		// Backspace targets the opening bracket from the argument's start (offset 0).
		// Delete targets it from the end of the function name (offset 1).
		state.setCurrentNode(function.getChild(backspace ? 1 : 0));
		state.setCurrentOffset(backspace ? 0 : 1);
		// Backspace: (f(|a),2); Delete: (f|(a),2)
		checker.typeKey(backspace ? JavaKeyCodes.VK_BACK_SPACE : JavaKeyCodes.VK_DELETE);
		// (f|a,2)
		checkEditorState("(fa,2)", 1, 0, 0);
	}

	@ParameterizedTest(name = "{0}, backspace={1}, placeholders={2}")
	@CsvSource({
		"$point, true, false",
		"$point, false, false",
		"$point, true, true",
		"$point, false, true",
		"$pointAt, true, false",
		"$pointAt, false, false",
		"$pointAt, true, true",
		"$pointAt, false, true",
		"$vector, true, false",
		"$vector, false, false",
		"$vector, true, true",
		"$vector, false, true",
	})
	@Issue("APPS-7021")
	void boundaryDeletionRemovesEmptyCoordinateTemplates(
			String template, boolean backspace, boolean placeholders) {
		// State comments use comma-separated coordinates for all three templates; | marks the caret.
		checker.type("7+");
		// 7+|
		checker.pressSingleKey(template + ":2");
		// 7+(|,)
		EditorState state = checker.getMathField().getInternal().getEditorState();
		FunctionNode coordinates = (FunctionNode) state.getCurrentNode().getParent();
		if (placeholders) {
			// Both syntax and character placeholders represent unused coordinate fields.
			coordinates.getChild(0).addChild(new PlaceholderNode("Coordinate"));
			coordinates.getChild(1).addChild(new CharPlaceholderNode());
			// 7+(|_,_) (_ denotes an unused placeholder)
		}
		// Root offsets 2 and 3 are immediately before and after the coordinate template.
		state.setCurrentNode(state.getRootNode());
		state.setCurrentOffset(backspace ? 3 : 2);
		// Backspace: 7+(_,_)|; Delete: 7+|(_,_) (or empty fields without placeholder nodes)
		checker.typeKey(backspace ? JavaKeyCodes.VK_BACK_SPACE : JavaKeyCodes.VK_DELETE);
		// 7+| (the empty template and its placeholder nodes have been removed)
		checkEditorState("7+", 2);
		checker.checkLength(2);
	}

	@ParameterizedTest(name = "{0}, backspace={1}, filledCoordinate={2}")
	@CsvSource({
		"$point, true, 0",
		"$point, false, 0",
		"$point, true, 1",
		"$point, false, 1",
		"$pointAt, true, 0",
		"$pointAt, false, 0",
		"$pointAt, true, 1",
		"$pointAt, false, 1",
		"$vector, true, 0",
		"$vector, false, 0",
		"$vector, true, 1",
		"$vector, false, 1",
	})
	@Issue("APPS-7021")
	void boundaryDeletionPreservesPartiallyFilledCoordinateTemplates(
			String template, boolean backspace, int filledCoordinate) {
		// State comments use comma-separated coordinates for all three templates; | marks the caret.
		checker.pressSingleKey(template + ":2");
		// (|,)
		EditorState state = checker.getMathField().getInternal().getEditorState();
		FunctionNode coordinates = (FunctionNode) state.getCurrentNode().getParent();
		// Select the start of the coordinate that will contain user content.
		state.setCurrentNode(coordinates.getChild(filledCoordinate));
		state.setCurrentOffset(0);
		// (|,) or (,|)
		checker.type("0");
		// (0|,) or (,0|) (zero is user content, not an empty field)
		// Root offset 1 is after the template; root offset 0 is before it.
		state.setCurrentNode(state.getRootNode());
		state.setCurrentOffset(backspace ? 1 : 0);
		// Backspace: (0,)| or (,0)|; Delete: |(0,) or |(,0)
		checker.typeKey(backspace ? JavaKeyCodes.VK_BACK_SPACE : JavaKeyCodes.VK_DELETE);
		// Backspace: (0,|) or (,0|); Delete: (|0,) or (|,0)
		int targetCoordinate = backspace ? 1 : 0;
		int offset = backspace && filledCoordinate == 1 ? 1 : 0;
		String expression = template + (filledCoordinate == 0 ? "(0,)" : "(,0)");
		checkEditorState(expression, offset, targetCoordinate, 0);
	}

	@ParameterizedTest(name = "{0}, backspace={1}")
	@CsvSource({
		"$point, true",
		"$point, false",
		"$pointAt, true",
		"$pointAt, false",
		"$vector, true",
		"$vector, false",
	})
	@Issue("APPS-7021")
	void boundaryDeletionPreservesProtectedEmptyCoordinateTemplates(
			String template, boolean backspace) {
		// State comments use comma-separated coordinates for all three templates; | marks the caret.
		checker.pressSingleKey(template + ":2");
		// (|,)
		checker.protect();
		// (|,) (the input's coordinate structure is now protected)
		EditorState state = checker.getMathField().getInternal().getEditorState();
		// Place the caret just outside the protected template, at the targeted boundary.
		state.setCurrentNode(state.getRootNode());
		state.setCurrentOffset(backspace ? 1 : 0);
		// Backspace: (,)|; Delete: |(,)
		checker.typeKey(backspace ? JavaKeyCodes.VK_BACK_SPACE : JavaKeyCodes.VK_DELETE);
		// Backspace: (,|); Delete: (|,) (the protected template is retained)
		checkEditorState(template + "(,)", 0, backspace ? 1 : 0, 0);
	}

	@ParameterizedTest(name = "{0}, firstCoordinate={1}")
	@CsvSource({
		"$point, ''",
		"$point, 1",
		"$pointAt, ''",
		"$pointAt, 1",
		"$vector, ''",
		"$vector, 1",
	})
	@Issue("APPS-7021")
	void backspaceAtCoordinateTemplateOpeningPreservesOtherCoordinates(
			String template, String firstCoordinate) {
		// State comments use comma-separated coordinates for all three templates; | marks the caret.
		checker.pressSingleKey(template + ":2");
		// (|,)
		checker.type(firstCoordinate);
		// (|,) or (1|,)
		checker.right(1);
		// (,|) or (1,|)
		checker.type("2");
		// (,2|) or (1,2|)
		EditorState state = checker.getMathField().getInternal().getEditorState();
		FunctionNode coordinates = (FunctionNode) state.getCurrentNode().getParent();
		// Position the caret at the first coordinate's start, just after the opening bracket.
		state.setCurrentNode(coordinates.getChild(0));
		state.setCurrentOffset(0);
		// (|,2) or (|1,2)
		String expression = template + "(" + firstCoordinate + ",2)";
		checkEditorState(expression, 0, 0, 0);
		checker.backspace(1);
		// |(,2) or |(1,2) (the populated template remains intact)
		checkEditorState(expression, 0);
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(
			strings = {
				"$point",
				"$pointAt",
				"$vector",
			})
	@Issue("APPS-7021")
	void backspaceAfterCoordinatePlaceholderPreservesOtherCoordinates(String template) {
		// State comments use comma-separated coordinates for all three templates; | marks the caret.
		checker.pressSingleKey(template + ":2");
		// (|,)
		checker.right(1);
		// (,|)
		checker.type("2");
		// (,2|)
		EditorState state = checker.getMathField().getInternal().getEditorState();
		FunctionNode coordinates = (FunctionNode) state.getCurrentNode().getParent();
		coordinates.getChild(0).addChild(new CharPlaceholderNode());
		// (_,2|) (_ denotes an unused placeholder)
		// Offset 1 places the caret immediately after the first coordinate's placeholder.
		state.setCurrentNode(coordinates.getChild(0));
		state.setCurrentOffset(1);
		// (_|,2)
		String expression = template + "(,2)";
		checkEditorState(expression, 1, 0, 0);
		checker.backspace(1);
		// (|,2) (the explicit placeholder node was removed, but the coordinate stays empty)
		checkEditorState(expression, 0, 0, 0);
		checker.backspace(1);
		// |(,2) (the remaining coordinate prevents template removal)
		checkEditorState(expression, 0);
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(
			strings = {
				"$point",
				"$pointAt",
				"$vector",
			})
	@Issue("APPS-7021")
	void selectionDeletesPointAndVectorTemplates(String template) {
		// State comments use comma-separated coordinates for all three templates; | marks the caret.
		checker.pressSingleKey(template + ":2");
		// (|,)
		checker.type("1");
		// (1|,)
		checker.right(1);
		// (1,|)
		checker.type("2");
		// (1,2|)
		checker.ctrlA();
		// The entire template (1,2) is selected.
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// The entire template and both coordinates have been removed.
		checkEditorState("", 0);
		checker.checkLength(0);
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(
			strings = {
				"$point",
				"$pointAt",
				"$vector",
			})
	@Issue("APPS-7021")
	void backspaceDeletesEmptiedPointAndVectorTemplates(String template) {
		// State comments use comma-separated coordinates for all three templates; | marks the caret.
		checker.pressSingleKey(template + ":2");
		// (|,)
		checker.type("1");
		// (1|,)
		checker.right(1);
		// (1,|)
		checker.type("2");
		// (1,2|)
		checker.backspace(1);
		// (1,|)
		checkEditorState(template + "(1,)", 0, 1, 0);
		checker.left(1);
		// (1|,)
		checker.backspace(1);
		// (|,)
		checkEditorState(template + "(,)", 0, 0, 0);
		checker.backspace(1);
		// The empty template has been removed.
		checkEditorState("", 0);
		checker.checkLength(0);
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(
			strings = {
				"$point",
				"$pointAt",
				"$vector",
			})
	@Issue("APPS-7021")
	void forwardDeleteEntersPointAndVectorTemplatesWithoutLosingCoordinates(String template) {
		// State comments use comma-separated coordinates for all three templates; | marks the caret.
		checker.pressSingleKey(template + ":2");
		// (|,)
		checker.type("1");
		// (1|,)
		checker.right(1);
		// (1,|)
		checker.type("2");
		// (1,2|)
		EditorState state = checker.getMathField().getInternal().getEditorState();
		// Root offset 0 is before the entire coordinate template.
		state.setCurrentNode(state.getRootNode());
		state.setCurrentOffset(0);
		// |(1,2)
		String expression = template + "(1,2)";
		checkEditorState(expression, 0);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// (|1,2)
		checkEditorState(expression, 0, 0, 0);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// (|,2)
		checkEditorState(template + "(,2)", 0, 0, 0);
		checker.type("3");
		// (3|,2)
		checkEditorState(template + "(3,2)", 1, 0, 0);
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(
			strings = {
				"$point",
				"$pointAt",
				"$vector",
			})
	@Issue("APPS-7021")
	void deletionPreservesPointAndVectorTemplateClosingBoundaries(String template) {
		// State comments use comma-separated coordinates for all three templates; | marks the caret.
		checker.pressSingleKey(template + ":2");
		// (|,)
		checker.type("1");
		// (1|,)
		checker.right(1);
		// (1,|)
		checker.type("2");
		// (1,2|)
		checker.right(1);
		// (1,2)|
		String expression = template + "(1,2)";
		checkEditorState(expression, 1);
		checker.backspace(1);
		// (1,2|)
		checkEditorState(expression, 1, 1, 0);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// (1,2)|
		checkEditorState(expression, 1);
		checker.backspace(1);
		// (1,2|)
		checker.backspace(1);
		// (1,|)
		checkEditorState(template + "(1,)", 0, 1, 0);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// (1,)|
		checkEditorState(template + "(1,)", 1);
	}

	@Test
	@Issue("APPS-7021")
	void forwardDeletePreservesSingleCellMatrix() {
		insertAtRoot("{{1}}", 1);
		// {{1}}|
		checker.left(1);
		// {{1|}}
		checkEditorState("{{1}}", 1, 0, 0);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// {{1}}|
		checkEditorState("{{1}}", 1);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// {{1}}|
		checkEditorState("{{1}}", 1);
	}

	@Test
	@Issue("APPS-7021")
	void forwardDeletePreservesProtectedSingleCellMatrix() {
		checker.matrixFromParser("{{1}}");
		// {{|1}}
		checker.right(1);
		// {{1|}}
		checkEditorState("{{1}}", 1, 0, 0);
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// {{1|}}
		checkEditorState("{{1}}", 1, 0, 0);
	}

	@Test
	@Issue("APPS-7021")
	void deletionCannotUnwrapProtectedGrouping() {
		checker.insert("(x)");
		// (x)|
		checker.protect();
		// (x)| (the grouping is now protected)
		checker.left(1);
		// (x|)
		checker.left(1);
		// (|x)
		checker.backspace(1);
		// (|x)
		checkEditorState("(x)", 0, 0, 0);
		checker.right(1);
		// (x|)
		checker.typeKey(JavaKeyCodes.VK_DELETE);
		// (x|)
		checkEditorState("(x)", 1, 0, 0);
	}

	/**
	 * Insert an expression and position the caret between its top-level nodes.
	 * @param input expression to insert
	 * @param offset index between root children; 0 is before the expression, while
	 * 1 is after its first node (for example, a complete function or bracketed group)
	 */
	private void insertAtRoot(String input, int offset) {
		checker.parse("");
		checker.insert(input);
		EditorState state = checker.getMathField().getInternal().getEditorState();
		state.setCurrentNode(state.getRootNode());
		state.setCurrentOffset(offset);
	}

	/**
	 * Assert the editor's expression and caret position without resetting its state,
	 * so subsequent editing steps can continue from the same position.
	 * @param expression expected GeoGebra serialization, without a caret marker
	 * @param caret expected caret path: offset in the current sequence, followed by
	 * each containing node's child index, moving outward to the root; for example,
	 * {@code (3, 1, 0)} means offset 3 in argument 1 of the root's child 0
	 */
	private void checkEditorState(String expression, Integer... caret) {
		EditorState state = checker.getMathField().getInternal().getEditorState();
		assertEquals(
				expression, GeoGebraSerializer.serialize(state.getRootNode(), (EditorFeatures) null));
		checker.checkCaret(caret);
	}

	@Test
	void testBackspaceAfterFraction() {
		checker.type("12/34").typeKey(JavaKeyCodes.VK_BACK_SPACE).checkAsciiMath("((12)/(3))");
	}

	@Test
	void spaceAfterFunctionShouldAddBrackets() {
		checker.setFormatConverter(new SyntaxAdapterImpl(AppCommonFactory.create().getKernel()));
		checker.type("sin 9x").checkAsciiMath("sin(9x)");
	}

	@Test
	void characterAfterFunctionShouldAddBrackets() {
		AppCommon app = AppCommonFactory.create();

		TemplateCatalog catalog = new TemplateCatalog();
		catalog.setForceBracketAfterFunction(true);
		EditorChecker inputBoxChecker = new EditorChecker(app, catalog);
		inputBoxChecker.setFormatConverter(new SyntaxAdapterImpl(app.getKernel()));

		inputBoxChecker.type("sin9x").checkAsciiMath("sin(9x)");
		inputBoxChecker.parse("");

		inputBoxChecker.type("sinhb").checkAsciiMath("sinh(b)");
		inputBoxChecker.parse("");

		inputBoxChecker.type("xsinxcosx").checkAsciiMath("xsin(xcos(x))");
		inputBoxChecker.parse("");

		inputBoxChecker.type("sin^2").right(1).type("a").checkAsciiMath("sin^(2)(a)");
		inputBoxChecker.parse("");

		inputBoxChecker.type("log_3").right(1).type("x").checkAsciiMath("log(3,x)");
	}

	@Test
	void testBackspaceWithBrackets() {
		checker.type("8/").typeKey(JavaKeyCodes.VK_BACK_SPACE).type("/2").checkAsciiMath("((8)/(2))");
	}

	@Test
	void typingPiShouldProduceUnicodeInInputBox() {
		TemplateCatalog catalog = new TemplateCatalog();
		catalog.enableSubstitutions();
		EditorChecker inputBoxChecker = new EditorChecker(AppCommonFactory.create(), catalog);
		inputBoxChecker.type("sin(pix)").checkAsciiMath("sin(" + Unicode.PI_STRING + "x)");
	}

	@Test
	void typingEpsilonShouldProduceUnicodeInInputBox() {
		TemplateCatalog catalog = new TemplateCatalog();
		catalog.enableSubstitutions();
		EditorChecker inputBoxChecker = new EditorChecker(AppCommonFactory.create(), catalog);
		inputBoxChecker.type("1+epsilon").checkAsciiMath("1+" + Unicode.epsilon);
	}

	@Test
	void typingEpsilonShouldNotProduceUnicodeByDefault() {
		TemplateCatalog catalog = new TemplateCatalog();
		EditorChecker inputBoxChecker = new EditorChecker(AppCommonFactory.create(), catalog);
		inputBoxChecker.type("1+epsilon").checkAsciiMath("1+epsilon");
	}

	@Test
	void typingOperatorsShouldProduceUnicode() {
		TemplateCatalog catalog = new TemplateCatalog();
		catalog.enableSubstitutions();
		EditorChecker inputBoxChecker = new EditorChecker(AppCommonFactory.create(), catalog);
		inputBoxChecker.type("x<=y").checkAsciiMath("x" + Unicode.LESS_EQUAL + "y");
		inputBoxChecker.type("x&&y").checkAsciiMath("x" + Unicode.AND + "y");
	}

	@Test
	void shouldRecognizeAbsAsSuffix() {
		checker.type("xabs(x").checkGGBMath("x abs(x)");
	}

	@Test
	void shouldRecognizeSqrtAsSuffix() {
		checker.type("xsqrt(x").checkGGBMath("x sqrt(x)");
	}

	@Test
	void shouldRecognizeSqrtAsSuffixWithConst() {
		// for constant no multiplication space added => we have to check the raw string
		checker.type("8sqrt(x").checkRaw("SequenceNode[8, FnSQRT[SequenceNode[x]]]");
	}

	@Test
	void shouldRecognizeDelta() {
		checker.type("Δy(1+y)").checkGGBMath("Δy(1 + y)");
	}

	@Test
	void shouldRecognizeDeltaAtEnd() {
		checker.type("yΔ(1+y)").checkGGBMath("yΔ(1 + y)");
	}

	@Test
	void gammaShouldBeRecognizedAsFunction() {
		checker.add(Unicode.Gamma + "(x)=gamma(x)");
		checker.type(Unicode.Gamma + "(1+y)").checkGGBMath(Unicode.Gamma + "(1 + y)");
	}

	@Test
	void testTypingPiWithComplex() {
		TemplateCatalog catalog = new TemplateCatalog();
		catalog.enableSubstitutions();
		EditorChecker inputBoxChecker = new EditorChecker(AppCommonFactory.create(), catalog);
		inputBoxChecker.type("3pi+ 4i").checkAsciiMath("3" + Unicode.PI_STRING + "+ 4i");
	}

	@Test
	void testTypingPiiWithComplex() {
		TemplateCatalog catalog = new TemplateCatalog();
		catalog.enableSubstitutions();
		EditorChecker inputBoxChecker = new EditorChecker(AppCommonFactory.create(), catalog);
		inputBoxChecker.type("3pii").checkAsciiMath("3" + Unicode.PI_STRING + "i");
	}

	@Test
	void testBracketsForSelection() {
		checker
				.type("x^2")
				.right(1)
				.type("+1")
				.setModifiers(KeyEvent.SHIFT_MASK)
				.left(5)
				.setModifiers(0)
				.type("(")
				.checkAsciiMath("(x^(2)+1)");
	}

	@Test
	void testBracketsForSelectionSin() {
		checker
				.type("sinx^2")
				.right(1)
				.type("+1")
				.setModifiers(KeyEvent.SHIFT_MASK)
				.left(4)
				.setModifiers(0)
				.type("(")
				.checkAsciiMath("sin(x^(2)+1)");
	}

	@Test
	void testBracketsForSelectionSingleChar() {
		checker
				.type("x^2")
				.right(1)
				.type("+1")
				.setModifiers(KeyEvent.SHIFT_MASK)
				.left(1)
				.setModifiers(0)
				.type("(")
				.checkAsciiMath("x^(2)+(1)");
	}

	@Test
	void testBracketsForSelectionAfterScript() {
		checker
				.type("x^2")
				.right(1)
				.type("+1")
				.setModifiers(KeyEvent.SHIFT_MASK)
				.left(2)
				.setModifiers(0)
				.type("(")
				.checkAsciiMath("x^(2)(+1)");
	}

	@Test
	void testCommaInPointEditorWithSelection() {
		checker
				.insert("(123,456)")
				.protect()
				.left(42)
				.setModifiers(KeyEvent.SHIFT_MASK)
				.right(42) // select as far as possible
				.type("7")
				.checkAsciiMath("(7,456)");
	}

	@Test
	@Issue("APPS-7553")
	void testBracketsSelectionAtomic() {
		checker
				.type("1+(2+3)+4")
				.setModifiers(KeyEvent.SHIFT_MASK)
				.left(3)
				.checkSelection("ArrayNode[SequenceNode[2, +, 3]]", "4");
		checker
				.type("1+(2+3)+4")
				.setModifiers(KeyEvent.SHIFT_MASK)
				.left(3)
				.right(1)
				.checkSelection("+", "4");
	}

	@Test
	@Issue("APPS-6129")
	void testCommaPastedIntoEditor() {
		checker
				.insert("(123,456)")
				.protect()
				.left(1)
				.setModifiers(KeyEvent.SHIFT_MASK)
				.left(3) // select 2nd coord
				.insert("(7,8)")
				.setModifiers(0)
				.left(4)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkAsciiMath("(123,78)");
	}

	@Test
	void testCommaInPointEditor() {
		checker
				.insert("(2,1)")
				.protect()
				.left(42) // go to the left of protected editor
				.type("3")
				.right(1) // cursor in front of comma
				.type(",4")
				.checkAsciiMath("(32,41)");
	}

	@Test
	void testPipeInPointEditor() {
		checker.setAllowAbs(false);
		checker
				.insert("(2,1)")
				.protect()
				.left(42) // go to the left of protected editor
				.type("3")
				.right(1) // cursor in front of comma
				.type("|4")
				.checkAsciiMath("(32,41)");
	}

	@Test
	void testPipeInPointEditorAustrian() {
		checker.setAllowAbs(false);
		checker
				.insert("(2" + Unicode.verticalLine + "1)")
				.protect()
				.left(42) // go to the left of protected editor
				.type("3")
				.right(1) // cursor in front of comma
				.type("|4")
				.checkAsciiMath("(32" + Unicode.verticalLine + "41)");
	}

	@Test
	void testCommaInMatrixEditor() {
		checker
				.insert("{{1,2},{3,4}}")
				.protect()
				.left(42) // go to the left of protected editor
				.type(",")
				.type(",")
				.checkAsciiMath("{{1,2},{3,4}}");
	}

	@Test
	void testSqrtInPointEditor() {
		checker.setFormatConverter(new SyntaxAdapterImpl(AppCommonFactory.create().getKernel()));
		checker.setForceBracketsAfterFunction();
		checker
				.insert("(2,1,0)")
				.protect()
				.left(5)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.type("sqrt9")
				.right(3)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.type("sin3")
				.checkAsciiMath("(sqrt(9),sin(3),0)");
	}

	@Test
	void testBracketsInPointEditor() {
		checker.insert("(2,3,3+4)").left(8).type("(").checkAsciiMath("((2),3,3+4)");

		checker.insert("(2,3,3+4)").left(7).type(")").checkAsciiMath("(2),3,3+4");

		checker.insert("(2,3,3+4)").protect().left(7).type(")").checkAsciiMath("((2),3,3+4)");

		checker.insert("(2,3,3+4)").left(6).type("(").checkAsciiMath("(2,(3),3+4)");

		checker.insert("(2,3,3+4)").left(5).type(")").checkAsciiMath("(2,3),3+4");

		checker.insert("(2,3,3+4)").protect().left(5).type(")").checkAsciiMath("(2,(3),3+4)");

		checker.insert("(2,3,3+4)").left(4).type("(").checkAsciiMath("(2,3,(3+4))");

		checker.insert("(2,3,3+4)").left(1).type(")").checkAsciiMath("(2,3,3+4)");

		checker.insert("(2,3,3+4)").protect().left(1).type(")").checkAsciiMath("(2,3,(3+4))");

		checker.insert("(2,3,3+4)").left(8).type("[").checkAsciiMath("([2],3,3+4)");

		checker.insert("(2,3,3+4)").left(5).type("]").checkAsciiMath("(2,[3],3+4)");
	}

	@Test
	void testEditorBackspace() {
		checker.type("ab cd(").left(3).typeKey(JavaKeyCodes.VK_BACK_SPACE).checkAsciiMath("abcd()");

		checker.type("a b(").left(3).typeKey(JavaKeyCodes.VK_BACK_SPACE).checkAsciiMath("ab()");

		checker
				.type("1+ N Solve(")
				.left(6)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkAsciiMath("1+ NSolve()");
	}

	@Test
	void testBackspaceLeavesIndexUnchanged() {
		checker
				.type("x^3/2")
				.left(5)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.type("y")
				.checkAsciiMath("y^(((3)/(2)))");
	}

	@Test
	void testBackspaceLeavesFraction() {
		checker
				.type("1-1/4")
				.left(4)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.type("+")
				.checkAsciiMath("1+((1)/(4))");
	}

	@Test
	void testBackspaceLeavesPower() {
		checker.type("12^34").left(3).typeKey(JavaKeyCodes.VK_BACK_SPACE).checkAsciiMath("1^(34)");
	}

	@Test
	void testBackspaceLeavesPowerOfLetters() {
		checker
				.type("e^t")
				.left(2)
				.type("-")
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkAsciiMath("e^(t)");

		checker.type("ee^t").left(2).typeKey(JavaKeyCodes.VK_BACK_SPACE).checkAsciiMath("e^(t)");
	}

	@Test
	void testBackspaceLeavesUnderscoreWithLetters() {
		checker
				.type("e_t")
				.left(2)
				.type("-")
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.checkAsciiMath("e_{t}");

		checker.type("ee_t").left(2).typeKey(JavaKeyCodes.VK_BACK_SPACE).checkAsciiMath("e_{t}");
	}

	@Test
	void testBackspaceLeavesSqrt() {
		checker
				.type("12-sqrt(45")
				.left(3)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.type("+")
				.checkAsciiMath("12+sqrt(45)");
	}

	@Test
	void noCommasPastedInProtectedField() {
		checker.insert("(2,3,3+4)").protect().left(3).insert("5,6").checkAsciiMath("(2,3,356+4)");
	}

	@Test
	void noCurlyBracesPastedInProtectedField() {
		checker.insert("(2,3,3+4)").protect().left(3).insert("{}").checkAsciiMath("(2,3,3+4)");
	}

	@Test
	void closeBracketShouldMoveCharactersOut() {
		checker.insert("123456789").left(6).type("(").right(2).type(")").checkAsciiMath("123(45)6789");
	}

	@Test
	void noMathFunctionFuseForFractions() {
		checker
				.type("x+π/2")
				.left(4)
				.typeKey(JavaKeyCodes.VK_BACK_SPACE)
				.type("-")
				.checkAsciiMath("x-((π)/(2))");
	}

	@Test
	void cursorInNoScript() {
		checker.type("x + 1/2").checkCursorNotInScript();
	}

	@Test
	void cursorInNoScriptXSquared() {
		checker.type("x^2").left(2).checkCursorNotInScript();
	}

	@Test
	void cursorInSuperscript() {
		checker.type("x^2^345").checkCursorInScript();
	}

	@Test
	void cursorInSubscript() {
		checker.type("x_2").checkCursorInScript();
	}

	@Test
	void cursorInNoSubscript() {
		checker.type("x_2").left(2).checkCursorNotInScript();
	}

	@Test
	void collapseSelectionOnArrowLeft() {
		checker.insert("1+2+3+4").select(3, 5).left(1).type("x").checkAsciiMath("1+2x+3+4");
	}

	@Test
	void collapseSelectionOnArrowRight() {
		checker.insert("1+2+3+4").select(3, 6).right(1).type("x").checkAsciiMath("1+2+3+x4");
	}

	@Test
	void shouldNotSerializeEmptyIntPartOfMixedNumber() {
		checker
				.setModifiers(KeyEvent.CTRL_MASK)
				.typeKey(JavaKeyCodes.VK_M)
				.setModifiers(0)
				.right(1)
				.type("2")
				.right(1)
				.type("3")
				.checkAsciiMath("((2)/(3))");
		checker
				.type("1+")
				.setModifiers(KeyEvent.CTRL_MASK)
				.typeKey(JavaKeyCodes.VK_M)
				.setModifiers(0)
				.right(1)
				.type("2")
				.right(1)
				.type("3")
				.checkGGBMath("1 + 2 / 3");
	}

	@Test
	void shouldNotSerializeMixedNumberIfDisabled() {
		startExam(ExamType.WTR);
		checker
				.setModifiers(KeyEvent.CTRL_MASK)
				.typeKey(JavaKeyCodes.VK_M)
				.setModifiers(0)
				.type("1")
				.right(1)
				.type("2")
				.right(1)
				.type("3")
				.checkGGBMath("1 * 2 / 3", getApp().getEditorFeatures());
	}

	@Test
	void shouldNotSerializeInvalidIntPartOfMixedNumber() {
		checker
				.setModifiers(KeyEvent.CTRL_MASK)
				.typeKey(JavaKeyCodes.VK_M)
				.setModifiers(0)
				.type("1+")
				.right(1)
				.type("2")
				.right(1)
				.type("3")
				.checkGGBMath("1 + 2 / 3");
	}

	@Test
	void shouldSerializeValidMixedNumber() {
		checker
				.setModifiers(KeyEvent.CTRL_MASK)
				.typeKey(JavaKeyCodes.VK_M)
				.setModifiers(0)
				.type("1")
				.right(1)
				.type("2")
				.right(1)
				.type("3")
				.checkGGBMath("1" + Unicode.INVISIBLE_PLUS + "2 / 3");
		checker
				.type("(")
				.setModifiers(KeyEvent.CTRL_MASK)
				.typeKey(JavaKeyCodes.VK_M)
				.setModifiers(0)
				.type("1")
				.right(1)
				.type("2")
				.right(1)
				.type("3")
				.right(1)
				.type(",1")
				.setModifiers(KeyEvent.CTRL_MASK)
				.typeKey(JavaKeyCodes.VK_M)
				.setModifiers(0)
				.type("2")
				.right(1)
				.type("3")
				.checkGGBMath(
						"(1" + Unicode.INVISIBLE_PLUS + "2 / 3, 1" + Unicode.INVISIBLE_PLUS + "2 / 3)");
	}

	@Test
	void shouldNotSerializeMixedNumberMMSRestriction() {
		startExam(ExamType.MMS); // has FeatureRestriction.DISABLE_MIXED_NUMBERS
		checker
				.setModifiers(KeyEvent.CTRL_MASK)
				.typeKey(JavaKeyCodes.VK_M)
				.setModifiers(0)
				.type("5")
				.right(1)
				.type("1")
				.right(1)
				.type("2")
				.checkGGBMath("5 * 1 / 2", getApp().getEditorFeatures());
	}

	@Test
	void shouldSerializeAssignment() {
		checker
				.type("a=")
				.setModifiers(KeyEvent.CTRL_MASK)
				.typeKey(JavaKeyCodes.VK_M)
				.setModifiers(0)
				.type("1")
				.right(1)
				.type("2")
				.right(1)
				.type("3")
				.checkGGBMath("1" + Unicode.INVISIBLE_PLUS + "2 / 3");
	}

	@Test
	void operatorShouldBeFollowedByZeroSpace() {
		checker.type("1+").checkPlaceholders("1+\u200b");
	}

	@Test
	void pointFromKeyboard() {
		checker.pressSingleKey("$point:2").type("1").right(1).type("2").checkAsciiMath("$point(1,2)");
	}

	@Test
	void vectorFromKeyboard() {
		checker.pressSingleKey("$vector:2").type("1").right(1).type("2").checkAsciiMath("$vector(1,2)");
	}

	@Test
	void vectorFromKeyboardLaTeX() {
		checker
				.pressSingleKey("$vector:2")
				.type("1")
				.right(1)
				.type("2")
				.checkLaTeX("\\begin{pmatrix}\\jlminput{1}\\\\\\jlminput{2}\\\\\\end{pmatrix}");
	}

	@Test
	void matrixFromKeyboard() {
		checker
				.pressSingleKey("$matrix:2:3")
				.type("1")
				.right(1)
				.type("2")
				.right(1)
				.type("3")
				.right(1)
				.type("4")
				.right(1)
				.type("5")
				.right(1)
				.type("6")
				.checkAsciiMath("{{1,2,3},{4,5,6}}");
	}

	@Test
	void matrixFromKeyboardLaTeX() {
		checker
				.pressSingleKey("$matrix:2:3")
				.type("5")
				.right(1)
				.type("-3")
				.right(1)
				.type("0")
				.right(1)
				.type("1")
				.right(1)
				.type("99")
				.right(1)
				.type("1")
				.checkLaTeX("\\begin{pmatrix} \\jlminput{5} & \\jlminput{-3} & \\jlminput{0} "
						+ "\\\\ \\jlminput{1} & \\jlminput{99} & \\jlminput{1} \\end{pmatrix}");
	}

	@Test
	@Issue("APPS-7802")
	void emptyMatrixFromKeyboardLaTeX() {
		checker
				.pressSingleKey("$matrix:2:3")
				.right(1)
				.checkLaTeXWithCursor("\\begin{pmatrix} \\jlminput{\\vspace{0.7}} "
						+ "& \\jlminput{\\jlmcursor{0.9}} & \\jlminput{\\vspace{0.7}} \\\\ "
						+ "\\jlminput{\\vspace{0.7}} & \\jlminput{\\vspace{0.7}} & "
						+ "\\jlminput{\\vspace{0.7}} \\end{pmatrix}");
	}

	@Test
	@Issue("APPS-7000")
	void pointToClipboard() {
		checker.pressSingleKey("$point:2").type("1").right(1).type("2").checkCopy("(1,2)");
	}

	@Test
	@Issue("APPS-7000")
	void vectorToClipboard() {
		checker.pressSingleKey("$vector:2").type("1").right(1).type("3").checkCopy("(1,3)");
	}

	@Test
	@Issue("APPS-6722")
	void minutesAndSecondsShouldBeTreatedAsDerivative() {
		checker.add("f(x)=x^4");
		checker.type("f\u2032").checkGGBMath("f'");
		checker.type("f\u2033").checkGGBMath("f''");
		checker.type("f\u2032\u2032").checkGGBMath("f''");
		checker.type("f\u2033\u2033").checkGGBMath("f''''");
		checker.type("f\u2033\u2032").checkGGBMath("f'''");
		checker.type("f''").left(1).insert("\u2032").checkGGBMath("f'''");
		checker.type("f'").left(1).insert("\u2033").checkGGBMath("f'''");
	}

	@Test
	void colonInLaTeXShouldNotAddSpace() {
		checker.type(":").checkLaTeX("\\mathpunct{:}");
	}

	@Test
	void extendSelection() {
		checker
				.type("123456")
				.left(3)
				.setModifiers(KeyEvent.SHIFT_MASK)
				.typeKey(JavaKeyCodes.VK_END)
				.checkSelection("4", "6");
		checker
				.type("123456")
				.left(3)
				.setModifiers(KeyEvent.SHIFT_MASK)
				.typeKey(JavaKeyCodes.VK_END)
				.left(2)
				.checkSelection("4", "4");
		checker
				.type("123456")
				.left(3)
				.setModifiers(KeyEvent.SHIFT_MASK)
				.typeKey(JavaKeyCodes.VK_HOME)
				.checkSelection("1", "3");
		checker
				.type("123456")
				.left(3)
				.setModifiers(KeyEvent.SHIFT_MASK)
				.typeKey(JavaKeyCodes.VK_HOME)
				.right(1)
				.checkSelection("2", "3");
		checker
				.type("123456")
				.left(3)
				.setModifiers(KeyEvent.SHIFT_MASK)
				.typeKey(JavaKeyCodes.VK_END)
				.typeKey(JavaKeyCodes.VK_HOME)
				.right(1)
				.checkSelection("2", "3");
		checker
				.type("123456")
				.left(6)
				.setModifiers(KeyEvent.SHIFT_MASK)
				.typeKey(JavaKeyCodes.VK_END)
				.typeKey(JavaKeyCodes.VK_HOME)
				.checkSelectionEmpty();
	}

	@Test
	void extendSelectionAtEnd() {
		checker
				.type("123456")
				.left(6)
				.setModifiers(KeyEvent.SHIFT_MASK)
				.typeKey(JavaKeyCodes.VK_HOME)
				.checkSelectionEmpty();
		checker
				.type("123456")
				.setModifiers(KeyEvent.SHIFT_MASK)
				.typeKey(JavaKeyCodes.VK_END)
				.checkSelectionEmpty();
		checker.type("1").left(1).shiftOn().right(1).left(1).checkSelectionEmpty();
		checker.type("1").shiftOn().left(1).right(1).checkSelectionEmpty();
	}

	@Test
	void extendSelectionPower() {
		checker
				.type("123^4")
				.setModifiers(KeyEvent.SHIFT_MASK)
				.left(3)
				.checkSelection("3", "FnSUPERSCRIPT[SequenceNode[4]]");
		checker
				.type("123^4")
				.setModifiers(KeyEvent.SHIFT_MASK)
				.left(4)
				.checkSelection("2", "FnSUPERSCRIPT[SequenceNode[4]]");
	}

	@Test
	void selectAll() {
		checker.type("123456").ctrlA().checkSelection("1", "6");
		checker.type("123456").ctrlA().shiftOn().left(2).checkSelection("1", "4");
	}

	@Test
	@Issue("APPS-7070")
	void spaceToMultiplication() {
		checker.setFormatConverter(new SyntaxAdapterImpl(AppCommonFactory.create().getKernel()));
		checker.type("1 2").checkAsciiMath("1*2");
		checker.type("1+ 2").checkAsciiMath("1+ 2");
		checker.type("\"1 2").checkAsciiMath("\"1 2\"");
	}

	@Test
	void surdLaTeX() {
		checker
				.insert(String.valueOf(Unicode.SQUARE_ROOT))
				.checkLaTeX("\\sqrt{{\\bgcolor{#e6e6eb}\\scalebox{1}[1.6]{\\phantom{g}}}}");
		checker.insert(Unicode.SQUARE_ROOT + "15-1").checkLaTeX("\\sqrt{15}-1");
	}
}
