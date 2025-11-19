/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.main;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.CheckForNull;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.gui.Editing;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.gui.view.table.TableValuesPoints;
import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.ManualPage;

/**
 * This interface is almost the same as GuiManager, just it is an interface and
 * doesn't implement anything, and contains only public methods. (So things from
 * GuiManager were moved to here.)
 * 
 * @author arpad
 *
 */

public interface GuiManagerInterface extends SettingListener {

	@MissingDoc
	void updateMenubar();

	@MissingDoc
	void updateMenubarSelection();

	/**
	 * Show popup menu for selected elements.
	 * @param selectedGeos selected elements
	 * @param view graphic view
	 * @param mouseLoc mouse location
	 */
	void showPopupMenu(ArrayList<GeoElement> selectedGeos,
					   EuclidianViewInterfaceCommon view,
					   GPoint mouseLoc);

	/**
	 * Show popup menu for choosing one of possible elements.
	 * @param selectedGeos selected elements
	 * @param geos elements to choose from
	 * @param view graphic view
	 */
	void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
							ArrayList<GeoElement> geos, EuclidianViewInterfaceCommon view,
							GPoint p);

	/**
	 * Update UI for new app mode.
	 * @param mode app mode
	 * @param modeSetter source of mode change
	 */
	void setMode(int mode, ModeSetter modeSetter);

	/**
	 * Redo last undone action.
	 */
	void redo();

	/**
	 * Undo last action.
	 */
	void undo();

	/**
	 * Save current construction (opens save dialog on first save).
	 * @return success
	 */
	boolean save();

	/**
	 * Set focused panel.
	 * @param event pointer event TODO pass view ID instead
	 * @param updatePropertiesView whether to update properties view
	 */
	void setFocusedPanel(AbstractEvent event, boolean updatePropertiesView);

	/**
	 * @param loc image corner
	 * @param object unused -- TODO
	 * @param fromClipboard whether it's from clipboard
	 * @param view view to insert image in
	 */
	void loadImage(GeoPoint loc, Object object, boolean fromClipboard,
				   EuclidianView view);

	/**
	 * loads the camera dialog
	 */
	void loadWebcam();

	/**
	 * @return whether AV is initialized and showing
	 */
	boolean hasAlgebraViewShowing();

	/**
	 * @return whether AV is initialized
	 */
	boolean hasAlgebraView();

	/**
	 * Update fonts in all components.
	 */
	void updateFonts();

	/**
	 * @return if construction protocolis initialized
	 */
	boolean isUsingConstructionProtocol();

	/**
	 * Append construction protocol XML to a builder.
	 * @param sb XML string builder
	 */
	void getConsProtocolXML(XMLStringBuilder sb);

	/**
	 * Show graphics view options context menu.
	 * @param view graphics view
	 * @param mouseLoc pointer location
	 */
	void showDrawingPadPopup(EuclidianViewInterfaceCommon view,
							 GPoint mouseLoc);

	/**
	 * Show 3D graphics view options context menu.
	 * @param view graphics view
	 * @param mouseLoc pointer location
	 */
	void showDrawingPadPopup3D(EuclidianViewInterfaceCommon view,
							   GPoint mouseLoc);

	@MissingDoc
	boolean hasSpreadsheetView();

	@MissingDoc
	void attachSpreadsheetView();

	/**
	 * Show or hide a view.
	 * @param visible whether to show
	 * @param viewID view ID
	 */
	void setShowView(boolean visible, int viewID);

	/**
	 * Show or hide a view.
	 * @param visible whether to show
	 * @param viewID view ID
	 * @param isPermanent whether the change is permanent (view can be detached on hide)
	 */
	void setShowView(boolean visible, int viewID, boolean isPermanent);

	/**
	 * @param viewID view ID
	 * @return whether view is showing
	 */
	boolean showView(int viewID);

	@MissingDoc
	View getConstructionProtocolData();

	@MissingDoc
	Editing getCasView();

	@MissingDoc
	boolean hasCasView();

	@MissingDoc
	SpreadsheetViewInterface getSpreadsheetView();

	@MissingDoc
	View getProbabilityCalculator();

	@MissingDoc
	View getDataAnalysisView();

	/**
	 * Get plot panel view
	 * @param id view ID
	 * @return plot panel view
	 */
	View getPlotPanelView(int id);

	@MissingDoc
	View getPropertiesView();

	@MissingDoc
	View getTableValuesView();

	@MissingDoc
	TableValuesPoints getTableValuesPoints();

	@MissingDoc
	boolean hasProbabilityCalculator();

	/**
	 * Add algebra view's settings to XML builder.
	 * @param sb XML builder
	 * @param asPreference if it's for the preference
	 */
	void getAlgebraViewXML(XMLStringBuilder sb, boolean asPreference);

	/**
	 * Update undo/redo and menu for selection.
	 */
	void updateActions();

	@MissingDoc
	void updateSpreadsheetColumnWidths();

	@MissingDoc
	void updateConstructionProtocol();

	@MissingDoc
	void updateAlgebraInput();

	/**
	 * Show or hide auxiliary objects.
	 * @param flag whether to show the auxiliary objects
	 */
	void setShowAuxiliaryObjects(boolean flag);

	@MissingDoc
	void updatePropertiesView();

	/**
	 * tells the properties view that mouse has been pressed
	 */
	void mousePressedForPropertiesView();

	/**
	 * tells the properties view that mouse has been released
	 *
	 * @param creatorMode
	 *            tells if ev is in creator mode (ie not move mode)
	 */
	void mouseReleasedForPropertiesView(boolean creatorMode);

	/**
	 * Update the UI after file was loaded.
	 * @param success whether loading was successful
	 * @param isMacroFile whether the loaded file is a .ggt
	 */
	void updateGUIafterLoadFile(boolean success, boolean isMacroFile);

	/**
	 * Start editing definition of an element (in AV or text tool dialog)
	 * @param geoElement element to edit
	 */
	void startEditing(GeoElement geoElement);

	@MissingDoc
	boolean noMenusOpen();

	@MissingDoc
	void openFile();

	@MissingDoc
	Layout getLayout();

	@MissingDoc
	void showGraphicExport();

	@MissingDoc
	void showPSTricksExport();

	@MissingDoc
	void showWebpageExport();

	@MissingDoc
	void detachPropertiesView();

	@MissingDoc
	boolean hasPropertiesView();

	@MissingDoc
	void attachPropertiesView();

	@MissingDoc
	void attachAlgebraView();

	@MissingDoc
	void attachCasView();

	@MissingDoc
	void attachConstructionProtocolView();

	@MissingDoc
	void attachProbabilityCalculatorView();

	@MissingDoc
	void attachDataAnalysisView();

	@MissingDoc
	void detachDataAnalysisView();

	@MissingDoc
	boolean hasDataAnalysisView();

	/**
	 * Attach a view which by using the view ID.
	 *
	 * @author Florian Sonner
	 *
	 * @param viewId
	 *            view ID
	 */
	void attachView(int viewId);

	@MissingDoc
	EuclidianView getActiveEuclidianView();

	@MissingDoc
	void showAxesCmd();

	@MissingDoc
	void showGridCmd();

	/**
	 * Update UI after redefinition.
	 * @param geo redefined element
	 */
	void doAfterRedefine(GeoElementND geo);

	/**
	 * Detach a view which by using the view ID.
	 *
	 * @author Florian Sonner
	 *
	 * @param viewId
	 *            view ID
	 */
	void detachView(int viewId);

	@MissingDoc
	void detachProbabilityCalculatorView();

	@MissingDoc
	void detachCasView();

	@MissingDoc
	void detachConstructionProtocolView();

	@MissingDoc
	void detachSpreadsheetView();

	@MissingDoc
	void detachAlgebraView();

	/**
	 * Set the dock panel layout.
	 * @param layout layout
	 */
	void setLayout(Layout layout);

	@MissingDoc
	void initialize();

	@MissingDoc
	void resetSpreadsheet();

	/**
	 * Enable / disable autoscroll in spreadsheet.
	 *
	 * @param scrollToShow
	 *            scrolling flag for spreadsheet
	 */
	void setScrollToShow(boolean scrollToShow);

	@MissingDoc
	void updateToolbar();

	/**
	 * @param idx
	 *            index
	 * @return whether secondary euclidian view with given index is showing
	 */
	boolean hasEuclidianView2(int idx);

	/**
	 * @param idx index
	 * @return secondary euclidian view
	 */
	EuclidianViewInterfaceCommon getEuclidianView2(int idx);

	/**
	 * @param idx index
	 * @return whether secondary euclidian view was initialized
	 */
	boolean hasEuclidianView2EitherShowingOrNot(int idx);

	/**
	 * @return algebra view
	 */
	Editing getAlgebraView();

	/**
	 * Apply algebra view settings.
	 */
	void applyAlgebraViewSettings();

	/**
	 * Update frame size.
	 */
	void updateFrameSize();

	/**
	 *
	 * @return id of view which is setting the active toolbar
	 */
	int getActiveToolbarId();

	/**
	 * @return construction protocol view
	 */
	ConstructionProtocolView getConstructionProtocolView();

	/**
	 * Show or hide navigation bar for construction protocol in given view.
	 * @param show whether to show
	 * @param id view ID
	 */
	void setShowConstructionProtocolNavigation(boolean show, int id);

	/**
	 * @param show whether to show navigation
	 * @param id view ID
	 * @param playButton whether to include play button
	 * @param playDelay value of delay between steps (in seconds)
	 * @param showProtButton whether to include CP button
	 */
	void setShowConstructionProtocolNavigation(boolean show, int id,
			boolean playButton, double playDelay, boolean showProtButton);

	/**
	 * Update checkboxes for construction protocol navigation setting.
	 * @param id view ID
	 */
	void updateCheckBoxesForShowConstructionProtocolNavigation(int id);

	/**
	 * Switch navigation bar buttons to pause.
	 */
	void setNavBarButtonPause();

	/**
	 * Switch navigation bar buttons to play.
	 */
	void setNavBarButtonPlay();

	/**
	 * #3490 "Create sliders for a, b?" Create Sliders / Cancel Yes: create
	 * sliders and draw line No: go back into input bar and allow user to change
	 * input
	 *
	 * @param string
	 *            eg "a, b"
	 * @return true/false
	 */
	boolean checkAutoCreateSliders(String string,
								   AsyncOperation<String[]> callback);

	/**
	 * Apply construction protocol settings.
	 * @param cpSettings settings
	 */
	void applyCPsettings(ConstructionProtocolSettings cpSettings);

	@MissingDoc
	ConstructionProtocolNavigation getCPNavigationIfExists();

	/**
	 * Returns the construction protocol navigation bar instance.
	 *
	 * @param id
	 *            view id
	 * @return construction protocol for the view id
	 */
	ConstructionProtocolNavigation getConstructionProtocolNavigation(int id);

	/**
	 * Returns the default construction protocol navigation bar instance.
	 *
	 * @return construction protocol for the view id
	 */
	ConstructionProtocolNavigation getConstructionProtocolNavigation();

	/**
	 * @return navigation bars for all the views
	 */
	Collection<ConstructionProtocolNavigation> getAllCPNavigations();

	@MissingDoc
	void logout();

	@MissingDoc
	int getEuclidianViewCount();

	/**
	 * Add single tool to main toolbar.
	 * @param mode tool's mode number
	 */
	void addToToolbarDefinition(int mode);

	/**
	 * @return main toolbar definition string
	 * (see {@link org.geogebra.common.gui.toolbar.ToolBar} for syntax)
	 */
	String getToolbarDefinition();

	/**
	 * Register construction protocol view
	 * @param view construction protocol view
	 */
	void registerConstructionProtocolView(ConstructionProtocolView view);

	/**
	 * Update the style bar in properties view.
	 */
	void updatePropertiesViewStylebar();

	/**
	 * Set URL for given image to the URL of a tool icon and start loading it.
	 * @param mode mode/tool ID
	 * @param geoImage image
	 * @param onload callback after loading
	 */
	void getToolImageURL(int mode, GeoImage geoImage, AsyncOperation<String> onload);

	@MissingDoc
	EuclidianViewInterfaceCommon getPlotPanelEuclidianView();

	/**
	 * Redraw Navigation Bars if necessary (e.g. step changed)
	 */
	void updateNavBars();

	/**
	 * Replace selected text in input bar.
	 * @param string replacement string
	 */
	void replaceInputSelection(String string);

	/**
	 * Set output bar content.
	 * @param definitionForInputBar text for input bar
	 */
	void setInputText(String definitionForInputBar);

	/**
	 * Set image corner from selected points.
	 * @param geoImage image to be positioned
	 */
	void setImageCornersFromSelection(GeoImage geoImage);

	/**
	 * Refresh toolbar to make sure the custom tool icons are up to date.
	 */
	void refreshCustomToolsInToolBar();

	/**
	 * Get XML for construction protocol and data analysis.
	 * @param sb XML builder
	 */
	void getExtraViewsXML(XMLStringBuilder sb);

	/**
	 * @param type help page type
	 * @param pageName help page specifier (tool or command name)
	 * @return help URL
	 */
	String getHelpURL(ManualPage type, String pageName);

	@MissingDoc
	String getReportBugUrl();

	@MissingDoc
	String getLicenseUrl();

	/**
	 * Open context menu for an element in AV.
	 * @param geo construction element
	 */
	void openMenuInAVFor(GeoElement geo);

	/**
	 * Add all views settings to XML builder.
	 * @param sb XML builder
	 * @param asPreference whether this is for preference XML (as opposed to .ggb file)
	 */
	void getViewsXML(XMLStringBuilder sb, boolean asPreference);

	/**
	 * Make the right panel (toolbar) go from full screen to default width.
	 */
	void closeFullscreenView();

	/**
	 * @return input keyboard button for Web input boxes
	 */
	@CheckForNull InputKeyboardButton getInputKeyboardButton();
}
