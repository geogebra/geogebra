package geogebra.phone;

import geogebra.html5.js.ResourcesInjector;
import geogebra.phone.gui.PhoneGUI;
import geogebra.web.gui.app.GeoGebraAppFrame;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.googlecode.gwtphonegap.client.PhoneGap;

/**
 * @author geogebra
 *
 */
public class Phone implements EntryPoint {
	
	private static PhoneGUI phoneGui;
	private static GeoGebraAppFrame appFrame;
	private static PhoneGap phoneGap = (PhoneGap) GWT.create(PhoneGap.class);

	public static PhoneGUI getGUI() {
		return phoneGui;
	}
	
	public void onModuleLoad() {
		appFrame = new GeoGebraAppFrame();
		appFrame.init();
		phoneGui = new PhoneGUI(appFrame.app);
		phoneGap.initializePhoneGap();
		ResourcesInjector.injectResources();
		RootLayoutPanel.get().clear();
		RootLayoutPanel.get().add(phoneGui);
	}
	
	public static PhoneGap getPhoneGap() {
		return phoneGap;
	}
}