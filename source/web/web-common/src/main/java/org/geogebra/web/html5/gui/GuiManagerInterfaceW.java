/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.gui;

import java.util.ArrayList;

import javax.annotation.CheckForNull;

import org.geogebra.common.annotation.MissingDoc;
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
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.TemplateChooserControllerI;
import org.gwtproject.user.client.ui.Widget;

/**
 * GUI manager for the web platform.
 */
public interface GuiManagerInterfaceW extends GuiManagerInterface {

	/**
	 * @param geos selected elements
	 * @param invoker invoker
	 * @param x x-coordinate relative to app frame
	 * @param y y-coordinate relative to app frame
	 */
	void showPopupMenu(ArrayList<GeoElement> geos, Widget invoker, int x, int y);

	/**
	 * @param evID view ID
	 * @param updatePropertiesView whether to update properties view
	 */
	void setFocusedPanel(int evID, boolean updatePropertiesView);

	/**
	 * @param width width in pixels
	 * @param height height in pixels
	 */
	void resize(int width, int height);

	/**
	 * @param viewId view ID
	 * @return toolbar definition
	 */
	String getToolbarDefinition(Integer viewId);

	/**
	 * @param snackbarRight whether the snack bar is on the right
	 * @return top offset of the move button
	 */
	int getMoveTopBelowSnackbar(int snackbarRight);

	/**
	 * Remove a tool from toolbar definition
	 * @param mode tool's mode number
	 */
	void removeFromToolbarDefinition(int mode);

	@MissingDoc
	String getCustomToolbarDefinition();

	@MissingDoc
	SetLabels getInputHelpPanel();

	/**
	 * @return Whether the input help panel is initialized.
	 */
	boolean hasInputHelpPanel();

	/**
	 * Set the algebra input
	 * @param ai algebra input
	 */
	void addAlgebraInput(AlgebraInput ai);

	@MissingDoc
	AlgebraInput getAlgebraInput();

	/**
	 * Set active toolbar.
	 * @param toolbarID view ID associated with the toolbar
	 */
	void setActiveToolbarId(int toolbarID);

	@MissingDoc
	void removePopup();

	/**
	 * Set current definition of the main toolbar
	 * @param toolBarDefinition toolbar definition.
	 */
	void setToolBarDefinition(String toolBarDefinition);

	/**
	 * Set active view.
	 * @param evID view ID
	 */
	void setActiveView(int evID);

	@MissingDoc
	boolean isDraggingViews();

	/**
	 * Start or stop dragging views.
	 * @param enable whether to enable dragging
	 * @param temporary whether this is temporary
	 */
	void setDraggingViews(boolean enable, boolean temporary);

	@MissingDoc
	void refreshDraggingViews();

	/**
	 * Set general definition string of the toolbar.
	 * @param toolbarDefinition toolbar definition
	 */
	void setGeneralToolBarDefinition(String toolbarDefinition);

	/**
	 * @param query search query
	 * @return Open File view with resources matching a query
	 */
	BrowseViewI getBrowseView(String query);

	/**
	 * @return Open File view
	 */
	BrowseViewI getBrowseView();

	/**
	 * @return true if Open File View is not null
	 */
	boolean isOpenFileViewLoaded();

	/**
	 * Show or hide the toolbar.
	 * @param show whether to show
	 */
	void showToolBar(boolean show);

	/**
	 * Show or hide the menu button.
	 * @param show whether to show
	 */
	void showMenuBar(boolean show);

	/**
	 * Show or hide the algebra input.
	 * @param show whether to show
	 */
	void showAlgebraInput(boolean show);

	/**
	 * @param ev view
	 * @param viewID view ID
	 * @return style bar for given view
	 */
	EuclidianStyleBar newEuclidianStylebar(EuclidianView ev, int viewID);

	/**
	 * @param ev view
	 * @return dynamic style bar for given view
	 */
	EuclidianStyleBar newDynamicStylebar(EuclidianView ev);

	/**
	 * Add style bar to a view
	 * @param ev view
	 * @param dynamicStylebar style bar
	 */
	void addStylebar(EuclidianView ev,
					 EuclidianStyleBar dynamicStylebar);

	@MissingDoc
	void recalculateEnvironments();

	@MissingDoc
	void exportGGB();

	/**
	 * Wait for login event, run callback after.
	 * @param onSuccess callback
	 */
	void listenToLogin(Runnable onSuccess);

	/**
	 * Update pixel ratio in the views.
	 * @param ratio device pixel ratio
	 */
	void setPixelRatio(double ratio);

	/**
	 * @param mode tool's mode number
	 * @return help URL for a tool
	 */
	String getTooltipURL(int mode);

	@MissingDoc
	void updateToolbarActions();

	@MissingDoc
	void resetMenu();

	@MissingDoc
	void resetMenuIfScreenChanged();

	@Override
	AlgebraView getAlgebraView();

	@Override
	SpreadsheetViewInterface getSpreadsheetView();

	/**
	 * Set active panel and toolbar.
	 * @param viewID view ID
	 */
	void setActivePanelAndToolbar(int viewID);

	@MissingDoc
	void switchToolsToAV();

	/**
	 * Get keyboard listener for a panel.
	 * @param panel dock panel
	 * @return keyboard listener
	 */
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
	 * @return whether the root panel is split vertically
	 */
	boolean isVerticalSplit(boolean fallback);

	/**
	 * Update header to match current exam state
	 */
	void updateUnbundledToolbarStyle();

	/**
	 * Show exam info dialog.
	 * @param examInfoBtn button to show dialog relative to
	 */
	void showExamInfoDialog(StandardButton examInfoBtn);

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

	@MissingDoc
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

	/**
	 * Called when side toolbar is opened or closed
	 * @param viewId active view of the side toolbar
	 * @param isVisible whether it's visible
	 */
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

	@MissingDoc
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

	/**
	 * Show or hide the spreadsheet view (Classic or unbundled).
	 */
	void toggleSpreadsheetView();

	/**
	 * Open a help page in a new tab or external browser (if running in Electron)
	 * @param page page type
	 * @param detail page specifier
	 */
	void openHelp(ManualPage page, @CheckForNull String detail);

	/**
	 * Focus keyboard input (AV, CAS), if not available, focus active graphics.
	 */
	void focus();
}