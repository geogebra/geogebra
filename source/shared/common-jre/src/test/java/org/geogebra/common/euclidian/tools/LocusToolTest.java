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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_LOCUS;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import org.geogebra.common.kernel.algos.AlgoLocus;
import org.geogebra.common.kernel.algos.AlgoLocusList;
import org.geogebra.common.kernel.algos.AlgoLocusSlider;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocusToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_LOCUS);
	}

	@Test
	void dependentPointThenMovingPointShouldCreateLocus() {
		add("c: (x - 3)^2 + (y + 3)^2 = 4");
		GeoPoint movingPoint = add("P = Point(c, 0)");
		GeoPoint locusPoint = add("Q = P + (1, 0)");

		clickRW(locusPoint.getInhomX(), locusPoint.getInhomY());
		clickRW(movingPoint.getInhomX(), movingPoint.getInhomY());

		assertPointDrivenLocus(locusPoint, movingPoint);
	}

	@Test
	void movingPointThenDependentPointShouldCreateLocus() {
		add("c: (x - 3)^2 + (y + 3)^2 = 4");
		GeoPoint movingPoint = add("P = Point(c, 0)");
		GeoPoint locusPoint = add("Q = P + (1, 0)");

		clickRW(movingPoint.getInhomX(), movingPoint.getInhomY());
		clickRW(locusPoint.getInhomX(), locusPoint.getInhomY());

		assertPointDrivenLocus(locusPoint, movingPoint);
	}

	@Test
	void sliderThenDependentPointShouldCreateLocus() {
		GeoNumeric slider = add("a = Slider(-2, 2, 0.1)");
		GeoPoint locusPoint = add("Q = (a + 2, a^2 - 3)");
		positionSlider(slider);

		click(200, 50);
		clickRW(2, -3);

		GeoLocus locus = getOnlyLocus(3);
		AlgoLocusSlider parentAlgorithm =
				assertInstanceOf(AlgoLocusSlider.class, locus.getParentAlgorithm());
		assertAll(
				() -> assertTrue(locus.isDefined()),
				() -> assertTrue(locus.isEuclidianVisible()),
				() -> assertSame(locusPoint, parentAlgorithm.getQ()),
				() -> assertSame(slider, parentAlgorithm.getInput()[1]));
	}

	@Test
	void emptyClickShouldNotCreatePointOrClearSelection() {
		add("c: (x - 3)^2 + (y + 3)^2 = 4");
		GeoPoint movingPoint = add("P = Point(c, 0)");
		GeoPoint locusPoint = add("Q = P + (1, 0)");

		clickRW(locusPoint.getInhomX(), locusPoint.getInhomY());
		clickRW(7, -7);
		checkContentLabels("c", "P", "Q");
		clickRW(movingPoint.getInhomX(), movingPoint.getInhomY());

		assertPointDrivenLocus(locusPoint, movingPoint);
	}

	@Test
	void unrelatedPathPointAndPointShouldNotCreateLocus() {
		add("c: (x - 3)^2 + (y + 3)^2 = 4");
		GeoPoint movingPoint = add("P = Point(c, 0)");
		add("Q = (6, -6)");

		clickRW(6, -6);
		clickRW(movingPoint.getInhomX(), movingPoint.getInhomY());

		checkContentLabels("c", "P", "Q");
	}

	@Test
	void pointIndependentOfSliderShouldNotCreateLocus() {
		GeoNumeric slider = add("a = Slider(-2, 2, 0.1)");
		add("Q = (2, -3)");
		positionSlider(slider);

		click(200, 50);
		clickRW(2, -3);

		checkContentLabels("a", "Q");
	}

	@Test
	void movingPointOnListPathShouldCreateLocus() {
		add("l = {Segment((1, -1), (3, -1)), Segment((3, -1), (3, -3))}");
		GeoPoint movingPoint = add("P = Point(l)");
		GeoPoint locusPoint = add("Q = P + (1, -1)");

		clickRW(locusPoint.getInhomX(), locusPoint.getInhomY());
		clickRW(movingPoint.getInhomX(), movingPoint.getInhomY());

		GeoLocus locus = getOnlyLocus(4);
		AlgoLocusList parentAlgorithm =
				assertInstanceOf(AlgoLocusList.class, locus.getParentAlgorithm());
		assertAll(
				() -> assertTrue(locus.isDefined()),
				() -> assertTrue(locus.isEuclidianVisible()),
				() -> assertSame(locusPoint, parentAlgorithm.getLocusPoint()),
				() -> assertSame(movingPoint, parentAlgorithm.getMovingPoint()));
	}

	private void assertPointDrivenLocus(GeoPoint locusPoint, GeoPoint movingPoint) {
		GeoLocus locus = getOnlyLocus(4);
		AlgoLocus parentAlgorithm = assertInstanceOf(AlgoLocus.class, locus.getParentAlgorithm());
		assertAll(
				() -> assertTrue(locus.isDefined()),
				() -> assertTrue(locus.isEuclidianVisible()),
				() -> assertSame(locusPoint, parentAlgorithm.getLocusPoint()),
				() -> assertSame(movingPoint, parentAlgorithm.getMovingPoint()));
	}

	private GeoLocus getOnlyLocus(int expectedObjectCount) {
		String[] objectNames = getApp().getGgbApi().getAllObjectNames();
		assertEquals(expectedObjectCount, objectNames.length, Arrays.toString(objectNames));
		GeoLocus result = null;
		for (String label : objectNames) {
			GeoElement geo = lookup(label);
			if (geo instanceof GeoLocus locus) {
				if (result != null) {
					fail("Expected exactly one locus");
				}
				result = locus;
			}
		}
		return result == null ? fail("Expected a locus") : result;
	}

	private static void positionSlider(GeoNumeric slider) {
		slider.setSliderLocation(100, 50, true);
		slider.setSliderWidth(200, true);
		slider.updateRepaint();
	}
}
