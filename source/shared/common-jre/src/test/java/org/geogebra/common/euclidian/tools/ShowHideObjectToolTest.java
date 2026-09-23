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
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHOW_HIDE_OBJECT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.SelectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShowHideObjectToolTest extends BaseToolTest {

	private EuclidianView view;
	private SelectionManager selection;

	@BeforeEach
	void setMode() {
		setMode(MODE_SHOW_HIDE_OBJECT);
		view = getApp().getActiveEuclidianView();
		selection = getApp().getSelectionManager();
	}

	@Test
	void clickObjectShouldHideItAfterLeavingTool() {
		GeoElement point = add("A = (1, -1)");

		clickRW(1, -1);

		assertAll(
				() -> assertTrue(point.isEuclidianVisible()),
				() -> assertTrue(selection.containsSelectedGeo(point)));

		setMode(MODE_MOVE);

		assertAll(
				() -> assertFalse(point.isEuclidianVisible()),
				() -> assertTrue(selection.getSelectedGeos().isEmpty()));
	}

	@Test
	void clickObjectTwiceShouldKeepItVisible() {
		GeoElement point = add("A = (1, -1)");

		clickRW(1, -1);
		assertTrue(selection.containsSelectedGeo(point));

		resetMouseLocation();
		clickRW(1, -1);
		assertFalse(selection.containsSelectedGeo(point));

		setMode(MODE_MOVE);

		assertTrue(point.isEuclidianVisible());
	}

	@Test
	void hoverShouldHighlightObjectWithoutSelectingIt() {
		GeoElement point = add("A = (1, -1)");

		moveMouseRW(1, -1);

		assertAll(
				() -> assertTrue(ec.getHighlightedgeos().contains(point)),
				() -> assertFalse(selection.containsSelectedGeo(point)),
				() -> assertTrue(point.isEuclidianVisible()));
	}

	@Test
	void clickEmptySpaceShouldNotSelectObject() {
		GeoElement point = add("A = (1, -1)");

		clickRW(4, -4);

		assertAll(
				() -> assertTrue(point.isEuclidianVisible()),
				() -> assertTrue(selection.getSelectedGeos().isEmpty()));
	}

	@Test
	void clickXAxisShouldHideOnlyXAxis() {
		view.setShowAxes(true, true);

		clickRW(1, 0);

		assertAll(() -> assertFalse(view.getShowXaxis()), () -> assertTrue(view.getShowYaxis()));
	}

	@Test
	void clickYAxisShouldHideOnlyYAxis() {
		view.setShowAxes(true, true);

		clickRW(0, -1);

		assertAll(() -> assertTrue(view.getShowXaxis()), () -> assertFalse(view.getShowYaxis()));
	}

	@Test
	void clickAxesIntersectionShouldNotHideEitherAxis() {
		view.setShowAxes(true, true);

		clickRW(0, 0);

		assertAll(() -> assertTrue(view.getShowXaxis()), () -> assertTrue(view.getShowYaxis()));
	}

	@Test
	void clickOverlappingObjectsShouldQueuePointOnlyForHiding() {
		GeoElement point = add("A = (1, -1)");
		GeoElement line = add("a: x + y = 0");

		clickRW(1, -1);
		setMode(MODE_MOVE);

		assertAll(
				() -> assertFalse(point.isEuclidianVisible()), () -> assertTrue(line.isEuclidianVisible()));
	}

	@Test
	void visibilityChangeShouldBeUndoableAfterLeavingTool() {
		getApp().setUndoActive(true);
		add("A = (1, -1)");
		getApp().storeUndoInfo();

		clickRW(1, -1);
		setMode(MODE_MOVE);
		assertFalse(lookup("A").isEuclidianVisible());

		getKernel().undo();

		assertTrue(lookup("A").isEuclidianVisible());
	}

	@Test
	void activatingToolShouldQueuePreselectedObjectForHiding() {
		setMode(MODE_MOVE);
		GeoElement point = add("A = (1, -1)");
		selection.setSelectedGeos(List.of(point));

		setMode(MODE_SHOW_HIDE_OBJECT);

		assertAll(
				() -> assertTrue(point.isEuclidianVisible()),
				() -> assertTrue(selection.containsSelectedGeo(point)));

		setMode(MODE_MOVE);

		assertFalse(point.isEuclidianVisible());
	}

	@Test
	void activatingToolShouldShowHiddenPreselectedObject() {
		setMode(MODE_MOVE);
		GeoElement point = addHidden("A = (1, -1)");
		selection.setSelectedGeos(List.of(point));

		setMode(MODE_SHOW_HIDE_OBJECT);

		assertAll(
				() -> assertTrue(point.isEuclidianVisible()),
				() -> assertFalse(selection.containsSelectedGeo(point)));

		setMode(MODE_MOVE);

		assertTrue(point.isEuclidianVisible());
	}

	@Test
	void clickRevealedObjectShouldKeepOnlyItVisibleAfterLeavingTool() {
		setMode(MODE_MOVE);
		GeoElement pointA = addHidden("A = (1, -1)");
		GeoElement pointB = addHidden("B = (2, -1)");

		setMode(MODE_SHOW_HIDE_OBJECT);

		assertAll(
				() -> assertTrue(pointA.isEuclidianVisible()),
				() -> assertTrue(pointB.isEuclidianVisible()),
				() -> assertTrue(selection.containsSelectedGeo(pointA)),
				() -> assertTrue(selection.containsSelectedGeo(pointB)));

		clickRW(1, -1);
		setMode(MODE_MOVE);

		assertAll(
				() -> assertTrue(pointA.isEuclidianVisible()),
				() -> assertFalse(pointB.isEuclidianVisible()));
	}

	@Test
	void activatingToolShouldNotRevealUnselectedIndependentNumberOrBoolean() {
		setMode(MODE_MOVE);
		GeoElement number = addHidden("a = 1");
		GeoElement bool = addHidden("b = true");

		setMode(MODE_SHOW_HIDE_OBJECT);

		assertAll(
				() -> assertFalse(number.isSetEuclidianVisible()),
				() -> assertFalse(bool.isSetEuclidianVisible()),
				() -> assertFalse(selection.containsSelectedGeo(number)),
				() -> assertFalse(selection.containsSelectedGeo(bool)));
	}

	@Test
	void activatingToolShouldRevealPreselectedIndependentNumberAndBoolean() {
		setMode(MODE_MOVE);
		GeoElement number = addHidden("a = Slider(-1, 1)");
		GeoElement bool = addHidden("b = true");
		selection.setSelectedGeos(List.of(number, bool));

		setMode(MODE_SHOW_HIDE_OBJECT);

		assertAll(
				() -> assertTrue(number.isSetEuclidianVisible()),
				() -> assertTrue(bool.isSetEuclidianVisible()),
				() -> assertTrue(selection.getSelectedGeos().isEmpty()));
	}

	@Test
	void activatingToolShouldRevealDependentNumberAndBoolean() {
		setMode(MODE_MOVE);
		add("a = 1");
		GeoElement number = addHidden("b = a + 1");
		GeoElement bool = addHidden("c = b > 0");

		setMode(MODE_SHOW_HIDE_OBJECT);

		assertAll(
				() -> assertTrue(number.isSetEuclidianVisible()),
				() -> assertTrue(bool.isSetEuclidianVisible()),
				() -> assertTrue(selection.containsSelectedGeo(number)),
				() -> assertTrue(selection.containsSelectedGeo(bool)));
	}

	@Test
	void activatingToolShouldIgnoreNonToggleablePreselectedObject()
			throws CircularDefinitionException {
		setMode(MODE_MOVE);
		GeoElement point = add("A = (1, -1)");
		GeoBoolean condition = add("condition = true");
		point.setShowObjectCondition(condition);
		selection.setSelectedGeos(List.of(point));
		assertFalse(point.isEuclidianToggleable());

		setMode(MODE_SHOW_HIDE_OBJECT);

		assertAll(
				() -> assertTrue(point.isEuclidianVisible()),
				() -> assertFalse(selection.containsSelectedGeo(point)));
	}

	@Test
	void clickConditionControlledObjectShouldNotOverrideCondition()
			throws CircularDefinitionException {
		GeoElement point = add("A = (1, -1)");
		GeoBoolean condition = add("condition = true");
		point.setShowObjectCondition(condition);
		assertFalse(point.isEuclidianToggleable());

		clickRW(1, -1);
		setMode(MODE_MOVE);

		assertTrue(point.isEuclidianVisible());
	}

	private GeoElement addHidden(String definition) {
		GeoElement geo = add(definition);
		geo.setEuclidianVisible(false);
		geo.updateRepaint();
		return geo;
	}
}
