package org.geogebra.web.geogebra3D.web.main.settings;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.settings.SettingsBuilderW;

public class SettingsBuilderW3D extends SettingsBuilderW {

	public SettingsBuilderW3D(App app) {
		super(app);
	}

	@Override
	protected int getEuclidianLength() {
		return 3;
	}
}
