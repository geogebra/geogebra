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

package org.geogebra.common.euclidian.smallscreen;

import org.geogebra.common.main.App;

public class AdjustViews {

	private App app;
	private boolean adjusted = false;

	public AdjustViews(App app) {
		this.app = app;
	}

	public boolean isPortrait() {
		return app.getWidth() < app.getHeight();
	}

	/**
	 * @param force
	 *            force adjustment if portrait/landscape mode did not change
	 */
	public void apply(boolean force) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().getLayout().getDockManager().adjustViews(force);
		}
	}

	public boolean isAdjusted() {
		return adjusted;
	}

	public void setAdjusted(boolean adjusted) {
		this.adjusted = adjusted;
	}
}
