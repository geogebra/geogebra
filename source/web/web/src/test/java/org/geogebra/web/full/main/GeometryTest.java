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
public class GeometryTest {

	@Test
	public void startApp() {
		AppletParameters params = new AppletParameters("geometry");
		params.setAttribute("showToolBar" , "true");
		AppW app = AppMocker.mockApplet(params);
		assertThat(app.getGuiManager().toolbarHasImageMode(), equalTo(true));
	}

}
