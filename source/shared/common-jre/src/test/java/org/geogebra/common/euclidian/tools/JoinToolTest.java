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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_JOIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JoinToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_JOIN);
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setCoordSystem(250, 250, 50, 50);
	}

	@Test
	void existingPointThenEmptySpaceShouldCreateLine() {
		add("A = (-1, 1)");
		clickRW(-1, 1);
		clickRW(2, -1);
		checkContent("A = (-1, 1)", "B = (2, -1)", "f: 2x + 3y = 1");
	}

	@Test
	void twoExistingPointsShouldCreateLine() {
		add("A = (-1, 1)");
		add("B = (2, -1)");

		clickRW(-1, 1);
		clickRW(2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f: 2x + 3y = 1");
	}

	@Test
	void emptySpaceThenExistingPointShouldCreateLine() {
		add("A = (2, -1)");

		clickRW(-1, 1);
		clickRW(2, -1);

		checkContent("A = (2, -1)", "B = (-1, 1)", "f: 2x + 3y = 1");
	}

	@Test
	void twoEmptySpaceClicksShouldCreatePointsAndLine() {
		clickRW(-1, 1);
		clickRW(2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f: 2x + 3y = 1");
	}

	@Test
	void firstPointAloneShouldNotCreateLine() {
		clickRW(-1, 1);

		checkContent("A = (-1, 1)");
	}

	@Test
	void clickingSamePointTwiceShouldNotCreateLine() {
		add("A = (-1, 1)");

		clickRW(-1, 1);
		clickRW(-1, 1);

		checkContent("A = (-1, 1)");
	}

	@Test
	void pointOnPathShouldBeUsedAsEndpoint() {
		add("g: y = -1");

		clickRW(2, -1);
		clickRW(-1, 1);

		GeoPoint pointOnPath = assertInstanceOf(GeoPoint.class, lookup("A"));
		assertTrue(pointOnPath.isPointOnPath());
		assertEquals("Point(g)", pointOnPath.getDefinition(StringTemplate.testTemplate));
		checkContent("g: y = -1", "A = (2, -1)", "B = (-1, 1)", "f: -2x - 3y = -1");
	}

	@Test
	void intersectionPointShouldBeUsedAsEndpoint() {
		add("g: y = -1");
		add("h: x = 2");

		clickRW(2, -1);
		clickRW(-1, 1);

		GeoPoint intersection = assertInstanceOf(GeoPoint.class, lookup("A"));
		assertFalse(intersection.isIndependent());
		assertEquals("Intersect(g, h)", intersection.getDefinition(StringTemplate.testTemplate));
		checkContent("g: y = -1", "h: x = 2", "A = (2, -1)", "B = (-1, 1)", "f: -2x - 3y = -1");
	}

	@Test
	void clickingPolygonFillShouldCreateFreeEndpoint() {
		add("A = (-2, 2)");
		add("B = (2, 2)");
		add("C = (2, -2)");
		add("D = (-2, -2)");
		add("p = Polygon(A, B, C, D)");
		Set<String> existingObjects = getObjectNames();

		clickRW(0, 0);
		clickRW(3, -3);

		List<GeoPoint> endpoints = getNewObjects(existingObjects, GeoPoint.class);
		assertEquals(2, endpoints.size());
		assertTrue(endpoints.stream().allMatch(GeoPoint::isIndependent));
		assertEquals(1, getNewObjects(existingObjects, GeoLine.class).size());
	}

	@Test
	void draggingBetweenEmptyLocationsShouldCreatePointsAndLine() {
		dragRW(-1, 1, 2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f: 2x + 3y = 1");
	}

	@Test
	void draggingFromExistingPointShouldCreateSecondPointAndLine() {
		add("A = (-1, 1)");

		dragRW(-1, 1, 2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f: 2x + 3y = 1");
	}

	@Test
	void draggingToExistingPointShouldCreateFirstPointAndLine() {
		add("A = (2, -1)");

		dragRW(-1, 1, 2, -1);

		checkContent("A = (2, -1)", "B = (-1, 1)", "f: 2x + 3y = 1");
	}

	@Test
	void draggingBetweenExistingPointsShouldCreateLine() {
		add("A = (-1, 1)");
		add("B = (2, -1)");

		dragRW(-1, 1, 2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f: 2x + 3y = 1");
	}
}
