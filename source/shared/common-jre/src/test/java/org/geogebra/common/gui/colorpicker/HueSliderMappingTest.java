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

package org.geogebra.common.gui.colorpicker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

final class HueSliderMappingTest {

	private static final double EPS = 1e-10;
	private final HueSliderMapping mapping = new HueSliderMapping(360, 12);

	@ParameterizedTest
	@CsvSource({"0, 12", "60, 68", "120, 124", "180, 180", "240, 236", "300, 292", "360, 348"})
	void rainbowStopsAndThumbStayWithinEndCaps(double hue, double expectedPosition) {
		assertEquals(expectedPosition, mapping.toPosition(hue), EPS);
	}

	@ParameterizedTest
	@CsvSource({"-20, 0", "0, 0", "12, 0", "68, 60", "180, 180", "292, 300"})
	void pointerPositionUsesInsetHueScale(double position, double expectedHue) {
		assertEquals(expectedHue, mapping.toHue(position), EPS);
	}

	@ParameterizedTest
	@ValueSource(doubles = {348, 360, 400, Double.POSITIVE_INFINITY})
	void rightEndCapDoesNotWrapHueToZero(double position) {
		double hue = mapping.toHue(position);
		assertEquals(Math.nextDown(360.0), hue);
		assertTrue(new HsvColor(hue, 1, 1).getHue() > 359);
		assertEquals(348, mapping.toPosition(hue), EPS);
	}

	@ParameterizedTest
	@ValueSource(doubles = {0, 0.001, 60, 180, 300, 359.999999})
	void pointerAtThumbPositionPreservesHue(double hue) {
		assertEquals(hue, mapping.toHue(mapping.toPosition(hue)), EPS);
	}

	@Test
	void zeroInsetUsesFullTrackWidth() {
		HueSliderMapping fullTrack = new HueSliderMapping(360, 0);
		assertEquals(0, fullTrack.toPosition(0));
		assertEquals(360, fullTrack.toPosition(360));
		assertEquals(90, fullTrack.toHue(90));
	}

	@ParameterizedTest
	@ValueSource(doubles = {0, 12, 24})
	void narrowTrackCollapsesToItsMidpoint(double width) {
		HueSliderMapping narrowTrack = new HueSliderMapping(width, 12);
		assertEquals(width / 2, narrowTrack.toPosition(0));
		assertEquals(width / 2, narrowTrack.toPosition(180));
		assertEquals(width / 2, narrowTrack.toPosition(360));
		assertEquals(0, narrowTrack.toHue(width / 2));
	}

	@Test
	void outOfRangeAndUndefinedValuesClampToEndpoints() {
		assertEquals(12, mapping.toPosition(-60));
		assertEquals(348, mapping.toPosition(420));
		assertEquals(12, mapping.toPosition(Double.NaN));
		assertEquals(0, mapping.toHue(Double.NaN));
		assertEquals(0, mapping.toHue(Double.NEGATIVE_INFINITY));
	}
}
