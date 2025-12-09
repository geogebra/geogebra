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

package org.geogebra.web.full.gui.menubar.action;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for Undo with multiple slides
 *
 * @author Zbynek
 *
 */
@RunWith(GgbMockitoTestRunner.class)
public class ClearAllActionTest {

	private static AppWFull app;

	/**
	 * Undo / redo with a single slide.
	 */
	@Test
	public void fileNew() {
		app = AppMocker
				.mockApplet(new AppletParameters("notes")
						.setAttribute("vendor", "bycs"));
		ClearAllAction action = new ClearAllAction(true);
		addObject("x");
		app.getSettings().getEuclidian(1).setBackground(GColor.PURPLE);
		action.execute(app);
		app.getSaveController().cancel();
		assertThat(app.getKernel().getConstruction()
				.getGeoSetConstructionOrder().size(), equalTo(0));
		assertThat(app.isSaved(), equalTo(true));
		EuclidianSettings euclidianSettings = app.getSettings().getEuclidian(1);
		// MOW-1259, MOW-1249
		assertEquals(GColor.WHITE, euclidianSettings.getBackground());
		assertFalse("Should not show grid", euclidianSettings.getShowGrid());
	}

	private static void addObject(String string) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(string,
				true);
	}
}
