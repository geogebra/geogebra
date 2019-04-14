package org.geogebra.common.main.settings.updater;

import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoLine;

public class GraphingSettingsUpdater extends SettingsUpdater {

	@Override
	public void resetSettingsAfterClearAll() {
		super.resetSettingsAfterClearAll();
		setExplicitEquationModeForDefaultLine();
	}

	private void setExplicitEquationModeForDefaultLine() {
		ConstructionDefaults defaults = getKernel().getConstruction().getConstructionDefaults();
		GeoLine line = (GeoLine) defaults.getDefaultGeo(ConstructionDefaults.DEFAULT_LINE);
		line.setMode(GeoLine.EQUATION_EXPLICIT);
	}
}
