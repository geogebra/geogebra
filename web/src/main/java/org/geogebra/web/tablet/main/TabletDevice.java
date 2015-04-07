package org.geogebra.web.tablet.main;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.tablet.TabletFileManager;
import org.geogebra.web.tablet.gui.browser.TabletBrowseGUI;
import org.geogebra.web.touch.main.TouchDevice;
import org.geogebra.web.web.gui.browser.BrowseGUI;
import org.geogebra.web.web.main.FileManager;

public class TabletDevice extends TouchDevice {

	@Override
	public FileManager createFileManager(AppW app) {
		return new TabletFileManager(app);
	}

	@Override
	public BrowseGUI createBrowseView(AppW app) {
		return new TabletBrowseGUI(app);
	}
}
