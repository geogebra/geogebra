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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SEGMENT_FIXED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.jre.headless.DialogManagerNoGui;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

class SegmentFixedToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_SEGMENT_FIXED);
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setCoordSystem(250, 250, 50, 50);
	}

	@Test
	void clickingEmptySpaceShouldCreateStartPointAndSegment() {
		prepareInput("2");

		clickRW(-1, 1);

		checkContent("A = (-1, 1)", "B = (1, 1)", "f = 2");
	}

	@Test
	void clickingExistingPointShouldCreateSegmentFromIt() {
		GeoPoint startPoint = add("A = (-1, 1)");
		prepareInput("2");

		clickRW(-1, 1);

		GeoSegment segment = assertInstanceOf(GeoSegment.class, lookup("f"));
		assertTrue(startPoint.isParentOf(segment));
		checkContent("A = (-1, 1)", "B = (1, 1)", "f = 2");
	}

	@Test
	void clickingExistingPointShouldOpenDialogWithThatPoint() {
		GeoPoint startPoint = add("A = (-1, 1)");

		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			clickRW(-1, 1);

			Mockito.verify(getOnlyDialog(dialogs))
					.showNumberInputDialogSegmentFixed(Mockito.anyString(), Mockito.same(startPoint));
			assertEquals(0, ec.selPoints());
		}
	}

	@Test
	void clickingEmptySpaceShouldOpenDialogWithCreatedPoint() {
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			clickRW(-1, 1);

			GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("A"));
			Mockito.verify(getOnlyDialog(dialogs))
					.showNumberInputDialogSegmentFixed(Mockito.anyString(), Mockito.same(startPoint));
			assertEquals(0, ec.selPoints());
			checkContent("A = (-1, 1)");
		}
	}

	@Test
	void movingMouseOverPointShouldNotOpenDialog() {
		add("A = (-1, 1)");

		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			moveMouseRW(-1, 1);

			Mockito.verify(getOnlyDialog(dialogs), Mockito.never())
					.showNumberInputDialogSegmentFixed(Mockito.anyString(), Mockito.any(GeoPointND.class));
			assertEquals(0, ec.selPoints());
		}
	}

	@Test
	void pointOnPathShouldBeUsedAsStartPoint() {
		GeoLine path = add("g: y = -1");
		prepareInput("2");

		clickRW(2, -1);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("A"));
		assertTrue(startPoint.isPointOnPath());

		path.setCoords(0, 1, 2);
		path.updateCascade();

		checkContent("g: y = -2", "A = (2, -2)", "B = (4, -2)", "f = 2");
	}

	@Test
	void intersectionPointShouldBeUsedAsStartPoint() {
		GeoLine horizontalLine = add("g: y = -1");
		GeoLine verticalLine = add("h: x = 2");
		prepareInput("2");

		clickRW(2, -1);

		GeoPoint intersection = assertInstanceOf(GeoPoint.class, lookup("A"));
		assertFalse(intersection.isIndependent());

		horizontalLine.setCoords(0, 1, 2);
		horizontalLine.updateCascade();
		verticalLine.setCoords(1, 0, -3);
		verticalLine.updateCascade();

		checkContent("g: y = -2", "h: x = 3", "A = (3, -2)", "B = (5, -2)", "f = 2");
	}

	@Test
	void clickingPolygonFillShouldCreateFreeStartPoint() {
		add("A = (-2, 2)");
		add("B = (2, 2)");
		add("C = (2, -2)");
		add("D = (-2, -2)");
		add("p = Polygon(A, B, C, D)");
		Set<String> existingObjects = getObjectNames();
		prepareInput("2");

		clickRW(0, 0);

		List<GeoPoint> points = getNewObjects(existingObjects, GeoPoint.class);
		assertEquals(List.of("(0, 0)", "(2, 0)"), definedValueStrings(points));
		assertTrue(points.get(0).isIndependent());
		assertFalse(points.get(1).isIndependent());
		GeoSegment segment = getOnlyNewObject(existingObjects, GeoSegment.class);
		assertEquals(2, segment.getDouble());
	}

	@Test
	void draggingNewStartPointShouldOpenDialogAtReleasePosition() {
		prepareInput("2");

		dragRW(-1, 1, 2, -1);

		checkContent("A = (2, -1)", "B = (4, -1)", "f = 2");
	}

	@Test
	void toolShouldCreateAnotherSegmentAfterCompletion() {
		prepareInput("2", "3");

		clickRW(-1, 1);
		clickRW(0, -2);

		checkContent("A = (-1, 1)", "B = (1, 1)", "f = 2", "C = (0, -2)", "D = (3, -2)", "g = 3");
	}

	private MockedConstruction<DialogManagerNoGui> mockDialogs() {
		MockedConstruction<DialogManagerNoGui> dialogs =
				Mockito.mockConstruction(DialogManagerNoGui.class);
		getApp().initDialogManager(false);
		return dialogs;
	}

	private DialogManagerNoGui getOnlyDialog(MockedConstruction<DialogManagerNoGui> dialogs) {
		assertEquals(1, dialogs.constructed().size());
		return dialogs.constructed().get(0);
	}
}
