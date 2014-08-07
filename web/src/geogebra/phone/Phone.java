package geogebra.phone;

import geogebra.html5.js.ResourcesInjector;
import geogebra.phone.gui.PhoneGUI;
import geogebra.touch.PhoneGapManager;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.main.AppWapplication;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * @author geogebra
 *
 */
public class Phone implements EntryPoint {
	
	private static PhoneGUI phoneGui;
	static GeoGebraAppFrame appFrame;

	public void onModuleLoad() {
		appFrame = new GeoGebraAppFrame();
		appFrame.init();
		appFrame.app.setFileManager(new FileManagerP());
		phoneGui = new PhoneGUI((AppWapplication) appFrame.app);
		PhoneGapManager.initializePhoneGap();
		ResourcesInjector.injectResources();
		RootLayoutPanel.get().clear();
		RootLayoutPanel.get().add(phoneGui);
	}
	
	public static PhoneGUI getGUI() {
		return phoneGui;
	}
}