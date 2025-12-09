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
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.layout.DockPanelD;
import org.geogebra.desktop.gui.view.probcalculator.ProbabilityCalculatorViewD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Dock panel for the probability calculator.
 */
public class ProbabilityCalculatorDockPanel extends DockPanelD {
	private static final long serialVersionUID = 1L;
	private final AppD app;

	/**
	 * @param app application
	 */
	public ProbabilityCalculatorDockPanel(AppD app) {
		super(App.VIEW_PROBABILITY_CALCULATOR, // view id
				"ProbabilityCalculator", // view title phrase
				"0", // toolbar string - move tool only, force!
				true, // style bar?
				8, // menu order
				'P' // menu shortcut
		);

		this.app = app;
		this.setOpenInFrame(false);

	}

	@Override
	protected JComponent loadComponent() {
		return ((ProbabilityCalculatorViewD) app.getGuiManager()
				.getProbabilityCalculator()).getWrapperPanel();
	}

	@Override
	protected JComponent loadStyleBar() {
		return ((GuiManagerD) app.getGuiManager()).getProbabilityCalculator()
				.getStyleBar().getWrappedToolbar();
	}

	@Override
	public ImageIcon getIcon() {
		return app.getMenuIcon(GuiResourcesD.MENU_VIEW_PROBABILITY);
	}

}
