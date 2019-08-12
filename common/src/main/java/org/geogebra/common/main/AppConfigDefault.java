package org.geogebra.common.main;

import org.geogebra.common.gui.toolcategorization.ToolCategorization.AppType;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;

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
				|| "3d".equals(appName) || "calculator".equals(appName);
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
	public boolean hasTableView() {
		return false;
	}

	@Override
	public SymbolicMode getSymbolicMode() {
		return SymbolicMode.NONE;
	}

	@Override
	public boolean hasSlidersInAV() {
		return true;
	}

	@Override
	public boolean hasAutomaticLabels() {
		return true;
	}

	@Override
	public boolean hasAutomaticSliders() {
		return true;
	}

	@Override
	public int getDefaultAlgebraStyle() {
		return Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE;
	}

	@Override
	public String getDefaultSearchTag() {
		return "";
	}

	@Override
	public int getDefaultLabelingStyle() {
		return ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC;
	}

	@Override
	public CommandNameFilter getCommandNameFilter() {
		return null;
	}

	@Override
	public boolean showToolsPanel() {
		return true;
	}

	@Override
	public String getAppCode() {
		return "classic";
	}

}
