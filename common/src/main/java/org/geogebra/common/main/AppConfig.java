package org.geogebra.common.main;

import org.geogebra.common.gui.toolcategorization.ToolCategorization.AppType;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;

public interface AppConfig {

	void adjust(DockPanelData dp);

	String getAVTitle();

	int getLineDisplayStyle();

	String getAppTitle();

	String getAppName();

	String getAppNameShort();

	String getTutorialKey();

	boolean showKeyboardHelpButton();

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

    /**
     *
     * @return true if grid is shown at start on the active (main) euclidian view
     */
	boolean showGridOnFileNew();

    /**
     *
     * @return true if axes are shown at start on the active (main) euclidian view
     */
    boolean showAxesOnFileNew();

	/**
	 * @return whether table view is available
	 */
	boolean hasTableView();

	/**
	 * @return symbolic mode for algebra view
	 */
	SymbolicMode getSymbolicMode();

	/**
	 * @return whether sliders in AV are allowed
	 */
	public boolean hasSlidersInAV();

	/**
	 * @return true if sliders are created automatically
	 */
	public boolean hasAutomaticSliders();

	/**
	 * @return whether objects should be labeled a, b, ...
	 */
	boolean hasAutomaticLabels();

	/**
	 * @return algebra style
	 */
	int getDefaultAlgebraStyle();

	/**
	 * @return search tag for Open Material screen
	 */
	String getDefaultSearchTag();

	/**
	 * @return labeling style
	 */
	int getDefaultLabelingStyle();

	/**
	 * @return the Command Selector for the app.
	 */
	CommandNameFilter getCommandNameFilter();

	/**
	 * @return whether the app should show the tools panel or not
	 */
	boolean showToolsPanel();

	/**
	 * @return with the app code which is also used in the url, like graphing,cas,
	 * classic etc..
	 */
	String getAppCode();
}
