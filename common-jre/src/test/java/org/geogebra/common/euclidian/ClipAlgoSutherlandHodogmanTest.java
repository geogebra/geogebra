package org.geogebra.common.euclidian;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.geogebra.common.kernel.MyPoint;
import org.junit.Test;

public class ClipAlgoSutherlandHodogmanTest {
	private static ClipAlgoSutherlandHodogman algo = new ClipAlgoSutherlandHodogman();

	@Test
	public void allInsideTest() {
		double[][] clipPoints = {
				{0, 0},
				{100, 0},
				{100, 100},
				{0, 100}
		};

		ArrayList< MyPoint > input = new ArrayList<>();
		input.add(new MyPoint(50, 50));
		input.add(new MyPoint(10, 80));
		input.add(new MyPoint(90, 80));

		ArrayList<MyPoint> actual = algo.process(input, clipPoints);
		assertEquals(input, actual);
	}
}
