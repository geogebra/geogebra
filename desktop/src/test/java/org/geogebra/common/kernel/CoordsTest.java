package org.geogebra.common.kernel;

import org.geogebra.common.kernel.Matrix.Coords;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class CoordsTest {

	@Test
	public void testProduct() {
		Coords v1 = new Coords(2);
		v1.val[0] = 3.0;
		v1.val[1] = 4.0;

		Assert.assertEquals(v1.dotproduct(v1), 25, 1E-8);
	}
}
