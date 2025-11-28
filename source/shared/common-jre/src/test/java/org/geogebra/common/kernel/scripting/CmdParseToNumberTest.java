package org.geogebra.common.kernel.scripting;

import static org.junit.Assert.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CmdParseToNumberTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void parseNumber() {
		GeoElement sv1 = evaluateGeoElement("sv1=22");
		evaluateGeoElement("ParseToNumber(sv1,\"s1\")");
		assertNull("Old definition should be discarded", sv1.getDefinition());
	}
}
