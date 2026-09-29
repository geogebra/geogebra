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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.jre.headless.DialogManagerNoGui;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.editor.share.util.Unicode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

class AngleFixedToolTest extends BaseToolTest {
	@BeforeEach
	void setMode() {
		setMode(EuclidianConstants.MODE_ANGLE_FIXED);
	}

	@Test
	void twoExistingPointsShouldCreateClockwiseAngle() {
		GeoPoint firstPoint = add("A=(0,0)");
		GeoPoint secondPoint = add("B=(0,-2)");
		prepareInput("90deg");
		clickRW(0, 0);
		clickRW(0, -2);
		GeoPoint rotatedPoint = assertInstanceOf(GeoPoint.class, lookup("A'"));
		GeoAngle angle = assertInstanceOf(GeoAngle.class, lookup(String.valueOf(Unicode.alpha)));
		assertAll(
				() -> checkContent(
						"A = (0, 0)",
						"B = (0, -2)",
						"A' = (2, -2)",
						Unicode.alpha + " = 90" + Unicode.DEGREE_STRING),
				() -> assertTrue(firstPoint.isParentOf(rotatedPoint)),
				() -> assertTrue(secondPoint.isParentOf(rotatedPoint)),
				() -> assertTrue(firstPoint.isParentOf(angle)),
				() -> assertTrue(secondPoint.isParentOf(angle)));
	}

	@Test
	void twoEmptyClicksShouldCreatePointsAndAngle() {
		prepareInput("90deg");
		clickRW(0, 0);
		clickRW(0, -2);
		GeoPoint firstPoint = assertInstanceOf(GeoPoint.class, lookup("A"));
		GeoPoint secondPoint = assertInstanceOf(GeoPoint.class, lookup("B"));
		GeoPoint rotatedPoint = assertInstanceOf(GeoPoint.class, lookup("A'"));
		GeoAngle angle = assertInstanceOf(GeoAngle.class, lookup(String.valueOf(Unicode.alpha)));

		assertAll(
				() -> checkContent(
						"A = (0, 0)",
						"B = (0, -2)",
						"A' = (2, -2)",
						Unicode.alpha + " = 90" + Unicode.DEGREE_STRING),
				() -> assertTrue(firstPoint.isParentOf(rotatedPoint)),
				() -> assertTrue(secondPoint.isParentOf(rotatedPoint)),
				() -> assertTrue(firstPoint.isParentOf(angle)),
				() -> assertTrue(secondPoint.isParentOf(angle)));
	}

	@Test
	void dialogShouldOpenOnlyAfterSecondPoint() {
		GeoPoint firstPoint = add("A = (1, -1)");
		GeoPoint secondPoint = add("B = (3, -2)");

		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			DialogManagerNoGui dialog = getOnlyDialog(dialogs);
			clickRW(1, -1);
			Mockito.verify(dialog, Mockito.never())
					.showNumberInputDialogAngleFixed(
							Mockito.anyString(),
							Mockito.any(GeoSegmentND[].class),
							Mockito.any(GeoPointND[].class),
							Mockito.any(GeoElement[].class),
							Mockito.same(ec));

			clickRW(3, -2);

			assertDialogInputs(dialog, new GeoSegmentND[0], firstPoint, secondPoint);
		}
	}

	@Test
	void clickingSegmentShouldOpenDialogImmediately() {
		add("A = (1, -2)");
		add("B = (4, -2)");
		GeoSegment segment = add("f = Segment(A, B)");

		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			clickRW(2, -2);

			assertDialogInputs(getOnlyDialog(dialogs), new GeoSegmentND[] {segment});
		}
	}

	@Test
	void pointThenSegmentShouldUseSegment() {
		GeoPoint ignoredPoint = add("A = (1, -1)");
		GeoPoint segmentStart = add("B = (2, -3)");
		GeoPoint segmentEnd = add("C = (4, -3)");
		add("f = Segment(B, C)");
		prepareInput("90deg");

		clickRW(1, -1);
		clickRW(3, -3);

		GeoPoint rotatedPoint = assertInstanceOf(GeoPoint.class, lookup("C'"));
		GeoAngle angle = assertInstanceOf(GeoAngle.class, lookup(String.valueOf(Unicode.alpha)));
		assertAll(
				() -> checkContent(
						"A = (1, -1)",
						"B = (2, -3)",
						"C = (4, -3)",
						"f = 2",
						"C' = (2, -5)",
						Unicode.alpha + " = 90" + Unicode.DEGREE_STRING),
				() -> assertTrue(segmentStart.isParentOf(rotatedPoint)),
				() -> assertTrue(segmentEnd.isParentOf(rotatedPoint)),
				() -> assertTrue(segmentStart.isParentOf(angle)),
				() -> assertTrue(segmentEnd.isParentOf(angle)),
				() -> assertFalse(ignoredPoint.isParentOf(rotatedPoint)),
				() -> assertFalse(ignoredPoint.isParentOf(angle)));
	}

	@Test
	void clickingConicShouldBeIgnored() {
		add("c = Circle((2, -2), 1)");
		int objectCount = getApp().getGgbApi().getAllObjectNames().length;

		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			DialogManagerNoGui dialog = getOnlyDialog(dialogs);
			clickRW(3, -2);

			assertEquals(objectCount, getApp().getGgbApi().getAllObjectNames().length);
			Mockito.verify(dialog, Mockito.never())
					.showNumberInputDialogAngleFixed(
							Mockito.anyString(),
							Mockito.any(GeoSegmentND[].class),
							Mockito.any(GeoPointND[].class),
							Mockito.any(GeoElement[].class),
							Mockito.same(ec));
		}
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

	private void assertDialogInputs(
			DialogManagerNoGui dialog, GeoSegmentND[] expectedSegments, GeoPointND... expectedPoints) {
		ArgumentCaptor<GeoSegmentND[]> segments = ArgumentCaptor.forClass(GeoSegmentND[].class);
		ArgumentCaptor<GeoPointND[]> points = ArgumentCaptor.forClass(GeoPointND[].class);
		Mockito.verify(dialog)
				.showNumberInputDialogAngleFixed(
						Mockito.anyString(),
						segments.capture(),
						points.capture(),
						Mockito.any(GeoElement[].class),
						Mockito.same(ec));
		assertAll(
				() -> assertArrayEquals(expectedSegments, segments.getValue()),
				() -> assertArrayEquals(expectedPoints, points.getValue()));
	}
}
