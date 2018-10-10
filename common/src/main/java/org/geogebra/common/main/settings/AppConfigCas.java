package org.geogebra.common.main.settings;

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
        return "GeoGebraCasCalculator";
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

}
