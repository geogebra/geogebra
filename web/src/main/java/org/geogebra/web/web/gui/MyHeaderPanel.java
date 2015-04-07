package org.geogebra.web.web.gui;

import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.HeaderPanel;

public abstract class MyHeaderPanel extends HeaderPanel {

	private HeaderPanelDeck frame;

	public void setFrame(HeaderPanelDeck frame) {
		this.frame = frame;
	}

	public void close() {
		if (frame != null) {
			this.getApp().onBrowserClose();
			frame.hideBrowser(this);
		}

	}

	public abstract AppW getApp();

}
