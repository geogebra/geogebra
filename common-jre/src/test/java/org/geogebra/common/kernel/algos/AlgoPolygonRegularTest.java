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
