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

package org.geogebra.web.geogebra3D.web.gui.view.properties;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.gui.dialog.options.OptionPanelW;
import org.geogebra.web.full.gui.dialog.options.OptionsEuclidianW;
import org.geogebra.web.full.gui.properties.PropertiesStyleBarW;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.geogebra3D.web.gui.dialog.options.OptionsEuclidian3DW;
import org.geogebra.web.geogebra3D.web.gui.dialog.options.OptionsEuclidianForPlaneW;
import org.geogebra.web.html5.main.AppW;

/**
 * Just adding 3D view for properties
 * 
 * @author mathieu
 *
 */
public class PropertiesView3DW extends PropertiesViewW {

	private OptionsEuclidianW euclidianPanel3D;
	private OptionsEuclidianW euclidianForPlanePanel;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            application
	 * @param op
	 *            selected option on start
	 */
	public PropertiesView3DW(AppW app, OptionType op) {
		super(app, op);
	}

	@Override
	public OptionPanelW getOptionPanel(OptionType type, int subType) {
		switch (type) {
		case EUCLIDIAN3D:
			if (euclidianPanel3D == null) {
				euclidianPanel3D = new OptionsEuclidian3DW((AppW) app,
						app.getEuclidianView3D());
				euclidianPanel3D.setLabels();
				euclidianPanel3D.setView((EuclidianView3DW) app
						.getEuclidianView3D());
			}

			return euclidianPanel3D;

		case EUCLIDIAN_FOR_PLANE:
			EuclidianView view = app.getActiveEuclidianView();
			if (!view.isViewForPlane()) {
				view = app.getViewForPlaneVisible();
			}
			if (euclidianForPlanePanel == null) {
				euclidianForPlanePanel = new OptionsEuclidianForPlaneW(
						(AppW) app, view);
				euclidianForPlanePanel.setLabels();
			} else {
				euclidianForPlanePanel.updateView(view);
				euclidianForPlanePanel.setLabels();
			}

			return euclidianForPlanePanel;
		default:
			return super.getOptionPanel(type, subType);
		}
	}

	@Override
	protected PropertiesStyleBarW newPropertiesStyleBar() {
		return new PropertiesStyleBar3DW(this, (AppW) app);
	}

}
