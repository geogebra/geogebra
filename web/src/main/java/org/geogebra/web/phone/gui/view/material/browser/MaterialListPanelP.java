package org.geogebra.web.phone.gui.view.material.browser;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.phone.PhoneLookAndFeel;
import org.geogebra.web.web.gui.browser.MaterialListPanel;

import com.google.gwt.user.client.Window;

/**
 * @see MaterialListPanel
 */
public class MaterialListPanelP extends MaterialListPanel {
	
	/**
	 * @param app
	 *            {@link AppW}
	 */
	public MaterialListPanelP(final AppW app) {
	    super(app);
		onResize();
    }

	@Override
	public void onResize() {
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight()
		        - PhoneLookAndFeel.PHONE_HEADER_HEIGHT);
	}
}
