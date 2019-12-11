package org.geogebra.common.main.settings;

import org.geogebra.common.kernel.geos.properties.FillType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
	public Set<FillType> getAvailableFillTypes() {
		return new HashSet<>(Arrays.asList(FillType.values()));
	}

	@Override
	public boolean isObjectDraggingRestricted() {
		return false;
	}

	@Override
	public CommandFilter getCommandFilter() {
		return null;
	}

	@Override
	public AppType getToolbarType() {
		return AppType.SUITE;
	}

	@Override
	public int getEnforcedLineEquationForm() {
		return -1;
	}

	@Override
	public int getEnforcedConicEquationForm() {
		return -1;
	}
}
