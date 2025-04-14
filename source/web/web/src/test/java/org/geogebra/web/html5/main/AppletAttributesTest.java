package org.geogebra.web.html5.main;

import static org.junit.Assert.assertNotNull;

import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class AppletAttributesTest {

	@Test
	public void testNullAppletParameters() {
		AppletParameters params = new AppletParameters("classic");
		params.setAttribute("appName", null);
		AppW app = AppMocker.mockApplet(params);
		assertNotNull(app.getKernel());
	}
}
