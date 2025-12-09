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

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.geogebra.common.main.App;
import org.geogebra.desktop.gui.layout.DockPanelD;
import org.geogebra.desktop.main.AppD;

/**
 * Dock panel for error of loading (used for 3D panel not supported by ggb
 * version &lt; 5.0)
 */
public class ErrorDockPanel extends DockPanelD {
	private static final long serialVersionUID = 1L;

	/**
	 * @param app application
	 * @param viewId view ID
	 */
	public ErrorDockPanel(AppD app, int viewId) {
		super(App.VIEW_NONE, // view id
				"ErrorWindow (viewId=" + viewId + ")", // view title phrase
				null, // toolbar string
				false, // style bar?
				4, // menu order
				'3' // menu shortcut
		);
		setApp(app);
	}

	@Override
	protected JComponent loadComponent() {
		return new JPanel();
	}

	@Override
	public void updatePanel() {
		if (component == null && isVisible()) {
			component = loadComponent();
			add(component, BorderLayout.CENTER);
		}
	}

	// unused methods
	@Override
	public final void setFocus(boolean hasFocus, boolean updatePropertiesView) {
		// noting to do
	}

	@Override
	public void closePanel() {
		// nothing to do
	}
}
