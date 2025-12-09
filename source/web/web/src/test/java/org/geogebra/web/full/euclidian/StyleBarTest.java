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

package org.geogebra.web.full.euclidian;

import static org.junit.Assert.assertTrue;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.gwtproject.user.client.ui.ComplexPanel;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub({ComplexPanel.class})
public class StyleBarTest {

	@Test
	public void updateGraphingStylebar() {
		AppWFull app = AppMocker
				.mockApplet(new AppletParameters("graphing"));
		EuclidianStyleBarW styleBar = new EuclidianStyleBarW(
				app.getActiveEuclidianView(), 1);
		checkUpdate(styleBar);
	}

	private static void checkUpdate(EuclidianStyleBarW styleBar) {
		styleBar.setOpen(true);
		styleBar.updateStyleBar();
		styleBar.updateButtons();
		// mostly implicitly asserting that we didn't crash, but visibility can be checked too
		assertTrue("Style bar should be visible", styleBar.isVisible());
	}

	@Test
	public void updateWhiteboardStylebar() {
		AppWFull app = AppMocker
				.mockApplet(new AppletParameters("notes"));
		EuclidianStyleBarW styleBar = new EuclidianStyleBarW(
				app.getActiveEuclidianView(), 1);
		checkUpdate(styleBar);
	}

}
