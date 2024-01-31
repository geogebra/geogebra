package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.junit.Test;

public class ModeChangeTest extends BaseControllerTest {

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
	public void leftMouseClickSwitchesToCorrectModeInNotes() {
		getApp().setConfig(new AppConfigNotes());
		setMode(EuclidianConstants.MODE_MOVE);
		click(100, 100);
		assertEquals(EuclidianConstants.MODE_SELECT_MOW, getApp().getMode());
	}

	@Test
	public void draggingInNotesShouldNotChangeModeAfterwards() {
		getApp().setConfig(new AppConfigNotes());
		setMode(EuclidianConstants.MODE_MOVE);
		dragStart(50, 50);
		dragEnd(100, 100);
		assertEquals(EuclidianConstants.MODE_MOVE, getApp().getMode());
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
		setMode(EuclidianConstants.MODE_PEN);
		dragStart(50, 50);
		dragEnd(150, 150);

		setMode(EuclidianConstants.MODE_SELECT_MOW);
		click(100, 100);

		setMode(EuclidianConstants.MODE_MOVE);
		assertNull(getApp().getActiveEuclidianView().getBoundingBox());
	}
}
