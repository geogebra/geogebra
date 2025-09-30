package org.geogebra.common.euclidian;

import static junit.framework.TestCase.assertEquals;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EuclidianViewForNotesTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupNotesApp();
	}

	@Test
	public void showAllObjectsPortrait() {
		evaluate("Segment((0,0),(10,10))");
		getApp().getEuclidianView1().setViewShowAllObjects(false, true);
		assertEquals("(-2.64967, -0.31767)",
				evaluateGeoElement("Corner(1)").toValueString(StringTemplate.editTemplate));
		assertEquals("(11.519, 10.31767)",
				evaluateGeoElement("Corner(3)").toValueString(StringTemplate.editTemplate));
	}

	@Test
	public void showAllObjectsLandscape() {
		evaluate("Segment((0,0),(20,10))");
		getApp().getEuclidianView1().setViewShowAllObjects(false, true);
		assertEquals("(-2.47228, -3.67011)",
				evaluateGeoElement("Corner(1)").toValueString(StringTemplate.editTemplate));
		assertEquals("(20.6288, 13.67011)",
				evaluateGeoElement("Corner(3)").toValueString(StringTemplate.editTemplate));
	}
}
