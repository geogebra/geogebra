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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_FREEHAND_SHAPE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianPen;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.test.TestEvent;
import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/** Tests Freehand Shape input, recognition, cancellation and construction persistence. */
class FreehandShapeToolTest extends BaseToolTest {

	@Test
	void enteringAndHoveringShouldNotCreateObjects() {
		Set<GeoElement> before = getConstructionSnapshot();
		setMode(MODE_FREEHAND_SHAPE);

		moveMouseRW(2, -2);

		assertEquals(MODE_FREEHAND_SHAPE, getApp().getMode());
		assertTrue(ec.getPen().isFreehand());
		assertEquals(before, getConstructionSnapshot());
	}

	@ParameterizedTest
	@CsvSource({
		"100,100,200,200",
		"200,200,100,100",
		"100,100,200,100",
		"200,100,100,100",
		"100,100,100,200",
		"100,200,100,100"
	})
	void straightGestureShouldCreateSegmentWithHiddenEndpoints(int x1, int y1, int x2, int y2) {
		setMode(MODE_FREEHAND_SHAPE);

		drawEdges(new int[][] {{x1, y1}, {x2, y2}});

		GeoSegment segment = getOnlyNewObject(Set.of(), GeoSegment.class);
		assertTrue(segment.isDefined());
		assertEquals(Math.hypot(x2 - x1, y2 - y1) / 50, segment.getLength(), 0.02);
		assertEndpointPair(segment, x1 / 50.0, -y1 / 50.0, x2 / 50.0, -y2 / 50.0);
		assertFalse(segment.getStartPoint().isEuclidianVisible());
		assertFalse(segment.getEndPoint().isEuclidianVisible());
		assertFalse(segment.isLabelVisible());
		assertEquals(3, getConstructionSnapshot().size());
	}

	@ParameterizedTest
	@CsvSource({
		"100,100,200,200",
		"200,200,100,100",
		"100,100,200,100",
		"200,100,100,100",
		"100,100,100,200",
		"100,200,100,100"
	})
	@Issue("APPS-7920")
	void pressAndDistantReleaseShouldRecognizeSegment(int x1, int y1, int x2, int y2) {
		setMode(MODE_FREEHAND_SHAPE);

		dragStart(x1, y1);
		pointerRelease(x2, y2);

		GeoSegment segment = getOnlyNewObject(Set.of(), GeoSegment.class);
		assertEndpointPair(segment, x1 / 50.0, -y1 / 50.0, x2 / 50.0, -y2 / 50.0);
		assertEquals(Math.hypot(x2 - x1, y2 - y1) / 50, segment.getLength(), 1E-8);
	}

	@ParameterizedTest
	@CsvSource({"100,100,300,108", "100,100,108,300", "108,300,100,100"})
	void almostAxisAlignedGestureShouldSnapToAxis(int x1, int y1, int x2, int y2) {
		setMode(MODE_FREEHAND_SHAPE);

		drawEdges(new int[][] {{x1, y1}, {x2, y2}});

		GeoSegment segment = getOnlyNewObject(Set.of(), GeoSegment.class);
		GeoPointND a = segment.getStartPoint();
		GeoPointND b = segment.getEndPoint();
		if (Math.abs(x2 - x1) > Math.abs(y2 - y1)) {
			assertEquals(a.getInhomY(), b.getInhomY(), 1E-8);
		} else {
			assertEquals(a.getInhomX(), b.getInhomX(), 1E-8);
		}
	}

	@Test
	void closedTriangleShouldCreateUnfilledPolygonWithHiddenVertices() {
		setMode(MODE_FREEHAND_SHAPE);
		int[][] vertices = {{100, 200}, {200, 100}, {300, 200}, {100, 200}};

		drawEdges(vertices);

		GeoPolygon polygon = getOnlyNewObject(Set.of(), GeoPolygon.class);
		assertPolygon(polygon, 3, 4);
		assertVerticesNear(polygon, vertices);
		assertEquals(7, getConstructionSnapshot().size());
	}

