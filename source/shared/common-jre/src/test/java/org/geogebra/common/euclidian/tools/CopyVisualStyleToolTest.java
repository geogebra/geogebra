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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_COPY_VISUAL_STYLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.geogebra.common.plugin.EuclidianStyleConstants.POINT_STYLE_CROSS;
import static org.geogebra.common.plugin.EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.SelectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CopyVisualStyleToolTest extends BaseToolTest {

	private SelectionManager selection;

	@BeforeEach
	void setMode() {
		setMode(MODE_COPY_VISUAL_STYLE);
		selection = getApp().getSelectionManager();
	}

	@Test
	void clickEmptySpaceShouldNotSelectSource() {
		clickRW(4, -4);

		assertAll(
				() -> assertNull(getApp().getGeoForCopyStyle()),
				() -> assertTrue(selection.getSelectedGeos().isEmpty()));
	}

	@Test
	void clickAxisShouldNotSelectSource() {
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setShowAxes(true, true);

		clickRW(1, 0);

		assertAll(
				() -> assertNull(getApp().getGeoForCopyStyle()),
				() -> assertTrue(selection.getSelectedGeos().isEmpty()),
				() -> assertTrue(view.getShowXaxis()));
	}

	@Test
	void clickObjectShouldSelectItAsStyleSource() {
		GeoPoint source = addStyledPoint("A = (1, -1)");

		clickRW(1, -1);

		assertAll(
				() -> assertSame(source, getApp().getGeoForCopyStyle()),
				() -> assertTrue(selection.containsSelectedGeo(source)));
	}

	@Test
	void clickTargetShouldCopyVisualStyleAndKeepSourceActive() {
		GeoPoint source = addStyledPoint("A = (1, -1)");
		GeoPoint target = addTargetPoint("B = (2, -2)");

		clickRW(1, -1);
		clickRW(2, -2);

		assertAll(
				() -> assertPointStyleCopied(source, target),
				() -> assertSame(source, getApp().getGeoForCopyStyle()),
				() -> assertTrue(selection.containsSelectedGeo(source)),
				() -> assertFalse(selection.containsSelectedGeo(target)));
	}

	@Test
	void clickSeveralTargetsShouldCopyStyleToAll() {
		GeoPoint source = addStyledPoint("A = (1, -1)");
		GeoPoint targetB = addTargetPoint("B = (2, -2)");
		GeoPoint targetC = addTargetPoint("C = (3, -3)");

		clickRW(1, -1);
		clickRW(2, -2);
		clickRW(3, -3);

		assertAll(
				() -> assertPointStyleCopied(source, targetB),
				() -> assertPointStyleCopied(source, targetC),
				() -> assertSame(source, getApp().getGeoForCopyStyle()));
	}

	@Test
	void clickSourceAgainShouldCancelSourceSelection() {
		GeoPoint source = addStyledPoint("A = (1, -1)");

		clickRW(1, -1);
		resetMouseLocation();
		clickRW(1, -1);

		assertAll(
				() -> assertNull(getApp().getGeoForCopyStyle()),
				() -> assertFalse(selection.containsSelectedGeo(source)));
	}

	@Test
	void clickOverlappingObjectsShouldChoosePointAsSource() {
		GeoPoint point = addStyledPoint("A = (1, -1)");
		add("a: x + y = 0");

		clickRW(1, -1);

		assertSame(point, getApp().getGeoForCopyStyle());
	}

	@Test
	void clickDifferentGeoTypeShouldCopySharedStyle() {
		GeoPoint source = addStyledPoint("A = (1, -1)");
		GeoLine target = add("a: y = -2");
		target.setObjColor(GColor.BLUE);
		target.updateRepaint();

		clickRW(1, -1);
		clickRW(3, -2);

		assertAll(
				() -> assertEquals(source.getObjectColor(), target.getObjectColor()),
				() -> assertSame(source, getApp().getGeoForCopyStyle()));
	}

	@Test
	void leavingToolShouldMakeAllCopiesUndoableAsOneAction() {
		getApp().setUndoActive(true);
		addStyledPoint("A = (1, -1)");
		addTargetPoint("B = (2, -2)");
		addTargetPoint("C = (3, -3)");
		getApp().storeUndoInfo();

		clickRW(1, -1);
		clickRW(2, -2);
		clickRW(3, -3);
		setMode(MODE_MOVE);

		assertAll(
				() -> assertEquals(GColor.RED, lookup("B").getObjectColor()),
				() -> assertEquals(GColor.RED, lookup("C").getObjectColor()));

		getKernel().undo();

		assertAll(
				() -> assertTargetPointStyle((GeoPoint) lookup("B")),
				() -> assertTargetPointStyle((GeoPoint) lookup("C")));
	}

	@Test
	void clickSourceAfterCopyShouldFinalizeUndoStep() {
		getApp().setUndoActive(true);
		addStyledPoint("A = (1, -1)");
		addTargetPoint("B = (2, -2)");
		getApp().storeUndoInfo();

		clickRW(1, -1);
		clickRW(2, -2);
		clickRW(1, -1);

		assertAll(
				() -> assertNull(getApp().getGeoForCopyStyle()),
				() -> assertEquals(GColor.RED, lookup("B").getObjectColor()));

		getKernel().undo();

		assertTargetPointStyle((GeoPoint) lookup("B"));
	}

	@Test
	void selectionRectangleThenSourceClickShouldStyleCompatibleTargetsOnly() {
		GeoPoint source = addStyledPoint("A = (4, -4)");
		GeoPoint pointTarget = addTargetPoint("B = (1, -1)");
		GeoElement conicTarget = add("c: (x - 2)^2 + (y + 2)^2 = 0.25");
		conicTarget.setObjColor(GColor.BLUE);
		conicTarget.updateRepaint();

		dragRW(0.5, -0.5, 2.5, -2.5);
		assertAll(
				() -> assertTrue(selection.containsSelectedGeo(pointTarget)),
				() -> assertTrue(selection.containsSelectedGeo(conicTarget)));

		clickRW(4, -4);

		assertAll(
				() -> assertPointStyleCopied(source, pointTarget),
				() -> assertEquals(GColor.BLUE, conicTarget.getObjectColor()),
				() -> assertSame(source, getApp().getGeoForCopyStyle()),
				() -> assertTrue(selection.getSelectedGeos().isEmpty()));
	}

	@Test
	void selectionRectangleWithOnlyIncompatibleTargetShouldChooseSourceWithoutStylingTarget() {
		GeoPoint source = addStyledPoint("A = (4, -4)");
		GeoElement conicTarget = add("c: (x - 2)^2 + (y + 2)^2 = 0.25");
		conicTarget.setObjColor(GColor.BLUE);
		conicTarget.updateRepaint();

		dragRW(1.5, -1.5, 2.5, -2.5);
		assertTrue(selection.containsSelectedGeo(conicTarget));

		clickRW(4, -4);

		assertAll(
				() -> assertEquals(GColor.BLUE, conicTarget.getObjectColor()),
				() -> assertSame(source, getApp().getGeoForCopyStyle()),
				() -> assertTrue(selection.containsSelectedGeo(source)),
				() -> assertTrue(selection.containsSelectedGeo(conicTarget)));
	}

	@Test
	void reactivatingToolShouldClearPreviousSource() {
		addStyledPoint("A = (1, -1)");
		clickRW(1, -1);
		setMode(MODE_MOVE);

		setMode(MODE_COPY_VISUAL_STYLE);

		assertAll(
				() -> assertNull(getApp().getGeoForCopyStyle()),
				() -> assertTrue(selection.getSelectedGeos().isEmpty()));
	}

	private GeoPoint addStyledPoint(String definition) {
		GeoPoint point = add(definition);
		point.setObjColor(GColor.RED);
		point.setPointSize(9);
		point.setPointStyle(POINT_STYLE_FILLED_DIAMOND);
		point.setLabelVisible(false);
		point.updateRepaint();
		return point;
	}

	private GeoPoint addTargetPoint(String definition) {
		GeoPoint point = add(definition);
		point.setObjColor(GColor.BLUE);
		point.setPointSize(3);
		point.setPointStyle(POINT_STYLE_CROSS);
		point.setLabelVisible(true);
		point.updateRepaint();
		return point;
	}

	private void assertPointStyleCopied(GeoPoint source, GeoPoint target) {
		assertAll(
				() -> assertEquals(source.getObjectColor(), target.getObjectColor()),
				() -> assertEquals(source.getPointSize(), target.getPointSize()),
				() -> assertEquals(source.getPointStyle(), target.getPointStyle()),
				() -> assertEquals(source.isLabelVisible(), target.isLabelVisible()));
	}

	private void assertTargetPointStyle(GeoPoint point) {
		assertAll(
				() -> assertEquals(GColor.BLUE, point.getObjectColor()),
				() -> assertEquals(3, point.getPointSize()),
				() -> assertEquals(POINT_STYLE_CROSS, point.getPointStyle()),
				() -> assertTrue(point.isLabelVisible()));
	}
}
