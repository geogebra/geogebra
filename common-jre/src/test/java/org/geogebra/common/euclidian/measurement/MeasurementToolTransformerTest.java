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
import org.junit.Before;
import org.junit.Test;

public class MeasurementToolTransformerTest extends BaseUnitTest {

	private EuclidianView view;
	private MeasurementController mc;

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Before
	public void setUp() {
		view = getApp().getActiveEuclidianView();
		mc = new MeasurementController(this::createToolImage) ;
	}

	private GeoImage createToolImage(int mode, String fileName) {
		GeoImage image = new GeoImage(getKernel().getConstruction());
		if (mode == MODE_RULER) {
			image.setImageFileName("Ruler", 400, 40);
		} else if (mode == MODE_TRIANGLE_PROTRACTOR) {
			image.setSize(200, 200);
		}
		return image;
	}

	@Test
	public void testRuler() {
		mc.toggleActiveTool(MODE_RULER);
		GPoint point = new GPoint(200, 275);
		List<GPoint> previewPoints = new ArrayList<>();
		previewPoints.add(new GPoint(199, 275));
		mc.applyTransformer(view, point, previewPoints);
		assertEquals(1, previewPoints.size());
	}

}
