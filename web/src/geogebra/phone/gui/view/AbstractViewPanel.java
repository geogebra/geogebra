package geogebra.phone.gui.view;

import geogebra.html5.main.AppW;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

public abstract class AbstractViewPanel extends FlowPanel implements ViewPanel {

	protected AppW app;

	public AbstractViewPanel(AppW app) {
		this.app = app;
		setStyleName(getViewPanelStyleName());
		onResize();
	}

	public void onResize() {
		setPixelSize(Window.getClientWidth(), Window.getClientHeight() - 43);
	}

	/**
	 * @return the style name of this panel
	 */
	protected abstract String getViewPanelStyleName();
}
