package org.geogebra.common.main;

import org.geogebra.common.gui.toolcategorization.ToolCategorization.AppType;
import org.geogebra.common.io.layout.DockPanelData;

public interface AppConfig {

	void adjust(DockPanelData dp);

	String getAVTitle();

	int getLineDisplayStyle();

	String getAppTitle();

	String getAppName();

	String getAppNameShort();

	String getTutorialKey();

	boolean showKeyboardHelpButton();

	boolean showObjectSettingsFromAV();

	boolean isSimpleMaterialPicker();

	boolean hasPreviewPoints();

	boolean allowsSuggestions();

	boolean shouldKeepRatioEuclidian();

	int getDefaultPrintDecimals();

	boolean hasSingleEuclidianViewWhichIs3D();

    /**
     * @return the decimal places that this app uses.
     */
    int[] getDecimalPlaces();

    /**
     * @return the significant places that this app uses.
     */
    int[] getSignificantFigures();

    /**
     * @return the array of characters that can be used for the angle labels.
     */
	boolean isGreekAngleLabels();

	/**
	 * @return whether to allow CAS commands in AV
	 */
	boolean isCASEnabled();

	/**
	 * @return suffix for preferences (in web)
	 */
	String getPreferencesKey();

	/**
	 * @return preferred perspective ID or null if user setting should be used
	 */
	String getForcedPerspective();

	/**
	 * @return whether AV can switch to tools panel and back
	 */
	boolean hasToolsInSidePanel();

	/**
	 * @return whether to use scientific keyboard layout
	 */
	boolean hasScientificKeyboard();

	/**
	 * @return whether match structures (functions, equations, vectors) are
	 *         enabled
	 */
	boolean isEnableStructures();

	/**
	 * 
	 * @return the toolbar type of the current app.
	 */
	AppType getToolbarType();

}
