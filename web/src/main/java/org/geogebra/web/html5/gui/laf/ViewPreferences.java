package org.geogebra.web.html5.gui.laf;

/**
 * View preferences that are vendor dependent
 */
public class ViewPreferences {

	private boolean isMobileFullScreenButtonEnabled;

	/**
	 * @return Whether the full screen button should be shown on mobile screens
	 */
	public boolean isMobileFullScreenButtonEnabled() {
		return isMobileFullScreenButtonEnabled;
	}

	/**
	 * @param mobileFullScreenButtonEnabled
	 * 				Whether the full screen button should be shown on mobile screens
	 */
	void setMobileFullScreenButtonEnabled(boolean mobileFullScreenButtonEnabled) {
		isMobileFullScreenButtonEnabled = mobileFullScreenButtonEnabled;
	}
}
