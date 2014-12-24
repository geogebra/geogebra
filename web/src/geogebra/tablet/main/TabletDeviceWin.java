package geogebra.tablet.main;

import geogebra.html5.main.AppW;
import geogebra.tablet.gui.browser.TabletBrowseGUI;
import geogebra.touch.WinFileManager;
import geogebra.touch.main.TouchDevice;
import geogebra.web.gui.browser.BrowseGUI;
import geogebra.web.gui.dialog.image.ImageInputDialog;
import geogebra.web.gui.dialog.image.UploadImageDialog;
import geogebra.web.main.FileManager;

public class TabletDeviceWin extends TouchDevice {

	@Override
	public FileManager getFileManager(AppW app) {
		return new WinFileManager(app);
	}

	@Override
	public BrowseGUI getBrowseGUI(AppW app) {
		return new TabletBrowseGUI(app);
	}

	@Override
	public boolean isOffline(AppW app) {
		return !app.getNetworkOperation().isOnline();
	}

	@Override
	public UploadImageDialog getImageInputDialog(AppW app) {
		return new ImageInputDialog(app);
	}
}
