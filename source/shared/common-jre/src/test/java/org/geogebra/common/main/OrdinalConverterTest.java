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

package org.geogebra.common.main;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.util.lang.Language;
import org.junit.Test;

public class OrdinalConverterTest {

	@Test
	public void testOrdinalNumbersForBulgarian() {
		Language language = Language.Bulgarian;
		assertOrdinalEquals("0-\u0442\u0438", 0, language);
		assertOrdinalEquals("1-\u0432\u0438", 1, language);
		assertOrdinalEquals("2-\u0440\u0438", 2, language);
		assertOrdinalEquals("3-\u0442\u0438", 3, language);
		assertOrdinalEquals("31-\u0432\u0438", 31, language);
		assertOrdinalEquals("32-\u0440\u0438", 32, language);
		assertOrdinalEquals("33-\u0442\u0438", 33, language);
		assertOrdinalEquals("47-\u0442\u0438", 47, language);
		assertOrdinalEquals("101-\u0432\u0438", 101, language);
		assertOrdinalEquals("102-\u0440\u0438", 102, language);
		assertOrdinalEquals("103-\u0442\u0438", 103, language);
		assertOrdinalEquals("224-\u0442\u0438", 224, language);
		assertOrdinalEquals("738-\u0442\u0438", 738, language);
	}

	@Test
	public void testOrdinalNumbersForCatalan() {
		assertOrdinalEquals("0", 0, Language.Catalan);
		assertOrdinalEquals("1r", 1, Language.Valencian);
		assertOrdinalEquals("2n", 2, Language.Catalan);
		assertOrdinalEquals("3r", 3, Language.Valencian);
		assertOrdinalEquals("4t", 4, Language.Catalan);
		assertOrdinalEquals("5e", 5, Language.Valencian);
		assertOrdinalEquals("11e", 11, Language.Catalan);
		assertOrdinalEquals("23e", 23, Language.Valencian);
		assertOrdinalEquals("55e", 55, Language.Catalan);
		assertOrdinalEquals("101e", 101, Language.Valencian);
	}

	@Test
	public void testOrdinalNumbersForEnglish() {
		assertOrdinalEquals("1st", 1, Language.English_Australia);
		assertOrdinalEquals("2nd", 2, Language.English_US);
		assertOrdinalEquals("3rd", 3, Language.English_UK);
		assertOrdinalEquals("7th", 7, Language.English_Australia);
		assertOrdinalEquals("12th", 12, Language.English_US);
		assertOrdinalEquals("21st", 21, Language.English_UK);
		assertOrdinalEquals("101st", 101, Language.English_Australia);
		assertOrdinalEquals("113th", 113, Language.English_US);
		assertOrdinalEquals("557th", 557, Language.English_UK);
		assertOrdinalEquals("1001st", 1001, Language.English_Australia);
	}

	@Test
	public void testOrdinalNumbersForFrench() {
		Language language = Language.French;
		assertOrdinalEquals("1er", 1, language);
		assertOrdinalEquals("2e", 2, language);
		assertOrdinalEquals("5e", 5, language);
		assertOrdinalEquals("21e", 21, language);
		assertOrdinalEquals("125e", 125, language);
	}

	@Test
	public void testOrdinalNumbersForHebrew() {
		Language language = Language.Hebrew;
		assertOrdinalEquals("\u200f\u200e1\u200e\u200f", 1, language);
		assertOrdinalEquals("\u200f\u200e2\u200e\u200f", 2, language);
		assertOrdinalEquals("\u200f\u200e5\u200e\u200f", 5, language);
		assertOrdinalEquals("\u200f\u200e21\u200e\u200f", 21, language);
		assertOrdinalEquals("\u200f\u200e137\u200e\u200f", 137, language);
	}

	@Test
	public void testOrdinalNumbersForIndonesian() {
		Language language = Language.Indonesian;
		assertOrdinalEquals("ke-1", 1, language);
		assertOrdinalEquals("ke-2", 2, language);
		assertOrdinalEquals("ke-13", 13, language);
		assertOrdinalEquals("ke-37", 37, language);
		assertOrdinalEquals("ke-223", 223, language);
	}

	@Test
	public void testOrdinalNumbersForSwedish() {
		Language language = Language.Swedish;
		assertOrdinalEquals("1:a", 1, language);
		assertOrdinalEquals("2:a", 2, language);
		assertOrdinalEquals("5:e", 5, language);
		assertOrdinalEquals("11:e", 11, language);
		assertOrdinalEquals("12:e", 12, language);
		assertOrdinalEquals("17:e", 17, language);
		assertOrdinalEquals("33:e", 33, language);
		assertOrdinalEquals("51:a", 51, language);
		assertOrdinalEquals("111:e", 111, language);
		assertOrdinalEquals("568:e", 568, language);
		assertOrdinalEquals("771:a", 771, language);
		assertOrdinalEquals("1012:e", 1012, language);
	}

	private void assertOrdinalEquals(String ordinal, int number, Language language) {
		String message = String.format(
				"The expected ordinal number for the input %d is %s!", number, ordinal);
		assertEquals(message, ordinal, OrdinalConverter.getOrdinalNumber(language, number));
	}
}
