package org.geogebra.web.full.main;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.gui.view.algebra.GeoElementValueConverter;
import org.geogebra.common.gui.view.algebra.ProtectiveGeoElementValueConverter;
import org.geogebra.common.util.ToStringConverter;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class AppWFullTest {

	@Test
	public void graphingUsesProtectiveFilter() {
		ToStringConverter outputFilter =
				AppMocker.mockGraphing().getGeoElementValueConverter();
		assertThat(outputFilter, instanceOf(ProtectiveGeoElementValueConverter.class));
	}

	@Test
	public void geometryUsesNoFilter() {
		ToStringConverter outputFilter =
				AppMocker.mockGeometry().getGeoElementValueConverter();
		assertThat(outputFilter, instanceOf(GeoElementValueConverter.class));
	}

	@Test
	public void casUsesNoFilter() {
		ToStringConverter outputFilter =
				AppMocker.mockCas().getGeoElementValueConverter();
		assertThat(outputFilter, instanceOf(GeoElementValueConverter.class));
	}
}