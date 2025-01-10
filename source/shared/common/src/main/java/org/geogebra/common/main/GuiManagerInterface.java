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
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.util.AsyncOperation;

/**
 * This interface is almost the same as GuiManager, just it is an interface and
 * doesn't implement anything, and contains only public methods. (So things from
 * GuiManager were moved to here.)
 * 
 * @author arpad
 *
 */

public interface GuiManagerInterface extends SettingListener {

	enum Help {
		COMMAND, TOOL, GENERIC
	}

	void updateMenubar();

	void updateMenubarSelection();

	void showPopupMenu(ArrayList<GeoElement> selectedGeos,
					   EuclidianViewInterfaceCommon euclidianViewInterfaceCommon,
					   GPoint mouseLoc);

	void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
							ArrayList<GeoElement> geos, EuclidianViewInterfaceCommon view,
							GPoint p);

	void setMode(int mode, ModeSetter m);

	void redo();

	void undo();

	boolean save();

	void setFocusedPanel(AbstractEvent event, boolean updatePropertiesView);

	void loadImage(GeoPoint loc, Object object, boolean altDown,
				   EuclidianView view);

	/**
	 * loads the camera dialog
	 */
	void loadWebcam();

	boolean hasAlgebraViewShowing();

	boolean hasAlgebraView();

	void updateFonts();

	boolean isUsingConstructionProtocol();

	void getConsProtocolXML(StringBuilder sb);

	void showDrawingPadPopup(EuclidianViewInterfaceCommon view,
							 GPoint mouseLoc);

	void showDrawingPadPopup3D(EuclidianViewInterfaceCommon view,
							   GPoint mouseLoc);

	boolean hasSpreadsheetView();

	void attachSpreadsheetView();

	void setShowView(boolean visible, int viewID);

	void setShowView(boolean visible, int viewID, boolean isPermanent);

	boolean showView(int viewID);

	View getConstructionProtocolData();

	Editing getCasView();

	boolean hasCasView();

	SpreadsheetViewInterface getSpreadsheetView();

	View getProbabilityCalculator();

	View getDataAnalysisView();

	View getPlotPanelView(int id);

	View getPropertiesView();

	View getTableValuesView();

	TableValuesPoints getTableValuesPoints();

	boolean hasProbabilityCalculator();

	void getAlgebraViewXML(StringBuilder sb, boolean asPreference);

	/**
	 * Update undo/redo and menu for selection.
	 */
	void updateActions();

	void updateSpreadsheetColumnWidths();

	void updateConstructionProtocol();

	void updateAlgebraInput();

	void setShowAuxiliaryObjects(boolean flag);

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

	void updateGUIafterLoadFile(boolean success, boolean isMacroFile);

	void startEditing(GeoElement geoElement);

	boolean noMenusOpen();

	void openFile();

	Layout getLayout();

	void showGraphicExport();

	void showPSTricksExport();

	void showWebpageExport();

	void detachPropertiesView();

	boolean hasPropertiesView();

	void attachPropertiesView();

	void attachAlgebraView();

	void attachCasView();

	void attachConstructionProtocolView();

	void attachProbabilityCalculatorView();

	void attachDataAnalysisView();

	void detachDataAnalysisView();

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

	EuclidianView getActiveEuclidianView();

	void showAxesCmd();

	void showGridCmd();

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

	void detachProbabilityCalculatorView();

	void detachCasView();

	void detachConstructionProtocolView();

	void detachSpreadsheetView();

	void detachAlgebraView();

	void openCommandHelp(String command);

	void openHelp(String page);

	void setLayout(Layout layout);

	void initialize();

	void resetSpreadsheet();

	/**
	 * Enable / disable autoscroll in spreadsheet.
	 *
	 * @param scrollToShow
	 *            scrolling flag for spreadsheet
	 */
	void setScrollToShow(boolean scrollToShow);

	void updateToolbar();

	/**
	 * @param idx
	 *            index
	 * @return whether secondary euclidian view with given index is showing
	 */
	boolean hasEuclidianView2(int idx);

	EuclidianViewInterfaceCommon getEuclidianView2(int idx);

	boolean hasEuclidianView2EitherShowingOrNot(int idx);

	Editing getAlgebraView();

	void applyAlgebraViewSettings();

	void updateFrameSize();

	/**
	 *
	 * @return id of view which is setting the active toolbar
	 */
	int getActiveToolbarId();

	ConstructionProtocolView getConstructionProtocolView();

	void setShowConstructionProtocolNavigation(boolean show, int id);

	void setShowConstructionProtocolNavigation(boolean show, int id,
			boolean playButton, double playDelay, boolean showProtButton);

	void updateCheckBoxesForShowConstructinProtocolNavigation(int id);

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

	void applyCPsettings(ConstructionProtocolSettings cpSettings);

	ConstructionProtocolNavigation getCPNavigationIfExists();

	ConstructionProtocolNavigation getConstructionProtocolNavigation(int id);

	ConstructionProtocolNavigation getConstructionProtocolNavigation();

	Collection<ConstructionProtocolNavigation> getAllCPNavigations();

	void logout();

	int getEuclidianViewCount();

	void addToToolbarDefinition(int mode);

	String getToolbarDefinition();

	void registerConstructionProtocolView(ConstructionProtocolView view);

	void updatePropertiesViewStylebar();

	void getToolImageURL(int mode, GeoImage geoImage, AsyncOperation<String> onload);

	EuclidianViewInterfaceCommon getPlotPanelEuclidanView();

	/**
	 * redraw Navigation Bars if necessary (eg step changed)
	 */
	void updateNavBars();

	void replaceInputSelection(String string);

	void setInputText(String definitionForInputBar);

	void setImageCornersFromSelection(GeoImage geoImage);

	void refreshCustomToolsInToolBar();

	void getExtraViewsXML(StringBuilder sb);

	String getHelpURL(Help type, String pageName);

	String getReportBugUrl();

	String getLicenseUrl();

	void openMenuInAVFor(GeoElement geo);

	void getViewsXML(StringBuilder sb, boolean asPreference);

	void closeFullscreenView();

	@CheckForNull
	InputKeyboardButton getInputKeyboardButton();

	default boolean isTableViewShowing() {
		return false;
	}
}
