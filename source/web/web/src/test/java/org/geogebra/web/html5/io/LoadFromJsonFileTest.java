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

import java.util.concurrent.CountDownLatch;

import org.geogebra.common.gui.layout.DockManager;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.awt.JLMContext2D;
import org.geogebra.web.full.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.euclidian.EuclidianSimplePanelW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.geogebra.web.util.file.FileIO;
import org.gwtproject.user.client.ui.ResizeComposite;
import org.gwtproject.user.client.ui.RootPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub({EuclidianSimplePanelW.class,
		JLMContext2D.class, RootPanel.class, ResizeComposite.class})
public class LoadFromJsonFileTest {
	private static final String CLOSED_AV_JSON_PATH =
			"src/test/resources/org/geogebra/web/html5/io/closedAV.json";
	private static final String jsonPath =
			"src/test/resources/org/geogebra/web/html5/io/inRegion.json";

	private AppWFull app;

	@Before
	public void initAssertions() {
		this.getClass().getClassLoader().setDefaultAssertionStatus(false);
	}

	@Test
	public void checkPanelIsClosed() {
		initAppFromFile();
		final ToolbarPanel toolbarPanel = initToolbarFromApp();
		final CountDownLatch latch = new CountDownLatch(1);
		app.invokeLater(() -> {
			assertTrue(toolbarPanel == null || toolbarPanel.isClosed());
			latch.countDown();
		});
		try {
			latch.await();
		} catch (InterruptedException e) {
			Log.debug(e);
		}
	}

	private void initAppFromFile() {
		AppletParameters articleElement =
				new AppletParameters("graphing");
		String json = FileIO.load(CLOSED_AV_JSON_PATH);
		articleElement.setAttribute("json", json);
		app = AppMocker.mockApplet(articleElement);
		app.setShowToolBar(true);
	}

	private ToolbarPanel initToolbarFromApp() {
		DockManager dockManager = app.getGuiManager().getLayout().getDockManager();
		ToolbarDockPanelW toolbarDockPanel =
				(ToolbarDockPanelW) dockManager.getPanel(App.VIEW_ALGEBRA);
		return toolbarDockPanel.getToolbar();
	}
}
