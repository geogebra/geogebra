package org.geogebra.common.kernel.algos;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.junit.Test;

public class AlgoBarChartTest extends BaseUnitTest {

	@Test
	public void showInEuclidianView() {
		GeoNumeric chart = addAvInput("BarChart({10, 11, 12, 13, 14}, {5, 8, 12, 0, 1})");
		assertThat(chart.showInEuclidianView(), is(true));
	}
}