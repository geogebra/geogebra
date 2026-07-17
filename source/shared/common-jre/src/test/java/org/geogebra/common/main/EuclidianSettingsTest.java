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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EuclidianSettingsTest extends BaseUnitTest {
	private static EuclidianSettings settings;

	@BeforeEach
	void setUp() {
		settings = new EuclidianSettings(getApp());
	}

	@Test
	void isometricBackgroundShouldShowGrid() {
		assertGridAt(BackgroundType.ISOMETRIC);
	}

	private static void assertGridAt(BackgroundType backgroundType) {
		settings.setBackgroundType(backgroundType);
		assertTrue(settings.getShowGrid());
	}

	@Test
	void polarBackgroundShouldShowGrid() {
		assertGridAt(BackgroundType.POLAR);
	}

	@Test
	void changeBackgroundFromIsometricShouldHideGrid() {
		changeBackgroundShouldHideGridFrom(BackgroundType.ISOMETRIC);
	}

	@Test
	void changeBackgroundFromPolarShouldHideGrid() {
		changeBackgroundShouldHideGridFrom(BackgroundType.POLAR);
	}

	private void changeBackgroundShouldHideGridFrom(BackgroundType backgroundType) {
		for (BackgroundType type: BackgroundType.values()) {
			if (noGridBackground(type)) {
				changeShouldHideGrid(backgroundType, type);
			}
		}
	}

	private static boolean noGridBackground(BackgroundType type) {
		return type != BackgroundType.ISOMETRIC && type != BackgroundType.POLAR;
	}

	private void changeShouldHideGrid(BackgroundType before, BackgroundType after) {
		assertGridAt(before);
		assertNoGridAt(after);
	}

	private static void assertNoGridAt(BackgroundType backgroundType) {
		settings.setBackgroundType(backgroundType);
		assertFalse(settings.getShowGrid());
	}

	@Test
	void noBackgroundShouldClearShowGrid() {
		settings.setBackgroundType(BackgroundType.ISOMETRIC);
		settings.setBackgroundType(BackgroundType.NONE);
		assertFalse(settings.getShowGrid());
	}
}
