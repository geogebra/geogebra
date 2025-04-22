package org.geogebra.common.kernel.implicit;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class AlgoIntersetImplicitPolynomialsTest extends BaseUnitTest {

	@Test
	@Issue("APPS-6451")
	public void testIntersect() {
		add("f:y=-(x-8)^2+5");
		add("eq:abs(y)=4");
		assertThat(add("{Intersect(f,eq)}"), hasValue("{(5, -4), (11, -4), (7, 4), (9, 4)}"));
	}
}
