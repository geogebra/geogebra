package org.geogebra.web.full.gui;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.HeaderPanel;

/**
 * Common code for whole screen GUIs such as material browser
 */
public abstract class MyHeaderPanel extends HeaderPanel implements SetLabels {

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
			frame.hidePanel(this);
		}
	}

	/**
	 * @return application
	 */
	public abstract AppW getApp();

	/**
	 * @param width
	 *            new width (pixels)
	 * @param height
	 *            new height (pixels)
	 */
	public abstract void resizeTo(int width, int height);

}