	@ParameterizedTest
	@CsvSource({"0,0.08", "30,0.08", "88,0.25"})
	@Issue("APPS-7920")
	void closedRectangleShouldCreateRightAnglesAndExpectedArea(int angle, double areaTolerance) {
		setMode(MODE_FREEHAND_SHAPE);
		int[][] vertices = {{100, 100}, {300, 100}, {300, 200}, {100, 200}, {100, 100}};
		for (int[] vertex : vertices) {
			double x = vertex[0] - 200;
			double y = vertex[1] - 150;
			double radians = Math.toRadians(angle);
			vertex[0] = 200 + (int) Math.round(x * Math.cos(radians) - y * Math.sin(radians));
			vertex[1] = 150 + (int) Math.round(x * Math.sin(radians) + y * Math.cos(radians));
		}

		drawEdges(vertices);

		GeoPolygon polygon = getOnlyNewObject(Set.of(), GeoPolygon.class);
		// Snapping the 88-degree gesture to vertical slightly changes the fitted area.
		assertPolygon(polygon, 4, 8, areaTolerance);
		assertVerticesNear(polygon, vertices);
		GeoPointND[] points = polygon.getPoints();
		for (int i = 0; i < points.length; i++) {
			GeoPointND a = points[i];
			GeoPointND b = points[(i + 1) % points.length];
			GeoPointND c = points[(i + 2) % points.length];
			assertEquals(
					0,
					(a.getInhomX() - b.getInhomX()) * (c.getInhomX() - b.getInhomX())
							+ (a.getInhomY() - b.getInhomY()) * (c.getInhomY() - b.getInhomY()),
					1E-8);
		}
		assertEquals(9, getConstructionSnapshot().size());
	}

	@Test
	void closedSkewQuadrilateralShouldNotBeForcedIntoRectangle() {
		setMode(MODE_FREEHAND_SHAPE);
		int[][] vertices = {{100, 100}, {300, 100}, {250, 200}, {100, 200}, {100, 100}};

		drawEdges(vertices);

		GeoPolygon polygon = getOnlyNewObject(Set.of(), GeoPolygon.class);
		assertPolygon(polygon, 4, 7);
		assertVerticesNear(polygon, vertices);
	}

