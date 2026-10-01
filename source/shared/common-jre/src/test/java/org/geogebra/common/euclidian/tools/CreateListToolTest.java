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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CREATE_LIST;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateListToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_CREATE_LIST);
	}

	@Test
	void selectionRectangleAroundTwoPointsShouldCreateList() {
		add("A = (1, -1)");
		add("B = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		dragRW(0.5, -0.5, 3.5, -3.5);

		assertList(existingObjects, "A", "B");
	}

	@Test
	void clickingTwoOverlappingPointsShouldCreateList() {
		add("A = (2, -2)");
		add("B = (2, -2)");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -2);

		assertList(existingObjects, "A", "B");
	}

	@Test
	void clickingObjectsSeparatelyShouldNotCreateList() {
		add("A = (1, -1)");
		add("B = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -1);
		resetMouseLocation();
		clickRW(3, -3);

		assertObjectNamesUnchanged(existingObjects);
	}

	@Test
	void selectionRectangleAroundSingleObjectShouldNotCreateList() {
		add("A = (2, -2)");
		Set<String> existingObjects = getObjectNames();

		dragRW(1.5, -1.5, 2.5, -2.5);

		assertObjectNamesUnchanged(existingObjects);
	}

	@Test
	void clickingEmptyViewShouldNotCreateList() {
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -2);

		assertObjectNamesUnchanged(existingObjects);
	}

	@Test
	void selectionRectangleShouldCreateListFromDifferentObjectTypes() {
		add("A = (1, -1)");
		add("c: (x - 3)^2 + (y + 3)^2 = 0.25");
		Set<String> existingObjects = getObjectNames();

		dragRW(0.25, -0.25, 3.75, -3.75);

		assertList(existingObjects, "c", "A");
	}

	@Test
	void selectionRectangleShouldExcludeSegmentEndpoints() {
		add("A = (1, -1)");
		add("B = (3, -1)");
		add("s = Segment(A, B)");
		add("C = (2, -3)");
		Set<String> existingObjects = getObjectNames();

		dragRW(0.5, -0.5, 3.5, -3.5);

		assertList(existingObjects, "s", "C");
	}

	@Test
	void selectionRectangleShouldExcludePolygonVerticesAndEdges() {
		add("A = (1, -1)");
		add("B = (3, -1)");
		add("C = (3, -3)");
		add("D = (1, -3)");
		add("p = Polygon(A, B, C, D)");
		add("E = (4, -4)");
		Set<String> existingObjects = getObjectNames();

		dragRW(0.5, -0.5, 4.5, -4.5);

		assertList(existingObjects, "p", "E");
	}

	@Test
	void selectionRectangleShouldExcludePolylineInputPoints() {
		add("A = (1, -1)");
		add("B = (2, -2)");
		add("C = (3, -1)");
		add("p = Polyline(A, B, C)");
		add("D = (2, -3)");
		Set<String> existingObjects = getObjectNames();

		dragRW(0.5, -0.5, 3.5, -3.5);

		assertList(existingObjects, "p", "D");
	}

	@Test
	void selectionRectangleShouldExcludeConicPartInputPoints() {
		add("A = (1, -1)");
		add("B = (3, -1)");
		add("c = Semicircle(A, B)");
		add("C = (2, -3)");
		Set<String> existingObjects = getObjectNames();

		dragRW(0.5, 0.5, 3.5, -3.5);

		assertList(existingObjects, "c", "C");
	}

	@Test
	void clickingOverlappingObjectsShouldExcludePolygon() {
		add("A = (1, -1)");
		add("B = (5, -1)");
		add("C = (5, -5)");
		add("D = (1, -5)");
		GeoPolygon polygon = add("p = Polygon(A, B, C, D)");
		add("E = (3, -3)");
		add("F = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		EuclidianView view = getApp().getActiveEuclidianView();
		view.setHits(
				new GPoint(view.toScreenCoordX(3), view.toScreenCoordY(-3)), PointerEventType.MOUSE);
		assertTrue(view.getHits().contains(polygon));
		moveMouseRW(3, -3);
		assertObjectNamesUnchanged(existingObjects);
		clickRW(3, -3);

		assertList(existingObjects, "E", "F");
	}

	@Test
	void createdListShouldUpdateWhenSourceChanges() {
		GeoPoint firstPoint = add("A = (1, -1)");
		add("B = (3, -3)");
		Set<String> existingObjects = getObjectNames();
		dragRW(0.5, -0.5, 3.5, -3.5);
		GeoList list = assertList(existingObjects, "A", "B");

		firstPoint.setCoords(4, -4, 1);
		firstPoint.updateRepaint();

		assertTrue(list.elements()
				.filter(GeoPoint.class::isInstance)
				.map(GeoPoint.class::cast)
				.anyMatch(point ->
						Math.abs(point.getInhomX() - 4) < 1E-8 && Math.abs(point.getInhomY() + 4) < 1E-8));
	}

	private GeoList assertList(Set<String> existingObjects, String... expectedLabels) {
		GeoList list = getOnlyNewObject(existingObjects, GeoList.class);
		String definition = list.getDefinition(StringTemplate.testTemplate);
		String[] actualLabels = definition.substring(1, definition.length() - 1).split(", ");
		assertAll(
				() -> assertEquals(expectedLabels.length, list.size()),
				() -> assertEquals(List.of(expectedLabels), List.of(actualLabels)));
		return list;
	}

	private void assertObjectNamesUnchanged(Set<String> existingObjects) {
		assertEquals(existingObjects, getObjectNames());
	}
}
