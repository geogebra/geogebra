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

package org.geogebra.common.cas;

import static org.junit.Assert.assertEquals;

import org.geogebra.editor.share.util.Unicode;
import org.junit.Test;

public class CASparserTest {

	@Test
	public void replaceIndicesShouldPassThroughPlainAscii() {
		assertEquals("abc", CASparser.replaceIndices("abc", true));
		assertEquals("abc", CASparser.replaceIndices("abc", false));
	}

	@Test
	public void replaceIndicesShouldHandleEmptyString() {
		assertEquals("", CASparser.replaceIndices("", true));
		assertEquals("", CASparser.replaceIndices("", false));
	}

	@Test
	public void replaceIndicesShouldPassSingleCharIndex() {
		assertEquals("a_1", CASparser.replaceIndices("a_1", true));
		assertEquals("a_1", CASparser.replaceIndices("a_1", false));
	}

	@Test
	public void replaceIndicesShouldEncodeNonWordSingleCharIndex() {
		assertEquals("a_unicode43u", CASparser.replaceIndices("a_+", true));
	}

	@Test
	public void replaceIndicesShouldUnwrapNonWordCharacter() {
		assertEquals("a_unicode43u", CASparser.replaceIndices("a_{+}", true));
	}

	@Test
	public void replaceIndicesShouldUnwrapIndex() {
		assertEquals("a_1", CASparser.replaceIndices("a_{1}", true));
		assertEquals("x_1,y_1", CASparser.replaceIndices("x_{1},y_{1}", true));
	}

	@Test
	public void replaceIndicesShouldEncodeLeadingUnderscoreIndex() {
		assertEquals("_a", CASparser.replaceIndices("_a", true));
	}

	@Test
	public void replaceIndicesShouldEncodeMultiCharBracedIndex() {
		assertEquals("a_unicode123u12unicode125u",
				CASparser.replaceIndices("a_{12}", true));
	}

	@Test
	public void replaceIndicesShouldEncodeUnicodeInsideBracedIndex() {
		// α = α = 945, β = β = 946
		assertEquals(
				"a_unicode123uunicode945uunicode946uunicode125u",
				CASparser.replaceIndices("a_{αβ}", true));
		assertEquals(
				"a_unicode123uunicode945uunicode946uunicode125u",
				CASparser.replaceIndices("a_{αβ}", false));
	}

	@Test
	public void replaceIndicesShouldEncodeChainedSingleCharIndices() {
		// '1' returns state to NORMAL, then '_b' starts a fresh single-char index
		assertEquals("a_1_b",
				CASparser.replaceIndices("a_1_b", true));
	}

	@Test
	public void replaceIndicesShouldUnescapeBackslashUnderscore() {
		assertEquals("_a", CASparser.replaceIndices("\\_a", true));
	}

	@Test
	public void replaceIndicesShouldReplaceEulerCharWithE() {
		String input = Unicode.EULER_STRING;
		assertEquals("e", CASparser.replaceIndices(input, true));
		assertEquals("e", CASparser.replaceIndices(input, false));
	}

	@Test
	public void replaceIndicesShouldEncodeNonAsciiWhenRequested() {
		// π = π = 960
		assertEquals("unicode960u", CASparser.replaceIndices("π", true));
	}

	@Test
	public void replaceIndicesShouldPreserveNonAsciiWhenNotRequested() {
		assertEquals("π", CASparser.replaceIndices("π", false));
	}

	@Test
	public void replaceIndicesShouldPreserveMeasuredAngle() {
		String input = String.valueOf(Unicode.MEASURED_ANGLE);
		assertEquals(input, CASparser.replaceIndices(input, true));
		assertEquals(input, CASparser.replaceIndices(input, false));
	}

	@Test
	public void replaceIndicesShouldPreserveOperators() {
		assertEquals("a+b*c-d/e",
				CASparser.replaceIndices("a+b*c-d/e", true));
	}

	@Test
	public void replaceIndicesShouldHandleConsecutiveBracedIndices() {
		// a_{12}+b_{34}
		String expected = "a_unicode123u12unicode125u+b_unicode123u34unicode125u";
		assertEquals(expected,
				CASparser.replaceIndices("a_{12}+b_{34}", true));
	}

	@Test
	public void replaceIndicesShouldEncodeNonAsciiOutsideIndex() {
		// Mix of plain char, non-ASCII, plain char
		assertEquals("aunicode945ub", CASparser.replaceIndices("aαb", true));
		assertEquals("aαb", CASparser.replaceIndices("aαb", false));
	}

	@Test
	public void replaceIndicesShouldKeepEulerEvenAtIndexStart() {
		// _ followed by Euler char: state transitions to UNDERSCORE for '_',
		// then Euler char is processed via UNDERSCORE branch (else → NORMAL),
		// which appendcodes the char as unicode<EULER_CODE>u, not 'e'.
		String input = "a_" + Unicode.EULER_CHAR;
		assertEquals("a_unicode" + (int) Unicode.EULER_CHAR + "u",
				CASparser.replaceIndices(input, true));
	}
}
