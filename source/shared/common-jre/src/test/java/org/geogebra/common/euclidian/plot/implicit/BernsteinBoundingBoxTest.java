package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class BernsteinBoundingBoxTest {
	@Test
	public void testSpitBox() {
		BernsteinBoundingBox box = newBox(-10, 10, -10, 10);
		BernsteinBoundingBox[] boxes = box.split();
		BernsteinBoundingBox[] expected = {
				newBox(-10, 0, -10, 0),
				newBox(0, 10, -10, 0),
				newBox(-10, 0, 0, 10),
				newBox(0, 10, 0, 10)
		};

		assertArrayEquals(expected, boxes);
	}

	private static BernsteinBoundingBox newBox(double xmin, double xmax, double ymin, double ymax) {
		return new BernsteinBoundingBox(xmin, ymin, xmax, ymax);
	}
}