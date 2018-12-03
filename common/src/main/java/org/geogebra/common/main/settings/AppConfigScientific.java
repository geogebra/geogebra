package org.geogebra.common.main.settings;

import org.geogebra.common.io.layout.Perspective;

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
	public boolean isGreekAngleLabels() {
		return false;
    }

	@Override
	public String getForcedPerspective() {
		return Perspective.SCIENTIFIC + "";
	}

	@Override
	public boolean hasToolsInSidePanel() {
		return false;
	}

	@Override
	public boolean hasScientificKeyboard() {
		return true;
	}

	@Override
	public boolean isEnableStructures() {
		return false;
	}

	@Override
	public boolean hasSlidersInAV() {
		return false;
	}
}
