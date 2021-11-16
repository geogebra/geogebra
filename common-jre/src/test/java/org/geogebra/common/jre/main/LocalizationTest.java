package org.geogebra.common.jre.main;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.test.LocalizationCommonUTF;
import org.junit.Before;
import org.junit.Test;

public class LocalizationTest {

	private LocalizationCommon loc;

	@Before
	public void init() {
		this.loc = new LocalizationCommonUTF(3);
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

}
