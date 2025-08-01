/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.gui.view.data.DataAnalysisModel.IDataAnalysisListener;
import org.geogebra.common.gui.view.data.PlotPanelEuclidianViewInterface;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesPoints;
import org.geogebra.common.gui.view.table.TableValuesPointsImpl;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.main.InputKeyboardButton;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.common.util.ManualPage;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

public abstract class GuiManager implements GuiManagerInterface {

	@Weak
	protected Kernel kernel;
	@Weak
	protected App app;
	protected HashMap<Integer, ConstructionProtocolNavigation> constProtocolNavigationMap;
	private HashMap<Integer, PlotPanelEuclidianViewInterface> plotPanelIDMap;
	private int lastUsedPlotPanelID = -App.VIEW_PLOT_PANEL;
	private boolean setModeFinished;
	protected ProbabilityCalculatorView probCalculator;
	protected TableValues tableValues;
	protected TableValuesPoints tableValuesPoints;

	/**
	 * Abstract constructor
	 * 
	 * @param app
	 *            application
	 */
	public GuiManager(App app) {
		this.app = app;
		this.kernel = app.getKernel();
		app.getSettings().getLabelSettings().addListener(this);
	}

	@Override
	public void updateMenubar() {
		// temporarily nothing
	}

	@Override
	public boolean hasAlgebraView() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUsingConstructionProtocol() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void getExtraViewsXML(StringBuilder sb) {
		if (isUsingConstructionProtocol()) {
			getConsProtocolXML(sb);
		}
		if (this.hasDataAnalysisView()) {
			((IDataAnalysisListener) getDataAnalysisView()).getModel()
					.getXML(sb);
		}
	}

	@Override
	public final void getConsProtocolXML(StringBuilder sb) {
		if (this.isUsingConstructionProtocol()) {
			getConstructionProtocolView().getXML(sb);
		}
		if (getApp().showConsProtNavigation()) {
			sb.append("\t<consProtNavigationBar ");
			sb.append("id=\"");
			getApp().getConsProtNavigationIds(sb);
			sb.append('\"');
			sb.append(" playButton=\"");
			sb.append(
					getConstructionProtocolNavigation().isPlayButtonVisible());
			sb.append('\"');
			sb.append(" playDelay=\"");
			sb.append(getConstructionProtocolNavigation().getPlayDelay());
			sb.append('\"');
			sb.append(" protButton=\"");
			sb.append(getConstructionProtocolNavigation()
					.isConsProtButtonVisible());
			sb.append('\"');
			sb.append(" consStep=\"");
			sb.append(kernel.getConstructionStep());
			sb.append('\"');
			sb.append("/>\n");
		}

	}

	@Override
	public boolean hasProbabilityCalculator() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param sb
	 *            XML builder
	 */
	public void getProbabilityCalculatorXML(StringBuilder sb) {
		if (probCalculator != null) {
			probCalculator.getXML(sb);
		}
	}

	@Override
	public void getViewsXML(StringBuilder sb, boolean asPreference) {
		// save ProbabilityCalculator settings
		if (hasProbabilityCalculator()) {
			getProbabilityCalculatorXML(sb);
		}

		// save AlgebraView settings
		if (hasAlgebraView()) {
			getAlgebraViewXML(sb, asPreference);
		}
	}

	/**
	 * @param sb
	 *            XML builder
	 * @param asPreference
	 *            whether this is for preferences
	 */
	public void getSpreadsheetViewXML(StringBuilder sb, boolean asPreference) {
		sb.append("<spreadsheetView>\n");
		app.getSettings().getSpreadsheet().getDimensionsXML(sb);
		app.getSettings().getSpreadsheet().getWidthsAndHeightsXML(sb);
		sb.append("</spreadsheetView>\n");
	}

	@Override
	public void updateActions() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateConstructionProtocol() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the associated app; overridden to avoid classcast
	 */
	protected App getApp() {
		return app;
	}

