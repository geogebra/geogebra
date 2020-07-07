package org.geogebra.common.kernel.statistics;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.junit.Test;

public class AlgoHistogramTest extends BaseUnitTest {

	@Test
	public void showInEuclidianView() {
		GeoNumeric histogram =
				addAvInput("Histogram({0, 1, 2, 3, 4, 5}, {2, 6, 8, 3, 1})");
		assertThat(histogram.showInEuclidianView(), is(true));
	}
}