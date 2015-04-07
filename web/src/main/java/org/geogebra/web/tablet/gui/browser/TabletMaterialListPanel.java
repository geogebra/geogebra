package org.geogebra.web.tablet.gui.browser;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.browser.MaterialListPanel;
import org.geogebra.web.web.gui.laf.GLookAndFeel;

import com.google.gwt.user.client.Window;

public class TabletMaterialListPanel extends MaterialListPanel {

	public TabletMaterialListPanel(final AppW app) {
		super(app);
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight()
		        - GLookAndFeel.BROWSE_HEADER_HEIGHT);
	}

	@Override
	public void onResize() {
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight()
		        - GLookAndFeel.BROWSE_HEADER_HEIGHT);
	}

}
