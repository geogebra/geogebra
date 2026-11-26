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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Locale;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link App#getInternalCommand(String)}.
 */
class InternalCommandTest extends BaseUnitTest {

	@Test
	void localizedNameIsTranslatedIgnoringCase() {
		getApp().setLocale(Locale.ENGLISH);
		assertAll(
				() -> assertEquals("Midpoint", getApp().getInternalCommand("Midpoint")),
				() -> assertEquals("Midpoint", getApp().getInternalCommand("midPOINT")),
				() -> assertNull(getApp().getInternalCommand("NoSuchCommand")));
	}

	@Test
	void languageChangeUpdatesTheTranslation() {
		getApp().setLocale(Locale.ENGLISH);
		assertEquals("Circle", getApp().getInternalCommand("Circle"));

		getApp().setLocale(Locale.GERMAN);
		assertAll(
				() -> assertEquals("Circle", getApp().getInternalCommand("Kreis")),
				() -> assertNull(getApp().getInternalCommand("Cercle")));

		getApp().setLocale(Locale.FRENCH);
		assertAll(
				() -> assertEquals("Circle", getApp().getInternalCommand("Cercle")),
				() -> assertNull(getApp().getInternalCommand("Kreis")));
	}

	@Test
	void nameTranslatingSeveralCommandsResolvesLikeTheParser() {
		// Mittelpunkt is the German name of both Midpoint and Center
		getApp().setLocale(Locale.GERMAN);
		assertEquals(
				getApp().getReverseCommand("Mittelpunkt"), getApp().getInternalCommand("Mittelpunkt"));
	}

	@Test
	void nameOfACommandWithoutSyntaxResolvesToTheCommandOfTheParser() {
		// in Japanese, IntersectionPaths is the name of IntersectPath and of the English-only
		// command IntersectionPaths, which has no syntax
		getApp().setLocale(Locale.JAPANESE);
		assertAll(
				() -> assertEquals("IntersectPath", getApp().getInternalCommand("IntersectionPaths")),
				() -> assertNotEquals(
						0,
						getApp().getAutocompleteProvider().getCompletions("IntersectionP").count()));
	}

	@Test
	void parserCommandDependsOnTheApp() {
		// Umfang is the German name of both Perimeter and Circumference
		AppConfigCas casConfig = new AppConfigCas();
		AppCommon casApp = AppCommonFactory.create(casConfig);
		casApp.getKernel().getAlgebraProcessor().addCommandFilter(casConfig.getCommandFilter());
		casApp.setLocale(Locale.GERMAN);
		casApp.resetCommandDict();
		assertAll(
				() -> assertEquals("Circumference", casApp.getReverseCommand("Umfang")),
				() -> assertEquals("Circumference", casApp.getInternalCommand("Umfang")));
	}
}
