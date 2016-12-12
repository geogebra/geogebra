package org.geogebra.web.tablet;

import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.web.html5.main.ExamUtil;

/**
 * LAF for Windows Store app
 *
 */
public class TabletWinLookAndFeel extends TabletLookAndFeel {
	/**
	 * Creates new LAF for Windows Store app
	 */
	public TabletWinLookAndFeel() {
		//
	}
	@Override
	public boolean exportSupported() {
		return true;
	}
	
	@Override
	public boolean supportsLocalSave() {
		return true;
	}

	@Override
	public void toggleFullscreen(boolean full) {
		ExamUtil.toggleFullscreen(full, false);
	}

	@Override
	public String getFrameStyleName() {
		return "TabletWin";
	}

	@Override
	public Versions getVersion(int dim, boolean app) {
		return Versions.WINDOWS_STORE;
	}

	@Override
	public boolean examSupported(boolean tablet) {
		return true;
	}

}
