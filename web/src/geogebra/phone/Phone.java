package geogebra.phone;

import geogebra.html5.js.ResourcesInjector;
import geogebra.phone.gui.PhoneGUI;
import geogebra.web.gui.app.GeoGebraAppFrame;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * @author geogebra
 *
 */
public class Phone implements EntryPoint {
	
	private static PhoneGUI phoneGui;
	private static GeoGebraAppFrame appFrame;

	public static PhoneGUI getGUI() {
		return phoneGui;
	}
	
	public void onModuleLoad() {
		appFrame = new GeoGebraAppFrame();
		appFrame.init();
		phoneGui = new PhoneGUI(appFrame.app);
		ResourcesInjector.injectResources();
		RootLayoutPanel.get().add(phoneGui);
	}
}