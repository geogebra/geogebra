package org.geogebra.common.main.settings;

import org.geogebra.common.kernel.geos.LabelType;

/**
 * Config for Scientific Calculator app
 */
public class AppConfigScientific extends AppConfigGraphing {

    @Override
    public String getAppTitle() {
        return "ScientificCalculator";
    }

    @Override
    public String getAppName() {
        return "GeoGebraScientificCalculator";
    }

    @Override
    public String getAppNameShort() {
        return "ScientificCalculator.short";
    }

    @Override
    public String getTutorialKey() {
        return "";
    }

	@Override
	public boolean allowsSuggestions() {
		return false;
	}

    @Override
    public char[] getAngleLabels() {
        return LabelType.lowerCaseLabels;
    }
}
