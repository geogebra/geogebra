package org.geogebra.common.main.settings;

import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;

/**
 * Config for CAS Calculator app
 */
public class AppConfigCas extends AppConfigGraphing {

	@Override
	public String getAppTitle() {
		return "CASCalculator";
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

	@Override
	public CommandNameFilter getCommandNameFilter() {
		return null;
	}

	@Override
	public boolean hasAutomaticLabels() {
		return false;
	}

	@Override
	public boolean hasAutomaticSliders() {
		return false;
	}

	@Override
	public boolean showToolsPanel() {
		return false;
	}
}
