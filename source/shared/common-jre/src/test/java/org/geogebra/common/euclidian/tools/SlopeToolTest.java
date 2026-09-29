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

package org.geogebra.common.euclidian.tools;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SLOPE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.settings.LabelVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SlopeToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_SLOPE);
	}

	@Test
	void clickingLineShouldCreateSlope() {
		GeoLine line = add("g: y = 2x - 3");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -1);

		assertSlope(existingObjects, 2, "m", GeoElementND.LABEL_NAME_VALUE, line);
	}

	@Test
	void clickingFunctionShouldCreateSlope() {
		GeoFunction function = add("f(x) = -3x + 2");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -1);

		assertSlope(existingObjects, -3, "m", GeoElementND.LABEL_NAME_VALUE, function);
	}

	@Test
	void clickingSegmentShouldCreateSlope() {
		add("A = (1, -1)");
		add("B = (3, -5)");
		GeoSegment segment = add("f = Segment(A, B)");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -3);

		assertSlope(existingObjects, -2, "m", GeoElementND.LABEL_NAME_VALUE, segment);
	}

	@Test
	void clickingVerticalLineShouldCreateUndefinedSlope() {
		GeoLine line = add("g: x = 2");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -3);

		GeoNumeric slope = getOnlyNewObject(existingObjects, GeoNumeric.class);
		assertAll(
				() -> assertFalse(slope.isDefined()),
				() -> assertTrue(slope.isSetEuclidianVisible()),
				() -> assertTrue(slope.isLabelVisible()),
				() -> assertTrue(line.isParentOf(slope)));
	}

	@Test
	void lineShouldTakePriorityOverOverlappingFunction() {
		GeoLine line = add("g: y = -1");
		GeoFunction function = add("f(x) = -1");
		Set<String> existingObjects = getObjectNames();

		moveMouseRW(3, -1);
		assertAll(
				() -> assertTrue(getApp().getActiveEuclidianView().getHits().contains(line)),
				() -> assertTrue(getApp().getActiveEuclidianView().getHits().contains(function)));
		clickRW(3, -1);

		GeoNumeric slope = assertSlope(existingObjects, 0, "m", GeoElementND.LABEL_NAME_VALUE, line);
		assertFalse(function.isParentOf(slope));
	}

	@Test
	void hiddenSlopeLabelShouldShowValue() {
		getApp().getSettings().getLabelSettings().setLabelVisibility(LabelVisibility.UseDefaults);
		getKernel()
				.getConstruction()
				.getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_NUMBER)
				.setLabelVisible(false);
		GeoLine line = add("g: y = 2x - 3");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -1);

		assertSlope(existingObjects, 2, "m", GeoElementND.LABEL_VALUE, line);
	}

	@Test
	void existingSlopeLabelsShouldBeSkipped() {
		add("m = 7");
		add("m_1 = 8");
		GeoLine line = add("g: y = x - 3");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -1);

		assertSlope(existingObjects, 1, "m_2", GeoElementND.LABEL_NAME_VALUE, line);
	}

	@Test
	void slopeLabelShouldContinueAboveTen() {
		add("m = 7");
		for (int index = 1; index <= 10; index++) {
			add("m_" + index + " = " + index);
		}
		GeoLine line = add("g: y = x - 3");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -1);

		assertSlope(existingObjects, 1, "m_{11}", GeoElementND.LABEL_NAME_VALUE, line);
	}

	@Test
	void clickingEmptyViewShouldDoNothing() {
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -2);

		assertEquals(existingObjects, getObjectNames());
	}

	@Test
	void clickingUnsupportedObjectShouldDoNothing() {
		GeoElement point = add("A = (2, -2)");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -2);

		assertAll(
				() -> assertEquals(existingObjects, getObjectNames()),
				() -> assertFalse(getApp().getSelectionManager().containsSelectedGeo(point)));
	}

	private GeoNumeric assertSlope(
			Set<String> existingObjects,
			double expectedValue,
			String expectedLabel,
			int expectedLabelMode,
			GeoElement input) {
		GeoNumeric slope = getOnlyNewObject(existingObjects, GeoNumeric.class);
		assertAll(
				() -> assertEquals(expectedValue, slope.getDouble(), 1E-8),
				() -> assertEquals(expectedLabel, slope.getLabelSimple()),
				() -> assertTrue(slope.isEuclidianVisible()),
				() -> assertTrue(slope.isLabelVisible()),
				() -> assertEquals(expectedLabelMode, slope.getLabelMode()),
				() -> assertTrue(input.isParentOf(slope)));
		return slope;
	}
}
