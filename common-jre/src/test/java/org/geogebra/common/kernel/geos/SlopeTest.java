package org.geogebra.common.kernel.geos;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.junit.Before;
import org.junit.Test;

public class SlopeTest extends BaseUnitTest {

	@Before
	public void setUp() {
		getApp().setGeometryConfig();
	}

	@Test
	public void showInEuclidianView() {
		addAvInput("f = Line((0,0), (1,1))");
		GeoNumeric slope = addAvInput("s = Slope(f)");
		assertThat(slope.showInEuclidianView(), is(true));
	}
}