	@Test
	void sparseRoundGestureShouldRecognizeConic() {
		setMode(MODE_FREEHAND_SHAPE);

		drawSamples(new int[][] {
			{200, 210}, {207, 207}, {210, 200}, {207, 193}, {200, 190}, {193, 193}, {190, 200}, {193, 207}
		});

		GeoConic circle = getOnlyNewObject(Set.of(), GeoConic.class);
		assertTrue(circle.isDefined());
		assertEquals(GeoConicNDConstants.CONIC_CIRCLE, circle.getType());
		assertEquals(4, circle.getMidpoint().getX(), 0.04);
		assertEquals(-4, circle.getMidpoint().getY(), 0.04);
		assertEquals(0.2, circle.getHalfAxis(0), 0.04);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void roundGestureShouldCreateCircleInEitherDirection(boolean reverse) {
		setMode(MODE_FREEHAND_SHAPE);

		drawEllipse(200, 200, 80, 80, reverse);

		GeoConic circle = getOnlyNewObject(Set.of(), GeoConic.class);
		assertEquals(GeoConicNDConstants.CONIC_CIRCLE, circle.getType());
		assertEquals(4, circle.getMidpoint().getX(), 0.03);
		assertEquals(-4, circle.getMidpoint().getY(), 0.03);
		assertEquals(1.6, circle.getHalfAxis(0), 0.03);
		assertTrue(circle.isDefined());
		assertEquals(2, getConstructionSnapshot().size());
	}

	@Test
	void ellipticGestureShouldCreateEllipseInClassic() {
		setMode(MODE_FREEHAND_SHAPE);

		drawEllipse(250, 200, 120, 60, false);

		GeoConic ellipse = getOnlyNewObject(Set.of(), GeoConic.class);
		assertEquals(GeoConicNDConstants.CONIC_ELLIPSE, ellipse.getType());
		assertEquals(5, ellipse.getMidpoint().getX(), 0.03);
		assertEquals(-4, ellipse.getMidpoint().getY(), 0.03);
		assertEquals(2.4, ellipse.getHalfAxis(0), 0.03);
		assertEquals(1.2, ellipse.getHalfAxis(1), 0.03);
		assertFalse(ellipse.isLabelVisible());
	}

	@Test
	void ellipseShouldBeRejectedInNotesAndNextGestureShouldStillWork() {
		getApp().setNotesConfig();
		setMode(MODE_FREEHAND_SHAPE);

		drawEllipse(250, 200, 120, 60, false);

		assertTrue(getConstructionSnapshot().isEmpty());
		drag(100, 100, 200, 200);
		assertTrue(getOnlyNewObject(Set.of(), GeoSegment.class).isDefined());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	@Issue("APPS-7920")
	void unrecognizedMonotonicGestureShouldFallBackToFunctionIncludingRelease(boolean reverse) {
		setMode(MODE_FREEHAND_SHAPE);
		ec.getPen().setPenColor(GColor.BLUE);
		int[][] samples = {{100, 100}, {120, 180}, {140, 120}, {160, 180}, {180, 100}, {200, 150}};
		if (reverse) {
			for (int i = 0; i < samples.length / 2; i++) {
				int[] temp = samples[i];
				samples[i] = samples[samples.length - i - 1];
				samples[samples.length - i - 1] = temp;
			}
		}

		drawSamples(samples);

		GeoFunction function = getOnlyNewObject(Set.of(), GeoFunction.class);
		assertTrue(function.isDefined());
		assertEquals(GColor.BLUE, function.getObjectColor());
		assertEquals(-2, function.value(2), 1E-8);
		assertEquals(-2.8, function.value(2.2), 1E-8);
		assertEquals(-3.6, function.value(2.4), 1E-8);
		assertEquals(-2.4, function.value(2.8), 1E-8);
		assertEquals(-3, function.value(4), 1E-8);
		assertTrue(Double.isNaN(function.value(1.98)));
		assertTrue(Double.isNaN(function.value(4.02)));
	}

	@Test
	void rejectedNonMonotonicGestureShouldLeaveNoObjectsOrUndo() {
		enableUndo();
		setMode(MODE_FREEHAND_SHAPE);

		drawSamples(new int[][] {
			{100, 100}, {180, 200}, {100, 200}, {180, 100}, {100, 150}, {180, 150}, {100, 100}
		});

		assertTrue(getConstructionSnapshot().isEmpty());
		assertFalse(getKernel().undoPossible());
		drag(250, 150, 350, 150);
		assertEndpointPair(getOnlyNewObject(Set.of(), GeoSegment.class), 5, -3, 7, -3);
	}

	@Test
	@Issue("APPS-7920")
	void modeChangeShouldResetFunctionBoundsFromDiscardedGesture() {
		setMode(MODE_FREEHAND_SHAPE);
		dragStart(400, 100);
		drag(500, 150);
		setMode(MODE_MOVE);
		setMode(MODE_FREEHAND_SHAPE);

		drawSamples(
				new int[][] {{100, 100}, {120, 180}, {140, 120}, {160, 180}, {180, 100}, {200, 150}});

		GeoFunction function = getOnlyNewObject(Set.of(), GeoFunction.class);
		assertEquals(-2, function.value(2), 1E-8);
		assertEquals(-3, function.value(4), 1E-8);
		assertTrue(Double.isNaN(function.value(4.02)));
	}

	@Test
	@Issue("APPS-7920")
	void modeChangeShouldDiscardQueuedPolygonEdges() {
		setMode(MODE_FREEHAND_SHAPE);
		drawEdges(new int[][] {{100, 100}, {200, 100}, {200, 200}});
		assertTrue(getOnlyNewObject(Set.of(), GeoFunction.class).isDefined());
		setMode(MODE_MOVE);
		setMode(MODE_FREEHAND_SHAPE);
		Set<String> before = getObjectNames();

		drawEdges(new int[][] {{200, 200}, {100, 200}, {100, 100}});

		// Each open gesture may fall back to a function, but their edges must not form a polygon.
		assertTrue(getOnlyNewObject(before, GeoFunction.class).isDefined());
		assertEquals(2, getConstructionSnapshot().size());
	}

	@Test
	void consecutivePartialGesturesShouldStillCompletePolygon() {
		setMode(MODE_FREEHAND_SHAPE);
		drawEdges(new int[][] {{100, 100}, {200, 100}, {200, 200}});
		Set<String> before = getObjectNames();

		drawEdges(new int[][] {{200, 200}, {100, 200}, {100, 100}});

		assertPolygon(getOnlyNewObject(before, GeoPolygon.class), 4, 4);
	}

	@Test
	@Issue("APPS-7920")
	void completingPolygonShouldDiscardUnrelatedOlderEdges() {
		setMode(MODE_FREEHAND_SHAPE);
		drawEdges(new int[][] {{100, 100}, {200, 100}, {200, 200}});
		drawEdges(new int[][] {{300, 100}, {400, 100}, {400, 200}});
		Set<String> before = getObjectNames();

		drawEdges(new int[][] {{400, 200}, {300, 200}, {300, 100}});

		GeoPolygon polygon = getOnlyNewObject(before, GeoPolygon.class);
		assertPolygon(polygon, 4, 4);
		assertVerticesNear(
				polygon, new int[][] {{300, 100}, {400, 100}, {400, 200}, {300, 200}, {300, 100}});
	}

	@Test
	void coordinatesShouldRespectIndependentViewScalesAndOffsets() {
		getApp().getActiveEuclidianView().setCoordSystem(250, 200, 40, 60);
		setMode(MODE_FREEHAND_SHAPE);

		drawEdges(new int[][] {{100, 100}, {180, 160}});

		assertEndpointPair(
				getOnlyNewObject(Set.of(), GeoSegment.class), -3.75, 5.0 / 3, -1.75, 2.0 / 3);
	}

	@ParameterizedTest
	@EnumSource(PointerEventType.class)
	@Issue("APPS-7920")
	void tapShouldNotCreateDegenerateShapeOrUndo(@NonNull PointerEventType pointer) {
		enableUndo();
		setMode(MODE_FREEHAND_SHAPE);

		click(100, 100, pointer);

		assertTrue(getConstructionSnapshot().isEmpty());
		assertFalse(getKernel().undoPossible());
	}

	@Test
	@Issue("APPS-7920")
	void releaseWithoutPressShouldNotCreateObjects() {
		setMode(MODE_FREEHAND_SHAPE);

		pointerRelease(100, 100);

		assertTrue(getConstructionSnapshot().isEmpty());
	}

	@Test
	@Issue("APPS-7920")
	void rightDragShouldNotCreateShapeOrChangeExistingPoint() {
		GeoPoint point = add("A=(2,-2)");
		setMode(MODE_FREEHAND_SHAPE);

		dragRWRight(2, -2, 4, -4);

		assertEquals(Set.of(point), getConstructionSnapshot());
		assertEquals(2, point.getInhomX(), 1E-8);
		assertEquals(-2, point.getInhomY(), 1E-8);
	}

	@Test
	@Issue("APPS-7920")
	void cancelledPinchShouldClearInkAndNotRecordUndo() {
		enableUndo();
		setMode(MODE_FREEHAND_SHAPE);
		EuclidianPen pen = ec.getPen();
		pen.handleMousePressedForPenMode(new TestEvent(100, 100));

		pen.handleMouseReleasedForPenMode(false, 100, 100, true, PointerEventType.TOUCH);

		assertTrue(getConstructionSnapshot().isEmpty());
		assertFalse(getKernel().undoPossible());
		GGraphics2D graphics = Mockito.mock(GGraphics2D.class);
		pen.setStyleAndRepaint(graphics);
		Mockito.verifyNoInteractions(graphics);
		drag(250, 150, 350, 150);
		assertEndpointPair(getOnlyNewObject(Set.of(), GeoSegment.class), 5, -3, 7, -3);
	}

	@Test
	void pinchAfterActualDrawingShouldRetainShape() {
		setMode(MODE_FREEHAND_SHAPE);
		EuclidianPen pen = ec.getPen();
		pen.handleMousePressedForPenMode(new TestEvent(100, 100));
		pen.handleMouseDraggedForPenMode(new TestEvent(200, 200));

		pen.handleMouseReleasedForPenMode(false, 200, 200, true, PointerEventType.TOUCH);

		assertEndpointPair(getOnlyNewObject(Set.of(), GeoSegment.class), 2, -2, 4, -4);
	}

	@Test
	void previewShouldUsePenStyleAndClearAfterRecognition() {
		getApp().getSettings().getPenTools().setLastSelectedPenColor(GColor.BLUE);
		getApp().getSettings().getPenTools().setLastPenThickness(12);
		getApp().getSettings().getPenTools().setLastPenOpacity(111);
		setMode(MODE_FREEHAND_SHAPE);
		dragStart(100, 100);
		drag(150, 150);
		GGraphics2D graphics = Mockito.mock(GGraphics2D.class);

		ec.getPen().setStyleAndRepaint(graphics);

		Mockito.verify(graphics).setColor(GColor.BLUE.deriveWithAlpha(111));
		ArgumentCaptor<GBasicStroke> stroke = ArgumentCaptor.forClass(GBasicStroke.class);
		Mockito.verify(graphics).setStroke(stroke.capture());
		assertEquals(6, stroke.getValue().getLineWidth(), 1E-8);
		Mockito.verify(graphics).draw(Mockito.any());
		pointerRelease(200, 200);
		assertEndpointPair(getOnlyNewObject(Set.of(), GeoSegment.class), 2, -2, 4, -4);
		Mockito.reset(graphics);
		ec.getPen().setStyleAndRepaint(graphics);
		Mockito.verifyNoInteractions(graphics);
	}

	@Test
	void eachGestureShouldCreateSeparateShape() {
		setMode(MODE_FREEHAND_SHAPE);
		drag(100, 100, 200, 100);
		GeoSegment first = getOnlyNewObject(Set.of(), GeoSegment.class);
		Set<String> before = getObjectNames();

		drag(250, 200, 350, 200);

		assertEndpointPair(first, 2, -2, 4, -2);
		assertEndpointPair(getOnlyNewObject(before, GeoSegment.class), 5, -4, 7, -4);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	@Issue("APPS-7920")
	void successfulGestureShouldConsumeAnchorWithoutDeletingIt(boolean provisional) {
		GeoPoint anchor = add("A=(2,-2)");
		setMode(MODE_FREEHAND_SHAPE);
		ec.getPen().setInitialPoint(anchor, provisional);
		Set<String> before = getObjectNames();

		drawEdges(new int[][] {{100, 100}, {200, 200}});

		GeoSegment first = getOnlyNewObject(before, GeoSegment.class);
		assertSame(anchor, first.getStartPoint());
		assertEndpointPair(first, 2, -2, 4, -4);
		Set<String> completed = getObjectNames();
		drag(300, 100, 400, 100);
		assertEndpointPair(getOnlyNewObject(completed, GeoSegment.class), 6, -2, 8, -2);
		assertTrue(first.isDefined());
		Set<String> both = getObjectNames();
		setMode(MODE_MOVE);
		assertEquals(both, getObjectNames());
	}

	@ParameterizedTest
	@CsvSource({"false,false", "false,true", "true,false", "true,true"})
	@Issue("APPS-7920")
	void cancelledAnchoredGestureShouldDiscardOnlyProvisionalAnchor(
			boolean provisional, boolean pinch) {
		GeoPoint anchor = add("A=(2,-2)");
		enableUndo();
		setMode(MODE_FREEHAND_SHAPE);
		ec.getPen().setInitialPoint(anchor, provisional);

		if (pinch) {
			ec.getPen().handleMousePressedForPenMode(new TestEvent(100, 100));
			ec.getPen().handleMouseReleasedForPenMode(false, 200, 200, true, PointerEventType.TOUCH);
		} else {
			click(100, 100);
		}

		assertEquals(provisional ? Set.of() : Set.of(anchor), getConstructionSnapshot());
		assertFalse(getKernel().undoPossible());
		drag(250, 150, 350, 150);
		assertEndpointPair(getOnlyNewObject(Set.of(), GeoSegment.class), 5, -3, 7, -3);
	}

	@Test
	@Issue("APPS-7920")
	void anchoredRectangleShouldUseInhomogeneousCoordinates() {
		GeoPoint anchor = add("A=(2,-2)");
		anchor.setCoords(4, -4, 2);
		setMode(MODE_FREEHAND_SHAPE);
		ec.getPen().setInitialPoint(anchor, false);
		Set<String> before = getObjectNames();
		int[][] vertices = {{100, 100}, {300, 100}, {300, 200}, {100, 200}, {100, 100}};

		drawEdges(vertices);

		GeoPolygon polygon = getOnlyNewObject(before, GeoPolygon.class);
		assertSame(anchor, polygon.getPoints()[0]);
		assertPolygon(polygon, 4, 8);
		assertVerticesNear(polygon, vertices);
	}

	@Test
	@Issue("APPS-7920")
	void anchoredCircleShouldUseInhomogeneousCoordinates() {
		GeoPoint anchor = add("A=(5.6,-4)");
		anchor.setCoords(11.2, -8, 2);
		setMode(MODE_FREEHAND_SHAPE);
		ec.getPen().setInitialPoint(anchor, false);
		Set<String> before = getObjectNames();

		drawEllipse(200, 200, 80, 80, false);

		GeoConic circle = getOnlyNewObject(before, GeoConic.class);
		assertTrue(circle.isDefined());
		assertEquals(GeoConicNDConstants.CONIC_CIRCLE, circle.getType());
		assertEquals(4, circle.getMidpoint().getX(), 0.03);
		assertEquals(-4, circle.getMidpoint().getY(), 0.03);
		assertEquals(1.6, circle.getHalfAxis(0), 0.03);
	}

	@Test
	void modeChangeShouldDiscardUnfinishedGesture() {
		setMode(MODE_FREEHAND_SHAPE);
		dragStart(100, 100);
		drag(200, 100);

		setMode(MODE_MOVE);
		setMode(MODE_FREEHAND_SHAPE);
		drag(250, 200, 350, 200);

		assertEndpointPair(getOnlyNewObject(Set.of(), GeoSegment.class), 5, -4, 7, -4);
	}

	@Test
	void switchingFromPenShouldRecognizeShapeAndSwitchingBackShouldDrawInk() {
		setMode(MODE_PEN);
		drag(100, 100, 200, 100);
		GeoLocusStroke stroke = getOnlyNewObject(Set.of(), GeoLocusStroke.class);
		List<String> original = strokeGeometry(stroke);
		Set<String> before = getObjectNames();
		setMode(MODE_FREEHAND_SHAPE);

		drag(250, 200, 350, 200);

		assertTrue(getOnlyNewObject(before, GeoSegment.class).isDefined());
		assertEquals(original, strokeGeometry(stroke));
		setMode(MODE_PEN);
		Set<String> shapes = getObjectNames();
		drag(400, 250, 500, 250);
		assertFalse(ec.getPen().isFreehand());
		assertFalse(getOnlyNewObject(shapes, GeoLocusStroke.class).getPoints().isEmpty());
	}

	@Test
	void undoRedoShouldHandleWholeShapeAndItsAuxiliaryPoints() {
		enableUndo();
		setMode(MODE_FREEHAND_SHAPE);
		drawEdges(new int[][] {{100, 100}, {300, 100}, {300, 200}, {100, 200}, {100, 100}});
		Set<String> first = getObjectNames();
		drag(350, 250, 450, 250);
		Set<String> both = getObjectNames();
		assertEquals(12, both.size());

		getKernel().undo();
		assertEquals(first, getObjectNames());
		assertPolygon(getOnlyNewObject(Set.of(), GeoPolygon.class), 4, 8);
		getKernel().undo();
		assertTrue(getConstructionSnapshot().isEmpty());
		getKernel().redo();
		assertEquals(first, getObjectNames());
		getKernel().redo();
		assertEquals(both, getObjectNames());
		assertTrue(getOnlyNewObject(first, GeoSegment.class).isDefined());
	}

	@Test
	void xmlRoundTripShouldPreserveRecognizedCircle() {
		setMode(MODE_FREEHAND_SHAPE);
		drawEllipse(200, 200, 80, 80, false);
		String label = getOnlyNewObject(Set.of(), GeoConic.class).getLabelSimple();
		Set<String> names = getObjectNames();

		getApp().setXML(getApp().getXML(), true);

		GeoConic circle = getOnlyNewObject(Set.of(), GeoConic.class);
		assertEquals(label, circle.getLabelSimple());
		assertEquals(names, getObjectNames());
		assertEquals(GeoConicNDConstants.CONIC_CIRCLE, circle.getType());
		assertEquals(1.6, circle.getHalfAxis(0), 0.03);
		assertEquals(4, circle.getMidpoint().getX(), 0.03);
		assertEquals(-4, circle.getMidpoint().getY(), 0.03);
	}

	private void enableUndo() {
		getApp().setUndoActive(true);
		getApp().storeUndoInfo();
	}

	private void assertEndpointPair(
			@NonNull GeoSegment segment, double x1, double y1, double x2, double y2) {
		GeoPointND a = segment.getStartPoint();
		GeoPointND b = segment.getEndPoint();
		if (Math.hypot(a.getInhomX() - x1, a.getInhomY() - y1)
				<= Math.hypot(a.getInhomX() - x2, a.getInhomY() - y2)) {
			assertPoint(a, x1, y1);
			assertPoint(b, x2, y2);
		} else {
			assertPoint(a, x2, y2);
			assertPoint(b, x1, y1);
		}
	}

	private void assertPoint(@NonNull GeoPointND point, double x, double y) {
		assertEquals(x, point.getInhomX(), 0.03);
		assertEquals(y, point.getInhomY(), 0.03);
	}

	private void assertPolygon(@NonNull GeoPolygon polygon, int vertices, double area) {
		assertPolygon(polygon, vertices, area, 0.08);
	}

	private void assertPolygon(
			@NonNull GeoPolygon polygon, int vertices, double area, double areaTolerance) {
		assertTrue(polygon.isDefined());
		assertEquals(vertices, polygon.getPoints().length);
		assertEquals(area, polygon.getDouble(), areaTolerance);
		assertEquals(0, polygon.getAlphaValue(), 1E-8);
		for (GeoPointND point : polygon.getPoints()) {
			assertFalse(point.isEuclidianVisible());
		}
		for (GeoSegmentND segment : polygon.getSegments()) {
			assertFalse(segment.toGeoElement().isSelectionAllowed(null));
			assertFalse(segment.isLabelVisible());
		}
	}

	private void assertVerticesNear(@NonNull GeoPolygon polygon, int[] @NonNull [] vertices) {
		for (int i = 0; i < vertices.length - 1; i++) {
			double x = vertices[i][0] / 50.0;
			double y = -vertices[i][1] / 50.0;
			boolean found = false;
			for (GeoPointND point : polygon.getPoints()) {
				found |= Math.hypot(point.getInhomX() - x, point.getInhomY() - y) < 0.12;
			}
			assertTrue(found, "Missing vertex near (" + x + ", " + y + ")");
		}
	}
}
