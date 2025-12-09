/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.main;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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

