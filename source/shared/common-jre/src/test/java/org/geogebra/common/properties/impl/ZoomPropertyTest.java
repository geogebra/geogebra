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

package org.geogebra.common.properties.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ZoomPropertyTest extends BaseAppTestSetup {

	private ZoomProperty property;

	@BeforeEach
	void setup() {
		setupApp(SuiteSubApp.GRAPHING);
		EuclidianView view = getApp().getActiveEuclidianView();
		property = new ZoomProperty(getAlgebraProcessor(), getLocalization(), view);
	}

	@Test
	void defaultValueIsHundredPercent() {
		assertEquals("100%", property.getValue());
	}

	@Test
	void setValueZoomsView() {
		property.setValue("200%");
		assertEquals("200%", property.getValue());
	}

	@Test
	void setValueAcceptsMissingPercentSuffix() {
		property.setValue("50");
		assertEquals("50%", property.getValue());
	}

	@Test
	void setValueRoundsToPercent() {
		property.setValue("0.1%");
		assertEquals("< 1%", property.getValue());
		property.setValue("23.4%");
		assertEquals("23%", property.getValue());
	}

	@Test
	void validateValueAcceptsPositivePercentage() {
		assertNull(property.validateValue("1E-7"));
		assertNull(property.validateValue("1%"));
		assertNull(property.validateValue("800%"));
	}

	@Test
	void validateValueRejectsOutOfRangeValues() {
		assertNotNull(property.validateValue("-50%"));
		assertNotNull(property.validateValue("0%"));
		assertNotNull(property.validateValue("1E-9"));
		assertNotNull(property.validateValue("801%"));
	}

	@Test
	void validateValueRejectsNonNumeric() {
		assertNotNull(property.validateValue("abc"));
	}

	@Test
	void validateValueAcceptsSpecialSuggestions() {
		assertNull(property.validateValue(getLocalization().getMenu("StandardView")));
		assertNull(property.validateValue(getLocalization().getMenu("ShowAllObjects")));
	}
}
