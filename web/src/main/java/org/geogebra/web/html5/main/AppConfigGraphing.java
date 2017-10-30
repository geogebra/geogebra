package org.geogebra.web.html5.main;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;

public class AppConfigGraphing implements AppConfig {

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

	public String getAVTitle() {
		return "Algebra";
	}

	public int getLineDisplayStyle() {
		return GeoLine.EQUATION_EXPLICIT;
	}

}
