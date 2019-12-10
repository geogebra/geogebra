package org.geogebra.common.main.settings;

import org.geogebra.common.gui.toolcategorization.AppType;
import org.geogebra.common.kernel.commands.selector.CommandFilter;

/**
 * Config for the Suite app (currently graphing before tool removal)
 */
public class AppConfigSuite extends AppConfigGraphing {

	@Override
	public String getAppCode() {
		return "suite";
	}

	@Override
	public CommandFilter getCommandFilter() {
		return null;
	}

	@Override
	public AppType getToolbarType() {
		return AppType.SUITE;
	}
}
