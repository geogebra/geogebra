package org.geogebra.common.spy.settings;

import org.geogebra.common.main.settings.EuclidianSettings;

class EuclidianSettingsSpy extends EuclidianSettings {

	@Override
	public int getFileWidth() {
		return 0;
	}

	@Override
	public int getFileHeight() {
		return 0;
	}
}
