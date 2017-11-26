package org.geogebra.common.main;

import org.geogebra.common.io.layout.DockPanelData;

public class AppConfigDefault implements AppConfig {

	@Override
	public void adjust(DockPanelData dp) {
		// do nothing
	}

	@Override
	public String getAVTitle() {
		return "Algebra";
	}

	@Override
	public int getLineDisplayStyle() {
		return -1;
	}

}
