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

package org.geogebra.web.tablet.main;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.web.full.main.FileManager;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.tablet.TabletFileManager;
import org.geogebra.web.touch.gui.view.ConstructionProtocolViewT;

import elemental2.dom.DomGlobal;

public class TabletDevice implements GDevice {

	@Override
	public ConstructionProtocolView getConstructionProtocolView(AppW app) {
		return new ConstructionProtocolViewT(app);
	}

	@Override
	public FileManager createFileManager(AppW app) {
		return new TabletFileManager(app);
	}

	@Override
	public void resizeView(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isOffline(AppW app) {
		return !DomGlobal.navigator.onLine;
	}
}
