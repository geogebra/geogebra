package org.geogebra.common.main.settings.config;

import org.geogebra.common.GeoGebraConstants;

/**
 * App config for mixed reality
 *
 */
public class AppConfigMixedReality extends AppConfigDefault {

	@Override
	public String getAppTitle() {
		return "MixedReality";
	}

	@Override
	public String getTutorialKey() {
		return "";
	}

	@Override
	public boolean showKeyboardHelpButton() {
		return false;
	}

	@Override
	public boolean isSimpleMaterialPicker() {
		return true;
	}

	@Override
	public GeoGebraConstants.Version getVersion() {
		return GeoGebraConstants.Version.MIXED_REALITY;
	}

	@Override
	public int getEnforcedLineEquationForm() {
		return -1;
	}

	@Override
	public int getEnforcedConicEquationForm() {
		return -1;
	}

	@Override
	public boolean shouldHideEquations() {
		return false;
	}

	@Override
	public boolean hasLabelForDescription() {
		return true;
	}
}
