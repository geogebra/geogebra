package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GeoElementTest extends BaseUnitTest {

	@Test
	public void isSimple() {
		GeoElement minusOne = addAvInput("-1");
		assertThat(minusOne.isSimple(), is(true));
	}
}