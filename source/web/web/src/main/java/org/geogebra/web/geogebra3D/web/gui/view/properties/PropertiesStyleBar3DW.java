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

import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.gui.properties.PropertiesStyleBarW;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.resources.client.ResourcePrototype;

/**
 * Style bar for properties view (in 3D)
 * 
 * @author mathieu
 *
 */
public class PropertiesStyleBar3DW extends PropertiesStyleBarW {

	/**
	 * constructor
	 * 
	 * @param propertiesView
	 *            properties view
	 * @param app
	 *            application
	 */
	public PropertiesStyleBar3DW(PropertiesViewW propertiesView, AppW app) {
		super(propertiesView, app);
	}

	@Override
	protected ResourcePrototype getTypeIcon(OptionType type) {
		SvgPerspectiveResources pr = SvgPerspectiveResources.INSTANCE;

		switch (type) {
		case EUCLIDIAN3D:
			return pr.menu_icon_graphics3D_transparent();
		case EUCLIDIAN_FOR_PLANE:
			return pr.menu_icon_graphics_extra();
		default:
			return super.getTypeIcon(type);
		}
	}

	@Override
	public void updateGUI() {

		super.updateGUI();

		setButtonVisible(OptionType.EUCLIDIAN3D,
				app.getGuiManager().showView(App.VIEW_EUCLIDIAN3D));

		setButtonVisible(OptionType.EUCLIDIAN_FOR_PLANE,
				app.hasEuclidianViewForPlaneVisible());

	}

	@Override
	protected boolean typeAvailable(OptionType type) {
		return true;
	}

}
