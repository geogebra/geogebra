package org.geogebra.common.spy.settings;

import org.geogebra.common.main.settings.Settings;

public class SettingsSpy extends Settings {

	public SettingsSpy() {
		euclidianSettings = new EuclidianSettingsSpy[1];
		euclidianSettings[0] = new EuclidianSettingsSpy();
	}

}
