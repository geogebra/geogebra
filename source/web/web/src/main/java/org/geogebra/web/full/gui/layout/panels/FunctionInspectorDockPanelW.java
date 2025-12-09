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
import org.geogebra.web.full.main.AppWFull;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.Widget;

/**
 * @author gabor
 * 
 * FunctionInspector dockpanel for Web
 *
 */
public class FunctionInspectorDockPanelW extends DockPanelW {
	
	/**
	 * default width of this panel
	 */
	public static final int DEFAULT_WIDTH = 480;

	/**
	 * @param app App
	 * Creates panel
	 */
	public FunctionInspectorDockPanelW(AppWFull app) {
		super(App.VIEW_FUNCTION_INSPECTOR, // view id
				null, // toolbar string
				true); // style bar?
		this.app = app;
		this.setEmbeddedSize(DEFAULT_WIDTH);
	}

	@Override
	protected Widget loadComponent() {
		return app.getDialogManager().getFunctionInspector().getWrappedPanel();
	}

	@Override
	protected Widget loadStyleBar() {
		return null;
	}
	
	@Override
	public boolean isStyleBarEmpty() {
		return true;
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return null;
	}

}
