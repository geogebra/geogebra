package org.geogebra.web.full.main;

import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class CasTest {

	@Test
	public void startApp() {
		AppMocker.mockApplet(new AppletParameters("cas"));
	}

}
