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

package org.geogebra.common.properties.impl.graphics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class RulingGridBoldPropertyTests extends BaseAppTestSetup {
	@Test
	void testSettingValue() {
		setupNotesApp();
		RulingGridBoldProperty rulingGridBoldProperty = new RulingGridBoldProperty(
				getLocalization(), getEuclidianSettings());

		rulingGridBoldProperty.setValue(true);
		assertTrue(rulingGridBoldProperty.getValue());
		assertTrue(getEuclidianSettings().isRulerBold());

		rulingGridBoldProperty.setValue(false);
		assertFalse(rulingGridBoldProperty.getValue());
		assertFalse(getEuclidianSettings().isRulerBold());
	}

	@ParameterizedTest
	@EnumSource(value = BackgroundType.class, names = {
			"SQUARE_SMALL",
			"SQUARE_BIG",
			"RULER",
	})
	void testBackgroundTypesWithAvailableBoldProperty(BackgroundType backgroundType) {
		setupNotesApp();
		RulingGridBoldProperty rulingGridBoldProperty = new RulingGridBoldProperty(
				getLocalization(), getEuclidianSettings());
		getEuclidianSettings().setBackgroundType(backgroundType);

		assertTrue(rulingGridBoldProperty.isAvailable());
	}

	@ParameterizedTest
	@EnumSource(value = BackgroundType.class, names = {
			"NONE",
			"ELEMENTARY12",
			"ELEMENTARY12_HOUSE",
			"ELEMENTARY34",
			"MUSIC",
			"SVG",
			"ELEMENTARY12_COLORED",
			"ISOMETRIC",
			"POLAR",
			"DOTS",
	})
	void testBackgroundTypesWithUnavailableBoldProperty(BackgroundType backgroundType) {
		setupNotesApp();
		RulingGridBoldProperty rulingGridBoldProperty = new RulingGridBoldProperty(
				getLocalization(), getEuclidianSettings());
		getEuclidianSettings().setBackgroundType(backgroundType);

		assertFalse(rulingGridBoldProperty.isAvailable());
	}
}
