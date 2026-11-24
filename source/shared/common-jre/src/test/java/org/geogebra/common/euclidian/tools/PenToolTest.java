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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianPen;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian.measurement.MeasurementController;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.GTimer;
import org.geogebra.test.TestEvent;
import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/** Tests pen gestures through the real controller and stroke lifecycle callbacks. */
class PenToolTest extends BaseToolTest {
	private enum Style {
		COLOR,
		THICKNESS,
		OPACITY,
		LINE_TYPE
	}

	private enum SampledPath {
		DENSE_LINE,
		SHARP_TURN,
		LONG_STEPS,
		CLOSED_LOOP
	}

	@Test
	void enteringPenModeShouldNotCreateConstructionElements() {
		Set<GeoElement> before = getConstructionSnapshot();

		setMode(MODE_PEN);

		assertEquals(before, getConstructionSnapshot());
		assertFalse(ec.getPen().isFreehand());
		assertEquals(MODE_PEN, getApp().getMode());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void defaultStrokeShouldBeDarkInClassicAndNotes(boolean notes) {
		if (notes) {
			getApp().setNotesConfig();
			getApp().getSettings().resetSettings(getApp());
		}
		setMode(MODE_PEN);
		Set<String> before = getObjectNames();

		drag(0, 0, 50, 50);

		GeoLocusStroke stroke = getOnlyNewObject(before, GeoLocusStroke.class);
		assertEquals(GeoGebraColorConstants.NEUTRAL_900, stroke.getObjectColor());
		assertEquals(EuclidianConstants.DEFAULT_PEN_SIZE, stroke.getLineThickness());
		assertEquals(255, stroke.getLineOpacity());
		assertTrue(stroke.isDefined());
		assertTrue(stroke.isEuclidianVisible());
		assertTrue(stroke.isPenStroke());
		assertEquals(GeoElement.TOOLTIP_OFF, stroke.getTooltipMode());
		assertEquals(Set.of(stroke), getConstructionSnapshot());
	}

	@ParameterizedTest
	@EnumSource(PointerEventType.class)
	void tapShouldCreateDefinedDotForEachPointer(@NonNull PointerEventType pointer) {
		setMode(MODE_PEN);

		click(100, 150, pointer);

		GeoLocusStroke stroke = onlyStroke();
		assertTrue(stroke.isDefined());
		assertEquals(2, stroke.getPoints().size());
		for (MyPoint point : stroke.getPoints()) {
			assertEquals(2, point.x, 1E-8);
			assertEquals(-3, point.y, 1E-8);
		}
		assertFalse(stroke.getPoints().isEmpty());
	}

	@ParameterizedTest
	@ValueSource(ints = {0, 1, 3, 4})
	void shortMovementShouldRespectSampleDistanceThreshold(int pixels) {
		setMode(MODE_PEN);
		dragStart(100, 100);
		drag(100 + pixels, 100);
		pointerRelease(100 + pixels, 100);

		List<MyPoint> points = onlyStroke().getPoints();
		assertEquals(2, points.size());
		assertEquals(2, points.get(0).x, 1E-8);
		assertEquals(pixels <= 3 ? 2 : 2.08, points.get(1).x, 1E-8);
		assertEquals(-2, points.get(0).y, 1E-8);
		assertEquals(-2, points.get(1).y, 1E-8);
	}

	@Test
	void strokeShouldUseBothViewScalesAndOffsets() {
		getApp().getActiveEuclidianView().setCoordSystem(250, 200, 40, 60);
		setMode(MODE_PEN);

		drag(100, 100, 180, 160);

		List<MyPoint> points = onlyStroke().getPoints();
		assertEquals(-3.75, points.get(0).x, 1E-8);
		assertEquals(5.0 / 3, points.get(0).y, 1E-8);
		MyPoint last = points.get(points.size() - 1);
		assertEquals(-1.75, last.x, 1E-8);
		assertEquals(2.0 / 3, last.y, 1E-8);
	}

	@Test
	void consecutiveGesturesShouldAppendDisconnectedPartsToSameStroke() {
		setMode(MODE_PEN);
		drag(100, 100, 180, 100);
		GeoLocusStroke stroke = onlyStroke();
		List<String> first = strokeGeometry(stroke);

		drag(250, 150, 350, 150);

		assertSame(stroke, onlyStroke());
		assertEquals(first, strokeGeometry(stroke).subList(0, first.size()));
		assertEquals(
				2,
				stroke.getPoints().stream()
						.filter(point -> point.getSegmentType() == SegmentType.MOVE_TO)
						.count());
	}

	@Test
	void timeoutShouldStartNewStrokeWithoutChangingPreviousOne() {
		setMode(MODE_PEN);
		drag(100, 100, 180, 100);
		GeoLocusStroke first = onlyStroke();
		List<String> original = strokeGeometry(first);
		Set<String> before = getObjectNames();

		ec.getPen().onRun();
		drag(250, 150, 350, 150);

		GeoLocusStroke next = getOnlyNewObject(before, GeoLocusStroke.class);
		assertNotSame(first, next);
		assertEquals(original, strokeGeometry(first));
	}

	@Test
	void leavingAndReturningToPenShouldStartNewStroke() {
		setMode(MODE_PEN);
		drag(100, 100, 180, 100);
		Set<String> before = getObjectNames();

		setMode(MODE_MOVE);
		setMode(MODE_PEN);
		drag(250, 150, 350, 150);

		getOnlyNewObject(before, GeoLocusStroke.class);
	}

	@ParameterizedTest
	@EnumSource(Style.class)
	void changedStyleShouldCreateNewStrokeAndPreserveOldStyle(@NonNull Style style) {
		setMode(MODE_PEN);
		drag(100, 100, 180, 100);
		GeoLocusStroke first = onlyStroke();
		Set<String> before = getObjectNames();
		List<String> original = strokeGeometry(first);
		Object originalStyle = styleValue(first, style);

		applyStyle(style, true);
		drag(250, 150, 350, 150);

		GeoLocusStroke next = getOnlyNewObject(before, GeoLocusStroke.class);
		assertEquals(original, strokeGeometry(first));
		assertFalse(next.getPoints().isEmpty());
		assertEquals(originalStyle, styleValue(first, style));
		assertNotEquals(originalStyle, styleValue(next, style));
		assertEquals(expectedChangedStyle(style), styleValue(next, style));
		assertEquals(styleValue(ec.getPen().getDefaultPenLine(), style), styleValue(next, style));
	}

	@ParameterizedTest
	@EnumSource(Style.class)
	void unchangedStyleShouldKeepAppendingToSameStroke(@NonNull Style style) {
		setMode(MODE_PEN);
		drag(100, 100, 180, 100);
		GeoLocusStroke first = onlyStroke();
		int originalPointCount = first.getPoints().size();

		applyStyle(style, false);
		drag(250, 150, 350, 150);

		assertSame(first, onlyStroke());
		assertTrue(first.getPoints().size() > originalPointCount);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void scaledPenSizeShouldFollowViewSetting(boolean scaled) {
		setMode(MODE_PEN);
		ec.getPen().setPenSize(12);
		getApp().getActiveEuclidianView().setCoordSystem(0, 0, 100, 100);
		getApp().getActiveEuclidianView().getSettings().setLineThicknessScaled(scaled);

		assertEquals(scaled ? 12 : 6, ec.getPen().getScaledPenSize(), 1E-8);
	}

	@Test
	void removingLastStrokeShouldAllowDrawingAgain() {
		setMode(MODE_PEN);
		drag(100, 100, 180, 100);
		onlyStroke().remove();
		assertTrue(getObjectNames().isEmpty());

		drag(250, 150, 350, 150);

		assertTrue(onlyStroke().isDefined());
		assertEquals(5, onlyStroke().getPoints().get(0).x, 1E-8);
	}

	@Test
	void removingUnrelatedObjectShouldNotSplitPendingStroke() {
		GeoPoint point = add("A=(1,-4)");
		setMode(MODE_PEN);
		drag(100, 100, 180, 100);
		GeoLocusStroke first = onlyStroke();

		point.remove();
		drag(250, 150, 350, 150);

		assertSame(first, onlyStroke());
	}

	@Test
	void rightDragOverEmptyCanvasShouldNotCreateStroke() {
		setMode(MODE_PEN);

		dragStart(100, 100, true);
		dragEnd(180, 100, true);

		assertTrue(getObjectNames().isEmpty());
	}

	@Test
	void undoRedoShouldHandleCreationAndAppendAsSeparateActions() {
		getApp().setNotesConfig();
		getApp().setUndoActive(true);
		getApp().storeUndoInfo();
		setMode(MODE_PEN);
		drag(100, 100, 180, 100);
		List<String> first = strokeGeometry(onlyStroke());
		drag(250, 150, 350, 150);
		List<String> combined = strokeGeometry(onlyStroke());

		getKernel().undo();
		assertEquals(first, strokeGeometry(onlyStroke()));
		getKernel().undo();
		assertTrue(getObjectNames().isEmpty());
		getKernel().redo();
		assertEquals(first, strokeGeometry(onlyStroke()));
		getKernel().redo();
		assertEquals(combined, strokeGeometry(onlyStroke()));
	}

	@Test
	@Issue("APPS-7920")
	void finishingWithoutSamplesShouldNotCreateStroke() {
		getApp().setNotesConfig();
		getApp().setUndoActive(true);
		getApp().storeUndoInfo();
		setMode(MODE_PEN);
		assertFalse(getKernel().undoPossible());

		ec.getPen().finishStrokePart();

		assertTrue(getObjectNames().isEmpty());
		assertFalse(getKernel().undoPossible());
	}

	@Test
	@Issue("APPS-7920")
	void pinchCancellingSingleSampleShouldNotCreateStrokeAndShouldClearPreview() {
		setMode(MODE_PEN);
		EuclidianPen pen = ec.getPen();
		pen.handleMousePressedForPenMode(new TestEvent(100, 100));

		pen.handleMouseReleasedForPenMode(false, 100, 100, true, PointerEventType.TOUCH);

		assertTrue(getObjectNames().isEmpty());
		GGraphics2D graphics = Mockito.mock(GGraphics2D.class);
		pen.setStyleAndRepaint(graphics);
		Mockito.verifyNoInteractions(graphics);
		drag(250, 150, 350, 150);
		assertEquals(5, onlyStroke().getPoints().get(0).x, 1E-8);
	}

	@Test
	void previewShouldUsePenStyleAndDisappearAfterRelease() {
		setMode(MODE_PEN);
		ec.getPen().setPenColor(GColor.RED);
		ec.getPen().getDefaultPenLine().setLineOpacity(111);
		dragStart(100, 100);
		drag(140, 100);
		drag(180, 100);
		GGraphics2D graphics = Mockito.mock(GGraphics2D.class);

		ec.getPen().setStyleAndRepaint(graphics);

		Mockito.verify(graphics).setColor(GColor.RED.deriveWithAlpha(111));
		Mockito.verify(graphics).setStroke(Mockito.any());
		ArgumentCaptor<GShape> preview = ArgumentCaptor.forClass(GShape.class);
		Mockito.verify(graphics).draw(preview.capture());
		assertTrue(preview.getValue().getBounds2D().getWidth() > 0);
		pointerRelease(180, 100);
		Mockito.reset(graphics);
		ec.getPen().setStyleAndRepaint(graphics);
		Mockito.verifyNoInteractions(graphics);
	}

	@ParameterizedTest
	@EnumSource(SampledPath.class)
	void sampledPathsShouldKeepEndpointsAndRemainStrokes(@NonNull SampledPath path) {
		int[][] samples =
				switch (path) {
					case DENSE_LINE ->
						new int[][] {
							{100, 100}, {101, 100}, {105, 100}, {110, 100},
							{115, 100}, {120, 100}, {125, 100}, {130, 100}
						};
					case SHARP_TURN ->
						new int[][] {
							{100, 100}, {110, 100}, {120, 100}, {120, 110}, {120, 120}, {110, 120}, {100, 120}
						};
					case LONG_STEPS ->
						new int[][] {{100, 100}, {140, 110}, {180, 120}, {220, 140}, {260, 180}, {300, 200}};
					case CLOSED_LOOP ->
						new int[][] {{100, 100}, {150, 100}, {150, 150}, {100, 150}, {100, 100}};
				};
		setMode(MODE_PEN);
		dragStart(samples[0][0], samples[0][1]);
		for (int i = 1; i < samples.length; i++) {
			drag(samples[i][0], samples[i][1]);
		}
		GGraphics2D graphics = Mockito.mock(GGraphics2D.class);
		ec.getPen().setStyleAndRepaint(graphics);
		Mockito.verify(graphics).draw(Mockito.any());
		int[] end = samples[samples.length - 1];
		pointerRelease(end[0], end[1]);

		GeoLocusStroke stroke = onlyStroke();
		List<MyPoint> points =
				stroke.getPoints().stream().filter(MyPoint::isDefined).toList();
		assertTrue(points.size() >= 2);
		assertEquals(2, points.get(0).x, 1E-8);
		assertEquals(-2, points.get(0).y, 1E-8);
		MyPoint last = points.get(points.size() - 1);
		assertEquals(end[0] / 50.0, last.x, 1E-8);
		assertEquals(-end[1] / 50.0, last.y, 1E-8);
		assertTrue(stroke.isDefined());
		// A closed path must retain its extent; endpoints alone would also accept a dot.
		double maxInputX =
				Arrays.stream(samples).mapToDouble(point -> point[0] / 50.0).max().orElseThrow();
		double minInputY =
				Arrays.stream(samples).mapToDouble(point -> -point[1] / 50.0).min().orElseThrow();
		assertTrue(
				points.stream().mapToDouble(point -> point.x).max().orElseThrow() >= maxInputX - 0.1);
		assertTrue(
				points.stream().mapToDouble(point -> point.y).min().orElseThrow() <= minInputY + 0.1);
		assertEquals(1, getObjectNames().size());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void resetShouldClearInitialAnchorAndRemoveOnlyTemporaryPoint(boolean temporary) {
		GeoPoint anchor = add("A=(1,-2)");
		setMode(MODE_PEN);
		ec.getPen().setInitialPoint(anchor, temporary);
		drag(150, 150, 200, 150);
		GeoLocusStroke first = onlyStroke();
		assertEquals(1, first.getPoints().get(0).x, 1E-8);
		assertEquals(-2, first.getPoints().get(0).y, 1E-8);

		ec.resetPen();

		ec.resetPen();
		assertEquals(!temporary, lookup("A") != null);
		Set<String> before = getObjectNames();
		drag(250, 150, 350, 150);
		GeoLocusStroke next = getOnlyNewObject(before, GeoLocusStroke.class);
		assertEquals(5, next.getPoints().get(0).x, 1E-8);
	}

	@ParameterizedTest
	@ValueSource(doubles = {2, -2})
	@Issue("APPS-7920")
	void initialAnchorShouldUseInhomogeneousCoordinates(double weight) {
		GeoPoint anchor = add("A=(2,-3)");
		anchor.setCoords(2 * weight, -3 * weight, weight);
		getApp().getActiveEuclidianView().setCoordSystem(250, 200, 40, 60);
		setMode(MODE_PEN);
		ec.getPen().setInitialPoint(anchor, false);

		drag(400, 400, 450, 450);

		MyPoint first = onlyStroke().getPoints().get(0);
		assertEquals(2, first.x, 1E-8);
		assertEquals(-3, first.y, 1E-8);
		assertSame(anchor, lookup("A"));
	}

	@Test
	void resettingDuringGestureShouldDiscardSamplesAndPreview() {
		setMode(MODE_PEN);
		dragStart(100, 100);
		drag(150, 150);

		ec.resetPen();
		pointerRelease(150, 150);

		assertTrue(getObjectNames().isEmpty());
		GGraphics2D graphics = Mockito.mock(GGraphics2D.class);
		ec.getPen().setStyleAndRepaint(graphics);
		Mockito.verifyNoInteractions(graphics);
	}

	@Test
	@Issue("APPS-7920")
	void cancelledPinchShouldNotAddUndoActionToExistingStroke() {
		getApp().setNotesConfig();
		getApp().setUndoActive(true);
		getApp().storeUndoInfo();
		setMode(MODE_PEN);
		drag(100, 100, 180, 100);
		List<String> before = strokeGeometry(onlyStroke());
		EuclidianPen pen = ec.getPen();
		pen.handleMousePressedForPenMode(new TestEvent(250, 150));

		pen.handleMouseReleasedForPenMode(false, 250, 150, true, PointerEventType.TOUCH);

		assertEquals(before, strokeGeometry(onlyStroke()));
		getKernel().undo();
		assertTrue(getObjectNames().isEmpty());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	@Issue("APPS-7920")
	void cancelledPinchShouldPreservePendingStrokeBoundary(boolean styleChanged) {
		setMode(MODE_PEN);
		drag(100, 100, 180, 100);
		GeoLocusStroke previous = onlyStroke();
		List<String> original = strokeGeometry(previous);
		Set<String> before = getObjectNames();
		EuclidianPen pen = ec.getPen();
		if (styleChanged) {
			pen.setPenColor(GColor.RED);
		} else {
			pen.onRun();
		}
		pen.handleMousePressedForPenMode(new TestEvent(250, 150));

		pen.handleMouseReleasedForPenMode(false, 250, 150, true, PointerEventType.TOUCH);

		assertEquals(before, getObjectNames());
		drag(300, 200, 400, 200);
		GeoLocusStroke next = getOnlyNewObject(before, GeoLocusStroke.class);
		assertEquals(original, strokeGeometry(previous));
		assertEquals(styleChanged ? GColor.RED : previous.getObjectColor(), next.getObjectColor());
		assertEquals(6, next.getPoints().get(0).x, 1E-8);
		assertEquals(-4, next.getPoints().get(0).y, 1E-8);
	}

	@Test
	void pinchWithActualMovementShouldRetainItsStroke() {
		setMode(MODE_PEN);
		EuclidianPen pen = ec.getPen();
		pen.handleMousePressedForPenMode(new TestEvent(100, 100));
		pen.handleMouseDraggedForPenMode(new TestEvent(150, 150));

		pen.handleMouseReleasedForPenMode(false, 150, 150, true, PointerEventType.TOUCH);

		assertEquals(List.of("2.0:-2.0:MOVE_TO", "3.0:-3.0:LINE_TO"), strokeGeometry(onlyStroke()));
	}

	@Test
	void rightDragShouldEraseOnlyStrokeWithoutDrawingInk() {
		GeoPoint point = add("A=(2.2,-2)");
		getApp().getSettings().getPenTools().setDeleteToolSize(64);
		setMode(MODE_PEN);
		drag(100, 100, 120, 100);

		dragStart(110, 100, true);
		dragEnd(111, 100, true);

		assertEquals(Set.of("A"), getObjectNames());
		assertSame(point, lookup("A"));
	}

	@Test
	void returningFromFreehandShouldReplaceRecognizerWithRegularPen() {
		setMode(EuclidianConstants.MODE_FREEHAND_SHAPE);
		assertTrue(ec.getPen().isFreehand());

		setMode(MODE_PEN);
		drag(100, 100, 150, 100);

		assertFalse(ec.getPen().isFreehand());
		assertTrue(onlyStroke().isDefined());
	}

	@Test
	void optionalMeasurementControllerShouldAllowUnconstrainedDrawing() {
		setMode(MODE_PEN);
		EuclidianPen pen = new EuclidianPen(getApp(), getApp().getActiveEuclidianView(), null);
		pen.handleMousePressedForPenMode(new TestEvent(100, 100));
		pen.handleMouseDraggedForPenMode(new TestEvent(150, 150));
		pen.startTimer();

		pen.handleMouseReleasedForPenMode(false, 150, 150, false, PointerEventType.MOUSE);

		assertTrue(onlyStroke().isDefined());
	}

	@Test
	void measurementTransformShouldReplaceUnconstrainedSamples() {
		setMode(MODE_PEN);
		MeasurementController measurement = Mockito.mock(MeasurementController.class);
		Mockito.when(measurement.applyTransformer(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenAnswer(invocation -> {
					List<GPoint2D> preview = invocation.getArgument(2);
					if (preview.size() < 2) {
						return false;
					}
					preview.clear();
					preview.add(new GPoint2D(100, 100));
					preview.add(new GPoint2D(200, 100));
					return true;
				});
		EuclidianPen pen = new EuclidianPen(getApp(), getApp().getActiveEuclidianView(), measurement);
		pen.handleMousePressedForPenMode(new TestEvent(100, 110));
		pen.handleMouseDraggedForPenMode(new TestEvent(150, 112));
		pen.handleMouseDraggedForPenMode(new TestEvent(200, 110));

		pen.handleMouseReleasedForPenMode(false, 200, 110, false, PointerEventType.MOUSE);

		List<MyPoint> points = onlyStroke().getPoints();
		assertEquals(2, points.size());
		assertEquals(2, points.get(0).x, 1E-8);
		assertEquals(-2, points.get(0).y, 1E-8);
		assertEquals(4, points.get(1).x, 1E-8);
		assertEquals(-2, points.get(1).y, 1E-8);
	}

	@Test
	void gestureShouldStopTimeoutUntilReleaseAndResetShouldStopItAgain() {
		setMode(MODE_PEN);
		GTimer timer = Mockito.mock(GTimer.class);
		EuclidianPen pen = penWithTimer(timer);

		pen.handleMousePressedForPenMode(new TestEvent(100, 100));
		pen.handleMouseDraggedForPenMode(new TestEvent(150, 150));
		pen.handleMouseReleasedForPenMode(false, 150, 150, false, PointerEventType.MOUSE);
		pen.resetPenState();

		InOrder order = Mockito.inOrder(timer);
		order.verify(timer).stop();
		order.verify(timer).start();
		order.verify(timer).stop();
		order.verifyNoMoreInteractions();
	}

	@Test
	@Issue("APPS-7920")
	void pinchCancellationShouldRestartTimeoutWithoutRecordingStroke() {
		setMode(MODE_PEN);
		GTimer timer = Mockito.mock(GTimer.class);
		EuclidianPen pen = penWithTimer(timer);
		pen.handleMousePressedForPenMode(new TestEvent(100, 100));
		pen.handleMouseDraggedForPenMode(new TestEvent(150, 150));
		pen.handleMouseReleasedForPenMode(false, 150, 150, false, PointerEventType.MOUSE);
		List<String> before = strokeGeometry(onlyStroke());
		Mockito.reset(timer);

		pen.handleMousePressedForPenMode(new TestEvent(250, 150));
		pen.handleMouseReleasedForPenMode(false, 250, 150, true, PointerEventType.TOUCH);

		InOrder order = Mockito.inOrder(timer);
		order.verify(timer).stop();
		order.verify(timer).start();
		assertEquals(before, strokeGeometry(onlyStroke()));
	}

	private @NonNull EuclidianPen penWithTimer(@NonNull GTimer timer) {
		UtilFactory factory = Mockito.spy(UtilFactory.getPrototype());
		Mockito.doReturn(timer).when(factory).newTimer(Mockito.any(), Mockito.eq(1500));
		try (MockedStatic<UtilFactory> prototype = Mockito.mockStatic(UtilFactory.class)) {
			prototype.when(UtilFactory::getPrototype).thenReturn(factory);
			EuclidianPen pen = new EuclidianPen(getApp(), getApp().getActiveEuclidianView(), null);
			Mockito.verify(factory).newTimer(Mockito.same(pen), Mockito.eq(1500));
			return pen;
		}
	}

	private @NonNull GeoLocusStroke onlyStroke() {
		return getOnlyNewObject(Set.of(), GeoLocusStroke.class);
	}

	private void applyStyle(@NonNull Style style, boolean changed) {
		EuclidianPen pen = ec.getPen();
		GeoElement line = pen.getDefaultPenLine();
		switch (style) {
			case COLOR -> pen.setPenColor(changed ? GColor.RED : line.getObjectColor());
			case THICKNESS -> pen.setPenSize(changed ? 24 : line.getLineThickness());
			case OPACITY -> line.setLineOpacity(changed ? 111 : line.getLineOpacity());
			case LINE_TYPE ->
				line.setLineType(
						changed ? EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT : line.getLineType());
		}
	}

	private @NonNull Object expectedChangedStyle(@NonNull Style style) {
		return switch (style) {
			case COLOR -> GColor.RED;
			case THICKNESS -> 24;
			case OPACITY -> 111;
			case LINE_TYPE -> EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT;
		};
	}

	private @NonNull Object styleValue(@NonNull GeoElement geo, @NonNull Style style) {
		return switch (style) {
			case COLOR -> geo.getObjectColor();
			case THICKNESS -> geo.getLineThickness();
			case OPACITY -> geo.getLineOpacity();
			case LINE_TYPE -> geo.getLineType();
		};
	}
}
