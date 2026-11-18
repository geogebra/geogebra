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

package org.geogebra.common.euclidian.tools.notes;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_STICKY_NOTE;
import static org.geogebra.common.kernel.Kernel.MAX_PRECISION;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.euclidian.draw.DrawStickyNote;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.euclidian.tools.BaseToolTest;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoStickyNote;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.geogebra.common.properties.impl.objects.NotesFontSizeProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InOrder;

class StickyNoteToolTest extends BaseToolTest {

	private EuclidianView view;

	@BeforeEach
	void setMode() {
		getApp().setConfig(new AppConfigNotes());
		setMode(MODE_STICKY_NOTE);
		view = getApp().getActiveEuclidianView();
		view.setCoordSystem(250, 250, 50, 50);
	}

	@Test
	void creationTest() {
		clickRW(1, 1);
		GeoInline stickyNote = assertInstanceOf(GeoInline.class, lookup("a"));
		DrawStickyNote drawable =
				assertInstanceOf(DrawStickyNote.class, view.getDrawableFor(stickyNote));
		GRectangle bounds = drawable.getBounds();
		assertNotNull(bounds);
		assertAll(
				() -> checkContent("a"),
				() -> assertEquals(160, bounds.getWidth()),
				() -> assertEquals(160, bounds.getHeight()),
				() -> assertEquals(GColor.STICKY_NOTE_YELLOW, stickyNote.getBackgroundColor()));
	}

	@Test
	@Issue("MOW-1950")
	void clickUsesDefaultSizeAndSmallDragUsesMinimumSize() {
		click(30, 40);
		setMode(MODE_STICKY_NOTE);
		dragStart(70, 80);
		// Exceed the 10px click threshold while requesting less than the minimum size.
		dragEnd(81, 91);

		checkContent("a", "b");
		GeoStickyNote clicked = assertInstanceOf(GeoStickyNote.class, lookup("a"));
		GeoStickyNote dragged = assertInstanceOf(GeoStickyNote.class, lookup("b"));
		assertAll(
				() -> assertEquals(GeoStickyNote.DEFAULT_WIDTH, clicked.getWidth(), MAX_PRECISION),
				() -> assertEquals(GeoStickyNote.DEFAULT_HEIGHT, clicked.getHeight(), MAX_PRECISION),
				() -> assertEquals(GeoInlineText.MIN_WIDTH, dragged.getWidth(), MAX_PRECISION),
				() -> assertEquals(GeoStickyNote.MIN_HEIGHT, dragged.getHeight(), MAX_PRECISION));
	}

	@ParameterizedTest
	@CsvSource({
		"0, 0, 160",
		"1, 1, 160",
		"10, 10, 160",
		"-10, -10, 160",
		"11, 0, 36",
		"80, 40, 80",
		"-80, -40, 80"
	})
	@Issue("MOW-1944")
	void creationShouldRespectDragThreshold(int dx, int dy, int expectedSide) {
		dragStart(300, 200);
		dragEnd(300 + dx, 200 + dy);

		GeoStickyNote note = assertInstanceOf(GeoStickyNote.class, lookup("a"));
		DrawStickyNote drawable = (DrawStickyNote) view.getDrawableFor(note);
		drawable.update();
		assertSquare(note, expectedSide);
	}

	@ParameterizedTest
	@CsvSource({"40, 80, 200, 240, 1.25, 1.5", "-40, -80, 120, 80, 0.75, 0.5"})
	@Issue("MOW-1944")
	void contentShouldScaleFromResizeStartThroughoutDrag(
			int firstDelta,
			int secondDelta,
			int firstSide,
			int secondSide,
			double firstScale,
			double secondScale) {
		GeoStickyNote note = createStickyNote();
		DrawStickyNote drawable = (DrawStickyNote) view.getDrawableFor(note);
		GPoint start = pressHandle(drawable, EuclidianBoundingBoxHandler.BOTTOM_RIGHT);

		drag(start.x + firstDelta, start.y + firstDelta);
		drawable.update();
		assertAll(
				() -> assertSquare(note, firstSide),
				() -> assertEquals(firstScale, drawable.getScale(), MAX_PRECISION));

		drag(start.x + secondDelta, start.y + secondDelta);
		drawable.update();
		assertAll(
				() -> assertSquare(note, secondSide),
				() -> assertEquals(secondScale, drawable.getScale(), MAX_PRECISION));

		// Returning to the original handle position must undo the preview scaling.
		drag(start.x, start.y);
		drawable.update();
		assertAll(
				() -> assertSquare(note, 160), () -> assertEquals(1, drawable.getScale(), MAX_PRECISION));
		pointerRelease(start.x, start.y);
	}

