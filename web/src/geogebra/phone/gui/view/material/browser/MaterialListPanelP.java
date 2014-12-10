package geogebra.phone.gui.view.material.browser;

import geogebra.html5.main.AppW;
import geogebra.web.gui.browser.MaterialListElement;
import geogebra.web.gui.browser.MaterialListPanel;
import geogebra.web.gui.laf.GLookAndFeel;

import com.google.gwt.user.client.Window;

public class MaterialListPanelP extends MaterialListPanel {
	
	public MaterialListPanelP(final AppW app) {
	    super(app);
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight() - GLookAndFeel.PHONE_HEADER_HEIGHT);
    }

	@Override
	public void onResize() {
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight() - GLookAndFeel.PHONE_HEADER_HEIGHT);
		for (final MaterialListElement elem : this.materials) {
			elem.onResize();
		}
	}
}