	/**
	 * Attach a view which by using the view ID.
	 * 
	 * @author Florian Sonner
	 * 
	 * @param viewId
	 *            view ID
	 */
	@Override
	public void attachView(int viewId) {
		switch (viewId) {
		case App.VIEW_ALGEBRA:
			attachAlgebraView();
			break;
		case App.VIEW_SPREADSHEET:
			attachSpreadsheetView();
			break;
		case App.VIEW_CAS:
			attachCasView();
			break;
		case App.VIEW_CONSTRUCTION_PROTOCOL:
			attachConstructionProtocolView();
			break;
		case App.VIEW_PROBABILITY_CALCULATOR:
			attachProbabilityCalculatorView();
			break;
		case App.VIEW_DATA_ANALYSIS:
			attachDataAnalysisView();
			break;
		case App.VIEW_PROPERTIES:
			attachPropertiesView();
			break;
		case App.VIEW_EUCLIDIAN:
		case App.VIEW_EUCLIDIAN2:
		case App.VIEW_FUNCTION_INSPECTOR:
			// handled elsewhere
			break;
		default:
			// ignore 3D view
			if (!App.isView3D(viewId) && (viewId < App.VIEW_EUCLIDIAN_FOR_PLANE_START
					|| viewId > App.VIEW_EUCLIDIAN_FOR_PLANE_START)) {
				Log.error("Error attaching VIEW: " + viewId);
			}
		}
	}

	@Override
	public void showAxesCmd() {
		// get ev with focus
		EuclidianViewInterfaceCommon ev = getActiveEuclidianView();

		boolean bothAxesShown = ev.getShowXaxis() && ev.getShowYaxis();
		if (getApp().getEuclidianView1() == ev) {
			getApp().getSettings().getEuclidian(1).setShowAxes(!bothAxesShown,
					!bothAxesShown);

		} else if (getApp().hasEuclidianView2EitherShowingOrNot(1)
				&& getApp().getEuclidianView2(1) == ev) {
			getApp().getSettings().getEuclidian(2).setShowAxes(!bothAxesShown,
					!bothAxesShown);
		} else if (getApp().isEuclidianView3D(ev)) {
			getApp().getSettings().getEuclidian(3).setShowAxes(!bothAxesShown);

		} else {
			ev.setShowAxes(!bothAxesShown, true);
		}

		ev.repaint();
		getApp().storeUndoInfo();
		getApp().updateMenubar();
	}

	@Override
	public void showGridCmd() {
		// get ev with focus
		EuclidianView ev = getActiveEuclidianView();
		if (getApp().getEuclidianView1() == ev) {
			getApp().getSettings().getEuclidian(1).showGrid(!ev.getShowGrid());

		} else if (getApp().hasEuclidianView2EitherShowingOrNot(1)
				&& getApp().getEuclidianView2(1) == ev) {
			getApp().getSettings().getEuclidian(2).showGrid(!ev.getShowGrid());
		} else if (getApp().isEuclidianView3D(ev)) {
			getApp().getSettings().getEuclidian(3).showGrid(!ev.getShowGrid());

		} else {
			ev.showGrid(!ev.getShowGrid());
		}

		ev.repaint();
		getApp().storeUndoInfo();
		getApp().updateMenubar();
	}

	@Override
	public void doAfterRedefine(GeoElementND geo) {
		// if a tracing geo has been redefined, then put it back into the
		// traceGeoCollection
		if (geo.getSpreadsheetTrace()) {
			getApp().getTraceManager()
					.addSpreadsheetTraceGeo(geo.toGeoElement());
		}
	}

	/**
	 * Detach a view which by using the view ID.
	 * 
	 * @author Florian Sonner
	 * 
	 * @param viewId
	 *            view ID
	 */
	@Override
	public void detachView(int viewId) {
		switch (viewId) {
		case App.VIEW_ALGEBRA:
			detachAlgebraView();
			break;
		case App.VIEW_SPREADSHEET:
			detachSpreadsheetView();
			break;
		case App.VIEW_CAS:
			detachCasView();
			break;
		case App.VIEW_CONSTRUCTION_PROTOCOL:
			detachConstructionProtocolView();
			break;
		case App.VIEW_PROBABILITY_CALCULATOR:
			detachProbabilityCalculatorView();
			break;
		case App.VIEW_PROPERTIES:
			detachPropertiesView();
			break;
		case App.VIEW_DATA_ANALYSIS:
			detachDataAnalysisView();
			break;
		case App.VIEW_EUCLIDIAN:
		case App.VIEW_EUCLIDIAN2:
			Log.debug("TODO: should we detach EV1/2?");
			break;
		default:
			if (App.isView3D(viewId)) {
				Log.debug("TODO: should we detach EV3D?");
			} else {
				Log.error("Error detaching VIEW: " + viewId);
			}
		}
	}

	@Override
	final public View getConstructionProtocolData() {
		ConstructionProtocolView view = getConstructionProtocolView();
		if (view != null) {
			return view.getData();
		}
		// eg Android
		Log.debug("not implemented");
		return null;
	}

