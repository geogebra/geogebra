package org.geogebra.common.main.settings;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;

/**
 * App-specific behaviors of Geometry app
 * 
 * @author Zbynek
 *
 */
public class AppConfigGeometry implements AppConfig {

	@Override
	public void adjust(DockPanelData dp) {
		if (dp.getViewId() == App.VIEW_ALGEBRA) {
			dp.makeVisible();
			dp.setLocation("3");
			dp.setToolMode(true);
		}
		else if (dp.getViewId() == App.VIEW_EUCLIDIAN) {
			dp.makeVisible();
			dp.setLocation("1");
		}

	}

	@Override
	public String getAVTitle() {
		return "Steps";
	}

	@Override
	public int getLineDisplayStyle() {
		return -1;
	}

	@Override
	public String getAppTitle() {
		return "Perspective.Geometry";
	}

	@Override
	public String getTutorialKey() {
		return "TutorialGeometry";
	}

	@Override
	public boolean showKeyboardHelpButton() {
		return true;
	}

	@Override
	public boolean showObjectSettingsFromAV() {
		return true;
	}

	@Override
	public boolean isSimpleMaterialPicker() {
		return false;
	}

	@Override
	public boolean hasPreviewPoints() {
		return true;
	}

}
