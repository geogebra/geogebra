package org.geogebra.common.main.settings.updater;

import org.geogebra.common.kernel.Kernel;

public class CasSettingsUpdater extends SettingsUpdater {

	@Override
	public void resetSettingsOnAppStart() {
		super.resetSettingsOnAppStart();
		getKernel().setAngleUnit(Kernel.ANGLE_RADIANT);
	}
}
