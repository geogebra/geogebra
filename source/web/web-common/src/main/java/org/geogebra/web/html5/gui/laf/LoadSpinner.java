/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
