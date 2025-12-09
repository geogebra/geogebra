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
 
package org.geogebra.common.jre.main;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Locale;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.util.lang.Language;
import org.geogebra.test.LocalizationCommonUTF;
import org.junit.Test;

public class LocalizationTest {

	private final LocalizationCommon loc = new LocalizationCommonUTF(3);

	@Test
	public void shouldLoadGermanProperties() {
		loc.setLocale(Locale.GERMAN);
		assertEquals("Farbe", loc.getMenu("Color"));
	}

	@Test
	public void shouldLoadBritishProperties() {
		loc.setLocale(Locale.UK);
		assertEquals("Colour", loc.getMenu("Color"));
	}

	@Test
	public void shouldLoadNynorskProperties() {
		loc.setLocale(new Locale("nn"));
		assertEquals("Farge", loc.getMenu("Color"));
	}

	@Test
	public void shouldReadPropertiesAsUTF8() {
		loc.setLocale(Locale.UK);
		assertEquals("R\u00b2", loc.getMenu("RSquare.Short"));
	}

	@Test
	public void getsClosestSupportedLocaleFromLanguageValues() {
		for (Language language : Language.values()) {
			Locale locale = Locale.forLanguageTag(language.toLanguageTag());
			Language closestSupported = loc.getClosestSupportedLanguage(locale);
			assertThat(locale, is(Locale.forLanguageTag(closestSupported.toLanguageTag())));
		}
	}

	@Test
	public void getsClosestSupportedLocaleFromLanguageTag() {
		assertLookupReturnsLanguageTag("en-CA", "en");
		assertLookupReturnsLanguageTag("mn-Mong-MN", "mn-Mong");
		assertLookupReturnsLanguageTag("nb-NO-Cyrl", "nb");
		assertLookupReturnsLanguageTag("zh", "zh-CN");
		assertLookupReturnsLanguageTag("zh-HK", "zh-CN");
	}

	@Test
	public void testReverseCommand() {
		loc.setLocale(Locale.UK);
		AppCommon app = new AppCommon(loc, new AwtFactoryCommon());
		app.resetCommandDict();
		assertNull(loc.getReverseCommand("x"));
		assertEquals("Center", loc.getReverseCommand("Centre"));
	}

	private void assertLookupReturnsLanguageTag(String lookupTag, String expectedTag) {
		assertThat(loc.getClosestSupportedLanguage(
				Locale.forLanguageTag(lookupTag)).toLanguageTag(), is(expectedTag));
	}
}
