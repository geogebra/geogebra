package org.geogebra.common.kernel.algos;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class AlgoOrthoLinePointLineTest extends BaseUnitTest {

	@Test
	@Issue("APPS-5523")
	public void repeatOrthogonalShouldNotUnderflow() {
		add("eq1: 0.1 x-0.1 y=0");
		add("eq2: 2 y=x");
		add("A=(1.31,1.31)");
		add("l1=IterationList(Intersect(eq1,"
				+ "PerpendicularLine(Intersect(eq2,PerpendicularLine(V,xAxis)),yAxis)),V,{A},15)");
		GeoLine last = add("PerpendicularLine(Element(l1,16),eq1)");
		assertThat(last, isDefined());
	}
}
