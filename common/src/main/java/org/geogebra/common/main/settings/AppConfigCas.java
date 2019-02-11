package org.geogebra.common.main.settings;

import org.geogebra.common.kernel.arithmetic.SymbolicMode;

/**
 * Config for CAS Calculator app
 */
public class AppConfigCas extends AppConfigGraphing {

    @Override
    public String getAppTitle() {
        return "CasCalculator";
    }

    @Override
    public String getAppName() {
		return "GeoGebraCASCalculator";
    }

    @Override
    public String getAppNameShort() {
        return "CasCalculator.short";
    }

    @Override
    public String getTutorialKey() {
        return "";
    }

	@Override
	public boolean isCASEnabled() {
		return true;
	}

	@Override
	public String getPreferencesKey() {
		return "_cas";
	}

	@Override
	public SymbolicMode getSymbolicMode() {
		return SymbolicMode.SYMBOLIC_AV;
	}

}
