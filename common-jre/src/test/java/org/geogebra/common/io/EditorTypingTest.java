package org.geogebra.common.io;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.test.TestStringUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;
import com.himamis.retex.editor.share.model.Korean;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class EditorTypingTest {

	private static AppCommon appC;

	@BeforeClass
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
		appC = BaseUnitTest.createAppCommon();
	}

	private void checkEditorInsert(String input, String output) {
		new EditorChecker().insert(input).checkAsciiMath(output);

	}

	private class EditorChecker {
		private MathFieldCommon mathField = new MathFieldCommon();

		protected EditorChecker() {
			// avoid synthetic access: can't be private
		}

		public void checkAsciiMath(String output) {
			MathSequence rootComponent = getRootComponent();
			Assert.assertEquals(output,
					GeoGebraSerializer.serialize(rootComponent));
		}

		public void checkGGBMath(String output) {
			MathSequence rootComponent = getRootComponent();

			String exp = GeoGebraSerializer.serialize(rootComponent);

			ExpressionNode en;
			try {
				en = appC.getKernel().getParser().parseExpression(exp);
				Assert.assertEquals(output, en.toString(StringTemplate.defaultTemplate));
			} catch (ParseException e) {
				e.printStackTrace();
				Assert.assertEquals(output, "Exception: " + e.toString());
			}

		}

		public EditorChecker type(String input) {
			KeyboardInputAdapter.emulateInput(mathField.getInternal(), input);
			return this;
		}

		public EditorChecker insert(String input) {
			mathField.insertString(input);
			return this;
		}

		public EditorChecker typeKey(int key) {
			mathField.getInternal().onKeyPressed(new KeyEvent(key, 0, '\0'));
			return this;
		}

		public void checkRaw(String output) {
			MathSequence rootComponent = getRootComponent();
			Assert.assertEquals(output, rootComponent + "");
		}

		private MathSequence getRootComponent() {
			MathFieldInternal mathFieldInternal = mathField.getInternal();
			EditorState editorState = mathFieldInternal.getEditorState();
			return editorState.getRootComponent();
		}
	}

	@Test
	public void testEditorUnicode() {
		checkEditorInsert(TestStringUtil.unicode("x/sqrt(x^2+4)"),
				TestStringUtil.unicode("x/sqrt(x^2+4)"));
		checkEditorInsert("x/(" + Unicode.EULER_STRING + "^x+1)",
				"x/(" + Unicode.EULER_STRING + "^x+1)");

		checkEditorInsert("3*x", "3*x");
	}

	@Test
	public void testEditor() {
		checkEditorInsert("sqrt(x/2)", "sqrt(x/2)");

		checkEditorInsert("1+2+3-4", "1+2+3-4");
		checkEditorInsert("12345", "12345");
		checkEditorInsert("1/2/3/4", "1/2/3/4");
		checkEditorInsert("Segment[(1,2),(3,4)]", "Segment[(1,2),(3,4)]");

		// typing second | starts another abs() clause
		checkEditorInsert("3|x", "3*abs(x)");
		checkEditorInsert("3 |x", "3 *abs(x)");
		checkEditorInsert("3*|x", "3*abs(x)");
		checkEditorInsert("x|xx", "x*abs(xx)");
		checkEditorInsert("x |x x", "x *abs(x x)");
		checkEditorInsert("x*|x*x", "x*abs(x*x)");
		checkEditorInsert("x sqrt(x)", "x sqrt(x)");
		checkEditorInsert("x" + Unicode.SQUARE_ROOT + "x+1", "x*sqrt(x+1)");
	}

	@Test
	public void testTrig() {

		type("sin(x)").checkGGBMath("sin(x)");
		type("sin(x)^2").checkGGBMath("sin" + Unicode.SUPERSCRIPT_2 + "(x)");
		type("sin(x)^3").checkGGBMath("sin" + Unicode.SUPERSCRIPT_3 + "(x)");
		type("sin(x)^123").checkGGBMath("sin" + Unicode.SUPERSCRIPT_1 + Unicode.SUPERSCRIPT_2
				+ Unicode.SUPERSCRIPT_3 + "(x)");
		type("sin^2").typeKey(JavaKeyCodes.VK_RIGHT).type("(x)")
				.checkGGBMath("sin" + Unicode.SUPERSCRIPT_2 + "(x)");
		type("sin^-1").typeKey(JavaKeyCodes.VK_RIGHT).type("(x)")
				.checkGGBMath("sin" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(x)");
	}

	@Test
	public void testKorean() {

		checkEditorInsert("\u3147\u314F\u3139\u314D\u314F", "\uC54C\uD30C");
		checkEditorInsert("\u314A\u315C\u3139\u3131\u314F", "\uCD9C\uAC00");

		checkEditorInsert("\u3142", "\u3142");
		checkEditorInsert("\u3142\u315C", "\uBD80");
		checkEditorInsert("\u3142\u315C\u3134", "\uBD84");
		checkEditorInsert("\u3142\u315C\u3134\u314E", "\uBD86");

		checkEditorInsert("\u3142\u315C\u3134\u314E\u3150", "\uBD84\uD574");
		checkEditorInsert("\u3142\u315C\u3134\u314E\u3155", "\uBD84\uD600");
		checkEditorInsert("\u3142\u315C\u3134\u314E\u315B", "\uBD84\uD6A8");

		checkEditorInsert("\u314D\u3157\u314E", "\uD407");
		checkEditorInsert("\u3141\u3163\u3142\u315C\u3134", "\uBBF8\uBD84");

		checkEditorInsert("\u3147\u315F", "\uC704");

		checkEditorInsert("\u3131\u314F\u3144", "\uAC12");

		checkEditorInsert("\u314E\u314F\u3134\u3146\u314F\u3147",
				"\uD55C\uC30D");

		checkEditorInsert("\u314E\u314F\u3145\u3145\u314F\u3147",
				"\uD56B\uC0C1");

		// small steps
		checkEditorInsert("\u314E\u314F", "\uD558");
		checkEditorInsert("\u314E\u314F\u3145", "\uD56B");
		checkEditorInsert("\u314E\u314F\u3145\u3145", "\uD56B\u3145");
		checkEditorInsert("\u314E\u314F\u3145\u3145\u314F", "\uD56B\uC0AC");

		checkEditorInsert("\u3131\u3161", "\uADF8");
		checkEditorInsert("\u3131\u3161\u3131", "\uADF9");
		checkEditorInsert("\u3131\u3161\u3132", "\uADFA");
		checkEditorInsert("\u3131\u3161\u3131\u3131\u314F", "\uADF9\uAC00");
		checkEditorInsert("\u3131\u3161\u3131\u3131\u314F\u3142",
				"\uADF9\uAC11");
		checkEditorInsert("\u3131\u3161\u3131\u3131\u314F\u3144",
				"\uADF9\uAC12");

		checkEditorInsert("\u314E\u314F\u3134\u3146\u314F\u3147",
				"\uD55C\uC30D");

		checkEditorInsert("\u314E\u314F\u3146\u314F\u3147", "\uD558\uC30D");

		checkEditorInsert("\u3134\u3153\u313C\u3147\u3163", "\uB113\uC774");
		checkEditorInsert("\u3147\u314F\u3136\u3137\u314F", "\uC54A\uB2E4");
		checkEditorInsert("\u3131\u314F\u3144\u3147\u3161\u3134",
				"\uAC12\uC740");

		checkEditorInsert("\u3131\u314F\u3144\u3145\u314F\u3134",
				"\uAC12\uC0B0");

		checkEditorInsert(Korean.flattenKorean("\uB098"), "\uB098");
		checkEditorInsert(Korean.flattenKorean("\uB108"), "\uB108");
		checkEditorInsert(Korean.flattenKorean("\uC6B0\uB9AC"), "\uC6B0\uB9AC");
		checkEditorInsert(Korean.flattenKorean("\uBBF8\uBD84"), "\uBBF8\uBD84");
		checkEditorInsert(Korean.flattenKorean("\uBCA1\uD130"), "\uBCA1\uD130");
		checkEditorInsert(Korean.flattenKorean("\uC0C1\uC218"), "\uC0C1\uC218");
		checkEditorInsert(Korean.flattenKorean("\uB2ED\uBA39\uC5B4"),
				"\uB2ED\uBA39\uC5B4");
		checkEditorInsert(Korean.flattenKorean("\uC6EC\uC77C"), "\uC6EC\uC77C");
		checkEditorInsert(Korean.flattenKorean("\uC801\uBD84"), "\uC801\uBD84");
		checkEditorInsert(Korean.flattenKorean("\uC288\uD37C\uB9E8"),
				"\uC288\uD37C\uB9E8");
		checkEditorInsert(Korean.flattenKorean("\u3138"), "\u1104");
		checkEditorInsert(Korean.flattenKorean("\uC778\uD14C\uADF8\uB784"),
				"\uC778\uD14C\uADF8\uB784");
		checkEditorInsert(Korean.flattenKorean("\u3137"), "\u1103");
		checkEditorInsert(Korean.flattenKorean("\u3131"), "\u1100");
		checkEditorInsert(Korean.flattenKorean("\u3134"), "\u1102");
		checkEditorInsert(Korean.flattenKorean("\uC8FC\uC778\uC7A5"),
				"\uC8FC\uC778\uC7A5");
		checkEditorInsert(
				Korean.flattenKorean("\uC774\uC81C\uC880\uC790\uC790"),
				"\uC774\uC81C\uC880\uC790\uC790");
		checkEditorInsert(
				Korean.flattenKorean("\uC544\uBAA8\uB974\uACA0\uB2E4"),
				"\uC544\uBAA8\uB974\uACA0\uB2E4");

		checkEditorInsert("\u3146\u1161\u11BC", "\uC30D");
		checkEditorInsert("\u110A\u1161\u11BC", "\uC30D");

		checkEditorInsert("\u3142\u315C", "\uBD80");
		checkEditorInsert("\u3142\u315E", "\uBDB8");
		checkEditorInsert("\u3142\u315E\u3139", "\uBDC0");
		checkEditorInsert("\u3142\u315E\u313A", "\uBDC1");

		// testEditor("\u3132", "\u1101");
		checkEditorInsert("\u3132\u314F", "\uAE4C");

		checkEditorInsert("\u3131\u3157\u3142\u3131\u3161\u3134",
				"\uACF1\uADFC");
		checkEditorInsert("\u3147\u3163\u3142\u3139\u3155\u3131",
				"\uC785\uB825");

		checkEditorInsert("\u3147\u3157\u314F\u3134\u3139\u315B",
				"\uC644\uB8CC");
		checkEditorInsert("\u3131\u3157\u3142\u314E\u314F\u3131\u3163",
				"\uACF1\uD558\uAE30");

		// some middle (vowel) characters need doubling (no other way to enter
		// them)
		// eg \u315c \u3153 = \u116f
		checkEditorInsert("\u3147\u315c", "\uc6b0");
		checkEditorInsert("\u3147\u315c\u3153", "\uc6cc");
		checkEditorInsert("\u3147\u315c\u3153\u3134", "\uc6d0");
		checkEditorInsert("\u3147\u3157\u314F", "\uC640");
		// ... and same for tail
		checkEditorInsert("\u3137\u314F\u3139\u3131", "\uB2ED");
	}

	@Test
	public void testInverseTrigEditor() {
		type("cos" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(1)/2").checkRaw(
				"MathSequence[FnFRAC[MathSequence[FnAPPLY[MathSequence[c, o, s, "
						+ Unicode.SUPERSCRIPT_MINUS + ", "
						+ Unicode.SUPERSCRIPT_1
						+ "], MathSequence[1]]], MathSequence[2]]]");
	}

	@Test
	public void testLogBase() {
		type("log_2").typeKey(JavaKeyCodes.VK_RIGHT).type("(4)").checkRaw(
				"MathSequence[FnLOG[MathSequence[2], MathSequence[4]]]");
	}

	@Test
	public void testSlash() {
		type("/1").typeKey(JavaKeyCodes.VK_RIGHT).type("2")
				.checkAsciiMath("(1)/(2)");
		type("1/2").checkAsciiMath("(1)/(2)");
		type("12").typeKey(JavaKeyCodes.VK_LEFT).type("/")
				.checkAsciiMath("(1)/()2");
	}

	@Test
	public void testDivision() {
		type(Unicode.DIVIDE + "1").typeKey(JavaKeyCodes.VK_RIGHT).type("2")
				.checkAsciiMath("1/2");
		type("1" + Unicode.DIVIDE + "2").checkAsciiMath("1/2");
		type("12").typeKey(JavaKeyCodes.VK_LEFT).type(Unicode.DIVIDE + "")
				.checkAsciiMath("1/2");
	}

	@Test
	public void testBracketsAroundFunction() {
		type("ln(x").typeKey(JavaKeyCodes.VK_LEFT)
				.typeKey(JavaKeyCodes.VK_LEFT).typeKey(JavaKeyCodes.VK_LEFT)
				.typeKey(JavaKeyCodes.VK_LEFT).type("(")
				.checkAsciiMath("(ln(x))");
	}

	@Test
	public void testBracketsAfterEquals() {
		type("f(p").typeKey(JavaKeyCodes.VK_RIGHT)
				.type("=ln(p*2.72").typeKey(JavaKeyCodes.VK_RIGHT)
				.type("+3)").checkAsciiMath("f(p)=(ln(p*2.72)+3)");
	}

	@Test
	public void testBackspace() {
		type("8" + Unicode.DIVIDE).typeKey(JavaKeyCodes.VK_BACK_SPACE).type(Unicode.DIVIDE + "2")
				.checkAsciiMath("8/2");
		type("8/").typeKey(JavaKeyCodes.VK_BACK_SPACE).type("/2")
				.checkAsciiMath("(8)/(2)");
	}

	private EditorChecker type(String input) {
		return new EditorChecker().type(input);
	}
}
