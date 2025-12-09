/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
