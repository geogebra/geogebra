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

import static org.geogebra.common.plugin.EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT;
import static org.geogebra.common.plugin.EuclidianStyleConstants.LINE_TYPE_DOTTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class RulingGridLineStylePropertyTests extends BaseAppTestSetup {
	@Test
	void testSettingValue() {
		setupNotesApp();
		RulingGridLineStyleProperty rulingGridLineStyleProperty = new RulingGridLineStyleProperty(
				getLocalization(), getEuclidianSettings());

		rulingGridLineStyleProperty.setValue(LINE_TYPE_DASHED_SHORT);
		assertEquals(LINE_TYPE_DASHED_SHORT, rulingGridLineStyleProperty.getValue());
		assertEquals(LINE_TYPE_DASHED_SHORT, getEuclidianSettings().getRulerLineStyle());

		rulingGridLineStyleProperty.setValue(LINE_TYPE_DOTTED);
		assertEquals(LINE_TYPE_DOTTED, rulingGridLineStyleProperty.getValue());
		assertEquals(LINE_TYPE_DOTTED, getEuclidianSettings().getRulerLineStyle());
	}

	@ParameterizedTest
	@EnumSource(value = BackgroundType.class, names = {
			"RULER",
			"SQUARE_SMALL",
			"SQUARE_BIG",
	})
	void testBackgroundTypesWithAvailableLineStyleProperty(BackgroundType backgroundType) {
		setupNotesApp();
		RulingGridLineStyleProperty rulingGridLineStyleProperty = new RulingGridLineStyleProperty(
				getLocalization(), getEuclidianSettings());
		getEuclidianSettings().setBackgroundType(backgroundType);

		assertTrue(rulingGridLineStyleProperty.isAvailable());
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
	void testBackgroundTypesWithUnavailableLineStyleProperty(BackgroundType backgroundType) {
		setupNotesApp();
		RulingGridLineStyleProperty rulingGridLineStyleProperty = new RulingGridLineStyleProperty(
				getLocalization(), getEuclidianSettings());
		getEuclidianSettings().setBackgroundType(backgroundType);

		assertFalse(rulingGridLineStyleProperty.isAvailable());
	}
}
