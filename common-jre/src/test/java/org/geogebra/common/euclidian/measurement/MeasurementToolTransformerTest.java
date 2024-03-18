package org.geogebra.common.euclidian.measurement;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.Before;
import org.junit.Ignore;
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
		measurementController = new MeasurementController(this::createToolImage) ;
	}

	private GeoImage createToolImage(int mode, String fileName) {
		GeoImage image = new GeoImage(getKernel().getConstruction());
		if (mode == MODE_RULER) {
			image.setImageFileName("Ruler", 400, 40);
			image.initStartPoint(new GeoPoint(getKernel().getConstruction(), rwX(100.0),
					rwY(300), 1), 0);
			image.initStartPoint(new GeoPoint(getKernel().getConstruction(), rwX(500),
					rwY(300), 1), 1);
		} else if (mode == MODE_TRIANGLE_PROTRACTOR) {
			image.setSize(400, 200);
		}
		return image;
	}

	private double rwX(double xRW) {
		return view.toRealWorldCoordX(xRW);
	}

	private double rwY(double yRW) {
		return view.toRealWorldCoordY(yRW);
	}

	@Ignore
	@Test
	public void testRuler() {
		measurementController.toggleActiveTool(MODE_RULER);
		GPoint point = new GPoint(200, 285);
		List<GPoint> penPoints = new ArrayList<>();
		List<GPoint> previewPoints = new ArrayList<>();
		previewPoints.add(new GPoint(145, 200));
		measurementController.applyTransformer(view, new GPoint(155, 285), previewPoints);
		assertEquals(2, previewPoints.size());
	}

}
