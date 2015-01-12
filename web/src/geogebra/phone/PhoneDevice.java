package geogebra.phone;

import geogebra.html5.gui.view.browser.BrowseViewI;
import geogebra.html5.main.AppW;
import geogebra.phone.gui.view.material.BrowseView;
import geogebra.touch.main.TouchDevice;
import geogebra.web.main.FileManager;

public class PhoneDevice extends TouchDevice {

	@Override
	public FileManager createFileManager(AppW app) {
		return new FileManagerP(app);
	}

	@Override
	public BrowseViewI createBrowseView(AppW app) {
		return new BrowseView(app);
	}

}
