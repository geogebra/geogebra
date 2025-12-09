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

package org.geogebra.web.html5.io;

import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.web.awt.JLMContext2D;
import org.geogebra.web.html5.euclidian.EuclidianSimplePanelW;
import org.geogebra.web.html5.main.AppWsimple;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.geogebra.web.util.file.FileIO;
import org.gwtproject.user.client.ui.RootPanel;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub({EuclidianSimplePanelW.class,
		JLMContext2D.class, RootPanel.class})
public class AppWSimpleLoadTest {
	private static final String jsonPath =
			"src/test/resources/org/geogebra/web/html5/io/inRegion.json";

	@Test
	public void testLoadApp() {
		AppletParameters articleElement = new AppletParameters("simple");
		String json = FileIO.load(jsonPath);
		articleElement.setAttribute("json", json);
		AppWsimple app = AppMocker.mockAppletSimple(articleElement);
		assertTrue(((GeoBoolean) app.getKernel().lookupLabel("visible")).getBoolean());
	}

}
