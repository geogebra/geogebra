package org.geogebra.common.main;

import org.geogebra.common.io.layout.DockPanelData;

public class AppConfigDefault implements AppConfig {

	public void adjust(DockPanelData dp) {
		// do nothing
	}

	public String getAVTitle() {
		return "Algebra";
	}

	public int getLineDisplayStyle() {
		return -1;
	}

}
