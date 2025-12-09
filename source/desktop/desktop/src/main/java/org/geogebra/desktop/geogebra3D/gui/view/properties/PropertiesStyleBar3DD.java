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

package org.geogebra.desktop.geogebra3D.gui.view.properties;

import javax.swing.JMenuItem;

import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.desktop.gui.view.properties.PropertiesStyleBarD;
import org.geogebra.desktop.main.AppD;

/**
 * Style bar for properties view (in 3D)
 * 
 * @author mathieu
 *
 */
public class PropertiesStyleBar3DD extends PropertiesStyleBarD {

	/**
	 * constructor
	 * 
	 * @param propertiesView
	 *            properties view
	 * @param app
	 *            application
	 */
	public PropertiesStyleBar3DD(PropertiesView propertiesView, AppD app) {
		super(propertiesView, app);
	}

	@Override
	protected PropertiesButton newPropertiesButton(OptionType type) {

		return new PropertiesButton();

	}

	@Override
	protected JMenuItem newJMenuItem(OptionType type) {

		return new JMenuItem();

	}

	@Override
	public void updateGUI() {

		super.updateGUI();

		buttonMap.get(OptionType.EUCLIDIAN3D)
				.setVisible(app.getGuiManager().showView(App.VIEW_EUCLIDIAN3D));

		buttonMap.get(OptionType.EUCLIDIAN_FOR_PLANE)
				.setVisible(app.hasEuclidianViewForPlaneVisible());

	}

}
