package geogebra.phone.gui.view.material.browser;

import geogebra.html5.main.AppW;
import geogebra.phone.PhoneLookAndFeel;
import geogebra.web.gui.browser.MaterialListPanel;

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
