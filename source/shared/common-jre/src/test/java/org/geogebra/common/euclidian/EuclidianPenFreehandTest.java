package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.plugin.GeoClass;
import org.junit.Test;

public class EuclidianPenFreehandTest extends BaseEuclidianControllerTest {

	@Test
	public void freehandPenShouldRecognizeSegment() {
		EuclidianPenFreehand freehandPen
				= new EuclidianPenFreehand(getApp(), getApp().getActiveEuclidianView());

		freehandPen.addPointPenMode(new GPoint(10, 10));
		freehandPen.addPointPenMode(new GPoint(15, 15));
		freehandPen.addPointPenMode(new GPoint(20, 20));

		assertEquals(GeoClass.SEGMENT,
				freehandPen.checkExpectedShape().getGeoClassType());
	}

	@Test
	public void restrictedFreehandPenShouldRecognizeFunction() {
		EuclidianPenFreehand freehandPen
				= new EuclidianPenFreehand(getApp(), getApp().getActiveEuclidianView());

		freehandPen.setExpected(EuclidianPenFreehand.ShapeType.function);

		freehandPen.addPointPenMode(new GPoint(10, 10));
		freehandPen.addPointPenMode(new GPoint(15, 15));
		freehandPen.addPointPenMode(new GPoint(20, 20));

		assertEquals(GeoClass.FUNCTION,
				freehandPen.checkExpectedShape().getGeoClassType());
	}

	@Test
	public void freehandPenShouldRecognizeConic() {
		EuclidianPenFreehand freehandPen
				= new EuclidianPenFreehand(getApp(), getApp().getActiveEuclidianView());

		freehandPen.addPointPenMode(new GPoint(0, 10));
		freehandPen.addPointPenMode(new GPoint(7, 7));
		freehandPen.addPointPenMode(new GPoint(10, 0));
		freehandPen.addPointPenMode(new GPoint(7, -7));
		freehandPen.addPointPenMode(new GPoint(0, -10));
		freehandPen.addPointPenMode(new GPoint(-7, -7));
		freehandPen.addPointPenMode(new GPoint(-10, 0));
		freehandPen.addPointPenMode(new GPoint(-7, 7));

		assertEquals(GeoClass.CONIC,
				freehandPen.checkExpectedShape().getGeoClassType());
	}

	@Test
	public void restrictedFreehandPenShouldFailRecognizingConic() {
		EuclidianPenFreehand freehandPen
				= new EuclidianPenFreehand(getApp(), getApp().getActiveEuclidianView());

		freehandPen.setExpected(EuclidianPenFreehand.ShapeType.function);

		freehandPen.addPointPenMode(new GPoint(0, 10));
		freehandPen.addPointPenMode(new GPoint(7, 7));
		freehandPen.addPointPenMode(new GPoint(10, 0));
		freehandPen.addPointPenMode(new GPoint(7, -7));
		freehandPen.addPointPenMode(new GPoint(0, -10));
		freehandPen.addPointPenMode(new GPoint(-7, -7));
		freehandPen.addPointPenMode(new GPoint(-10, 0));
		freehandPen.addPointPenMode(new GPoint(-7, 7));

		assertNull(freehandPen.checkExpectedShape());
	}
}
