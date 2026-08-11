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

package org.geogebra.editor.share.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FormulaConverterTest {
	static final String END_PMATRIX = " \\end{pmatrix}";
	static final String BEGIN_PMATRIX = "\\begin{pmatrix} ";
	static final String GREY_BOX =
			"\\bgcolor{#e6e6eb}\\scalebox{1}[1.6]{\\phantom{g}}";
	private final static String PLACEHOLDER1
			= "{" + GREY_BOX + "}";
	private final static String PLACEHOLDER = "{" + PLACEHOLDER1 + "}";
	private final FormulaConverter converter = new FormulaConverter();

	@BeforeEach
	void setup() {
		converter.getTexSerializer().useSimpleMatrixPlaceholders(true);
	}

	@Test
	void testConvertColumnVector() {
		assertEquals(BEGIN_PMATRIX + "1 \\\\ 2" + END_PMATRIX,
				converter.convert("{{1}, {2}}"));
	}

	@Test
	void testConvertEmptyColumnVector() {
		assertEquals(BEGIN_PMATRIX + PLACEHOLDER
						+ " \\\\ " + PLACEHOLDER
						+ " \\\\ " + PLACEHOLDER + END_PMATRIX,
				converter.convert("{{}, {}, {}}"));
	}

	@Test
	void testConvertColumnVectorWithEmptyValue() {
		assertEquals(BEGIN_PMATRIX + PLACEHOLDER
						+ " \\\\ 2" + END_PMATRIX,
				converter.convert("{{?}, {2}}"));
	}

	@Test
	void testConvertMatrix() {
		assertEquals(BEGIN_PMATRIX + "1 & {\\nbsp{}2} \\\\ 3 & {\\nbsp{}4}" + END_PMATRIX,
				converter.convert("{{1, 2}, {3, 4}}"));
	}

	@Test
	void testConvertEmptyMatrix() {
		assertEquals(BEGIN_PMATRIX + PLACEHOLDER + " & " + PLACEHOLDER + " \\\\ "
						+ PLACEHOLDER + " & " + PLACEHOLDER + END_PMATRIX,
				converter.convert("{{,},{,}}"));
	}

	@Test
	void testConvertMatrixWithEmptyValue() {
		assertEquals(BEGIN_PMATRIX + "1 & {\\nbsp{}2} \\\\ 3 & "
						+ PLACEHOLDER + END_PMATRIX,
				converter.convert("{{1, 2}, {3,}}"));
	}

	@Test
	void testConvertEmpty2DPoint() {
		assertEquals("\\left({" + PLACEHOLDER1
				+ "," + PLACEHOLDER1 + "}\\right)", converter.convert("(?,?)"));
	}

	@Test
	void testConvertEmpty3DPoint() {
		assertEquals("\\left({" + PLACEHOLDER1
				+ "," + PLACEHOLDER1 + "," + PLACEHOLDER1
				+ "}\\right)", converter.convert("(,,)"));
	}

	@Test
	void testConvertSemiEmptyPointNumberInLeft() {
		assertEquals("\\left({4," + PLACEHOLDER1 + ","
						+ PLACEHOLDER1 + "}\\right)",
				converter.convert("(4,,)"));
	}

	@Test
	void testConvertSemiEmptyPointNumberInMiddle() {
		assertEquals("\\left({" + PLACEHOLDER1 + ",4,"
						+ PLACEHOLDER1 + "}\\right)",
				converter.convert("(,4,)"));
	}

	@Test
	void testConvertSemiEmptyPointNumberInRight() {
		assertEquals("\\left({" + PLACEHOLDER1 + ","
						+ PLACEHOLDER1 + ",4}\\right)",
				converter.convert("(,,4)"));
	}

}
