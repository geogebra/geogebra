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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SEGMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.test.TestEvent;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SegmentToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_SEGMENT);
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setCoordSystem(250, 250, 50, 50);
	}

	@Test
	void existingPointThenEmptySpaceShouldCreateSegment() {
		add("A = (-1, 1)");

		clickRW(-1, 1);
		clickRW(2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f = 3.60555");
	}

	@Test
	void twoExistingPointsShouldCreateSegment() {
		add("A = (-1, 1)");
		add("B = (2, -1)");

		clickRW(-1, 1);
		clickRW(2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f = 3.60555");
	}

	@Test
	void emptySpaceThenExistingPointShouldCreateSegment() {
		add("A = (2, -1)");

		clickRW(-1, 1);
		clickRW(2, -1);

		checkContent("A = (2, -1)", "B = (-1, 1)", "f = 3.60555");
	}

	@Test
	void twoEmptySpaceClicksShouldCreatePointsAndSegment() {
		clickRW(-1, 1);
		clickRW(2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f = 3.60555");
	}

	@Test
	void firstPointAloneShouldNotCreateSegment() {
		clickRW(-1, 1);

		checkContent("A = (-1, 1)");
	}

	@Test
	void clickingSamePointTwiceShouldNotCreateSegment() {
		add("A = (-1, 1)");

		clickRW(-1, 1);
		clickRW(-1, 1);

		checkContent("A = (-1, 1)");
	}

	@Test
	void pointOnPathShouldBeUsedAsEndpoint() {
		GeoLine path = add("g: y = -1");

		clickRW(2, -1);
		clickRW(-1, 1);

		GeoPoint pointOnPath = assertInstanceOf(GeoPoint.class, lookup("A"));
		assertTrue(pointOnPath.isPointOnPath());

		path.setCoords(0, 1, 2);
		path.updateCascade();

		checkContent("g: y = -2", "A = (2, -2)", "B = (-1, 1)", "f = 4.24264");
	}

	@Test
	void intersectionPointShouldBeUsedAsEndpoint() {
		GeoLine horizontalLine = add("g: y = -1");
		GeoLine verticalLine = add("h: x = 2");

		clickRW(2, -1);
		clickRW(-1, 1);

		GeoPoint intersection = assertInstanceOf(GeoPoint.class, lookup("A"));
		assertFalse(intersection.isIndependent());

		horizontalLine.setCoords(0, 1, 2);
		horizontalLine.updateCascade();
		verticalLine.setCoords(1, 0, -3);
		verticalLine.updateCascade();

		checkContent("g: y = -2", "h: x = 3", "A = (3, -2)", "B = (-1, 1)", "f = 5");
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
		assertEquals(List.of("(0, 0)", "(3, -3)"), definedValueStrings(endpoints));
		GeoSegment segment = getOnlyNewObject(existingObjects, GeoSegment.class);
		assertEquals(3 * Math.sqrt(2), segment.getDouble(), 1E-8);
	}

	@Test
	void altClickShouldSnapEndpointAngleToFifteenDegrees() {
		clickRW(0, 0);
		EuclidianView view = getApp().getActiveEuclidianView();
		TestEvent altEvent = new TestEvent(view.toScreenCoordX(2), view.toScreenCoordY(-1)) {
			@Override
			public boolean isAltDown() {
				return true;
			}
		};

		ec.wrapMouseMoved(altEvent);
		ec.wrapMousePressed(altEvent);
		ec.wrapMouseReleased(altEvent);

		GeoPoint endpoint = assertInstanceOf(GeoPoint.class, lookup("B"));
		GeoSegment segment = assertInstanceOf(GeoSegment.class, lookup("f"));
		assertEquals(-Math.PI / 6, Math.atan2(endpoint.getInhomY(), endpoint.getInhomX()), 1E-8);
		assertEquals(Math.sqrt(5), segment.getDouble(), 1E-8);
	}

	@Test
	void draggingBetweenEmptyLocationsShouldCreatePointsAndSegment() {
		dragRW(-1, 1, 2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f = 3.60555");
	}

	@Test
	void draggingFromExistingPointShouldCreateSecondPointAndSegment() {
		add("A = (-1, 1)");

		dragRW(-1, 1, 2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f = 3.60555");
	}

	@Test
	void draggingToExistingPointShouldCreateFirstPointAndSegment() {
		add("A = (2, -1)");

		dragRW(-1, 1, 2, -1);

		checkContent("A = (2, -1)", "B = (-1, 1)", "f = 3.60555");
	}

	@Test
	void draggingBetweenExistingPointsShouldCreateSegment() {
		add("A = (-1, 1)");
		add("B = (2, -1)");

		dragRW(-1, 1, 2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f = 3.60555");
	}

	@Test
	void toolShouldCreateAnotherSegmentAfterCompletion() {
		dragRW(-1, 1, 2, -1);
		dragRW(-1, 1, 3, -2);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f = 3.60555", "C = (3, -2)", "g = 5");
	}

	@Test
	@Issue("APPS-5779")
	void releasingNewSecondEndpointAwayFromPressShouldMoveIt() {
		clickRW(0, 0);
		pressRW(2, -2);
		releaseRW(4, -3);

		checkContent("A = (0, 0)", "B = (4, -3)", "f = 5");
	}

	@Test
	@Issue("APPS-5779")
	void gridCapturingShouldApplyToReleasedSecondEndpoint() {
		getApp()
				.getSettings()
				.getEuclidian(1)
				.setPointCapturing(EuclidianStyleConstants.POINT_CAPTURING_ON_GRID);

		clickRW(0.2, -0.2);
		pressRW(2.2, -2.2);
		releaseRW(4.2, -3.2);

		checkContent("A = (0, 0)", "B = (4, -3)", "f = 5");
	}

	@Test
	@Issue("APPS-5779")
	void releasingExistingSecondEndpointAwayFromPressShouldMoveIt() {
		add("A = (2, -2)");
		clickRW(0, 0);

		pressRW(2, -2);
		releaseRW(4, -3);

		checkContent("A = (4, -3)", "B = (0, 0)", "f = 5");
	}
}
