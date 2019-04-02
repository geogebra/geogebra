package org.geogebra.common.main.settings;

import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.main.AppConfigDefault;

/**
 * Config for Mebis Notes and GGB Notes
 *
 */
public class AppConfigNotes extends AppConfigDefault {

	private boolean mebisVendor;

	public AppConfigNotes(boolean isMebisVendor) {
		this.mebisVendor = isMebisVendor;
	}

	@Override
	public String getPreferencesKey() {
		return "_notes";
	}

	@Override
	public String getForcedPerspective() {
		return Perspective.NOTES + "";
	}

	@Override
	public String getAppTitle() {
		return mebisVendor ? "Tafel" : "Notes";
	}

}
