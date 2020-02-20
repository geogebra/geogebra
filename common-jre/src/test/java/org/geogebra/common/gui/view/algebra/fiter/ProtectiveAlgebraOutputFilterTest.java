package org.geogebra.common.gui.view.algebra.fiter;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProtectiveAlgebraOutputFilterTest extends BaseUnitTest {

	private ProtectiveAlgebraOutputFilter filter = new ProtectiveAlgebraOutputFilter();

	@Before
	public void setUp() {
		getApp().setGraphingConfig();
	}

	@Test
	public void isAllowed() {
		GeoElement fitLine = addAvInput("FitLine((0,0),(1,1),(2,2))");
		assertThat(filter.isAllowed(fitLine), is(true));

		GeoElement line = addAvInput("Line((0,0),(1,1))");
		assertThat(filter.isAllowed(line), is(false));
	}
}