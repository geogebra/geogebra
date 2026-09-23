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

package org.geogebra.common.euclidian.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PointToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(EuclidianConstants.MODE_POINT);
	}

	@Test
	void pointTool() {
		click(0, 0);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)");
	}

	@Test
	void pointToolClickExistingPointDoesNotCreateDuplicate() {
		click(0, 0);
		click(100, 100);
		click(0, 0);
		checkContent("A = (0, 0)", "B = (2, -2)");
	}

	@Test
	void pointToolCreateAxisPoint() {
		getApp().getActiveEuclidianView().setShowAxes(true, true);
		click(50, 0);
		checkContent("A = (1, 0)");
		assertEquals("Point(xAxis)", lookup("A").getDefinition(StringTemplate.testTemplate));
	}

	@Test
	void pointToolCreateIntersectPoint() {
		getApp().getActiveEuclidianView().setShowAxes(true, true);
		click(0, 0);
		checkContent("A = (0, 0)");
		assertEquals("Intersect(xAxis, yAxis)", lookup("A").getDefinition(StringTemplate.testTemplate));
	}

	@Test
	void pointToolCreatePointOnLine() {
		add("f: y = -1");
		click(50, 50);

		checkContent("f: y = -1", "A = (1, -1)");
		assertEquals("Point(f)", lookup("A").getDefinition(StringTemplate.testTemplate));
	}

	@Test
	void pointToolDragNewPoint() {
		dragRW(1, -1, 2, -2);

		checkContent("A = (2, -2)");
	}

	@Test
	void pointToolPreviewPath() {
		GeoElement line = add("f: y = -1");
		ec.wrapMouseMoved(new TestEvent(50, 50));

		assertTrue(ec.getHighlightedgeos().contains(line));
		checkContent("f: y = -1");
	}

	@Test
	void pointToolCreatePointOnCircle() {
		add("c: Circle((2, -2), 1)");
		click(150, 100);

		checkContent("c: (x - 2)² + (y + 2)² = 1", "A = (3, -2)");
		assertEquals("Point(c)", lookup("A").getDefinition(StringTemplate.testTemplate));
	}

	@Test
	void pointToolCreateFreePointInsidePolygon() {
		add("A = (0, 0)");
		add("B = (4, 0)");
		add("C = (4, -4)");
		add("D = (0, -4)");
		add("poly = Polygon(A, B, C, D)");
		click(100, 100);

		GeoElement point = lookup("E");
		assertEquals("E = (2, -2)", point.toString(StringTemplate.editTemplate));
		assertFalse(point.isPointOnPath());
		assertFalse(point.isPointInRegion());
	}
}
