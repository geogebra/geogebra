package geogebra.tablet.main;

import geogebra.html5.main.AppW;
import geogebra.touch.WinFileManager;
import geogebra.touch.main.TouchDevice;
import geogebra.web.gui.browser.BrowseGUI;
import geogebra.web.gui.dialog.image.ImageInputDialogWin;
import geogebra.web.gui.dialog.image.UploadImageDialog;
import geogebra.web.main.FileManager;

public class TabletDeviceWin extends TouchDevice {

	@Override
	public FileManager createFileManager(AppW app) {
		return new WinFileManager(app);
	}

	@Override
	public BrowseGUI createBrowseView(AppW app) {
		return new BrowseGUI(app);
	}

	@Override
	public boolean isOffline(AppW app) {
		return !app.getNetworkOperation().isOnline();
	}

	@Override
	public UploadImageDialog getImageInputDialog(AppW app) {
		return new ImageInputDialogWin(app);
	}
}
