package org.geogebra.web.full.main;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class GraphingTest {

	@Test
	public void startApp() {
		AppW app = AppMocker.mockApplet(new AppletParameters("graphing"));
		assertThat(app.getGuiManager().toolbarHasImageMode(), equalTo(false));
	}

}
