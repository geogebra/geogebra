package org.geogebra.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import org.geogebra.common.media.GeoGebraURLParser;
import org.junit.Assert;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

@SuppressWarnings("javadoc")
public class StringUtilTest {
	private static final boolean IS_JAVA_8 = System.getProperty("java.version").startsWith("1.8.");

	@Test
	public void isLetterShouldComplyWithJava() {
		assumeTrue(IS_JAVA_8);
		String falseNeg = "";
		String falsePos = "";
		for (int cc = 0; cc < 65536; ++cc) {
			char c = (char) cc;
			if (Character.isLetter(
					c) != com.himamis.retex.editor.share.input.Character
							.isLetter(c)) {
				if (Character.isLetter(c)) {
					falseNeg += c;
				} else {
					falsePos += c;
				}
			}
		}

		assertEquals(7512, falseNeg.length());
		assertEquals(-1477782608, falseNeg.hashCode());
		assertEquals(4583, falsePos.length());
		assertEquals(-1032620861, falsePos.hashCode());
	}

	@Test
	public void isDigitShouldComplyWithJava() {
		assumeTrue(IS_JAVA_8);
		String falseNeg = "";
		String falsePos = "";
		for (int cc = 0; cc < 65536; ++cc) {
			char c = (char) cc;
			if (Character.isDigit(c) != StringUtil.isDigit(c)) {
				if (Character.isDigit(c)) {
					falseNeg += c;
				} else {
					falsePos += c;
				}
			}
		}
		assertEquals(StringUtil.toHexString(falseNeg),
				falseNeg,
				"\u07C0\u07C1\u07C2\u07C3\u07C4\u07C5\u07C6\u07C7\u07C8\u07C9"
						+ "\u1090\u1091\u1092\u1093\u1094\u1095\u1096\u1097\u1098\u1099"
						+ "\u1946\u1947\u1948\u1949\u194A\u194B\u194C\u194D\u194E\u194F"
						+ "\u19D0\u19D1\u19D2\u19D3\u19D4\u19D5\u19D6\u19D7\u19D8\u19D9"
						+ "\u1A80\u1A81\u1A82\u1A83\u1A84\u1A85\u1A86\u1A87\u1A88\u1A89"
						+ "\u1A90\u1A91\u1A92\u1A93\u1A94\u1A95\u1A96\u1A97\u1A98\u1A99"
						+ "\uA620\uA621\uA622\uA623\uA624\uA625\uA626\uA627\uA628\uA629"
						+ "\uA900\uA901\uA902\uA903\uA904\uA905\uA906\uA907\uA908\uA909"
						+ "\uA9D0\uA9D1\uA9D2\uA9D3\uA9D4\uA9D5\uA9D6\uA9D7\uA9D8\uA9D9"
						+ "\uAA50\uAA51\uAA52\uAA53\uAA54\uAA55\uAA56\uAA57\uAA58\uAA59"
						+ "\uABF0\uABF1\uABF2\uABF3\uABF4\uABF5\uABF6\uABF7\uABF8\uABF9"
						+ "\uFF10\uFF11\uFF12\uFF13\uFF14\uFF15\uFF16\uFF17\uFF18\uFF19");

		assertEquals(StringUtil.toHexString(falsePos), falsePos,
				"");
	}

	@Test
	public void isWhitespaceShouldComplyWithJava() {
		assumeTrue(IS_JAVA_8);
		for (int cc = 0; cc < 65536; ++cc) {
			char c = (char) cc;
			if (Character.isWhitespace(c) != StringUtil.isWhitespace(c)) {
				Assert.fail("isWhitespace failed " + c + " "
						+ StringUtil.toHexString(c) + Character.isWhitespace(c)
						+ " " + StringUtil.isWhitespace(c));
			}
		}
	}

	@Test
	public void checkURIparser() {
		assertEquals("k89JtCqY", GeoGebraURLParser
				.getIDfromURL("https://www.geogebra.org/m/k89JtCqY"));
		assertEquals("k89JtCqY", GeoGebraURLParser
				.getIDfromURL("http://www.geogebra.org/m/k89JtCqY"));
		assertEquals("k89JtCqY",
				GeoGebraURLParser.getIDfromURL("www.geogebra.org/m/k89JtCqY"));
		assertEquals("k89JtCqY",
				GeoGebraURLParser.getIDfromURL("http://ggbm.at/k89JtCqY"));
		assertEquals("k89JtCqY",
				GeoGebraURLParser.getIDfromURL("http://ggbtu.be/mk89JtCqY"));

	}

	@Test
	public void checkUriParserWithM() {
		assertEquals("mAukGjbN", GeoGebraURLParser
				.getIDfromURL("https://www.geogebra.org/m/mAukGjbN"));
	}

	@Test
	public void testNewlines() {
		String in = "a\n\n\n\nb";
		String out = "<div>a</div><div><br></div><div><br></div><div><br></div><div>b</div>";
		compatibleNewlines(in, out);
	}

	@Test
	public void testNewlinesTrailing() {
		String in = "\n\na\nb";
		String out = "<div><br></div><div><br></div><div>a</div><div>b</div>";
		compatibleNewlines(in, out);
	}

	@Test
	public void testNumberToIndex() {
		assertEquals("" + Unicode.SUPERSCRIPT_2 + Unicode.SUPERSCRIPT_7,
				StringUtil.numberToIndex(27));
		assertEquals("" + Unicode.SUPERSCRIPT_MINUS
						+ Unicode.SUPERSCRIPT_2 + Unicode.SUPERSCRIPT_7,
				StringUtil.numberToIndex(-27));
		assertEquals("" + Unicode.SUPERSCRIPT_0,
				StringUtil.numberToIndex(0));
	}

	@Test
	public void testIndexToNumber() {
		assertEquals(27, StringUtil.indexToNumber("" + Unicode.SUPERSCRIPT_2
						+ Unicode.SUPERSCRIPT_7));
		assertEquals(-27, StringUtil.indexToNumber("" + Unicode.SUPERSCRIPT_MINUS
						+ Unicode.SUPERSCRIPT_2 + Unicode.SUPERSCRIPT_7));
		assertEquals(0, StringUtil.indexToNumber("" + Unicode.SUPERSCRIPT_0));
	}

	private static void compatibleNewlines(String in, String out) {
		assertEquals(out, StringUtil.newlinesToHTML(in));
		assertEquals(StringUtil.toJavaString(in),
				StringUtil.toJavaString(StringUtil.htmlToNewlines(out)));
	}

}
