package org.geogebra.common.main.settings.updater;

public class CasSettingsUpdater extends SettingsUpdater {

	@Override
	public void resetSettingsOnAppStart() {
		super.resetSettingsOnAppStart();
		getKernel().setAngleUnit(getKernel().getApplication().getConfig().getDefaultAngleUnit());
	}
}