	@Override
	public void setShowConstructionProtocolNavigation(boolean show, int id) {
		getConstructionProtocolNavigation(id).setVisible(show);

		if (show) {
			if (getApp().getActiveEuclidianView() != null) {
				getApp().getActiveEuclidianView().resetMode();
			}

			// the navigation bar currently needs the full Construction Protocol
			// to work (to be notified about updates ie objects added / deleted)
			getConstructionProtocolView();
		}
	}

	@Override
	public void setShowConstructionProtocolNavigation(boolean show, int id,
			boolean playButton, double playDelay, boolean showProtButton) {
		setShowConstructionProtocolNavigation(show, id);

		getConstructionProtocolNavigation().setPlayButtonVisible(playButton);
		getConstructionProtocolNavigation().setPlayDelay(playDelay);
		getConstructionProtocolNavigation()
				.setConsProtButtonVisible(showProtButton);
	}

	@Override
	public void updateNavBars() {
		if (constProtocolNavigationMap != null) {
			for (ConstructionProtocolNavigation navBar
					: constProtocolNavigationMap.values()) {
				navBar.update();
			}
		}
	}

	@Override
	final public ConstructionProtocolNavigation getConstructionProtocolNavigation(
			int id) {
		if (constProtocolNavigationMap == null) {
			constProtocolNavigationMap = new HashMap<>();
		}

		ConstructionProtocolNavigation constProtocolNavigation = constProtocolNavigationMap
				.get(id);
		if (constProtocolNavigation == null) {
			constProtocolNavigation = newConstructionProtocolNavigation(id);
			constProtocolNavigationMap.put(id, constProtocolNavigation);
		}

		return constProtocolNavigation;
	}

	@Override
	final public Collection<ConstructionProtocolNavigation> getAllCPNavigations() {
		if (constProtocolNavigationMap == null) {
			return null;
		}

		return constProtocolNavigationMap.values();
	}

	@Override
	final public void setNavBarButtonPause() {
		if (constProtocolNavigationMap != null) {
			for (ConstructionProtocolNavigation cpn : constProtocolNavigationMap
					.values()) {
				cpn.setButtonPause();
			}
		}
	}

	@Override
	final public void setNavBarButtonPlay() {
		if (constProtocolNavigationMap != null) {
			for (ConstructionProtocolNavigation cpn : constProtocolNavigationMap
					.values()) {
				cpn.setButtonPlay();
			}
		}
	}

	/**
	 * 
	 * @return new construction protocol navigation bar instance
	 */
	protected abstract ConstructionProtocolNavigation newConstructionProtocolNavigation(
			int viewID);

	@Override
	public ConstructionProtocolNavigation getConstructionProtocolNavigation() {
		return getConstructionProtocolNavigation(App.VIEW_EUCLIDIAN);
	}

	/**
	 * Returns the construction protocol navigation bar instance or null, if it
	 * not exists.
	 */
	@Override
	public ConstructionProtocolNavigation getCPNavigationIfExists() {
		if (constProtocolNavigationMap == null) {
			return null;
		}
		return constProtocolNavigationMap.get(App.VIEW_EUCLIDIAN);
	}

	@Override
	public abstract void updateCheckBoxesForShowConstructionProtocolNavigation(
			int id);

	@Override
	public void applyCPsettings(ConstructionProtocolSettings cps) {
		if (constProtocolNavigationMap == null) {
			return;
		}

		for (ConstructionProtocolNavigation constProtocolNavigation : constProtocolNavigationMap
				.values()) {
			constProtocolNavigation
					.setConsProtButtonVisible(cps.showConsProtButton());
			constProtocolNavigation.setPlayDelay(cps.getPlayDelay());
			constProtocolNavigation.setPlayButtonVisible(cps.showPlayButton());
		}
	}

	@Override
	public void registerConstructionProtocolView(ConstructionProtocolView cpv) {
		if (constProtocolNavigationMap == null) {
			ConstructionProtocolNavigation cpn = getConstructionProtocolNavigation();
			cpn.register(cpv);
		} else {
			for (ConstructionProtocolNavigation cpn : constProtocolNavigationMap
					.values()) {
				cpn.register(cpv);
			}
		}
	}

	// ==================================
	// PlotPanel ID handling
	// =================================

	protected HashMap<Integer, PlotPanelEuclidianViewInterface> getPlotPanelIDMap() {
		if (plotPanelIDMap == null) {
			plotPanelIDMap = new HashMap<>();
		}
		return plotPanelIDMap;
	}

