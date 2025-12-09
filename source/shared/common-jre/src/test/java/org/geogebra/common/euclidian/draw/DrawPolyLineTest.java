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

package org.geogebra.common.euclidian.draw;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GGraphicsCommon;
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

	@Test
	public void shouldBeThickWhenKeyboardHighlighted() {
		GeoPolyLine poly = add("Polyline((1,-1),(5,-1))");
		getApp().getSelectionManager().setKeyboardSelection(poly);
		List<Double> widths = new ArrayList<>();
		GGraphics2D g2 = new GGraphicsCommon() {
			public void setStroke(GBasicStroke stroke) {
				widths.add(stroke.getLineWidth());
			}
		};
		getDrawable(poly).draw(g2);
		assertEquals(List.of(4.0, 10.0), widths);
	}

}
