package org.geogebra.common.kernel;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.jre.factory.FormatFactoryJre;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class CoordsTest {

	@Test
	public void testProduct() {
		Coords v1 = new Coords(2);
		v1.val[0] = 3.0;
		v1.val[1] = 4.0;

		assertEquals(v1.dotproduct(v1), 25, 1E-8);
	}

	@Test
	public void testToString() {
		FormatFactory.setPrototypeIfNull(new FormatFactoryJre());
		Coords v1 = new Coords(4);
		v1.set(.5, .31, -.17);
		assertEquals(v1.toString(2), "(+0.50  +0.31  -0.17  +0.00)");
	}

	@Test
	public void testProjectPlaneToHorizontalShouldKeepZCoord() {
		Coords origin = new Coords(1, 2, 3, 1);
		Coords output = new Coords(4);
		origin.projectPlaneThruV(CoordMatrix4x4.identity(),
				new Coords(89.13, 14.19, 17.21),
				output);

		assertEquals(0, output.getZ(), 1E-18);
	}
}
