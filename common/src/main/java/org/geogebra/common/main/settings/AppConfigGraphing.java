package org.geogebra.common.main.settings;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;

/**
 * Config for Graphing Calculator app
 */
public class AppConfigGraphing implements AppConfig {

	@Override
	public void adjust(DockPanelData dp) {
		if (dp.getViewId() == App.VIEW_ALGEBRA) {
			dp.makeVisible();
			dp.setLocation("3");
		}
		else if (dp.getViewId() == App.VIEW_EUCLIDIAN) {
			dp.makeVisible();
			dp.setLocation("1");
		}
	}

	@Override
	public String getAVTitle() {
		return "Algebra";
	}

	@Override
	public int getLineDisplayStyle() {
		return GeoLine.EQUATION_EXPLICIT;
	}

	@Override
	public String getAppTitle() {
		return "GraphingCalculator";
	}

	@Override
	public String getAppName() {
		return "GeoGebraGraphingCalculator";
	}

	@Override
	public String getAppNameShort() {
		return "GraphingCalculator.short";
	}

	@Override
	public String getTutorialKey() {
		return "TutorialGraphing";
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

	@Override
	public boolean allowsSuggestions() {
		return true;
	}

	@Override
	public boolean shouldKeepRatioEuclidian() {
		return false;
	}

	@Override
	public int getDefaultPrintDecimals() {
		return Kernel.STANDARD_PRINT_DECIMALS;
	}

	@Override
	public boolean hasSingleEuclidianViewWhichIs3D() {
		return false;
	}
}
