package org.geogebra.web.html5.main;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;

public class AppConfigGeometry implements AppConfig {

	@Override
	public void adjust(DockPanelData dp) {
		if (dp.getViewId() == App.VIEW_ALGEBRA) {
			dp.makeVisible();
			dp.setLocation("3");
		}
		else if (dp.getViewId() == App.VIEW_EUCLIDIAN) {
			dp.makeVisible();
			dp.setLocation("1");
		}

	}

	@Override
	public String getAVTitle() {
		return "Steps";
	}

	@Override
	public int getLineDisplayStyle() {
		return -1;
	}

}