	@Test
	@Issue("MOW-1944")
	void fixedFontSizeFieldShouldFollowResizeScale() throws NotApplicablePropertyException {
		GeoStickyNote note = createStickyNote();
		note.setAutoFontSize(false);
		DrawStickyNote drawable = (DrawStickyNote) view.getDrawableFor(note);
		InlineTextController controller = mock(InlineTextController.class);
		when(controller.getFormat("size", (Number) 16)).thenReturn(20.0);
		drawable.setTextController(controller);
		NotesFontSizeProperty property = new NotesFontSizeProperty(getLocalization(), note);
		GPoint start = pressHandle(drawable, EuclidianBoundingBoxHandler.BOTTOM_RIGHT);

		drag(start.x + 40, start.y + 40);
		assertEquals("25", property.getValue());
		drag(start.x + 80, start.y + 80);
		assertEquals("30", property.getValue());
		drag(start.x, start.y);
		assertEquals("20", property.getValue());
		pointerRelease(start.x, start.y);
		verify(controller, never()).format(eq("size"), any());
	}

	@ParameterizedTest
	@CsvSource({"50", "100"})
	@Issue("MOW-1944")
	void contentScaleShouldSurviveViewZoom(int viewScale) {
		GeoStickyNote note = createStickyNote();
		DrawStickyNote drawable = (DrawStickyNote) view.getDrawableFor(note);
		view.setCoordSystem(250, 250, viewScale, viewScale);
		drawable.update();
		int originalSide = (int) note.getWidth();
		GPoint start = pressHandle(drawable, EuclidianBoundingBoxHandler.BOTTOM_RIGHT);

		dragEnd(start.x + originalSide / 2, start.y + originalSide / 2);
		drawable.update();
		assertAll(
				() -> assertSquare(note, originalSide * 1.5),
				() -> assertEquals(240, note.getContentWidth(), MAX_PRECISION),
				() -> assertEquals(240, note.getContentHeight(), MAX_PRECISION));

		view.setCoordSystem(250, 250, 50, 50);
		drawable.update();
		assertAll(
				() -> assertSquare(note, 240), () -> assertEquals(1, drawable.getScale(), MAX_PRECISION));
	}

	@ParameterizedTest
	@CsvSource({"25, false", "50, false", "100, false", "50, true", "100, true"})
	@Issue("MOW-1944")
	void resizeReleaseShouldNormalizeBeforeEditingResumes(int viewScale, boolean automatic) {
		GeoStickyNote note = createStickyNote();
		note.setAutoFontSize(automatic);
		DrawStickyNote drawable = (DrawStickyNote) view.getDrawableFor(note);
		InlineTextController controller = mock(InlineTextController.class);
		when(controller.hasContent()).thenReturn(true);
		drawable.setTextController(controller);
		view.setCoordSystem(250, 250, viewScale, viewScale);
		drawable.update();
		int originalSide = (int) note.getWidth();
		GPoint start = pressHandle(drawable, EuclidianBoundingBoxHandler.BOTTOM_RIGHT);

		dragEnd(start.x + originalSide / 2, start.y + originalSide / 2);
		drawable.update();

		assertAll(
				() -> assertSquare(note, originalSide * 1.5),
				() -> assertEquals(240, note.getContentWidth(), MAX_PRECISION),
				() -> assertEquals(240, note.getContentHeight(), MAX_PRECISION),
				() -> assertEquals(1, note.getScale(), MAX_PRECISION),
				() -> assertEquals(automatic, note.isAutoFontSize()));
		InOrder order = inOrder(controller);
		order.verify(controller).onResizeEnd(1.5);
		order.verify(controller).toForeground(anyInt(), anyInt());
	}

