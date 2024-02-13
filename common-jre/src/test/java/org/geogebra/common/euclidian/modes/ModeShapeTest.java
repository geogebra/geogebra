package org.geogebra.common.euclidian.modes;

import java.util.List;

import org.geogebra.common.euclidian.BaseEuclidianControllerTest;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Assert;
import org.junit.Test;

public class ModeShapeTest extends BaseEuclidianControllerTest {

	@Test
	public void shapeMaskTool() {
		setMode(EuclidianConstants.MODE_MASK);
		dragStart(50, 50);
		dragEnd(200, 150);
		checkContent("q1 = 6");
		GeoElement mask = getApp().getKernel().lookupLabel("q1");
		Assert.assertEquals(1, mask.getAlphaValue(), Kernel.MIN_PRECISION);
	}

	@Test
	public void maskShouldBeInFrontOfObjects() {
		setMode(EuclidianConstants.MODE_MASK);
		dragStart(50, 50);
		dragEnd(200, 150);
		setMode(EuclidianConstants.MODE_SHAPE_RECTANGLE);
		dragStart(50, 50);
		dragEnd(300, 150);
		// fill the shape rectangle
		add("SetFilling(q2, 100%)");
		click(100, 75);
		assertSelected(
				"Clicking intersection of object and mask should select mask",
				"q1");
		click(250, 75);
		assertSelected("Clicking outside mask should select object", "q2");
	}

	private void assertSelected(String message, String string) {
		List<GeoElement> selection = getApp().getSelectionManager()
				.getSelectedGeos();
		Assert.assertEquals(message, string, selection.get(0).getLabelSimple());
	}

}
