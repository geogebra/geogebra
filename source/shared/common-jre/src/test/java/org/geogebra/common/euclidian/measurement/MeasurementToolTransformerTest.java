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

package org.geogebra.common.euclidian.measurement;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.Before;
import org.junit.Test;

public class MeasurementToolTransformerTest extends BaseUnitTest {

	private EuclidianView view;
	private MeasurementController measurementController;

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Before
	public void setUp() {
		view = getApp().getActiveEuclidianView();
		measurementController = new MeasurementController((mode, file) ->
				createToolImage(mode, file, getConstruction(), getApp().getActiveEuclidianView())) ;
	}

	/**
	 * @param mode one of MODE_RULER, MODE_PROTRACTOR, MODE_TRIANGLE_PROTRACTOR
	 * @param fileName filename
	 * @param cons construction
	 * @param view active view
	 * @return ruler or protractor image
	 */
	public static GeoImage createToolImage(int mode, String fileName, Construction cons,
			EuclidianView view) {
		GeoImage image = new GeoImage(cons);
		image.setImageFileName(fileName, 400, mode == MODE_RULER ? 40 : 300);
		image.initStartPoint(new GeoPoint(cons, rwX(100.0, view),
				rwY(300, view), 1), 0);
		image.initStartPoint(new GeoPoint(cons, rwX(500, view),
				rwY(300, view), 1), 1);
		image.setLabel(null);
		return image;
	}

	private static double rwX(double xRW, EuclidianView view) {
		return view.toRealWorldCoordX(xRW);
	}

	private static double rwY(double yRW, EuclidianView view) {
		return view.toRealWorldCoordY(yRW);
	}

	@Test
	public void testRuler() {
		measurementController.toggleActiveTool(MODE_RULER);
		List<GPoint> previewPoints = new ArrayList<>();
		previewPoints.add(new GPoint(200, 309));
		GPoint secondPoint = new GPoint(210, 305);
		measurementController.applyTransformer(view, secondPoint, previewPoints);
		previewPoints.add(secondPoint);
		measurementController.applyTransformer(view, new GPoint(220, 307), previewPoints);
		assertEquals(2, previewPoints.size());
		// 302 = bottom coordinate + line thickness
		assertEquals(new GPoint(200, 302), previewPoints.get(0));
		assertEquals(new GPoint(220, 302), previewPoints.get(1));
	}

}
