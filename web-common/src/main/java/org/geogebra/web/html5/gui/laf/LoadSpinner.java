package org.geogebra.web.html5.gui.laf;

import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Class to wrap load spinner
 *
 * @author laszlo
 */
public class LoadSpinner extends FlowPanel {

	/**
	 * Constructor to create a spinner.
	 */
	public LoadSpinner() {
		setStyleName("mk-spinner-wrap");
		FlowPanel content = new FlowPanel();
		content.setStyleName("mk-spinner-ring");
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
