package org.geogebra.web.full.main;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.ToStringConverter;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class AppWFullTest {

	@Before
	public void setupFormat() {
		FormatFactory.setPrototypeIfNull(new TestFormatFactory());
	}

	@Test
	public void graphingUsesProtectiveFilter() {
		AppWFull app = AppMocker.mockGraphing();
		ToStringConverter outputFilter =
				app.getGeoElementValueConverter();
		assertEquals("Ray((0, 0), (1, 1))", outputFilter.convert(addEquation(app,
				"Ray((0,0),(1,1))")));
	}

	private GeoElement addEquation(AppWFull app, String equation) {
		return app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand(equation, false)[0].toGeoElement();
	}

	@Test
	public void geometryUsesNoFilter() {
		AppWFull app = AppMocker.mockGeometry();
		ToStringConverter outputFilter = app.getGeoElementValueConverter();
		assertEquals("y = x", outputFilter.convert(addEquation(app,
				"Ray((0,0),(1,1))")));
	}

	@Test
	public void casUsesNoFilter() {
		AppWFull app = AppMocker.mockCas();
		ToStringConverter outputFilter = app.getGeoElementValueConverter();
		assertEquals("-x - y = -1.0",
				outputFilter.convert(addEquation(app, "PerpendicularBisector((0,0),(1,1))")));
	}
}