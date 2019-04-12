package org.geogebra.desktop.geogebra3D.settings;

import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.SettingsBuilder;

public class SettingsBuilder3D extends SettingsBuilder {

	public SettingsBuilder3D(App app) {
		super(app);
	}

	@Override
	protected int getEuclidianLength() {
		return 3;
	}
}
