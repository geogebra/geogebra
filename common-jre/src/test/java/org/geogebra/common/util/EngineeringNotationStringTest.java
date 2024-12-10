package org.geogebra.common.util;

import static com.himamis.retex.editor.share.util.Unicode.CENTER_DOT;
import static com.himamis.retex.editor.share.util.Unicode.SUPERSCRIPT_0;
import static com.himamis.retex.editor.share.util.Unicode.SUPERSCRIPT_3;
import static com.himamis.retex.editor.share.util.Unicode.SUPERSCRIPT_6;
import static com.himamis.retex.editor.share.util.Unicode.SUPERSCRIPT_MINUS;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.junit.Test;

public class EngineeringNotationStringTest extends BaseUnitTest {

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput1() {
		assertEquals("12 " + CENTER_DOT + " 10" + SUPERSCRIPT_0,
				EngineeringNotationString.format(12, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput2() {
		assertEquals("123.456 " + CENTER_DOT + " 10" + SUPERSCRIPT_3,
				EngineeringNotationString.format(123456, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput3() {
		assertEquals("-18 " + CENTER_DOT + " 10" + SUPERSCRIPT_0,
				EngineeringNotationString.format(-18, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput4() {
		assertEquals("-7.654321 " + CENTER_DOT + " 10" + SUPERSCRIPT_6,
				EngineeringNotationString.format(-7654321, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput5() {
		assertEquals("0 " + CENTER_DOT + " 10" + SUPERSCRIPT_0,
				EngineeringNotationString.format(0, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForIntegerAsInput6() {
		assertEquals("0 " + CENTER_DOT + " 10" + SUPERSCRIPT_0,
				EngineeringNotationString.format(-0, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput1() {
		assertEquals("500 " + CENTER_DOT + " 10" + SUPERSCRIPT_MINUS + SUPERSCRIPT_3,
				EngineeringNotationString.format(0.5, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput2() {
		assertEquals("10 " + CENTER_DOT + " 10" + SUPERSCRIPT_MINUS + SUPERSCRIPT_3,
				EngineeringNotationString.format(0.01, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput3() {
		assertEquals("3 " + CENTER_DOT + " 10" + SUPERSCRIPT_MINUS + SUPERSCRIPT_3,
				EngineeringNotationString.format(0.003, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput4() {
		assertEquals("-17.32 " + CENTER_DOT + " 10" + SUPERSCRIPT_0,
				EngineeringNotationString.format(-17.32, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput5() {
		assertEquals("126.3122 " + CENTER_DOT + " 10" + SUPERSCRIPT_0,
				EngineeringNotationString.format(126.3122, StringType.GEOGEBRA));
	}

	@Test
	public void testCorrectEngineeringNotationForDecimalAsInput6() {
		assertEquals("5.0001 " + CENTER_DOT + " 10" + SUPERSCRIPT_0,
				EngineeringNotationString.format(5.000100, StringType.GEOGEBRA));
	}
}
