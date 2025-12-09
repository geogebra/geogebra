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

package org.geogebra.common.main;

import java.util.List;

import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoElement;

public class GlobalKeyDispatcherHeadless extends GlobalKeyDispatcher {

	public GlobalKeyDispatcherHeadless(AppCommon app) {
		super(app);
	}

	@Override
	protected void createNewWindow() {
		// no UI
	}

	@Override
	protected void showPrintPreview(App app2) {
		// no UI
	}

	@Override
	protected boolean handleCtrlShiftN(boolean isAltDown) {
		return false;
	}

	@Override
	protected void copyDefinitionsToInputBarAsList(List<GeoElement> geos) {
		// no UI
	}
}
