package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.util.debug.Log;
import org.junit.Test;

public class FreehandFunctionTest extends BaseControllerTest {

	@Test
	public void tmp() {
		EuclidianPenFreehand freehandPen
				= new EuclidianPenFreehand(getApp(), getApp().getActiveEuclidianView());

		freehandPen.addPointPenMode(new GPoint(10, 10));
		freehandPen.addPointPenMode(new GPoint(15, 15));
		freehandPen.addPointPenMode(new GPoint(20, 20));

		Log.debug(freehandPen.checkExpectedShape());
	}

}
