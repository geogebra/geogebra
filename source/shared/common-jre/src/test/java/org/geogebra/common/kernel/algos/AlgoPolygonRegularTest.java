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

package org.geogebra.common.kernel.algos;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class AlgoPolygonRegularTest extends BaseUnitTest {

	@Test
	public void polygonInListShouldNotLabelSegments() {
		add("n=3");
		add("A=(0,0)");
		add("B=(1,0)");
		add("l1={Polygon(A,B,n)}");
		assertArrayEquals(new String[]{"n", "A", "B", "l1"},
				getApp().getGgbApi().getAllObjectNames());
		add("SetValue(n,4)");
		assertArrayEquals(new String[]{"n", "A", "B", "l1"},
				getApp().getGgbApi().getAllObjectNames());
	}

	@Test
	public void polygonShouldLabelNewSegments() {
		add("n=3");
		add("A=(0,0)");
		add("B=(1,0)");
		add("p1=Polygon(A,B,n)");
		assertEquals(8,
				getApp().getGgbApi().getAllObjectNames().length);
		add("SetValue(n,4)");
		assertEquals(10,
				getApp().getGgbApi().getAllObjectNames().length);
	}
}
