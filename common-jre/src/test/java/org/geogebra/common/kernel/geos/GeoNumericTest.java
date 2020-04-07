package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GeoNumericTest extends BaseUnitTest {

	@Test
	public void euclidianShowabilityOfOperationResult() {
		GeoNumeric numeric = addAvInput("4+6");
		assertThat(numeric.isEuclidianShowable(), is(false));
	}
}