package org.geogebra.web.html5.gui.laf;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Class to wrap load spinner
 *
 * @author laszlo
 */
public class LoadSpinner extends FlowPanel {

	/**
	 * Constructor to create a spinner.
	 * @param isMebis - true if mebis tafel
	 */
	public LoadSpinner(boolean isMebis) {
		setStyleName("mk-spinner-wrap");
		FlowPanel content = new FlowPanel();
		content.setStyleName("mk-spinner-ring");
		if (isMebis) {
			content.addStyleName("mebis");
		}
		add(content);
	}

	/**
	 * Show spinner.
	 */
	public void show() {
		setVisible(true);
	}

	/**
	 * Hide spinner.
	 */
	public void hide() {
		setVisible(false);
	}
}
