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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_HIGHLIGHTER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianPen;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.settings.PenToolsSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
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

/** Tests highlighter input, translucent style, settings isolation and stroke persistence. */
class HighlighterToolTest extends BaseToolTest {

	@Test
	void enteringModeAndHoveringShouldNotCreateStroke() {
		Set<GeoElement> before = getConstructionSnapshot();

		setMode(MODE_HIGHLIGHTER);
		moveMouseRW(2, -2);

		assertEquals(MODE_HIGHLIGHTER, getApp().getMode());
		assertFalse(ec.getPen().isFreehand());
		assertEquals(before, getConstructionSnapshot());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	@Issue("APPS-7920")
	void freshActivationShouldUseHighlighterDefaultsInClassicAndNotes(boolean notes) {
		if (notes) {
			getApp().setNotesConfig();
			getApp().getSettings().resetSettings(getApp());
		}
		setMode(MODE_HIGHLIGHTER);

		drag(100, 100, 200, 150);

		GeoLocusStroke stroke = onlyStroke();
		assertStyle(stroke, GColor.newColorRGB(0x388C83), 40, 77);
		assertTrue(stroke.isDefined());
		assertTrue(stroke.isEuclidianVisible());
		assertEquals(GeoElement.TOOLTIP_OFF, stroke.getTooltipMode());
		assertEquals(Set.of(stroke), getConstructionSnapshot());
	}

	@Test
	@Issue("APPS-7920")
	void freshActivationShouldReadSavedHighlighterSettingsAndIgnorePenOpacity() {
		PenToolsSettings settings = getApp().getSettings().getPenTools();
		settings.setLastSelectedHighlighterColor(GColor.BLUE);
		settings.setLastHighlighterThickness(64);
		settings.setLastPenOpacity(111);
		setMode(MODE_HIGHLIGHTER);

		drag(100, 100, 200, 150);

		assertStyle(onlyStroke(), GColor.BLUE, 64, 77);
		assertEquals(111, settings.getLastPenOpacity());
	}

	@ParameterizedTest
	@EnumSource(PointerEventType.class)
	void tapShouldCreateTranslucentDotForEachPointer(@NonNull PointerEventType pointer) {
		setMode(MODE_HIGHLIGHTER);

		click(100, 150, pointer);

		GeoLocusStroke stroke = onlyStroke();
		assertFalse(stroke.getPoints().isEmpty());
		for (MyPoint point : stroke.getPoints()) {
			assertEquals(2, point.x, 1E-8);
			assertEquals(-3, point.y, 1E-8);
		}
		assertEquals(77, stroke.getLineOpacity());
	}

	@Test
	void drawingOverPointShouldCreateInkWithoutMovingOrSelectingPoint() {
		GeoPoint point = add("A=(2,-2)");
		setMode(MODE_HIGHLIGHTER);
		Set<String> before = getObjectNames();

		drag(100, 100, 200, 200);

		GeoLocusStroke stroke = getOnlyNewObject(before, GeoLocusStroke.class);
		assertEquals(77, stroke.getLineOpacity());
		assertEquals(2, point.getInhomX(), 1E-8);
		assertEquals(-2, point.getInhomY(), 1E-8);
		assertTrue(getApp().getSelectionManager().getSelectedGeos().isEmpty());
		assertEquals(Set.of(point, stroke), getConstructionSnapshot());
	}

	@Test
	void consecutiveGesturesShouldAppendDisconnectedPartsWithSameAlpha() {
		setMode(MODE_HIGHLIGHTER);
		drag(100, 100, 180, 100);
		GeoLocusStroke first = onlyStroke();
		List<String> original = strokeGeometry(first);

		drag(250, 150, 350, 150);

		assertSame(first, onlyStroke());
		assertEquals(original, strokeGeometry(first).subList(0, original.size()));
		assertEquals(
				2,
				first.getPoints().stream()
						.filter(point -> point.getSegmentType() == SegmentType.MOVE_TO)
						.count());
		assertEquals(77, first.getLineOpacity());
	}

	@Test
	void timeoutShouldStartNewTranslucentStrokeWithoutChangingPreviousStroke() {
		setMode(MODE_HIGHLIGHTER);
		drag(100, 100, 180, 100);
		GeoLocusStroke first = onlyStroke();
		List<String> original = strokeGeometry(first);
		Set<String> before = getObjectNames();

		ec.getPen().onRun();
		drag(250, 150, 350, 150);

		GeoLocusStroke next = getOnlyNewObject(before, GeoLocusStroke.class);
		assertNotSame(first, next);
		assertEquals(original, strokeGeometry(first));
		assertStyle(next, first.getObjectColor(), 40, 77);
	}

	@Test
	void colorChangeShouldSplitStrokeAndOnlyPersistHighlighterColor() {
		setMode(MODE_HIGHLIGHTER);
		drag(100, 100, 180, 100);
		GeoLocusStroke first = onlyStroke();
		GColor originalColor = first.getObjectColor();
		PenToolsSettings settings = getApp().getSettings().getPenTools();
		GColor penColor = settings.getLastSelectedPenColor();
		Set<String> before = getObjectNames();

		ec.getPen().setPenColor(GColor.RED);
		drag(250, 150, 350, 150);

		assertStyle(getOnlyNewObject(before, GeoLocusStroke.class), GColor.RED, 40, 77);
		assertEquals(originalColor, first.getObjectColor());
		assertEquals(GColor.RED, settings.getLastSelectedHighlighterColor());
		assertEquals(penColor, settings.getLastSelectedPenColor());
	}

	@Test
	void thicknessChangeShouldSplitStrokeAndOnlyPersistHighlighterThickness() {
		setMode(MODE_HIGHLIGHTER);
		drag(100, 100, 180, 100);
		GeoLocusStroke first = onlyStroke();
		Set<String> before = getObjectNames();

		ec.getPen().setPenSize(64);
		drag(250, 150, 350, 150);

		assertStyle(getOnlyNewObject(before, GeoLocusStroke.class), first.getObjectColor(), 64, 77);
		assertEquals(40, first.getLineThickness());
		assertEquals(64, getApp().getSettings().getPenTools().getLastHighlighterThickness());
		assertEquals(6, getApp().getSettings().getPenTools().getLastPenThickness());
	}

	@Test
	void opacityChangeShouldSplitStrokeWithoutChangingSavedPenOpacity() {
		getApp().getSettings().getPenTools().setLastPenOpacity(111);
		setMode(MODE_HIGHLIGHTER);
		drag(100, 100, 180, 100);
		GeoLocusStroke first = onlyStroke();
		Set<String> before = getObjectNames();

		ec.getPen().getDefaultPenLine().setLineOpacity(90);
		drag(250, 150, 350, 150);

		assertStyle(getOnlyNewObject(before, GeoLocusStroke.class), first.getObjectColor(), 40, 90);
		assertEquals(77, first.getLineOpacity());
		assertEquals(111, getApp().getSettings().getPenTools().getLastPenOpacity());
	}

	@Test
	void lineTypeChangeShouldSplitStrokeAndPreserveOldLineType() {
		setMode(MODE_HIGHLIGHTER);
		drag(100, 100, 180, 100);
		GeoLocusStroke first = onlyStroke();
		Set<String> before = getObjectNames();

		ec.getPen().getDefaultPenLine().setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		drag(250, 150, 350, 150);

		GeoLocusStroke next = getOnlyNewObject(before, GeoLocusStroke.class);
		assertEquals(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT, next.getLineType());
		assertEquals(EuclidianStyleConstants.LINE_TYPE_FULL, first.getLineType());
		assertEquals(77, next.getLineOpacity());
	}

	@Test
	void reapplyingCurrentStyleShouldKeepAppendingToSameStroke() {
		setMode(MODE_HIGHLIGHTER);
		drag(100, 100, 180, 100);
		GeoLocusStroke first = onlyStroke();
		int points = first.getPoints().size();
		EuclidianPen pen = ec.getPen();

		pen.setPenColor(pen.getPenColor());
		pen.setPenSize(pen.getPenSize());
		pen.getDefaultPenLine().setLineType(pen.getPenLineStyle());
		pen.getDefaultPenLine().setLineOpacity(pen.getDefaultPenLine().getLineOpacity());
		drag(250, 150, 350, 150);

		assertSame(first, onlyStroke());
		assertTrue(first.getPoints().size() > points);
	}

	@Test
	void toolbarStyleRefreshShouldReadSettingsAndRestoreHighlighterAlpha() {
		setMode(MODE_HIGHLIGHTER);
		ec.getPen().getDefaultPenLine().setLineOpacity(90);
		drag(100, 100, 180, 100);
		GeoLocusStroke first = onlyStroke();
		Set<String> before = getObjectNames();
		PenToolsSettings settings = getApp().getSettings().getPenTools();
		settings.setLastSelectedHighlighterColor(GColor.BLUE);
		settings.setLastHighlighterThickness(64);

		ec.getPen().updateMode();
		drag(250, 150, 350, 150);

		assertStyle(getOnlyNewObject(before, GeoLocusStroke.class), GColor.BLUE, 64, 77);
		assertEquals(90, first.getLineOpacity());
	}

	@Test
	@Issue("APPS-7920")
	void switchingModesShouldKeepPenAndHighlighterSettingsIndependent() {
		setMode(MODE_PEN);
		ec.getPen().setPenColor(GColor.RED);
		ec.getPen().setPenSize(12);
		ec.getPen().getDefaultPenLine().setLineOpacity(111);
		drag(100, 100, 180, 100);
		setMode(MODE_HIGHLIGHTER);
		ec.getPen().setPenColor(GColor.BLUE);
		ec.getPen().setPenSize(30);
		drag(200, 150, 300, 150);

		setMode(MODE_PEN);
		Set<String> before = getObjectNames();
		drag(350, 200, 450, 200);

		GeoLocusStroke penStroke = getOnlyNewObject(before, GeoLocusStroke.class);
		assertEquals(GColor.RED, penStroke.getObjectColor());
		assertEquals(12, penStroke.getLineThickness());
		assertEquals(111, penStroke.getLineOpacity());
		setMode(MODE_HIGHLIGHTER);
		assertEquals(GColor.BLUE, ec.getPen().getPenColor());
		assertEquals(30, ec.getPen().getPenSize());
		assertEquals(77, ec.getPen().getDefaultPenLine().getLineOpacity());
		Set<String> highlightedBefore = getObjectNames();
		drag(500, 200, 600, 200);
		assertStyle(getOnlyNewObject(highlightedBefore, GeoLocusStroke.class), GColor.BLUE, 30, 77);
	}

	@Test
	void settingsListenerRefreshShouldNotOverwritePenSettings() {
		setMode(MODE_HIGHLIGHTER);
		PenToolsSettings settings = getApp().getSettings().getPenTools();
		settings.setLastPenOpacity(111);
		GColor penColor = settings.getLastSelectedPenColor();
		settings.addListener(changed -> ec.getPen().updateMode());

		ec.getPen().setPenSize(64);
		ec.getPen().setPenColor(GColor.BLUE);
		drag(100, 100, 180, 100);

		assertStyle(onlyStroke(), GColor.BLUE, 64, 77);
		assertEquals(penColor, settings.getLastSelectedPenColor());
		assertEquals(6, settings.getLastPenThickness());
		assertEquals(111, settings.getLastPenOpacity());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void previewShouldApplyAlphaAndScaleThicknessWithoutChangingSavedSize(boolean scaled) {
		setMode(MODE_HIGHLIGHTER);
		getApp().getActiveEuclidianView().setCoordSystem(250, 200, 100, 60);
		getApp().getActiveEuclidianView().getSettings().setLineThicknessScaled(scaled);
		dragStart(100, 100);
		drag(140, 100);
		drag(180, 100);
		GGraphics2D graphics = Mockito.mock(GGraphics2D.class);

		ec.getPen().setStyleAndRepaint(graphics);

		Mockito.verify(graphics).setColor(GColor.newColorRGB(0x388C83).deriveWithAlpha(77));
		ArgumentCaptor<GBasicStroke> style = ArgumentCaptor.forClass(GBasicStroke.class);
		Mockito.verify(graphics).setStroke(style.capture());
		assertEquals(scaled ? 40 : 20, style.getValue().getLineWidth(), 1E-8);
		ArgumentCaptor<GShape> preview = ArgumentCaptor.forClass(GShape.class);
		Mockito.verify(graphics).draw(preview.capture());
		assertTrue(preview.getValue().getBounds2D().getWidth() > 0);
		pointerRelease(180, 100);
		assertEquals(40, onlyStroke().getLineThickness());
		assertEquals(40, getApp().getSettings().getPenTools().getLastHighlighterThickness());
		Mockito.reset(graphics);
		ec.getPen().setStyleAndRepaint(graphics);
		Mockito.verifyNoInteractions(graphics);
	}

	@Test
	void strokeCoordinatesShouldUseBothViewScalesAndOffsets() {
		getApp().getActiveEuclidianView().setCoordSystem(250, 200, 40, 60);
		setMode(MODE_HIGHLIGHTER);

		drag(100, 100, 180, 160);

		List<MyPoint> points = onlyStroke().getPoints();
		assertEquals(-3.75, points.get(0).x, 1E-8);
		assertEquals(5.0 / 3, points.get(0).y, 1E-8);
		MyPoint last = points.get(points.size() - 1);
		assertEquals(-1.75, last.x, 1E-8);
		assertEquals(2.0 / 3, last.y, 1E-8);
	}

	@Test
	void modeChangeShouldDiscardPendingInkAndStartNewStrokeOnReturn() {
		setMode(MODE_HIGHLIGHTER);
		drag(100, 100, 180, 100);
		GeoLocusStroke first = onlyStroke();
		List<String> original = strokeGeometry(first);
		Set<String> before = getObjectNames();
		dragStart(250, 150);
		drag(300, 150);

		setMode(MODE_MOVE);
		setMode(MODE_HIGHLIGHTER);
		drag(350, 200, 450, 200);

		GeoLocusStroke next = getOnlyNewObject(before, GeoLocusStroke.class);
		assertEquals(original, strokeGeometry(first));
		assertEquals(7, next.getPoints().get(0).x, 1E-8);
		assertEquals(-4, next.getPoints().get(0).y, 1E-8);
		assertEquals(77, next.getLineOpacity());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	@Issue("APPS-7920")
	void returningFromFreehandShouldDrawStrokeInsteadOfRecognizingShape(boolean custom) {
		setMode(EuclidianConstants.MODE_FREEHAND_SHAPE);
		assertTrue(ec.getPen().isFreehand());
		GColor color = custom ? GColor.BLUE : GColor.newColorRGB(0x388C83);
		int thickness = custom ? 64 : 40;
		getApp().getSettings().getPenTools().setLastSelectedHighlighterColor(color);
		getApp().getSettings().getPenTools().setLastHighlighterThickness(thickness);

		setMode(MODE_HIGHLIGHTER);
		dragStart(100, 100);
		drag(150, 100);
		drag(150, 150);
		drag(100, 150);
		dragEnd(100, 100);

		assertFalse(ec.getPen().isFreehand());
		assertEquals(Set.of(onlyStroke()), getConstructionSnapshot());
		assertStyle(onlyStroke(), color, thickness, 77);
	}

	@Test
	void rightDragShouldEraseOnlyInkAndAllowFurtherHighlighting() {
		GeoPoint point = add("A=(2.2,-2)");
		getApp().getSettings().getPenTools().setDeleteToolSize(64);
		setMode(MODE_HIGHLIGHTER);
		drag(100, 100, 120, 100);

		dragRWRight(2.2, -2, 2.22, -2);

		assertEquals(Set.of(point), getConstructionSnapshot());
		drag(250, 150, 350, 150);
		assertEquals(77, onlyStroke().getLineOpacity());
		assertEquals(5, onlyStroke().getPoints().get(0).x, 1E-8);
	}

	@Test
	void cancelledPinchShouldNotCreateInkOrUndoAndShouldClearPreview() {
		enableNotesUndo();
		setMode(MODE_HIGHLIGHTER);
		EuclidianPen pen = ec.getPen();
		pen.handleMousePressedForPenMode(new TestEvent(100, 100));

		pen.handleMouseReleasedForPenMode(false, 100, 100, true, PointerEventType.TOUCH);

		assertTrue(getObjectNames().isEmpty());
		assertFalse(getKernel().undoPossible());
		GGraphics2D graphics = Mockito.mock(GGraphics2D.class);
		pen.setStyleAndRepaint(graphics);
		Mockito.verifyNoInteractions(graphics);
		drag(250, 150, 350, 150);
		assertEquals(77, onlyStroke().getLineOpacity());
		assertEquals(5, onlyStroke().getPoints().get(0).x, 1E-8);
	}

	@Test
	void undoRedoShouldPreserveTranslucencyAndSeparateAppendFromCreation() {
		enableNotesUndo();
		setMode(MODE_HIGHLIGHTER);
		ec.getPen().setPenColor(GColor.BLUE);
		ec.getPen().setPenSize(64);
		drag(100, 100, 180, 100);
		List<String> first = strokeGeometry(onlyStroke());
		drag(250, 150, 350, 150);
		List<String> combined = strokeGeometry(onlyStroke());
		String label = onlyStroke().getLabelSimple();

		getKernel().undo();
		assertEquals(first, strokeGeometry(onlyStroke()));
		assertStyle(onlyStroke(), GColor.BLUE, 64, 77);
		getKernel().undo();
		assertTrue(getObjectNames().isEmpty());
		getKernel().redo();
		assertEquals(first, strokeGeometry(onlyStroke()));
		getKernel().redo();
		assertEquals(combined, strokeGeometry(onlyStroke()));
		assertStyle(onlyStroke(), GColor.BLUE, 64, 77);
		assertEquals(label, onlyStroke().getLabelSimple());
	}

	@Test
	void xmlRoundTripShouldPreserveInkWithoutOverwritingCurrentToolSettings() {
		getApp().setNotesConfig();
		setMode(MODE_HIGHLIGHTER);
		ec.getPen().setPenColor(GColor.BLUE);
		ec.getPen().setPenSize(64);
		drag(100, 100, 180, 100);
		drag(250, 150, 350, 150);
		List<String> before = strokeGeometry(onlyStroke());
		String label = onlyStroke().getLabelSimple();
		String xml = getApp().getXML();
		PenToolsSettings settings = getApp().getSettings().getPenTools();
		settings.setLastSelectedHighlighterColor(GColor.RED);
		settings.setLastHighlighterThickness(12);

		getApp().setXML(xml, true);

		assertEquals(before, strokeGeometry(onlyStroke()));
		assertEquals(label, onlyStroke().getLabelSimple());
		assertStyle(onlyStroke(), GColor.BLUE, 64, 77);
		// Ordinary documents store the ink style; tool settings are only saved in templates.
		assertEquals(
				GColor.RED, getApp().getSettings().getPenTools().getLastSelectedHighlighterColor());
		assertEquals(12, getApp().getSettings().getPenTools().getLastHighlighterThickness());
		setMode(MODE_MOVE);
		setMode(MODE_HIGHLIGHTER);
		Set<String> loaded = getObjectNames();
		drag(400, 200, 500, 200);
		assertStyle(getOnlyNewObject(loaded, GeoLocusStroke.class), GColor.RED, 12, 77);
	}

	@Test
	void renderedStrokeShouldUseTranslucentPaintAndHighlighterWidth() {
		setMode(MODE_HIGHLIGHTER);
		drag(100, 100, 200, 150);
		GeoLocusStroke stroke = onlyStroke();
		GGraphics2D graphics = Mockito.mock(GGraphics2D.class);
		// Exercise the vector path directly instead of a cached bitmap.
		getApp().setExporting(ExportType.SVG, 1);
		try {
			getDrawable(stroke).draw(graphics);
		} finally {
			getApp().setExporting(ExportType.NONE, 1);
		}

		Mockito.verify(graphics).setPaint(stroke.getObjectColor().deriveWithAlpha(77));
		ArgumentCaptor<GBasicStroke> style = ArgumentCaptor.forClass(GBasicStroke.class);
		Mockito.verify(graphics).setStroke(style.capture());
		assertEquals(20, style.getValue().getLineWidth(), 1E-8);
		Mockito.verify(graphics, Mockito.atLeastOnce()).draw(Mockito.any());
	}

	@Test
	void denseSamplingShouldKeepEndpointsAndTransparency() {
		setMode(MODE_HIGHLIGHTER);
		dragStart(100, 100);
		for (int x = 105; x <= 200; x += 5) {
			drag(x, 100);
		}
		pointerRelease(200, 100);

		GeoLocusStroke stroke = onlyStroke();
		List<MyPoint> points =
				stroke.getPoints().stream().filter(MyPoint::isDefined).toList();
		assertEquals(2, points.get(0).x, 1E-8);
		assertEquals(-2, points.get(0).y, 1E-8);
		MyPoint last = points.get(points.size() - 1);
		assertEquals(4, last.x, 1E-8);
		assertEquals(-2, last.y, 1E-8);
		assertEquals(77, stroke.getLineOpacity());
	}

	@Test
	void reselectingActiveToolShouldNotSplitStrokeOrResetCustomStyle() {
		setMode(MODE_HIGHLIGHTER);
		ec.getPen().setPenSize(64);
		ec.getPen().getDefaultPenLine().setLineOpacity(90);
		drag(100, 100, 180, 100);
		GeoLocusStroke first = onlyStroke();
		int length = first.getPoints().size();

		setMode(MODE_HIGHLIGHTER);
		drag(250, 150, 350, 150);

		assertSame(first, onlyStroke());
		assertTrue(first.getPoints().size() > length);
		assertEquals(64, first.getLineThickness());
		assertEquals(90, first.getLineOpacity());
	}

	@Test
	@Issue("APPS-7920")
	void rebuildingEngineShouldPreserveBothToolsSavedSettings() {
		PenToolsSettings settings = getApp().getSettings().getPenTools();
		settings.setLastSelectedPenColor(GColor.RED);
		settings.setLastPenThickness(12);
		settings.setLastPenOpacity(111);
		settings.setLastSelectedHighlighterColor(GColor.BLUE);
		settings.setLastHighlighterThickness(64);
		setMode(EuclidianConstants.MODE_FREEHAND_SHAPE);

		setMode(MODE_PEN);
		drag(100, 100, 180, 100);

		assertStyle(onlyStroke(), GColor.RED, 12, 111);
		Set<String> before = getObjectNames();
		setMode(MODE_HIGHLIGHTER);
		drag(250, 150, 350, 150);
		assertStyle(getOnlyNewObject(before, GeoLocusStroke.class), GColor.BLUE, 64, 77);
	}

	@ParameterizedTest
	@CsvSource({"false, false", "false, true", "true, false", "true, true"})
	@Issue("APPS-7920")
	void enteringInkModeShouldNotNotifySettingsListenersOfUserEdits(
			boolean highlighter, boolean rebuild) {
		setMode(rebuild ? EuclidianConstants.MODE_FREEHAND_SHAPE : MODE_MOVE);
		PenToolsSettings settings = getApp().getSettings().getPenTools();
		settings.setLastSelectedPenColor(GColor.RED);
		settings.setLastPenThickness(12);
		settings.setLastPenOpacity(111);
		settings.setLastSelectedHighlighterColor(GColor.BLUE);
		settings.setLastHighlighterThickness(64);
		EuclidianPen previous = ec.getPen();
		List<Integer> changes = new ArrayList<>();
		settings.addListener(changed -> changes.add(
				highlighter ? changed.getLastHighlighterThickness() : changed.getLastPenThickness()));

		setMode(highlighter ? MODE_HIGHLIGHTER : MODE_PEN);

		if (rebuild) {
			assertNotSame(previous, ec.getPen());
		} else {
			assertSame(previous, ec.getPen());
		}
		assertTrue(changes.isEmpty());
		assertEquals(GColor.RED, settings.getLastSelectedPenColor());
		assertEquals(12, settings.getLastPenThickness());
		assertEquals(111, settings.getLastPenOpacity());
		assertEquals(GColor.BLUE, settings.getLastSelectedHighlighterColor());
		assertEquals(64, settings.getLastHighlighterThickness());
		drag(100, 100, 180, 100);
		assertStyle(
				onlyStroke(),
				highlighter ? GColor.BLUE : GColor.RED,
				highlighter ? 64 : 12,
				highlighter ? 77 : 111);
		ec.getPen().setPenSize(72);
		assertEquals(List.of(72), changes);
	}

	private void enableNotesUndo() {
		getApp().setNotesConfig();
		getApp().setUndoActive(true);
		getApp().storeUndoInfo();
	}

	private @NonNull GeoLocusStroke onlyStroke() {
		return getOnlyNewObject(Set.of(), GeoLocusStroke.class);
	}

	private void assertStyle(
			@NonNull GeoElement stroke, @NonNull GColor color, int thickness, int opacity) {
		assertEquals(color, stroke.getObjectColor());
		assertEquals(thickness, stroke.getLineThickness());
		assertEquals(opacity, stroke.getLineOpacity());
	}
}
