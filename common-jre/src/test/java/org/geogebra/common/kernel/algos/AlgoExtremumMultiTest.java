package org.geogebra.common.kernel.algos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.Test;

public class AlgoExtremumMultiTest extends BaseUnitTest {

	@Test
	public void testSin() {
		GeoPoint[] extremums = getElements("Extremum(sin(x), 1, 7 )");
		assertThat(extremums.length, equalTo(2));
		assertThat(extremums[0].getAlgebraDescriptionDefault(), equalTo("A = (1.57, 1)"));
		assertThat(extremums[1].getAlgebraDescriptionDefault(), equalTo("B = (4.71, -1)"));
	}

	@Test
	public void testPolynomial() {
		GeoPoint[] extremums = getElements("Extremum(x^3-3x)");
		assertThat(extremums.length, equalTo(2));
		assertThat(extremums[0].getAlgebraDescriptionDefault(), equalTo("A = (-1, 2)"));
		assertThat(extremums[1].getAlgebraDescriptionDefault(), equalTo("B = (1, -2)"));
	}
}