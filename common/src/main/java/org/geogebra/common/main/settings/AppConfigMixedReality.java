package org.geogebra.common.main.settings;

import org.geogebra.common.main.AppConfigDefault;

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
}
