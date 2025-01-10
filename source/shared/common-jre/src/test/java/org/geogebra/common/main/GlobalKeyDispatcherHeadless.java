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
