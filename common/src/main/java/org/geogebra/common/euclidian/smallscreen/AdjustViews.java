package org.geogebra.common.euclidian.smallscreen;

import org.geogebra.common.main.App;

public class AdjustViews {

	private App app;
	private boolean adjusted = false;

	public AdjustViews(App app) {
		this.app = app;
	}

	public boolean isPortait() {
		return app.getWidth() < app.getHeight();
	}

	public void apply() {
		if (app.getGuiManager() != null) {
			app.getGuiManager().getLayout().getDockManager().adjustViews();
		}
	}

	public boolean isAdjusted() {
		return adjusted;
	}

	public void setAdjusted(boolean adjusted) {
		this.adjusted = adjusted;
	}
}
