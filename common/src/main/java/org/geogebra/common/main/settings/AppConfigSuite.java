package org.geogebra.common.main.settings;

import org.geogebra.common.kernel.geos.properties.FillType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
}
