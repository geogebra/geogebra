package org.geogebra.web.phone.gui.view;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.phone.PhoneLookAndFeel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

public abstract class AbstractViewPanel extends FlowPanel implements ViewPanel {


	public AbstractViewPanel(AppW app) {
		setStyleName(getViewPanelStyleName());
	}

	@Override
	public void onResize() {
		setPixelSize(Window.getClientWidth(), Window.getClientHeight()
		        - PhoneLookAndFeel.PHONE_HEADER_HEIGHT);
	}

	/**
	 * @return the style name of this panel
	 */
	protected abstract String getViewPanelStyleName();
}
