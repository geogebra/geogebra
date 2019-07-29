package org.geogebra.web.html5.gui;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.javax.swing.GOptionPaneW;

import com.google.gwt.user.client.Command;

public interface GuiManagerInterfaceW extends GuiManagerInterface {

	void showPopupMenu(ArrayList<GeoElement> geos, AlgebraView invoker,
	        GPoint p);

	void setFocusedPanel(int evID, boolean updatePropertiesView);

	void resize(int width, int height);

	String getToolbarDefinition(Integer viewId);

	boolean moveMoveFloatingButtonUp(int left, int width, boolean isSmall);

	void moveMoveFloatingButtonDown(boolean isSmall, boolean wasMoved);

	void removeFromToolbarDefinition(int mode);

	String getCustomToolbarDefinition();

	SetLabels getInputHelpPanel();

	void addAlgebraInput(AlgebraInput ai);

	AlgebraInput getAlgebraInput();

	Command getShowAxesAction();

	Command getShowGridAction();

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

	void showSciSettingsView();

	void showToolBar(boolean show);

	void showMenuBar(boolean show);

	void showAlgebraInput(boolean show);

	EuclidianStyleBar newEuclidianStylebar(EuclidianView ev, int viewID);

	EuclidianStyleBar newDynamicStylebar(EuclidianView ev);

	void addStylebar(EuclidianView ev,
			EuclidianStyleBar dynamicStylebar);

	void recalculateEnvironments();

	void updateStyleBarPositions(boolean menuOpen);

	void exportGGB();

	void listenToLogin();

	void setOnScreenKeyboardTextField(MathKeyboardListener textField);

	boolean focusScheduled(boolean setNotGet,
			boolean setOrGetScheduledPrioritized, boolean setOrGetAllowed);

	void setPixelRatio(double ratio);

	String getTooltipURL(int mode);

	GOptionPaneW getOptionPane();

	void updateToolbarActions();

	void resetMenu();

	void resetMenuIfScreenChanged();

	@Override
	AlgebraView getAlgebraView();

	@Override
	SpreadsheetViewInterface getSpreadsheetView();

	void onScreenEditingEnded();

	void setActivePanelAndToolbar(int viewID);

	void updateKeyboardLanguage();

	boolean getKeyboardShouldBeShownFlag();

	void addKeyboardAutoHidePartner(GPopupPanel popup);

	void switchToolsToAV();

	boolean isKeyboardClosedByUser();

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
	 * @param fallback
	 *            fallback value
	 * @return whether root panel is split vertically
	 */
	boolean isVerticalSplit(boolean fallback);

	/**
	 * @param style
	 *            exam ok (teal), or exam cheat (red)
	 */
	void setUnbundledHeaderStyle(String style);

	/**
	 * init on click for exam info button
	 */
	void initInfoBtnAction();

	/**
	 * Show table view with new column
	 * If table was empty before, min/max/step
	 * dialog shows up.
	 *
	 * @param geo
	 *            {@link GeoElement}
	 */
	void showTableValuesView(GeoElement geo);

	/**
	 * Updates the unbundled toolbar.
	 */
	void updateUnbundledToolbar();

	/**
	 * Adds the main menu button to the global header.
	 */
	void menuToGlobalHeader();
}