	/**
	 * Adds the given PlotPanelEuclidianView instance to the plotPanelIDMap and
	 * returns a unique viewID
	 * 
	 * @param plotPanel
	 *            plot panel
	 * @return plot panel ID
	 */
	public int assignPlotPanelID(PlotPanelEuclidianViewInterface plotPanel) {
		lastUsedPlotPanelID--;
		int viewID = lastUsedPlotPanelID;
		getPlotPanelIDMap().put(viewID, plotPanel);
		return viewID;
	}

	@Override
	public PlotPanelEuclidianViewInterface getPlotPanelView(int viewID) {
		return getPlotPanelIDMap().get(viewID);
	}

	@Override
	public void setMode(int mode0, ModeSetter modeSetter) {
		int mode = mode0;
		setModeFinished = false;

		// can't move this after otherwise Object Properties doesn't work
		kernel.notifyModeChanged(mode, modeSetter);
		// notifyModeChanged called another setMode => nothing to do here
		if (setModeFinished) {
			return;
		}
		// select toolbar button, returns *actual* mode selected - only for
		// desktop

		int newMode = setToolbarMode(mode, modeSetter);

		if (mode != EuclidianConstants.MODE_SELECTION_LISTENER
				&& newMode != mode) {
			mode = newMode;
			kernel.notifyModeChanged(mode, modeSetter);
		}

		if (mode == EuclidianConstants.MODE_PROBABILITY_CALCULATOR) {

			// show or focus the probability calculator
			if (showView(App.VIEW_PROBABILITY_CALCULATOR)) {
				this.getLayout().getDockManager()
						.setFocusedPanel(App.VIEW_PROBABILITY_CALCULATOR);
			} else {
				setShowView(true, App.VIEW_PROBABILITY_CALCULATOR);
				probCalculator.setProbabilityCalculator(Dist.NORMAL, null,
						false);
			}

			// nothing more to do, so reset to move mode
			getApp().setMoveMode();
		}

		if ((mode == EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS
				|| mode == EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS
				|| mode == EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS)
				&& modeSetter == ModeSetter.TOOLBAR) {
			// save the selected geos so they can be re-selected later
			ArrayList<GeoElement> temp = new ArrayList<>();
			if (getApp().getSelectionManager().getSelectedGeos() != null) {
				temp.addAll(getApp().getSelectionManager().getSelectedGeos());
			}

			if (getApp().getGuiManager() != null) {
				getApp().getDialogManager().showDataSourceDialog(mode, true);
				getApp().setMoveMode();
			}

			// reselect the geos
			getApp().getSelectionManager().setSelectedGeos(temp);
		}

		setModeFinished = true;
	}

	protected void setProbCalculator(ProbabilityCalculatorView pc) {
		this.probCalculator = pc;
	}

	/**
	 * @param mode
	 *            app mode
	 * @param m
	 *            mode setter
	 * @return sets the toolbar's mode
	 */
	protected int setToolbarMode(int mode, ModeSetter m) {
		return 0;
		// should be implemented in subclasses if needed
	}

	@Override
	final public String getHelpURL(final ManualPage type, String pageName) {
		// try to get help for given language
		// eg http://help.geogebra.org/en-GB/cmd/FitLogistic

		final StringBuilder urlSB = new StringBuilder();
		urlSB.append(GeoGebraConstants.GEOGEBRA_HELP_WEBSITE).append("en/");

		switch (type) {
		case COMMAND:
			String cmdPageName = getApp().getLocalization().getEnglishCommand(
					pageName);
			if (StringUtil.empty(cmdPageName)) {
				urlSB.append("Commands");
			} else {
				urlSB.append("commands/");
				urlSB.append(cmdPageName);
			}
			break;
		case TOOL:
			urlSB.append("tools/");
			urlSB.append(pageName);
			break;
		default:
			urlSB.append(type.getURL());
		}
		if (!app.getLocalization().languageIs("en")) {
			urlSB.append("?redirect=").append(getApp().getLocalization().getLanguageTag());
		}
		return urlSB.toString();
	}

	@Override
	public String getReportBugUrl() {
		return GeoGebraConstants.REPORT_BUG_URL;
	}

	@Override
	public String getLicenseUrl() {
		return GeoGebraConstants.GGB_LICENSE_URL;
	}

