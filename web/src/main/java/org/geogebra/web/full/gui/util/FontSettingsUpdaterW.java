package org.geogebra.web.full.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.updater.FontSettingsUpdater;
import org.geogebra.web.richtext.impl.CarotaUtil;

public class FontSettingsUpdaterW extends FontSettingsUpdater {

	public FontSettingsUpdaterW(App app) {
		super(app);
	}

	@Override
	public void setAppFontSize(int fontSize) {
		super.setAppFontSize(fontSize);
		if (getApp().isWhiteboardActive()) {
			CarotaUtil.setDefaultFontSize(fontSize);
		}
	}
}
