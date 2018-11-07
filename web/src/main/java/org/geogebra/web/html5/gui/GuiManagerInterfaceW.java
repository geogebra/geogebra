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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;

public interface GuiManagerInterfaceW extends GuiManagerInterface {

	public void showPopupMenu(ArrayList<GeoElement> geos, AlgebraView invoker,
	        GPoint p);

	public void setFocusedPanel(int evID, boolean updatePropertiesView);

	public void resize(int width, int height);

	public String getToolbarDefinition(Integer viewId);

	boolean moveMoveFloatingButtonUp(int left, int width, boolean isSmall);

	void moveMoveFloatingButtonDown(boolean isSmall, boolean wasMoved);

	public void removeFromToolbarDefinition(int mode);

	public String getCustomToolbarDefinition();

	public SetLabels getInputHelpPanel();

	public void addAlgebraInput(AlgebraInput ai);

	public AlgebraInput getAlgebraInput();

	public Command getShowAxesAction();

	public Command getShowGridAction();

	public void setActiveToolbarId(int toolbarID);

	public void removePopup();

	public void setToolBarDefinition(String toolBarDefinition);

	public void setActiveView(int evID);

	public boolean isDraggingViews();

	public void setDraggingViews(boolean b, boolean temporary);

	public void refreshDraggingViews();

	public void setGeneralToolBarDefinition(String toolbarDefinition);

	public BrowseViewI getBrowseView(String query);

	public BrowseViewI getBrowseView();

	public void showToolBar(boolean show);

	public void showMenuBar(boolean show);

	public void showAlgebraInput(boolean show);

	public EuclidianStyleBar newEuclidianStylebar(EuclidianView ev, int viewID);

	public EuclidianStyleBar newDynamicStylebar(EuclidianView ev);

	public void addStylebar(EuclidianView ev,
			EuclidianStyleBar dynamicStylebar);

	public String getMenuBarHtml(ImageResource imgRes, String name, boolean b);

	public void recalculateEnvironments();

	public void updateStyleBarPositions(boolean menuOpen);

	public void exportGGB();

	public void listenToLogin();

	public void setOnScreenKeyboardTextField(MathKeyboardListener textField);

	public boolean focusScheduled(boolean setNotGet,
			boolean setOrGetScheduledPrioritized, boolean setOrGetAllowed);

	public void setPixelRatio(double ratio);

	public String getTooltipURL(int mode);

	public GOptionPaneW getOptionPane();

	public void updateToolbarActions();

	public void resetMenu();

	public void resetMenuIfScreenChanged();

	@Override
	public AlgebraView getAlgebraView();

	@Override
	public SpreadsheetViewInterface getSpreadsheetView();

	public void onScreenEditingEnded();

	void setActivePanelAndToolbar(int viewID);

	public void updateKeyboardLanguage();

	public boolean getKeyboardShouldBeShownFlag();

	public void addKeyboardAutoHidePartner(GPopupPanel popup);

	public void switchToolsToAV();

	public boolean isKeyboardClosedByUser();

	public MathKeyboardListener getKeyboardListener(DockPanel panel);

	/**
	 * There are some drawables which not drawn on the canvas of euclidian view,
	 * but added for an AbsolutePanel which hides the canvas. (e.g. inputbox)
	 * When we remove all drawable, we must to clear this AbsolutePanel too.
	 */
	public void clearAbsolutePanels();

	/**
	 * Update global tab of properties if exists
	 */
	public void updateGlobalOptions();

	/**
	 * @param fallback
	 *            fallback value
	 * @return whether root panel is split vertically
	 */
	public boolean isVerticalSplit(boolean fallback);

	/**
	 * @param style
	 *            exam ok (teal), or exam cheat (red)
	 */
	public void setUnbundledHeaderStyle(String style);
	
	/**
	 * init on click for exam info button
	 */
	public void initInfoBtnAction();

	/**
	 * Initializes Table View
	 * 
	 * @param min
	 *            min x-value.
	 * @param max
	 *            max x-value.
	 * @param step
	 *            x step value.
	 * @param geo
	 *            first geo to add.
	 */
	void initTableValuesView(double min, double max, double step, GeoElement geo);

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
}
