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

package org.geogebra.common.gui.stylebar;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.TestEvent;
import org.junit.Before;
import org.junit.Test;

public class StylebarPositionerTest extends BaseUnitTest {

	StylebarPositioner sp;

	@Before
	public void setupViewport() {
		add("ZoomIn(0,0,8,6)");
		sp = new StylebarPositioner(getApp());
	}

	@Test
	public void positionForPoint() {
		getApp().getSelectionManager().addSelectedGeo(add("A=(1,1)"));
		assertEquals(new GPoint(75, 399), sp.getPositionForStyleBar(50, 50));
		// bigger style bar won't fit below, show above
		assertEquals(new GPoint(75, 349), sp.getPositionForStyleBar(50, 100));
	}

	@Test
	public void positionForTwoPoints() {
		getApp().getSelectionManager().addSelectedGeo(add("A=(1,1)"));
		getApp().getSelectionManager().addSelectedGeo(add("A=(7,5)"));
		assertEquals(new GPoint(675, 151), sp.getPositionForStyleBar(50, 50));
	}

	@Test
	public void useLastHitForFunction() {
		GeoElement function = add("f:x");
		pointerDown(200, 400);
		assertTrue("Should select function", function.isSelected());
		assertEquals(new GPoint(210, 410), sp.getPositionForStyleBar(50, 50));
		getApp().getSelectionManager().removeAllSelectedGeos();
		assertThat(sp.getPositionForStyleBar(50, 50), nullValue());
		getApp().getSelectionManager().addSelectedGeo(function);
		assertEquals(new GPoint(210, 410), sp.getPositionForStyleBar(50, 50));
	}

	@Test
	public void discardLastHitAfterFunctionDeselected() {
		add("f:x");
		GeoElement inputBox = add("InputBox()");
		pointerDown(200, 400);
		assertEquals(new GPoint(210, 410), sp.getPositionForStyleBar(50, 50));
		pointerDown(50, 50);
		assertTrue("Should select input box", inputBox.isSelected());
		assertThat(sp.getPositionForStyleBar(50, 50), nullValue());
	}

	private void pointerDown(int x, int y) {
		getApp().getActiveEuclidianView().getEuclidianController()
				.wrapMousePressed(new TestEvent(x, y));
	}

	@Test
	public void pieChartStyleBarPositionStaysConsistent() {
		GeoElement pieChart = add("PieChart[{1,2,3}]");
		pointerDown(-200, 400);
		assertTrue(pieChart.isSelected());
		assertEquals(new GPoint(16, 214), sp.getPositionForStyleBar(50, 50));
		pointerDown(-150, 450);
		assertEquals(new GPoint(16, 214), sp.getPositionForStyleBar(50, 50));
	}
}
