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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.measurement.MeasurementController;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
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

	@Test
	public void rotateCurve() {
		GeoCurveCartesian curve = add("BezierCurve((0,0),(2,0),(1,2),(3,2))");
		EuclidianView view = getApp().getActiveEuclidianView();
		view.getEuclidianController().selectAndShowSelectionUI(curve);
		RotateBoundingBox rotateBoundingBox = new RotateBoundingBox(
				view.getEuclidianController(), new MeasurementController(null));
		view.getEuclidianController().lastMouseLoc = new GPoint(0, 100);
		view.getEuclidianController().isMultiResize = true;
		rotateBoundingBox.rotate(view.getBoundingBox().getRectangle(), 100, 100);
		assertEquals("BezierCurve((0.87507, -0.69099), (2.49254, 0.48535),"
				+ " (0.50746, 1.51465), (2.12493, 2.69099))",
				curve.getDefinition(StringTemplate.editTemplate));
	}
}
