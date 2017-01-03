package org.geogebra.web.web.gui;

import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.HeaderPanel;

/**
 * Common code for whole screen GUIs such as material browser
 *
 */
public abstract class MyHeaderPanel extends HeaderPanel {

	private HeaderPanelDeck frame;

	/**
	 * @param frame
	 *            app frame
	 */
	public void setFrame(HeaderPanelDeck frame) {
		this.frame = frame;
	}

	/**
	 * Hide the panel and notify app frame
	 */
	public void close() {
		if (frame != null) {
			this.getApp().onBrowserClose();
			frame.hideBrowser(this);
		}

	}

	/**
	 * @return application
	 */
	public abstract AppW getApp();

}
