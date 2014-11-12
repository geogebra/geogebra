package geogebra.phone;

import geogebra.html5.main.AppW;
import geogebra.touch.main.TouchDevice;
import geogebra.web.gui.browser.BrowseGUI;
import geogebra.web.main.FileManager;

public class PhoneDevice extends TouchDevice {

	@Override
	public FileManager getFileManager(AppW app) {
		return new FileManagerP(app);
	}

	@Override
	public BrowseGUI getBrowseGUI(AppW app) {
		return new BrowseGUI(app);
	}

}
