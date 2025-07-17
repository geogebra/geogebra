package org.geogebra.web.full.main;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jsinterop.base.JsPropertyMap;

@RunWith(GgbMockitoTestRunner.class)
public class GraphingTest {

	private AppWFull app;

	@Before
	public void setup() {
		FormatFactory.setPrototypeIfNull(new TestFormatFactory());
		app = AppMocker.mockApplet(new AppletParameters("graphing"));
	}

	@Test
	public void startApp() {
		assertThat(app.getGuiManager().toolbarHasImageMode(), equalTo(false));
	}

	@Test
	public void equationFormInitialized() {
		GeoElementND line = app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand("2x + 4 = 6", false)[0];
		assertEquals("2x + 4 = 6", line.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	public void noEquationDragging() {
		assertTrue("should not allow dragging equations",
				app.getSettings().getAlgebra().isEquationChangeByDragRestricted());
	}

	@Test
	public void syntaxesShouldBeFiltered() {
		AppMocker.mockLocalization(key ->
				"Invert.Syntax".equals(key) ? "[ <function> ]\n[ <matrix> ]" : key);
		assertEquals(1, app.getAutocompleteProvider()
				.getSyntaxes("Invert").size());
	}

	@Test
	public void openKeyboardShouldNotInitializeOpenFileView() {
		app.showKeyboard(((DockPanelW) app.getLayout()
				.getDockManager().getPanel(App.VIEW_ALGEBRA)).getKeyboardListener(), true);
		assertThat(app.getGuiManager().isOpenFileViewLoaded(), equalTo(false));
	}

}

