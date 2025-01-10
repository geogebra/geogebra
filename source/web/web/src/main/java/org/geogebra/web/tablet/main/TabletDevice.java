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
