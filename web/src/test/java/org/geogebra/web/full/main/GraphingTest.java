package org.geogebra.web.full.main;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class GraphingTest {

	private AppW app;

	@Before
	public void setup() {
		app = AppMocker.mockApplet(new AppletParameters("graphing"));
	}

	@Test
	public void startApp() {
		assertThat(app.getGuiManager().toolbarHasImageMode(), equalTo(false));
	}

	@Test
	public void openKeyboardShouldNotInitializeOpenFileView() {
		app.showKeyboard(((DockPanelW) app.getLayout()
				.getDockManager().getPanel(App.VIEW_ALGEBRA)).getKeyboardListener(), true);
		assertThat(app.getGuiManager().isOpenFileViewLoaded(), equalTo(false));
	}

}
