package org.geogebra.common.euclidian.draw;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.euclidian.BaseEuclidianControllerTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.junit.Test;

public class DrawPolyLineTest extends BaseEuclidianControllerTest {

	@Test
	public void splitByDrag() {
		getApp().setNotesConfig();
		GeoPolyLine poly = add("Polyline((1,-1),(5,-1))");
		ec.selectAndShowSelectionUI(poly);
		dragStart(150, 50);
		dragEnd(250, 350);
		assertEquals("Polyline((1, -1), (5, -7), (5, -1))",
				poly.getDefinition(StringTemplate.testTemplate));
	}

	@Test
	public void splitByDoubleClick() {
		getApp().setNotesConfig();
		GeoPolyLine poly = add("Polyline((1,-1),(5,-1))");
		ec.selectAndShowSelectionUI(poly);
		click(150, 50);
		click(150, 50);
		assertEquals("Polyline((1, -1), (3, -2), (5, -1))",
				poly.getDefinition(StringTemplate.testTemplate));
		click(150, 100);
		click(150, 100);
		assertEquals("Polyline((1, -1), (5, -1))",
				poly.getDefinition(StringTemplate.testTemplate));
	}
}
