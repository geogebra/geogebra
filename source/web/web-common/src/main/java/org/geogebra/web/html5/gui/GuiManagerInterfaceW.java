package org.geogebra.web.html5.gui;

import java.util.ArrayList;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.euclidian.TextRendererSettings;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.util.ManualPage;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.main.TemplateChooserControllerI;
import org.gwtproject.user.client.ui.Widget;

public interface GuiManagerInterfaceW extends GuiManagerInterface {

	void showPopupMenu(ArrayList<GeoElement> geos, Widget invoker, int x, int y);

	void setFocusedPanel(int evID, boolean updatePropertiesView);

	void resize(int width, int height);

	String getToolbarDefinition(Integer viewId);

	int getMoveTopBelowSnackbar(int snackbarRight);

	void removeFromToolbarDefinition(int mode);

	String getCustomToolbarDefinition();

	SetLabels getInputHelpPanel();

	void addAlgebraInput(AlgebraInput ai);

	AlgebraInput getAlgebraInput();

	void setActiveToolbarId(int toolbarID);

	void removePopup();

	void setToolBarDefinition(String toolBarDefinition);

	void setActiveView(int evID);

	boolean isDraggingViews();

	void setDraggingViews(boolean b, boolean temporary);

	void refreshDraggingViews();

	void setGeneralToolBarDefinition(String toolbarDefinition);

	BrowseViewI getBrowseView(String query);

	BrowseViewI getBrowseView();

	/**
	 * @return true if Open File View is not null
	 */
	boolean isOpenFileViewLoaded();

	void showSciSettingsView();

	void showToolBar(boolean show);

	void showMenuBar(boolean show);

	void showAlgebraInput(boolean show);

	EuclidianStyleBar newEuclidianStylebar(EuclidianView ev, int viewID);

	EuclidianStyleBar newDynamicStylebar(EuclidianView ev);

	void addStylebar(EuclidianView ev,
					 EuclidianStyleBar dynamicStylebar);

	void recalculateEnvironments();

	void exportGGB();

	void listenToLogin(Runnable onSuccess);

	void setPixelRatio(double ratio);

	String getTooltipURL(int mode);

	void updateToolbarActions();

	void resetMenu();

	void resetMenuIfScreenChanged();

	@Override
	AlgebraView getAlgebraView();

	@Override
	SpreadsheetViewInterface getSpreadsheetView();

	void setActivePanelAndToolbar(int viewID);

	void switchToolsToAV();

	MathKeyboardListener getKeyboardListener(DockPanel panel);

	/**
	 * There are some drawables which not drawn on the canvas of euclidian view,
	 * but added for an AbsolutePanel which hides the canvas. (e.g. inputbox)
	 * When we remove all drawable, we must to clear this AbsolutePanel too.
	 */
	void clearAbsolutePanels();

	/**
	 * Update global tab of properties if exists
	 */
	void updateGlobalOptions();

	/**
	 * @param fallback fallback value
	 * @return whether root panel is split vertically
	 */
	boolean isVerticalSplit(boolean fallback);

	/**
	 * Update header to match current exam state
	 */
	void updateUnbundledToolbarStyle();

	/**
	 * init on click for exam info button
	 */
	void initInfoBtnAction();

	/**
	 * Show table view with new column
	 * If table was empty before, min/max/step
	 * dialog shows up.
	 *
	 * @param geo {@link GeoElement}
	 */
	void showTableValuesView(GeoElement geo);

	/**
	 * Updates the unbundled toolbar.
	 */
	void updateUnbundledToolbar();

	void updateUnbundledToolbarContent();

	/**
	 * Adds the main menu button to the global header.
	 */
	void menuToGlobalHeader();

	/**
	 * Initializes the share button in the global header.
	 */
	void initShareActionInGlobalHeader();

	/**
	 * Creates and adds a symbolic editor to the panel.
	 *
	 * @return the editor
	 */
	SymbolicEditor createSymbolicEditor(EuclidianViewW view, TextRendererSettings settings);

	/**
	 * @return templates controller
	 */
	TemplateChooserControllerI getTemplateController();

	/**
	 * callback for save after successful login
	 *
	 * @param runAfterLogin - callback
	 */
	void setRunAfterLogin(Runnable runAfterLogin);

	void onToolbarVisibilityChanged(int viewId, boolean isVisible);

	/**
	 * @param geo - to add to table of values
	 */
	void addGeoToTV(GeoElement geo);

	/**
	 * @param label - of geo to be removed from the table of values
	 */
	void removeGeoFromTV(String label);

	/**
	 * Shows/hides table of values view
	 */
	void toggleTableValuesView();

	/**
	 * @param min  - starting value of table
	 * @param max  - ending value of table
	 * @param step - step value of table
	 * @throws InvalidValuesException if ((max - min) / step) is out of range
	 */
	void setValues(double min, double max, double step) throws InvalidValuesException;

	/**
	 * @param column - index of column in the table of values
	 * @param show   - true if point should be shown, false otherwise
	 */
	void showPointsTV(int column, boolean show);

	boolean isAlgebraViewActive();

	/**
	 * @return whether toolbar (classic or unbundled) is showing and contains image tool
	 */
	boolean toolbarHasImageMode();

	/**
	 * Add a custom tool with given properties
	 *
	 * @param iconUrl the URL of the tool icon.
	 * @param name The name of the tool.
	 * @param category to put the tool in.
	 * @param callback the action of the tool.
	 */
	void addToolToNotesToolbox(String iconUrl, String name, String category, Object callback);

	void toggleSpreadsheetView();

	void openHelp(ManualPage page, @CheckForNull String detail);
}