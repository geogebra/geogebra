package geogebra.phone;

import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.js.ResourcesInjector;
import geogebra.phone.gui.PhoneGUI;
import geogebra.touch.PhoneGapManager;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.main.AppWapplication;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.googlecode.gwtphonegap.client.event.BackButtonPressedEvent;
import com.googlecode.gwtphonegap.client.event.BackButtonPressedHandler;

/**
 * @author geogebra
 *
 */
public class Phone implements EntryPoint {
	
	private static PhoneGUI phoneGui;
	static GeoGebraAppFrame appFrame;

	public void onModuleLoad() {
		appFrame = new GeoGebraAppFrame(new GLookAndFeel());
		appFrame.init();
		appFrame.app.setFileManager(new FileManagerP());
		PhoneGapManager.initializePhoneGap();
		PhoneGapManager.getPhoneGap().getEvent().getBackButton()
		        .addBackButtonPressedHandler(new BackButtonPressedHandler() {

			        @Override
			        public void onBackButtonPressed(
			                final BackButtonPressedEvent event) {
				        goBack();
			        }
		        });
		ResourcesInjector.injectResources();
		phoneGui = new PhoneGUI((AppWapplication) appFrame.app);
		RootLayoutPanel.get().clear();
		RootLayoutPanel.get().add(phoneGui);
	}
	
	public static PhoneGUI getGUI() {
		return phoneGui;
	}
	
	public static void goBack() {
		phoneGui.showLastView();
	}

}