package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.geogebra.test.annotation.Issue;
import org.junit.Before;
import org.junit.Test;

public class BoundingBoxTest extends BaseEuclidianControllerTest {

	@Before
	public void setupNotes() {
		getApp().setConfig(new AppConfigNotes());
	}

	@Test
	@Issue("MOW-1518")
	public void hittingHandlerShouldHavePriority() {
		getApp().setMode(EuclidianConstants.MODE_SELECT_MOW);
		GeoConic circle = add("Circle((1,-1),1)");
		circle.setAlphaValue(1);
		click(50, 50);
		getApp().getActiveEuclidianView().repaintView();
		// hit drag handler in the center: should be accepted
		dragStart(100, 100);
		assertEquals(EuclidianBoundingBoxHandler.BOTTOM_RIGHT,
				getApp().getActiveEuclidianView().getHitHandler());
		// hit drag handler at the border of the hit zone: still accepted
		dragStart(114, 100);
		assertEquals(EuclidianBoundingBoxHandler.BOTTOM_RIGHT,
				getApp().getActiveEuclidianView().getHitHandler());
		assertTrue("circle should be selected", circle.isSelected());
		// hit too far
		dragStart(115, 100);
		assertEquals(EuclidianBoundingBoxHandler.UNDEFINED,
				getApp().getActiveEuclidianView().getHitHandler());
		assertFalse("circle should not be selected", circle.isSelected());
	}
}
