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
