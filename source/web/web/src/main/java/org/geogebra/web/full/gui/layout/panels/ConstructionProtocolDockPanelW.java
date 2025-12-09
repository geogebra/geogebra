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
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolViewW;
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolViewW.ConsProtocolScrollPanel;
import org.geogebra.web.full.main.AppWFull;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.Panel;

public class ConstructionProtocolDockPanelW extends NavigableDockPanelW {

	/**
	 * @param app
	 *            application
	 */
	public ConstructionProtocolDockPanelW(AppWFull app) {
		super(App.VIEW_CONSTRUCTION_PROTOCOL, null, false);
		this.app = app;
		this.setShowStyleBar(true);
		this.setEmbeddedSize(300);
	}

	@Override
    public ResourcePrototype getIcon() {
		return getResources().menu_icon_construction_protocol();
	}

	@Override
	protected Panel getViewPanel() {
		return ((ConstructionProtocolViewW) app.getGuiManager()
				.getConstructionProtocolView()).getOuterScrollPanel();
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return getResources().styleBar_ConstructionProtocol();
	}

	@Override
	public void resizeContent(Panel content) {
		super.resizeContent(content);
		if (content instanceof ConsProtocolScrollPanel) {
			((ConsProtocolScrollPanel) content).onResize();
		}
	}
}
