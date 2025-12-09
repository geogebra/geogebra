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

package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Settings for CAS in HTML5
 *
 */
public class OptionsCASW implements OptionPanelW {

	private AppW app;
	private FlowPanel optionsPanel;
	private ComponentCheckbox showRoots;
	private ComponentCheckbox showNavigation;

	/**
	 * @param app - app
	 */
	public OptionsCASW(AppW app) {
		this.app = app;
		createGUI();
    }

	private void createGUI() {
		showRoots = new ComponentCheckbox(app.getLocalization(), false,
				"CASShowRationalExponentsAsRoots", selected -> {
			app.getSettings().getCasSettings().setShowExpAsRoots(selected);
			updateGUI();
		});

		showNavigation = new ComponentCheckbox(app.getLocalization(), false, "NavigationBar",
				selected -> {
			app.toggleShowConstructionProtocolNavigation(App.VIEW_CAS);
			updateGUI();
		});

		optionsPanel = new FlowPanel();
		optionsPanel.addStyleName("propertiesPanel");
		optionsPanel.addStyleName("simplePropertiesPanel");

		optionsPanel.add(showRoots);
		optionsPanel.add(showNavigation);

		setLabels();
		updateGUI();
	}

	/**
	 * Update the language
	 */
	public void setLabels() {
		showRoots.setLabels();
		showNavigation.setLabels();
	}

	@Override
	public void updateGUI() {
		showRoots.setSelected(app.getSettings().getCasSettings()
				.getShowExpAsRoots());
		showNavigation.setSelected(app.showConsProtNavigation(App.VIEW_CAS));
    }

	@Override
	public Widget getWrappedPanel() {
		return optionsPanel;
    }

	@Override
	public void onResize(int height, int width) {
		// TODO Auto-generated method stub
	}

	@Override
	public MultiRowsTabPanel getTabPanel() {
		// TODO Auto-generated method stub
		return null;
	}
}
