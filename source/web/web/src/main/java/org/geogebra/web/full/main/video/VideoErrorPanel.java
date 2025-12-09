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

package org.geogebra.web.full.main.video;

import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.util.PersistablePanel;
import org.gwtproject.user.client.ui.Label;

/**
 * Panel to display message is video is not available
 *
 * @author Laszlo
 */
public class VideoErrorPanel extends PersistablePanel {
	private final Localization loc;
	private Label error;
	private String errorId;

	/**
	 * Constructor
	 */
	VideoErrorPanel(Localization loc, String errorId) {
		this.loc = loc;
		this.errorId = errorId;
		createGUI();
		stylePanel();
		setErrorMessage();
	}

	private void createGUI() {
		error = new Label();
		add(error);
	}

	private void stylePanel() {
		setWidth("100%");
		setHeight("100%");
		addStyleName("mowWidget");
		addStyleName("error");
	}

	private void setErrorMessage() {
		error.setText(loc.getError(errorId));
	}
}
