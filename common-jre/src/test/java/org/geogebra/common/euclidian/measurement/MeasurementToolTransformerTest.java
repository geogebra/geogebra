package org.geogebra.common.euclidian.measurement;

import static org.geogebra.common.euclidian.measurement.MeasurementToolId.RULER;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.junit.Before;
import org.junit.Test;

public class MeasurementToolTransformerTest extends BaseUnitTest {

	private EuclidianView view;
	private MeasurementController mc;

	@Before
	public void setUp() {
		view = getApp().getActiveEuclidianView();
		mc = view.getEuclidianController().getMeasurementController();
	}

	@Test
	public void testRuler() {
		mc.selectTool(RULER);
		MeasurementToolTransformer transformer = createTransformer(RULER);
		transformer.reset(view, List.of(new GPoint(0,0)));

	}

	private MeasurementToolTransformer createTransformer(MeasurementToolId id) {
		return new MeasurementToolTransformer(mc, id.getEdges());
	}
}
