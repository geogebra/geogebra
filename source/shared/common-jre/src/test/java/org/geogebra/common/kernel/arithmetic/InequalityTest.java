package org.geogebra.common.kernel.arithmetic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class InequalityTest extends BaseUnitTest {

	@Test
	public void shouldIncludeDoubleRootTwice() {
		assertThat(getZeros("x^3-4x^2 < 0"), is(new Long[]{0L, 0L, 4L}));
		assertThat(getZeros("x^7-4x^6 < 0"), is(new Long[]{0L, 0L, 4L}));
		assertThat(getZeros("x^3-4x^2 >= 0"), is(new Long[]{0L, 0L, 4L}));
	}

	@Test
	public void shouldSkipDoubleRoot() {
		assertThat(getZeros("x^3-4x^2 > 0"), is(new Long[]{4L}));
		assertThat(getZeros("x^7-4x^6 > 0"), is(new Long[]{4L}));
		assertThat(getZeros("x^3-4x^2 <= 0"), is(new Long[]{4L}));
	}

	private Long[] getZeros(String s) {
		GeoFunction ineq = add(s);
		return ineq.getIneqs().getIneq().getZeros().stream()
				.map(Math::round).toArray(Long[]::new);
	}
}
