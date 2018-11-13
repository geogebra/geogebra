package org.geogebra.web.tablet;

import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.web.full.gui.exam.ExamUtil;

/**
 * LAF for Windows Store app
 *
 */
public class TabletWinLookAndFeel extends TabletLookAndFeel {
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
		ExamUtil.toggleFullscreen(full);
	}

	@Override
	public String getFrameStyleName() {
		return "TabletWin";
	}

	@Override
	public Versions getVersion(int dim, String appName) {
		return Versions.WINDOWS_STORE;
	}

	@Override
	public boolean examSupported() {
		return true;
	}

}
