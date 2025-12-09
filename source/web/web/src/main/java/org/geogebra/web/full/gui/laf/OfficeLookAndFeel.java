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

package org.geogebra.web.full.gui.laf;

import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.main.App;
import org.geogebra.web.shared.SignInController;

/**
 * @author geogebra
 * Look and Feel for SMART
 *
 */
public class OfficeLookAndFeel extends SmartLookAndFeel {
	
	@Override
	public boolean undoRedoSupported() {
		return true;
	}
	
	@Override
	public boolean isSmart() {
		return false;
	}

	@Override
	public String getType() {
		return "office";
	}

	@Override
	public SignInController getSignInController(App app) {
		return new SignInController(app, 2000, null);
    }

	@Override
	public Platform getPlatform(int dim, String appName) {
		return Platform.POWERPOINT;
	}
}
