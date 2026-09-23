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
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SHOW_HIDE_LABEL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.SelectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ShowHideLabelToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_SHOW_HIDE_LABEL);
	}

	@Test
	void clickObjectShouldToggleLabelVisibility() {
		GeoElement point = add("A = (1, -1)");
		point.setLabelVisible(false);
		point.updateRepaint();

		clickRW(1, -1);
		assertTrue(point.isLabelVisible());

		resetMouseLocation();
		clickRW(1, -1);
		assertFalse(point.isLabelVisible());
	}

	@Test
	void hoverShouldHighlightObjectWithoutTogglingLabel() {
		GeoElement point = add("A = (1, -1)");
		point.setLabelVisible(false);
		point.updateRepaint();

		moveMouseRW(1, -1);

		assertTrue(ec.getHighlightedgeos().contains(point));
		assertFalse(point.isLabelVisible());
	}

	@Test
	void clickEmptySpaceShouldNotToggleLabel() {
		GeoElement point = add("A = (1, -1)");
		point.setLabelVisible(false);
		point.updateRepaint();

		clickRW(4, -4);

		assertFalse(point.isLabelVisible());
	}

	@Test
	void clickAxisShouldNotToggleItsLabel() {
		getApp().getActiveEuclidianView().setShowAxes(true, true);
		GeoElement xAxis = getKernel().getXAxis();
		xAxis.setLabelVisible(true);

		clickRW(1, 0);

		assertTrue(xAxis.isLabelVisible());
	}

	@Test
	void clickOverlappingObjectsShouldTogglePointLabelOnly() {
		GeoElement point = add("A = (1, -1)");
		GeoElement line = add("a: x + y = 0");
		point.setLabelVisible(false);
		line.setLabelVisible(false);

		clickRW(1, -1);

		assertTrue(point.isLabelVisible());
		assertFalse(line.isLabelVisible());
	}

	@Test
	void labelVisibilityChangeShouldBeUndoableAfterLeavingTool() {
		getApp().setUndoActive(true);
		GeoElement point = add("A = (1, -1)");
		point.setLabelVisible(false);
		point.updateRepaint();
		getApp().storeUndoInfo();

		clickRW(1, -1);
		setMode(MODE_MOVE);
		getKernel().undo();

		assertFalse(lookup("A").isLabelVisible());
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void activatingToolShouldTogglePreselectedObjectLabel(boolean initialVisibility) {
		setMode(MODE_MOVE);
		GeoElement point = add("A = (1, -1)");
		point.setLabelVisible(initialVisibility);
		SelectionManager selectionManager = getApp().getSelectionManager();
		selectionManager.setSelectedGeos(List.of(point));

		setMode(MODE_SHOW_HIDE_LABEL);

		assertAll(
				() -> assertEquals(!initialVisibility, point.isLabelVisible()),
				() -> assertTrue(selectionManager.getSelectedGeos().isEmpty()));
	}
}
