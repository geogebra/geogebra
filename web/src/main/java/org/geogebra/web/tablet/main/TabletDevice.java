package org.geogebra.web.tablet.main;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.tablet.TabletFileManager;
import org.geogebra.web.tablet.gui.browser.TabletBrowseGUI;
import org.geogebra.web.touch.FileManagerT;
import org.geogebra.web.touch.main.TouchDevice;
import org.geogebra.web.web.gui.browser.BrowseGUI;
import org.geogebra.web.web.main.FileManager;

public class TabletDevice extends TouchDevice {

	@Override
	public FileManager createFileManager(AppW app) {
		if (AppW.useCordova()){
			return new FileManagerT(app);
		}
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
		if (AppW.useCordova()){
			return super.isOffline(app);
		}
		return !isOnlineNative();	
	}

	private native boolean isOnlineNative() /*-{
		return $wnd.navigator.onLine;
	}-*/;
}
