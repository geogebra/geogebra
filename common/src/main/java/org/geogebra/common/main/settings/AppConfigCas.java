package org.geogebra.common.main.settings;

import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilterFactory;

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
		return "cas_tutorials";
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
		return CommandNameFilterFactory.createCasCommandNameFilter();
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

	@Override
	public String getAppCode() {
		return "cas";
	}
}
