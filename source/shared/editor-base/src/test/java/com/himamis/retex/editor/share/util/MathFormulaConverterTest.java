package com.himamis.retex.editor.share.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathFormulaConverterTest {
	public static final String END_PMATRIX = " \\end{pmatrix}";
	public static final String BEGIN_PMATRIX = "\\begin{pmatrix} ";
	public static final String GREY_BOX =
			"\\bgcolor{#dcdcdc}\\scalebox{1}[1.6]{\\phantom{g}}";
	private final static String PLACEHOLDER1
			= "{" + GREY_BOX + "}";
	private final static String PLACEHOLDER = "{" + PLACEHOLDER1 + "}";
	private final MathFormulaConverter converter = new MathFormulaConverter();

	@Test
	public void testConvertColumnVector() {
		assertEquals(BEGIN_PMATRIX + "1 \\\\ 2" + END_PMATRIX,
				converter.convert("{{1}, {2}}"));
	}

	@Test
	public void testConvertEmptyColumnVector() {
		assertEquals(BEGIN_PMATRIX + PLACEHOLDER
						+ " \\\\ " + PLACEHOLDER
						+ " \\\\ " + PLACEHOLDER + END_PMATRIX,
				converter.convert("{{}, {}, {}}"));
	}

	@Test
	public void testConvertColumnVectorWithEmptyValue() {
		assertEquals(BEGIN_PMATRIX + PLACEHOLDER
						+ " \\\\ 2" + END_PMATRIX,
				converter.convert("{{?}, {2}}"));
	}

	@Test
	public void testConvertMatrix() {
		assertEquals(BEGIN_PMATRIX + "1 & {\\nbsp{}2} \\\\ 3 & {\\nbsp{}4}" + END_PMATRIX,
				converter.convert("{{1, 2}, {3, 4}}"));
	}

	@Test
	public void testConvertEmptyMatrix() {
		assertEquals(BEGIN_PMATRIX + PLACEHOLDER + " & " + PLACEHOLDER + " \\\\ "
						+ PLACEHOLDER + " & " + PLACEHOLDER + END_PMATRIX,
				converter.convert("{{,},{,}}"));
	}

	@Test
	public void testConvertMatrixWithEmptyValue() {
		assertEquals(BEGIN_PMATRIX + "1 & {\\nbsp{}2} \\\\ 3 & "
						+ PLACEHOLDER + END_PMATRIX,
				converter.convert("{{1, 2}, {3,}}"));
	}

	@Test
	public void testConvertEmpty2DPoint() {
		assertEquals("\\left({" + PLACEHOLDER1
				+ "," + PLACEHOLDER1 + "}\\right)", converter.convert("(?,?)"));
	}

	@Test
	public void testConvertEmpty3DPoint() {
		assertEquals("\\left({" + PLACEHOLDER1
				+ "," + PLACEHOLDER1 + "," + PLACEHOLDER1
				+ "}\\right)", converter.convert("(,,)"));
	}

	@Test
	public void testConvertSemiEmptyPointNumberInLeft() {
		assertEquals("\\left({4," + PLACEHOLDER1 + ","
						+ PLACEHOLDER1 + "}\\right)",
				converter.convert("(4,,)"));
	}

	@Test
	public void testConvertSemiEmptyPointNumberInMiddle() {
		assertEquals("\\left({" + PLACEHOLDER1 + ",4,"
						+ PLACEHOLDER1 + "}\\right)",
				converter.convert("(,4,)"));
	}

	@Test
	public void testConvertSemiEmptyPointNumberInRight() {
		assertEquals("\\left({" + PLACEHOLDER1 + ","
						+ PLACEHOLDER1 + ",4}\\right)",
				converter.convert("(,,4)"));
	}

}
