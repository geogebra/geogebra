package org.geogebra.common.euclidian.smallscreen;

import org.geogebra.common.main.App;

public class AdjustViews {

	private App app;
	private boolean adjusted = false;

	public AdjustViews(App app) {
		this.app = app;
	}

	public boolean isPortrait() {
		return app.getWidth() < app.getHeight();
	}

	/**
	 * @param force
	 *            force adjustment if portrait/landscape mode did not change
	 */
	public void apply(boolean force) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().getLayout().getDockManager().adjustViews(force);
		}
	}

	public boolean isAdjusted() {
		return adjusted;
	}

	public void setAdjusted(boolean adjusted) {
		this.adjusted = adjusted;
	}
}
