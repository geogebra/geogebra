package org.geogebra.common.euclidian;

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
}
