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

package org.geogebra.web.full.gui.properties.ui.panel;

import static org.geogebra.common.properties.PropertyView.*;

import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.gwtproject.user.client.ui.FlowPanel;

public class ActionableButtonPanel extends FlowPanel {

	/**
	 * Create the actionable button panel
	 * @param actionableButtonRow {@link ActionableButtonRow}
	 */
	public ActionableButtonPanel(ActionableButtonRow actionableButtonRow) {
		addStyleName("actionableButtonPanel");
		buildGUI(actionableButtonRow);
	}

	private void buildGUI(ActionableButtonRow actionableButtonRow) {
		for (int i = 0; i < actionableButtonRow.count(); i++) {
			StandardButton button = new StandardButton(actionableButtonRow.getLabel(i));
			button.addStyleName(actionableButtonRow.getStyleName(i));
			int finalI = i;
			button.addFastClickHandler(source -> actionableButtonRow.performAction(finalI));
			add(button);
		}
	}
}
