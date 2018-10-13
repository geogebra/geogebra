package org.geogebra.common.main;

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

	boolean isCASEnabled();

	String getPreferencesKey();

	String getForcedPerspective();

	boolean hasToolsInSidePanel();
}
