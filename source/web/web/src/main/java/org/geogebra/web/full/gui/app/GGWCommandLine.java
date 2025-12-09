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

package org.geogebra.web.full.gui.app;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.inputbar.AlgebraInputW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.RequiresResize;

/**
 * Wraps the input bar
 *
 */
public class GGWCommandLine extends Composite implements RequiresResize {
	
	private AlgebraInputW algebraInput;

	/**
	 * Create new input bar wrapper
	 */
	public GGWCommandLine() {
		algebraInput = new AlgebraInputW();
		initWidget(algebraInput);
	}

	/**
	 * @param app
	 *            application
	 */
	public void attachApp(App app) {
		algebraInput.init((AppW) app);
	}

	@Override
	public void onResize() {
		algebraInput.onResize();
    }

	/**
	 * @return whether input bar has focus
	 */
	public boolean hasFocus() {
		return algebraInput.hasFocus();
    }
}