	@Test
	@Issue("MOW-1944")
	void releasingHandleWithoutDraggingShouldNotCommitFontSizing() {
		GeoStickyNote note = createStickyNote();
		DrawStickyNote drawable = (DrawStickyNote) view.getDrawableFor(note);
		InlineTextController controller = mock(InlineTextController.class);
		drawable.setTextController(controller);
		GPoint start = pressHandle(drawable, EuclidianBoundingBoxHandler.BOTTOM_RIGHT);
		pointerRelease(start.x, start.y);

		assertSquare(note, 160);
		assertEquals(160, note.getContentWidth(), MAX_PRECISION);
		assertEquals(1, note.getScale(), MAX_PRECISION);
		verify(controller, never()).onResizeEnd(org.mockito.ArgumentMatchers.anyDouble());
	}

	@Test
	@Issue("MOW-1944")
	void repeatedResizeShouldUseTheNormalizedLayout() {
		GeoStickyNote note = createStickyNote();
		DrawStickyNote drawable = (DrawStickyNote) view.getDrawableFor(note);
		InlineTextController controller = mock(InlineTextController.class);
		drawable.setTextController(controller);
		GPoint start = pressHandle(drawable, EuclidianBoundingBoxHandler.BOTTOM_RIGHT);
		dragEnd(start.x + 80, start.y + 80);

		start = pressHandle(drawable, EuclidianBoundingBoxHandler.BOTTOM_RIGHT);
		dragEnd(start.x + 120, start.y + 120);
		drawable.update();

		assertSquare(note, 360);
		assertEquals(360, note.getContentWidth(), MAX_PRECISION);
		assertEquals(1, note.getScale(), MAX_PRECISION);
		verify(controller, times(2)).onResizeEnd(1.5);
	}

	@ParameterizedTest
	@EnumSource(
			value = EuclidianBoundingBoxHandler.class,
			names = {"TOP_LEFT", "TOP_RIGHT", "BOTTOM_LEFT", "BOTTOM_RIGHT"})
	@Issue("MOW-1944")
	void cornerResizeShouldGrowSquareAndKeepOppositeCornerFixed(EuclidianBoundingBoxHandler handler) {
		assertCornerResize(handler, 80, 40);
	}

	@ParameterizedTest
	@EnumSource(
			value = EuclidianBoundingBoxHandler.class,
			names = {"TOP_LEFT", "TOP_RIGHT", "BOTTOM_LEFT", "BOTTOM_RIGHT"})
	@Issue("MOW-1944")
	void cornerResizeShouldShrinkSquareAndKeepOppositeCornerFixed(
			EuclidianBoundingBoxHandler handler) {
		assertCornerResize(handler, -40, -20);
	}

	@ParameterizedTest
	@EnumSource(
			value = EuclidianBoundingBoxHandler.class,
			names = {"LEFT", "RIGHT", "TOP", "BOTTOM"})
	@Issue("MOW-1944")
	@Disabled("Side-handle visibility and hit-testing belong to a separate bounding-box ticket")
	void stickyNoteShouldNotExposeSideResizeHandles(EuclidianBoundingBoxHandler handler) {
		GeoStickyNote note = createStickyNote();
		DrawStickyNote drawable = (DrawStickyNote) view.getDrawableFor(note);
		drawable.update();
		GPoint midpoint = getHandlePosition(drawable.getBounds(), handler);

		assertEquals(
				EuclidianBoundingBoxHandler.UNDEFINED,
				drawable.getBoundingBox().getHitHandler(midpoint.x, midpoint.y, 0));
	}

