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

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.desktop.geogebra3D.App3D;
import org.geogebra.desktop.geogebra3D.gui.dialogs.options.OptionsEuclidian3DD;
import org.geogebra.desktop.gui.dialog.options.OptionPanelD;
import org.geogebra.desktop.gui.dialog.options.OptionsEuclidianD;
import org.geogebra.desktop.gui.dialog.options.OptionsEuclidianForPlaneD;
import org.geogebra.desktop.gui.view.properties.PropertiesStyleBarD;
import org.geogebra.desktop.gui.view.properties.PropertiesViewD;
import org.geogebra.desktop.main.AppD;

/**
 * Just adding 3D view for properties
 * 
 * @author mathieu
 *
 */
public class PropertiesView3DD extends PropertiesViewD {

	private OptionsEuclidianD euclidianPanel3D;
	private OptionsEuclidianForPlaneD euclidianForPlanePanel;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            application
	 */
	public PropertiesView3DD(AppD app) {
		super(app);
	}

	@Override
	public OptionPanelD getOptionPanel(OptionType type) {

		switch (type) {
		case EUCLIDIAN3D:
			if (euclidianPanel3D == null) {
				euclidianPanel3D = new OptionsEuclidian3DD((AppD) app,
						((App3D) app).getEuclidianView3D());
				euclidianPanel3D.setLabels();
			}

			return euclidianPanel3D;

		case EUCLIDIAN_FOR_PLANE:
			EuclidianView view = app.getActiveEuclidianView();
			if (!view.isViewForPlane()) {
				view = app.getViewForPlaneVisible();
			}
			if (euclidianForPlanePanel == null) {
				euclidianForPlanePanel = new OptionsEuclidianForPlaneD(
						(AppD) app, view);
				euclidianForPlanePanel.setLabels();
			} else {
				euclidianForPlanePanel.updateView(view);
				euclidianForPlanePanel.setLabels();
			}

			return euclidianForPlanePanel;
		}

		return super.getOptionPanel(type);
	}

	@Override
	public void setLabels() {

		super.setLabels();

		if (euclidianPanel3D != null) {
			euclidianPanel3D.setLabels();
		}

		if (euclidianForPlanePanel != null) {
			euclidianForPlanePanel.setLabels();
		}

	}

	@Override
	public void updateFonts() {

		if (isIniting) {
			return;
		}

		super.updateFonts();

		if (euclidianPanel3D != null) {
			euclidianPanel3D.updateFont();
		}

		if (euclidianForPlanePanel != null) {
			euclidianForPlanePanel.updateFont();
		}

	}

	@Override
	protected PropertiesStyleBarD newPropertiesStyleBar() {
		return new PropertiesStyleBar3DD(this, (AppD) app);
	}

	@Override
	public void updatePanelGUI(int id) {
		if (id == App.VIEW_EUCLIDIAN3D && euclidianPanel3D != null) {
			euclidianPanel3D.updateGUI();
		} else {
			super.updatePanelGUI(id);
		}
	}

}
