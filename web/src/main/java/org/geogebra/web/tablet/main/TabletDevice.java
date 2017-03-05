package org.geogebra.web.tablet.main;

import org.geogebra.common.main.Feature;
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

	@Override
	public void resizeView(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isOffline(AppW app) {
		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)) {
			return !isOnlineNative();
		}
		return super.isOffline(app);
	}

	private native boolean isOnlineNative() /*-{
		return $wnd.navigator.onLine;
	}-*/;
}