	@ParameterizedTest
	@EnumSource(
			value = EuclidianBoundingBoxHandler.class,
			names = {"LEFT", "RIGHT", "TOP", "BOTTOM"})
	@Issue("MOW-1944")
	@Disabled("Side-handle regression coverage belongs to a separate bounding-box ticket")
	void inlineTextShouldRetainSideResizeHandles(EuclidianBoundingBoxHandler handler) {
		GeoInlineText text = new GeoInlineText(getConstruction(), new GPoint2D(-1, 1));
		// Separate the side hit zones from the corner hit zones.
		text.setSize(160, 160);
		text.setLabel("text");
		DrawInlineText drawable = (DrawInlineText) view.getDrawableFor(text);
		drawable.update();
		GPoint midpoint = getHandlePosition(drawable.getBounds(), handler);

		assertEquals(handler, drawable.getBoundingBox().getHitHandler(midpoint.x, midpoint.y, 0));
	}

	@Test
	@Issue("MOW-1944")
	@Disabled("Rotation-handle regression coverage belongs to a separate bounding-box ticket")
	void rotationHandleShouldRemainAvailable() {
		GeoStickyNote note = createStickyNote();
		DrawStickyNote drawable = (DrawStickyNote) view.getDrawableFor(note);
		drawable.update();
		GPoint bottom = getHandlePosition(drawable.getBounds(), EuclidianBoundingBoxHandler.BOTTOM);

		assertEquals(
				EuclidianBoundingBoxHandler.ROTATION,
				drawable.getBoundingBox().getHitHandler(bottom.x, bottom.y + 32, 0));
	}

	private void assertCornerResize(
			EuclidianBoundingBoxHandler handler, int horizontalChange, int verticalChange) {
		GeoStickyNote note = createStickyNote();
		DrawStickyNote drawable = (DrawStickyNote) view.getDrawableFor(note);
		GRectangle originalBounds = drawable.getBounds();
		GPoint start = pressHandle(drawable, handler);
		dragEnd(
				start.x + handler.getDx() * horizontalChange, start.y + handler.getDy() * verticalChange);
		drawable.update();
		GRectangle resizedBounds = drawable.getBounds();

		assertAll(
				() -> assertEquals(note.getWidth(), note.getHeight(), MAX_PRECISION),
				() -> assertTrue(
						horizontalChange > 0 ? note.getWidth() > 160 : note.getWidth() < 160,
						"Resize should change the note in the drag direction"),
				() -> assertEquals(
						handler.getDx() > 0 ? originalBounds.getMinX() : originalBounds.getMaxX(),
						handler.getDx() > 0 ? resizedBounds.getMinX() : resizedBounds.getMaxX(),
						MAX_PRECISION),
				() -> assertEquals(
						handler.getDy() > 0 ? originalBounds.getMinY() : originalBounds.getMaxY(),
						handler.getDy() > 0 ? resizedBounds.getMinY() : resizedBounds.getMaxY(),
						MAX_PRECISION));
	}

	private GeoStickyNote createStickyNote() {
		clickRW(-1, 1);
		GeoStickyNote note = assertInstanceOf(GeoStickyNote.class, lookup("a"));
		note.setContent("[{\"text\":\"Sticky note\",\"size\":60}]");
		return note;
	}

	private GPoint pressHandle(DrawStickyNote drawable, EuclidianBoundingBoxHandler handler) {
		drawable.update();
		GPoint point = getHandlePosition(drawable.getBounds(), handler);
		dragStart(point.x, point.y);
		assertEquals(handler, view.getHitHandler(), "Drag must start on the resize handle");
		return point;
	}

	private GPoint getHandlePosition(GRectangle bounds, EuclidianBoundingBoxHandler handler) {
		int x = (int) (bounds.getX() + (handler.getDx() + 1) * bounds.getWidth() / 2);
		int y = (int) (bounds.getY() + (handler.getDy() + 1) * bounds.getHeight() / 2);
		return new GPoint(x, y);
	}

	private void assertSquare(GeoStickyNote note, double side) {
		assertAll(
				() -> assertEquals(side, note.getWidth(), MAX_PRECISION, "width"),
				() -> assertEquals(side, note.getHeight(), MAX_PRECISION, "height"));
	}
}
