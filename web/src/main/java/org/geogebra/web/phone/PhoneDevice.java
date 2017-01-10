package org.geogebra.web.phone;

import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.phone.gui.view.material.BrowseView;
import org.geogebra.web.touch.main.TouchDevice;
import org.geogebra.web.web.main.FileManager;

public class PhoneDevice extends TouchDevice {
	private Phone phone;

	public PhoneDevice(Phone phone) {
		this.phone = phone;
	}
	@Override
	public FileManager createFileManager(AppW app) {
		return new FileManagerP(app);
	}

	@Override
	public BrowseViewI createBrowseView(AppW app) {
		return new BrowseView(app, phone);
	}

	public void resizeView(int width, int height) {
		// TODO Auto-generated method stub

	}

	public Phone getPhone() {
		return phone;
	}

}
