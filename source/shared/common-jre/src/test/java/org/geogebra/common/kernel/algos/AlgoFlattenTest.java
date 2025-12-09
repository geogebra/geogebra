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

package org.geogebra.common.kernel.algos;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoList;
import org.junit.Test;

public class AlgoFlattenTest extends BaseUnitTest {

	@Test
	public void shouldNotCreateExtraObjects() {
		add("square={Polygon((0,0),(0,1),(1,1),(1,0))}");
		add("k=7");
		GeoList grid = add("grid=Flatten[Sequence[Translate["
				+ "Sequence[Translate[square, i * (1, 0)], i, 0, k], j * (0, 1)"
				+ "], j, 0, k]]");
		assertThat(grid.size(), is(64));
		assertThat(getCountOfSegmentsInConstruction(), is(4));
		add("SetValue(k,10)");
		assertThat(grid.size(), is(121));
		// move slider back and forth
		add("SetValue(k,7)");
		add("SetValue(k,10)");
		add("SetValue(k,7)");
		assertThat(grid.size(), is(64));
		assertThat(getCountOfSegmentsInConstruction(), is(4));
	}

	private int getCountOfSegmentsInConstruction() {
		return (int) getConstruction().getAlgoList().stream()
				.filter(k -> k instanceof AlgoJoinPointsSegment).count();
	}
}
