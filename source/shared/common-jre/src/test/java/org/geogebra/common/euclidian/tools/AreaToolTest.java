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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_AREA;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AreaToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_AREA);
	}

	@Test
	void clickingPolygonShouldCreateAreaText() {
		add("A = (1, -1)");
		add("B = (5, -1)");
		add("C = (5, -3)");
		add("D = (1, -3)");
		GeoElement polygon = add("p = Polygon(A, B, C, D)");
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -2);

		assertPolygonAreaText(existingObjects, polygon);
	}

	@Test
	void clickingCircleShouldCreateAreaText() {
		GeoElement circle = add("c = Circle((2, -2), 1)");
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -2);

		assertConicAreaText(existingObjects, Math.PI, circle);
	}

	@Test
	void clickingEllipseShouldCreateAreaText() {
		GeoElement ellipse = add("e: x^2 / 4 + (y + 2)^2 = 1");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -2);

		assertConicAreaText(existingObjects, 2 * Math.PI, ellipse);
	}

	@Test
	void clickingCircularSectorShouldCreateAreaText() {
		GeoElement sector = add("s = CircularSector((2, -2), (4, -2), (2, 0))");
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -1);

		assertConicAreaText(existingObjects, Math.PI, sector);
	}

	@Test
	void clickingCircularArcShouldBeIgnored() {
		add("a = CircularArc((2, -2), (4, -2), (2, 0))");
		Set<String> existingObjects = getObjectNames();

		clickRW(2 + Math.sqrt(2), -2 + Math.sqrt(2));

		assertAll(
				() -> assertEquals(existingObjects, getObjectNames()),
				() -> assertTrue(getApp().getSelectionManager().getSelectedConicNDList().isEmpty()));
	}

	@Test
	void clickingEmptyViewShouldDoNothing() {
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -2);

		assertEquals(existingObjects, getObjectNames());
	}

	@Test
	void clickingUnsupportedObjectShouldDoNothing() {
		GeoElement line = add("g: y = -1");
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -1);

		assertAll(
				() -> assertEquals(existingObjects, getObjectNames()),
				() -> assertFalse(getApp().getSelectionManager().containsSelectedGeo(line)));
	}

	@Test
	void overlappingConicShouldTakePriorityOverPolygon() {
		GeoElement circle = add("c = Circle((3, -2), 1)");
		add("A = (1, -0.5)");
		add("B = (5, -0.5)");
		add("C = (5, -3.5)");
		add("D = (1, -3.5)");
		GeoElement polygon = add("p = Polygon(A, B, C, D)");
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -1);

		GeoText text = assertConicAreaText(existingObjects, Math.PI, circle);
		assertFalse(polygon.isParentOf(text));
	}

	private void assertPolygonAreaText(Set<String> existingObjects, GeoElement polygon) {
		GeoText text = getOnlyNewObject(existingObjects, GeoText.class);
		assertAll(
				() -> assertEquals(0, getNewObjects(existingObjects, GeoNumeric.class).size()),
				() -> assertTrue(text.isEuclidianVisible()),
				() -> assertTextContainsValue(text, polygon),
				() -> assertTrue(polygon.isParentOf(text)));
	}

	private GeoText assertConicAreaText(
			Set<String> existingObjects, double expectedArea, GeoElement conic) {
		GeoNumeric area = getOnlyNewObject(existingObjects, GeoNumeric.class);
		GeoText text = getOnlyNewObject(existingObjects, GeoText.class);
		assertAll(
				() -> assertEquals(expectedArea, area.getDouble(), 1E-8),
				() -> assertTrue(text.isEuclidianVisible()),
				() -> assertTextContainsValue(text, area),
				() -> assertTrue(conic.isParentOf(area)),
				() -> assertTrue(conic.isParentOf(text)));
		return text;
	}
}
