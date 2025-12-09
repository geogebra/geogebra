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

package org.geogebra.common.euclidian;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.geogebra.common.main.settings.config.AppConfigUnrestrictedGraphing;
import org.junit.Test;

public class ModeChangeTest extends BaseEuclidianControllerTest {

	@Test
	public void previewPointsShouldBeRemovedOnCancel() {
		getApp().getKernel().setUndoActive(true);
		setMode(EuclidianConstants.MODE_JOIN);
		click(50, 50);
		checkContent("A = (1, -1)");
		setMode(EuclidianConstants.MODE_POINT);
		checkContent(); // no objects after reset
	}

	@Test
	public void switchingToMoveModeShouldDeselectGeoInNotes() {
		getApp().setConfig(new AppConfigNotes());
		setMode(EuclidianConstants.MODE_PEN);
		dragStart(50, 50);
		dragEnd(150, 150);

		setMode(EuclidianConstants.MODE_SELECT_MOW);
		click(100, 100);

		setMode(EuclidianConstants.MODE_MOVE);
		assertTrue(getApp().getSelectionManager().getSelectedGeos().isEmpty());
	}

	@Test
	public void switchingToMoveModeShouldClearBoundingBoxInNotes() {
		getApp().setConfig(new AppConfigNotes());
		setMode(EuclidianConstants.MODE_SHAPE_RECTANGLE);
		dragStart(50, 50);
		dragEnd(150, 150);
		assertNotNull(getApp().getActiveEuclidianView().getBoundingBox());
		setMode(EuclidianConstants.MODE_SELECT_MOW);
		click(100, 100);

		setMode(EuclidianConstants.MODE_MOVE);
		assertNull(getApp().getActiveEuclidianView().getBoundingBox());
	}

	@Test
	public void switchingToMoveModeShouldDeselectGeosInGraphing() {
		getApp().setConfig(new AppConfigUnrestrictedGraphing());
		setMode(EuclidianConstants.MODE_PEN);
		dragStart(50, 50);
		dragEnd(150, 150);

		setMode(EuclidianConstants.MODE_POINT);
		click(200, 200);

		setMode(EuclidianConstants.MODE_SELECT);
		click(100, 100);
		click(200, 200);

		setMode(EuclidianConstants.MODE_MOVE);
		assertTrue(getApp().getSelectionManager().getSelectedGeos().isEmpty());
	}

	@Test
	public void switchingToMoveModeShouldClearBoundingBoxesInGraphing() {
		getApp().setConfig(new AppConfigUnrestrictedGraphing());
		setMode(EuclidianConstants.MODE_PEN);
		dragStart(50, 50);
		dragEnd(150, 150);

		setMode(EuclidianConstants.MODE_POINT);
		click(200, 200);

		setMode(EuclidianConstants.MODE_SELECT);
		click(100, 100);
		click(200, 200);

		setMode(EuclidianConstants.MODE_MOVE);
		assertNull(getApp().getActiveEuclidianView().getBoundingBox());
	}
}
