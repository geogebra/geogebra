package org.geogebra.common.main.settings;

import org.geogebra.common.main.App;

public class SettingsBuilder3D extends SettingsBuilder {

	public SettingsBuilder3D(App app) {
		super(app);
	}

	@Override
	protected int getEuclidianLength() {
		return 3;
	}
}
