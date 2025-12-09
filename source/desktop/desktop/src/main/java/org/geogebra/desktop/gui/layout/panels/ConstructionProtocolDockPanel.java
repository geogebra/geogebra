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

package org.geogebra.desktop.gui.layout.panels;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.geogebra.common.main.App;
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolViewD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

public class ConstructionProtocolDockPanel extends NavigableDockPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * @param app application
	 */
	public ConstructionProtocolDockPanel(AppD app) {
		super(App.VIEW_CONSTRUCTION_PROTOCOL, // view id
				"ConstructionProtocol", // view title phrase
				null, // toolbar string
				true, // style bar?
				7, // menu order
				'L' // ctrl-shift-L
		);

		setApp(app);
		this.setShowStyleBar(true);
	}

	@Override
	protected JComponent getViewPanel() {
		return ((ConstructionProtocolViewD) (app.getGuiManager()
				.getConstructionProtocolView())).getCpPanel();
	}

	@Override
	protected JComponent loadStyleBar() {
		return ((ConstructionProtocolViewD) app.getGuiManager()
				.getConstructionProtocolView()).getStyleBar();
	}

	@Override
	public ImageIcon getIcon() {
		return app.getMenuIcon(GuiResourcesD.MENU_VIEW_CONSTRUCTION_PROTOCOL);
	}
}
