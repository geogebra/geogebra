package org.geogebra.common.jre.main;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.util.lang.Language;
import org.geogebra.test.LocalizationCommonUTF;
import org.junit.Before;
import org.junit.Test;

public class LocalizationTest {

	private LocalizationCommon loc;

	@Before
	public void init() {
		loc  = spy(new LocalizationCommonUTF(3));
		when(loc.getSupportedLanguages(false)).thenReturn(Language.values());
	}

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
			Locale closestSupported = loc.getClosestSupportedLocale(locale);
			assertThat(locale, is(closestSupported));
		}
	}

	@Test
	public void getsClosestSupportedLocaleFromLanguageTag() {
		assertLookupReturnsLanguageTag("mn-Mong-MN", "mn-Mong");
		assertLookupReturnsLanguageTag("nb-NO-Cyrl", "nb");
		assertLookupReturnsLanguageTag("zh", "zh-CN");
	}

	private void assertLookupReturnsLanguageTag(String lookupTag, String expectedTag) {
		assertThat(loc.getClosestSupportedLocale(Locale.forLanguageTag(lookupTag)),
				is(Locale.forLanguageTag(expectedTag)));
	}
}
