package geogebra.phone;

import geogebra.html5.js.ResourcesInjector;
import geogebra.phone.gui.PhoneGUI;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * @author geogebra
 *
 */
public class Phone implements EntryPoint {
	
	private PhoneGUI phoneGui;

	@Override
    public void onModuleLoad() {
		ResourcesInjector.injectResources();
		this.phoneGui = new PhoneGUI();
		
		RootLayoutPanel.get().add(this.phoneGui);
	}

}
