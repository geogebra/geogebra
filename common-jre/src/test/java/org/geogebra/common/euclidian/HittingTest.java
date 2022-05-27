package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.junit.Test;

public class HittingTest extends BaseControllerTest {

	@Test
	public void polygonTool() {
		add("p1=Polygon((0, 0), (2, 0),(2, -2), (0, -2))");
		add("p2=Polygon((2, 0), (4, 0),(4, -2), (2, -2))");
		for (String segment: getApp().getGgbApi().getAllObjectNames("segment")) {
			getApp().getGgbApi().setFixed(segment, true, false);
		}
		setMode(EuclidianConstants.MODE_MOVE);
		click(96, 50, PointerEventType.TOUCH);
		assertEquals("p1", getSelectedLabel());
		click(104, 50, PointerEventType.TOUCH);
		assertEquals("p2", getSelectedLabel());
	}

	private String getSelectedLabel() {
		return getApp().getSelectionManager().getSelectedGeos().get(0).getLabelSimple();
	}
}
