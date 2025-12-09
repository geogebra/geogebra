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

package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.full.main.AppWFull;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.Widget;

public class PropertiesDockPanelW extends DockPanelW {

	private PropertiesViewW view;
	
	/**
	 * @param app
	 *            application
	 */
	public PropertiesDockPanelW(AppWFull app) {
		super(
			App.VIEW_PROPERTIES, // view id
			null, // toolbar string
			false // style bar?
		);
		
		this.app = app;
		super.setDialog(true);
		this.setShowStyleBar(true);
	}

	private void getPropertiesView() {
		view = (PropertiesViewW) app.getGuiManager().getPropertiesView();
	}

	@Override
	protected Widget loadComponent() {
		getPropertiesView();
		return view.getWrappedPanel();
	}

	@Override
	public void onResize() {
		// this hack may be temporary
		if (view != null) {
			view.onResize();
			view.repaintView();
		}
	}
	
	@Override
	public boolean isStyleBarEmpty() {
		return false;
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return null;
	}

}
