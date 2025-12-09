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
import org.geogebra.web.full.gui.view.probcalculator.ProbabilityCalculatorViewW;
import org.geogebra.web.full.main.AppWFull;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.Widget;

/**
 * @author gabor
 * 
 *         ProabilityCalculator dockpanel for Web
 *
 */
public class ProbabilityCalculatorDockPanelW extends DockPanelW {

	/**
	 * default width of this panel
	 */
	public static final int DEFAULT_WIDTH = 480;

	/**
	 * @param app
	 *            App Creates panel
	 */
	public ProbabilityCalculatorDockPanelW(AppWFull app) {
		super(App.VIEW_PROBABILITY_CALCULATOR,
				app.isSuite() ? null : "0", // toolbar string - move tool only, force!
				true);

		this.app = app;
		this.setEmbeddedSize(DEFAULT_WIDTH);
	}

	@Override
	public void onResize() {
		super.onResize();
		if (app.getGuiManager().hasProbabilityCalculator()) {
			((ProbabilityCalculatorViewW) app.getGuiManager()
					.getProbabilityCalculator()).onResize();
		}
	}

	@Override
	protected Widget loadComponent() {
		return ((ProbabilityCalculatorViewW) app.getGuiManager()
				.getProbabilityCalculator()).getWrapperPanel();
	}

	@Override
	public void setAlone(boolean isAlone) {
		setCloseButtonVisible(!isAlone);
		super.setAlone(isAlone);
	}

	@Override
	public boolean isStyleBarEmpty() {
		return true;
	}

	@Override
	public ResourcePrototype getIcon() {
		return getResources().menu_icon_probability();
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return null;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			((ProbabilityCalculatorViewW) app.getGuiManager()
					.getProbabilityCalculator()).createGeoElements();
		}
	}
}
