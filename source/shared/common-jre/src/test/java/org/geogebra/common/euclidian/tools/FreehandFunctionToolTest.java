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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_FREEHAND_FUNCTION;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_FREEHAND_SHAPE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_POLYGON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianPen;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.test.TestEvent;
import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

/** Tests function-only freehand input, sampling, cancellation and persistence. */
class FreehandFunctionToolTest extends BaseToolTest {

	@Test
	void enteringAndHoveringShouldNotCreateObjects() {
		Set<GeoElement> before = getConstructionSnapshot();
		setMode(MODE_FREEHAND_FUNCTION);

		moveMouseRW(2, -2);

		assertEquals(MODE_FREEHAND_FUNCTION, getApp().getMode());
		assertTrue(ec.getPen().isFreehand());
		assertEquals(before, getConstructionSnapshot());
	}

	@ParameterizedTest
	@CsvSource({
		"100,100,200,200",
		"200,200,100,100",
		"100,200,200,100",
		"200,100,100,200",
		"100,100,200,100",
		"200,100,100,100",
		"100,100,101,200",
		"101,200,100,100"
	})
	@Issue("APPS-7920")
	void straightGestureShouldCreateFunctionInsteadOfSegment(int x1, int y1, int x2, int y2) {
		setMode(MODE_FREEHAND_FUNCTION);

		drawSamples(new int[][] {{x1, y1}, {x2, y2}});

		GeoFunction function = onlyFunction();
		assertTrue(function.isDefined());
		assertEquals(-y1 / 50.0, function.value(x1 / 50.0), 1E-8);
		assertEquals(-y2 / 50.0, function.value(x2 / 50.0), 1E-8);
		assertEquals(-(y1 + y2) / 100.0, function.value((x1 + x2) / 100.0), 1E-8);
		assertDomain(function, Math.min(x1, x2) / 50.0, Math.max(x1, x2) / 50.0);
		assertEquals(Set.of(function), getConstructionSnapshot());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void curvedGestureShouldInterpolateGapsInEitherDirection(boolean reverse) {
		setMode(MODE_FREEHAND_FUNCTION);
		int[][] samples = {{100, 100}, {120, 180}, {140, 120}, {160, 180}, {180, 100}, {200, 150}};
		if (reverse) {
			reverseSamples(samples);
		}

		drawSamples(samples);

		GeoFunction function = onlyFunction();
		assertEquals(-2, function.value(2), 1E-8);
		assertEquals(-2.8, function.value(2.2), 1E-8);
		assertEquals(-3.6, function.value(2.4), 1E-8);
		assertEquals(-2.4, function.value(2.8), 1E-8);
		assertEquals(-3, function.value(4), 1E-8);
		assertDomain(function, 2, 4);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	@Issue("APPS-7920")
	void verticalPausesShouldBeAllowedInEitherDrawingDirection(boolean reverse) {
		setMode(MODE_FREEHAND_FUNCTION);
		int[][] samples = reverse
				? new int[][] {{160, 160}, {140, 150}, {140, 140}, {120, 130}, {120, 120}, {100, 100}}
				: new int[][] {{100, 100}, {120, 120}, {120, 130}, {140, 140}, {140, 150}, {160, 160}};

		drawSamples(samples);

		GeoFunction function = onlyFunction();
		assertDomain(function, 2, 3.2);
		assertEquals(-2, function.value(2), 1E-8);
		assertEquals(-3.2, function.value(3.2), 1E-8);
	}

	@Test
	void smallBacktrackShouldStillProduceInterpolatedFunction() {
		setMode(MODE_FREEHAND_FUNCTION);

		drawSamples(new int[][] {
			{100, 100},
			{120, 120},
			{140, 140},
			{160, 160},
			{155, 155},
			{180, 180},
			{200, 200},
			{220, 220},
			{240, 240},
			{260, 260},
			{280, 280},
			{300, 300}
		});

		GeoFunction function = onlyFunction();
		assertDomain(function, 2, 6);
		assertEquals(-3.1, function.value(3.1), 1E-8);
		assertEquals(-5, function.value(5), 1E-8);
	}

	@ParameterizedTest
	@CsvSource({"false,true", "true,true", "false,false", "true,false"})
	@Issue("APPS-7920")
	void backtrackingToleranceShouldIncludeTenPercentButRejectMore(boolean reverse, boolean atLimit) {
		setMode(MODE_FREEHAND_FUNCTION);
		int[][] samples = {
			{100, 100},
			{120, 120},
			{140, 140},
			{160, 160},
			{155, 155},
			{180, 180},
			{200, 200},
			{220, 220},
			{240, 240},
			{260, 260},
			{280, 280}
		};
		if (!atLimit) {
			samples = Arrays.copyOf(samples, samples.length - 1);
		}
		if (reverse) {
			reverseSamples(samples);
		}

		drawSamples(samples);

		if (atLimit) {
			GeoFunction function = onlyFunction();
			assertDomain(function, 2, 5.6);
			assertEquals(-3.1, function.value(3.1), 1E-8);
			assertEquals(-5, function.value(5), 1E-8);
		} else {
			assertTrue(getConstructionSnapshot().isEmpty());
		}
	}

	@Test
	@Issue("APPS-7920")
	void sparseRoundGestureShouldBeRejectedAndAllowNextFunction() {
		enableUndo();
		setMode(MODE_FREEHAND_FUNCTION);

		drawSamples(new int[][] {
			{200, 210}, {207, 207}, {210, 200}, {207, 193}, {200, 190}, {193, 193}, {190, 200}, {193, 207}
		});

		assertTrue(getConstructionSnapshot().isEmpty());
		assertFalse(getKernel().undoPossible());
		drawSamples(new int[][] {{100, 100}, {200, 200}});
		assertDomain(onlyFunction(), 2, 4);
	}

	@ParameterizedTest
	@EnumSource(PointerEventType.class)
	void eachPointerShouldCreateFunction(@NonNull PointerEventType pointer) {
		setMode(MODE_FREEHAND_FUNCTION);
		ec.setDraggingDelay(0);

		ec.wrapMousePressed(new TestEvent(100, 100, pointer, false));
		ec.wrapMouseDragged(new TestEvent(150, 150, pointer, false), true);
		ec.wrapMouseReleased(new TestEvent(200, 200, pointer, false));

		GeoFunction function = onlyFunction();
		assertEquals(-3, function.value(3), 1E-8);
		assertDomain(function, 2, 4);
	}

	@Test
	@Issue("APPS-7920")
	void phoneTouchPathShouldRemainFunctionOnlyAcrossGestures() {
		setMode(MODE_FREEHAND_FUNCTION);
		ec.setDraggingDelay(0);
		ec.getPen().getDefaultPenLine().setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);

		ec.touchStartPhone(new TestEvent(100, 100, PointerEventType.TOUCH, false));
		ec.wrapMouseDragged(new TestEvent(150, 150, PointerEventType.TOUCH, false), true);
		ec.touchEndPhone(new TestEvent(200, 200, PointerEventType.TOUCH, false));

		GeoFunction first = onlyFunction();
		assertDomain(first, 2, 4);
		assertEquals(-3, first.value(3), 1E-8);
		assertEquals(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT, first.getLineType());
		Set<String> before = getObjectNames();
		ec.touchStartPhone(new TestEvent(250, 200, PointerEventType.TOUCH, false));
		ec.wrapMouseDragged(new TestEvent(300, 150, PointerEventType.TOUCH, false), true);
		ec.touchEndPhone(new TestEvent(350, 100, PointerEventType.TOUCH, false));
		GeoFunction second = getOnlyNewObject(before, GeoFunction.class);
		assertDomain(second, 5, 7);
		assertEquals(-3, second.value(6), 1E-8);
		assertEquals(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT, second.getLineType());
		assertEquals(MODE_FREEHAND_FUNCTION, getApp().getMode());
		assertEquals(Set.of(first, second), getConstructionSnapshot());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	@Issue("APPS-7920")
	void verticalGestureShouldNotCreateUndefinedFunctionOrUndo(boolean reverse) {
		enableUndo();
		setMode(MODE_FREEHAND_FUNCTION);

		drawSamples(
				reverse
						? new int[][] {{100, 200}, {100, 150}, {100, 100}}
						: new int[][] {{100, 100}, {100, 150}, {100, 200}});

		assertTrue(getConstructionSnapshot().isEmpty());
		assertFalse(getKernel().undoPossible());
		drawSamples(new int[][] {{200, 100}, {300, 200}});
		assertDomain(onlyFunction(), 4, 6);
	}

	@Test
	void closedCircleShouldBeRejectedWithoutCreatingConic() {
		setMode(MODE_FREEHAND_FUNCTION);

		drawEllipse(200, 200, 80, 80, false);

		assertTrue(getConstructionSnapshot().isEmpty());
	}

	@Test
	void closedPolygonShouldBeRejectedWithoutCreatingPolygon() {
		setMode(MODE_FREEHAND_FUNCTION);

		drawEdges(new int[][] {{100, 100}, {300, 100}, {300, 200}, {100, 200}, {100, 100}});

		assertTrue(getConstructionSnapshot().isEmpty());
	}

	@Test
	void nonMonotonicScribbleShouldLeaveNoObjectsOrUndo() {
		enableUndo();
		setMode(MODE_FREEHAND_FUNCTION);

		drawSamples(new int[][] {
			{100, 100}, {180, 200}, {100, 200}, {180, 100}, {100, 150}, {180, 150}, {100, 100}
		});

		assertTrue(getConstructionSnapshot().isEmpty());
		assertFalse(getKernel().undoPossible());
	}

	@Test
	@Issue("APPS-7920")
	void ignoredJitterShouldNotExtendFunctionDomain() {
		setMode(MODE_FREEHAND_FUNCTION);
		dragStart(100, 100);
		drag(99, 100);
		drag(150, 150);
		pointerRelease(200, 200);

		GeoFunction function = onlyFunction();
		assertDomain(function, 2, 4);
		assertTrue(Double.isNaN(function.value(1.98)));
		assertEquals(-2, function.value(2), 1E-8);
		assertEquals(-3, function.value(3), 1E-8);
	}

	@Test
	void functionCoordinatesShouldUseBothViewScalesAndOffsets() {
		getApp().getActiveEuclidianView().setCoordSystem(250, 200, 40, 60);
		setMode(MODE_FREEHAND_FUNCTION);

		drawSamples(new int[][] {{100, 100}, {180, 160}});

		GeoFunction function = onlyFunction();
		assertDomain(function, -3.75, -1.75);
		assertEquals(5.0 / 3, function.value(-3.75), 1E-8);
		assertEquals(7.0 / 6, function.value(-2.75), 1E-8);
		assertEquals(2.0 / 3, function.value(-1.75), 1E-8);
	}

	@ParameterizedTest
	@EnumSource(PointerEventType.class)
	void tapShouldNotCreateFunctionOrUndo(@NonNull PointerEventType pointer) {
		enableUndo();
		setMode(MODE_FREEHAND_FUNCTION);

		click(100, 100, pointer);

		assertTrue(getConstructionSnapshot().isEmpty());
		assertFalse(getKernel().undoPossible());
	}

	@Test
	void releaseWithoutPressShouldNotCreateObjects() {
		setMode(MODE_FREEHAND_FUNCTION);

		pointerRelease(200, 200);

		assertTrue(getConstructionSnapshot().isEmpty());
	}

	@Test
	void rightDragShouldNotCreateFunction() {
		setMode(MODE_FREEHAND_FUNCTION);

		dragRWRight(2, -2, 4, -4);

		assertTrue(getConstructionSnapshot().isEmpty());
	}

	@Test
	void cancelledPinchShouldClearPreviewAndAllowNextFunction() {
		enableUndo();
		setMode(MODE_FREEHAND_FUNCTION);
		EuclidianPen pen = ec.getPen();
		pen.handleMousePressedForPenMode(new TestEvent(100, 100));

		pen.handleMouseReleasedForPenMode(false, 200, 200, true, PointerEventType.TOUCH);

		assertTrue(getConstructionSnapshot().isEmpty());
		assertFalse(getKernel().undoPossible());
		GGraphics2D graphics = Mockito.mock(GGraphics2D.class);
		pen.setStyleAndRepaint(graphics);
		Mockito.verifyNoInteractions(graphics);
		drawSamples(new int[][] {{250, 150}, {350, 150}});
		assertDomain(onlyFunction(), 5, 7);
	}

	@Test
	void pinchAfterActualDrawingShouldRetainFunctionAndUndo() {
		enableUndo();
		setMode(MODE_FREEHAND_FUNCTION);
		EuclidianPen pen = ec.getPen();
		pen.handleMousePressedForPenMode(new TestEvent(100, 100));
		pen.handleMouseDraggedForPenMode(new TestEvent(150, 150));

		pen.handleMouseReleasedForPenMode(false, 200, 200, true, PointerEventType.TOUCH);

		GeoFunction function = onlyFunction();
		assertDomain(function, 2, 4);
		assertEquals(-3, function.value(3), 1E-8);
		assertTrue(getKernel().undoPossible());
		GGraphics2D graphics = Mockito.mock(GGraphics2D.class);
		pen.setStyleAndRepaint(graphics);
		Mockito.verifyNoInteractions(graphics);
	}

	@Test
	void styleShouldPreservePenColorAndLineTypeWithoutChangingHighlighterSettings() {
		getApp().getSettings().getPenTools().setLastSelectedPenColor(GColor.BLUE);
		GColor highlighter = getApp().getSettings().getPenTools().getLastSelectedHighlighterColor();
		setMode(MODE_FREEHAND_FUNCTION);
		ec.getPen().getDefaultPenLine().setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);

		drawSamples(new int[][] {{100, 100}, {200, 200}});

		GeoFunction function = onlyFunction();
		assertEquals(GColor.BLUE, function.getObjectColor());
		assertEquals(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT, function.getLineType());
		assertEquals(
				highlighter, getApp().getSettings().getPenTools().getLastSelectedHighlighterColor());
	}

	@Test
	void consecutiveGesturesShouldKeepSeparateFunctionsAndDomains() {
		setMode(MODE_FREEHAND_FUNCTION);
		drawSamples(new int[][] {{100, 100}, {200, 200}});
		GeoFunction first = onlyFunction();
		Set<String> before = getObjectNames();

		drawSamples(new int[][] {{300, 200}, {400, 100}});

		GeoFunction second = getOnlyNewObject(before, GeoFunction.class);
		assertDomain(first, 2, 4);
		assertDomain(second, 6, 8);
		assertEquals(-3, first.value(3), 1E-8);
		assertEquals(-3, second.value(7), 1E-8);
		assertEquals(Set.of(first, second), getConstructionSnapshot());
	}

	@Test
	void modeChangeShouldDiscardUnfinishedGestureAndItsBounds() {
		setMode(MODE_FREEHAND_FUNCTION);
		dragStart(400, 100);
		drag(500, 200);
		setMode(MODE_MOVE);
		setMode(MODE_FREEHAND_FUNCTION);

		drawSamples(new int[][] {{100, 100}, {200, 200}});

		assertDomain(onlyFunction(), 2, 4);
	}

	@Test
	void functionModeShouldInitializeAfterTemporaryFreehandEngineWasCleared() {
		setMode(MODE_POLYGON);
		ec.prepareModeForFreehand();
		ec.resetModeAfterFreehand();

		setMode(MODE_FREEHAND_FUNCTION);
		drawSamples(new int[][] {{100, 100}, {200, 200}});

		assertDomain(onlyFunction(), 2, 4);
		assertTrue(ec.getPen().isFreehand());
	}

	@Test
	@Issue("APPS-7920")
	void switchingBetweenShapeAndFunctionShouldResetRecognitionRestriction() {
		setMode(MODE_FREEHAND_SHAPE);
		drag(100, 100, 200, 200);
		Set<String> before = getObjectNames();

		setMode(MODE_FREEHAND_FUNCTION);
		drawSamples(new int[][] {{250, 100}, {350, 200}});

		GeoFunction function = getOnlyNewObject(before, GeoFunction.class);
		assertDomain(function, 5, 7);
		setMode(MODE_FREEHAND_SHAPE);
		Set<String> functions = getObjectNames();
		drag(400, 200, 500, 100);
		assertTrue(getOnlyNewObject(functions, GeoSegment.class).isDefined());
		assertEquals(-3, function.value(6), 1E-8);
	}

	@Test
	void switchingBackToPenShouldDrawInkInsteadOfFunction() {
		setMode(MODE_FREEHAND_FUNCTION);
		drawSamples(new int[][] {{100, 100}, {200, 200}});
		GeoFunction function = onlyFunction();
		Set<String> before = getObjectNames();

		setMode(MODE_PEN);
		drag(250, 150, 350, 150);

		assertFalse(getOnlyNewObject(before, GeoLocusStroke.class).getPoints().isEmpty());
		assertFalse(ec.getPen().isFreehand());
		assertEquals(-3, function.value(3), 1E-8);
	}

	@Test
	void switchingFromPenShouldCreateFunctionAndPreserveExistingInk() {
		setMode(MODE_PEN);
		ec.getPen().setPenColor(GColor.BLUE);
		drag(100, 100, 200, 200);
		GeoLocusStroke ink = getOnlyNewObject(Set.of(), GeoLocusStroke.class);
		List<String> geometry = strokeGeometry(ink);
		Set<String> before = getObjectNames();

		setMode(MODE_FREEHAND_FUNCTION);
		drawSamples(new int[][] {{250, 200}, {350, 100}});

		GeoFunction function = getOnlyNewObject(before, GeoFunction.class);
		assertDomain(function, 5, 7);
		assertEquals(-3, function.value(6), 1E-8);
		assertEquals(GColor.BLUE, function.getObjectColor());
		assertEquals(geometry, strokeGeometry(ink));
		assertEquals(Set.of(ink, function), getConstructionSnapshot());
	}

	@Test
	void undoRedoShouldRestoreFunctionSamplesAndSeparateGestures() {
		enableUndo();
		setMode(MODE_FREEHAND_FUNCTION);
		drawSamples(new int[][] {{100, 100}, {200, 200}});
		String label = onlyFunction().getLabelSimple();
		drawSamples(new int[][] {{300, 200}, {400, 100}});
		Set<String> both = getObjectNames();
		assertEquals(2, both.size());

		getKernel().undo();
		assertEquals(Set.of(label), getObjectNames());
		assertEquals(-3, onlyFunction().value(3), 1E-8);
		getKernel().undo();
		assertTrue(getConstructionSnapshot().isEmpty());
		getKernel().redo();
		assertDomain(onlyFunction(), 2, 4);
		getKernel().redo();
		assertEquals(both, getObjectNames());
		assertEquals(-3, getOnlyNewObject(Set.of(label), GeoFunction.class).value(7), 1E-8);
	}

	@Test
	void xmlRoundTripShouldPreserveSamplesDomainAndStyle() {
		setMode(MODE_FREEHAND_FUNCTION);
		ec.getPen().setPenColor(GColor.BLUE);
		drawSamples(
				new int[][] {{100, 100}, {120, 180}, {140, 120}, {160, 180}, {180, 100}, {200, 150}});
		String label = onlyFunction().getLabelSimple();

		getApp().setXML(getApp().getXML(), true);

		GeoFunction function = onlyFunction();
		assertEquals(label, function.getLabelSimple());
		assertEquals(GColor.BLUE, function.getObjectColor());
		assertDomain(function, 2, 4);
		assertEquals(-2, function.value(2), 1E-8);
		assertEquals(-3.6, function.value(2.4), 1E-8);
		assertEquals(-3, function.value(4), 1E-8);
		Set<String> loaded = getObjectNames();
		drawSamples(new int[][] {{300, 200}, {400, 100}});
		assertDomain(getOnlyNewObject(loaded, GeoFunction.class), 6, 8);
	}

	private void enableUndo() {
		getApp().setUndoActive(true);
		getApp().storeUndoInfo();
	}

	private void reverseSamples(int[] @NonNull [] samples) {
		for (int i = 0; i < samples.length / 2; i++) {
			int[] temp = samples[i];
			samples[i] = samples[samples.length - i - 1];
			samples[samples.length - i - 1] = temp;
		}
	}

	private @NonNull GeoFunction onlyFunction() {
		return getOnlyNewObject(Set.of(), GeoFunction.class);
	}

	private void assertDomain(@NonNull GeoFunction function, double min, double max) {
		assertEquals(min, function.getIntervalMin(), 1E-8);
		assertEquals(max, function.getIntervalMax(), 1E-8);
		assertTrue(Double.isFinite(function.value(min)));
		assertTrue(Double.isFinite(function.value(max)));
		assertTrue(Double.isNaN(function.value(min - 0.01)));
		assertTrue(Double.isNaN(function.value(max + 0.01)));
	}
}