	@Override
	public void redo() {
		getApp().setWaitCursor();
		kernel.redo();
		getApp().setDefaultCursor();
	}

	@Override
	public void undo() {
		getApp().setWaitCursor();
		kernel.undo();
		getApp().setDefaultCursor();
	}

	@Override
	public abstract String getToolbarDefinition();

	/**
	 * Set definition of the main toolbar.
	 * @param toolBarDefinition toolbar definition (see {@link ToolBar} for the syntax)
	 */
	public abstract void setToolBarDefinition(String toolBarDefinition);

	@Override
	public void refreshCustomToolsInToolBar() {
		String oldToolbar = getToolbarDefinition() == null
				? ToolBar.getAllTools(getApp()) : getToolbarDefinition();
		setToolBarDefinition(refreshCustomToolsInToolBar(oldToolbar));
	}

	/**
	 * Add / remove macros from the toolbar.
	 * 
	 * @param initial
	 *            initial toolbar definition
	 * @return new toolbar definition
	 */
	public String refreshCustomToolsInToolBar(String initial) {
		int macroCount = kernel.getMacroNumber();

		// add the ones that have (showInToolbar == true) into the toolbar if
		// they are not already there.

		StringBuilder customToolBar = new StringBuilder("");

		for (int i = 0; i < macroCount; i++) {
			Macro macro = kernel.getMacro(i);
			int macroMode = EuclidianConstants.MACRO_MODE_ID_OFFSET + i;
			int macroViewId = macro.getViewId() != null ? macro.getViewId()
					: App.VIEW_EUCLIDIAN;
			int activeViewId = getActiveToolbarId();

			if (macro.isShowInToolBar()
					&& !(initial.contains(String.valueOf(macroMode)))
					&& (macroViewId == activeViewId)) {
				customToolBar.append(" ");
				customToolBar.append(macroMode);
			}
		}

		String toolbarDef = initial.trim();
		String last = "";
		try {
			// get the last tool mode number in the toolbar def string
			String[] tools = toolbarDef.split(" ");
			last = tools[tools.length - 1];
			int lastToolId = Integer.parseInt(last);

			if (lastToolId >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
				return toolbarDef + customToolBar.toString();
			}
			return "".equals(customToolBar.toString()) ? toolbarDef
					: toolbarDef + " ||" + customToolBar.toString();
		} catch (NumberFormatException e) {
			// could not identify the last tool so just add the custom tools
			// onto the end
			if (last.contains("|")) {
				return toolbarDef + customToolBar.toString();
			}
			return "".equals(customToolBar.toString()) ? toolbarDef
					: toolbarDef + " ||" + customToolBar.toString();
		}
	}

	@Override
	public void replaceInputSelection(String string) {
		// override this in platforms where needed
	}

	@Override
	public void setInputText(String string) {
		// override this in platforms where needed
	}

	/**
	 * Loads the image and sets its corners
	 * 
	 * @param geoImage
	 *            The image.
	 */
	@Override
	public void setImageCornersFromSelection(GeoImage geoImage) {
		getApp().getImageManager().setCornersFromSelection(geoImage, getApp());

		// make sure only the last image will be selected
		GeoElement[] geos = { geoImage };
		getApp().getActiveEuclidianView().getEuclidianController().clearSelections();
		getApp().getActiveEuclidianView().getEuclidianController()
				.memorizeJustCreatedGeos(geos);
		if (!app.isWhiteboardActive()) {
			getApp().setMoveMode();
		}
		getApp().getActiveEuclidianView().resetMode();

	}

	@Override
	public TableValuesPoints getTableValuesPoints() {
		return tableValuesPoints;
	}

	@Override
	public TableValues getTableValuesView() {
		if (tableValues == null) {
			tableValues = createTableValuesView();
			kernel.attach(tableValues);
			TableValuesModel model = tableValues.getTableValuesModel();
			tableValuesPoints = TableValuesPointsImpl.create(kernel.getConstruction(),
					tableValues, model);
			kernel.notifyAddAll(tableValues);
		}
		return tableValues;
	}

	public TableValues getTableValuesViewOrNull() {
		return tableValues;
	}

	/**
	 * Create a table values view..
	 *
	 * @return TableValuesView
	 */
	protected TableValuesView createTableValuesView() {
		return new TableValuesView(kernel);
	}

	@Override
	public void closeFullscreenView() {
		// only needed in web
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		updateMenubar();
	}

	@Override
	public InputKeyboardButton getInputKeyboardButton() {
		return null;
	}

}
