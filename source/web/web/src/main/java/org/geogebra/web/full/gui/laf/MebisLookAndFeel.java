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

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.browser.MebisSignInController;
import org.geogebra.web.html5.gui.laf.SignInControllerI;

/**
 * LAF for Mebis environment
 */
public class MebisLookAndFeel extends GLookAndFeel {

	@Override
	public SignInControllerI getSignInController(App app) {
		return new MebisSignInController();
	}

	@Override
	public boolean hasLoginButton() {
		return false;
	}

}
