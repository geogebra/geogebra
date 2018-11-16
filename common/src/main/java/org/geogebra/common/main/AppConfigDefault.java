package org.geogebra.common.main;

import org.geogebra.common.gui.toolcategorization.ToolCategorization.AppType;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.kernel.Kernel;

/**
 * Config for Classic and derived apps (MR)
 */
public class AppConfigDefault implements AppConfig {

	@Override
	public void adjust(DockPanelData dp) {
		// do nothing
	}

	@Override
	public String getAVTitle() {
		return "Algebra";
	}

	@Override
	public int getLineDisplayStyle() {
		return -1;
	}

	@Override
	public String getAppTitle() {
		return "math_apps";
	}

	@Override
	public String getAppName() {
		return getAppTitle();
	}

	@Override
	public String getAppNameShort() {
		return getAppTitle();
	}

	/**
	 * @param appName
	 *            app name
	 * @return whether app name is one of the unbundled apps
	 */
	public static boolean isUnbundledOrNotes(String appName) {
		return "graphing".equals(appName) || "geometry".equals(appName)
				|| "cas".equals(appName) || "notes".equals(appName)
				|| "3d".equals(appName) || "scientific".equals(appName);
	}

	@Override
	public String getTutorialKey() {
		return "TutorialClassic";
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
		return false;
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
		return Kernel.STANDARD_PRINT_DECIMALS_SHORT;
	}

	@Override
	public boolean hasSingleEuclidianViewWhichIs3D() {
		return false;
	}

	@Override
	public int[] getDecimalPlaces() {
		return new int[] {0, 1, 2, 3, 4, 5, 10, 15};
	}

	@Override
	public int[] getSignificantFigures() {
		return new int[] {3, 5, 10, 15};
	}

	@Override
	public boolean isGreekAngleLabels() {
		return true;
	}

	@Override
	public boolean isCASEnabled() {
		return true;
	}

	@Override
	public String getPreferencesKey() {
		return "";
	}

	@Override
	public String getForcedPerspective() {
		return null;
	}

	@Override
	public boolean hasToolsInSidePanel() {
		return false;
	}

	@Override
	public boolean hasScientificKeyboard() {
		return false;
	}

	@Override
	public boolean isEnableStructures() {
		return true;
	}

	@Override
	public AppType getToolbarType() {
		return AppType.GRAPHING_CALCULATOR;
	}

    @Override
    public boolean showGridOnFileNew() {
	    return true;
    }

    @Override
    public boolean showAxesOnFileNew() {
        return true;
    }

	@Override
	public boolean hasTableView(App app) {
		return false;
	}
}
