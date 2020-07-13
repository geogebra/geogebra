package org.geogebra.common.kernel.algos;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.junit.Test;

public class AlgoSequenceTest extends BaseUnitTest {

	@Test
	public void testAngleVisibility() {
		addAvInput("A = (0, 0)");
		addAvInput("B = (1, 1)");
		addAvInput("C = (0, 1)");
		addAvInput("list = Sequence(Angle(A,B,C), i, 1, 1)");
		GeoAngle angle = addAvInput("list(1)");
		assertThat(angle.showInEuclidianView(), is(true));
	}
}