package org.geogebra.common.properties.impl.objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class LabelPropertyTests extends BaseAppTestSetup {
	@Test
	public void testSuccessfulConstruction() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("f(x) = x");
		assertDoesNotThrow(() -> new LabelProperty(getLocalization(), geoElement));
	}

	@Test
	public void testConstructingNotApplicableProperty() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoText geoText = evaluateGeoElement("\"\"");
		assertThrows(NotApplicablePropertyException.class,
				() -> new LabelProperty(getLocalization(), geoText));
	}
}
