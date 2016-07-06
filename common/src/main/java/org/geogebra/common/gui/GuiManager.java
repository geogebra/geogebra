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
import java.util.Iterator;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.gui.view.data.PlotPanelEuclidianViewInterface;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.DIST;
import org.geogebra.common.util.debug.Log;

public abstract class GuiManager implements GuiManagerInterface {

	public enum Help {
		COMMAND, TOOL, GENERIC
	}

	/**
	 * possible GeoGebraTube syntaxes
	 * http://www.geogebratube.org/material/show/id/111
	 * http://www.geogebratube.org/student/m111
	 * http://www.geogebratube.org/student/cXX/m111/options
	 * www.geogebratube.org/material/show/id/111
	 * www.geogebratube.org/student/m111
	 * www.geogebratube.org/student/cXX/m111/options
	 * http://geogebratube.org/material/show/id/111
	 * http://geogebratube.org/student/m111
	 * http://geogebratube.org/student/cXX/m111/options http://ggbtu.be/m111
	 * http://ggbtu.be/cXX/m111/options http://www.ggbtu.be/m111
	 * http://www.ggbtu.be/cXX/options
	 * 
	 * in an iframe, src= http://www.geogebratube.org/material/iframe/id/111
	 * http
	 * ://www.geogebratube.org/material/iframe/id/111/param1/val1/param2/val2
	 * /... http://ggbtu.be/e111 http://ggbtu.be/e111?param1=&param2=..
	 * 
	 * 
	 * also can have ?mobile=true ?mobile=false on end
	 */
	private static final String ggbTubeOld = "geogebratube.org/";
	private static final String ggbTube = "tube.geogebra.org/";
	private static final String ggbTubeBeta = "beta.geogebra.org/";
	private static final String ggbTubeShort = "ggbtu.be/";
	private static final String ggbMatShort = "ggbm.at/";
	private static final String material = "/material/show/id/";
	public static final int DESKTOP = 0;
	public static final int WEB = 1;
	public static final int TOUCH = 2;

	protected Kernel kernel;
	protected App app;

	/**
	 * Abstract constructor
	 */
	public GuiManager() {
		setCallerApp();
	}

	public void updateMenubar() {
	} // temporarily nothing

	public boolean hasAlgebraView() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isUsingConstructionProtocol() {
		// TODO Auto-generated method stub
		return false;
	}

	public final void getConsProtocolXML(StringBuilder sb) {
		if (this.isUsingConstructionProtocol())
			sb.append(getConstructionProtocolView().getConsProtocolXML());
		if (app.showConsProtNavigation()) {
			sb.append("\t<consProtNavigationBar ");
			sb.append("id=\"");
			app.getConsProtNavigationIds(sb);
			sb.append('\"');
			sb.append(" playButton=\"");
			sb.append(getConstructionProtocolNavigation().isPlayButtonVisible());
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

	public boolean hasProbabilityCalculator() {
		// TODO Auto-generated method stub
		return false;
	}

	public void getProbabilityCalculatorXML(StringBuilder sb) {
		if (probCalculator != null)
			probCalculator.getXML(sb);
	}

	public void getSpreadsheetViewXML(StringBuilder sb, boolean asPreference) {
		// TODO Auto-generated method stub

	}

	public void updateActions() {
		// TODO Auto-generated method stub

	}

	public void updateConstructionProtocol() {
		// TODO Auto-generated method stub

	}

	public boolean loadURL(String urlString) {
		return loadURL(urlString, true);
	}

	public boolean loadURL(String urlString, boolean suppressErrorMsg) {
		String processedUrlString = urlString.trim();

		boolean success = false;
		boolean isMacroFile = false;
		getApp().setWaitCursor();

		try {
			// check first for ggb/ggt file
			if ((processedUrlString.endsWith(".ggb") || processedUrlString
					.endsWith(".ggt"))
					&& (processedUrlString.indexOf("?") == -1)) { // This isn't
																	// a ggb
																	// file,
				// however ends with ".ggb":
				// http://www.geogebra.org/web/test42/?f=_circles5.ggb
				loadURL_GGB(processedUrlString);

				// special case: urlString is from GeoGebraTube
				// eg http://www.geogebratube.org/student/105 changed to
				// http://www.geogebratube.org/files/material-105.ggb
			} else if (processedUrlString
					.indexOf(GeoGebraConstants.GEOGEBRA_WEBSITE) > -1
					|| processedUrlString.indexOf(
							GeoGebraConstants.GEOGEBRA_WEBSITE_BETA) > -1
					|| processedUrlString.indexOf(ggbTube) > -1
					|| processedUrlString.indexOf(ggbTubeShort) > -1
					|| processedUrlString.indexOf(ggbMatShort) > -1
					|| processedUrlString.indexOf(ggbTubeBeta) > -1
					|| processedUrlString.indexOf(ggbTubeOld) > -1) {

				// remove eg http:// if it's there
				if (processedUrlString.indexOf("://") > -1) {
					processedUrlString = processedUrlString.substring(
							processedUrlString.indexOf("://") + 3,
							processedUrlString.length());
				}
				// remove hostname
				processedUrlString = processedUrlString.substring(
						processedUrlString.indexOf('/'),
						processedUrlString.length());

				String id;

				// determine the start position of ID in the URL
				int start = -1;

				if (processedUrlString.startsWith(material)) {
					start = material.length();
				} else {
					start = processedUrlString.lastIndexOf("/m") + 2;
				}

				// no valid URL?
				if (start == -1) {
					Log.debug("problem parsing: " + processedUrlString);
					return false;
				}

				// the end position is either before the next slash or at the
				// end of the string
				int end = -1;
				if (start > -1) {
					end = processedUrlString.indexOf('/', start);
				}

				if (end == -1) {
					end = processedUrlString.length();
				}

				// fetch ID
				id = processedUrlString.substring(start, end);

				processedUrlString = "https://www.geogebra.org/files/material-";

				// Add the login token to assure that private files of the
				// logged in user can be accessed
				processedUrlString += id + ".ggb";
				if (app.getLoginOperation().isLoggedIn()) {
					String token = app.getLoginOperation().getModel()
							.getLoggedInUser().getLoginToken();
					if (token != null) {
						processedUrlString += "?lt=" + token;
					}
				}

				Log.debug(processedUrlString);
				success = loadURL_GGB(processedUrlString);

				// special case: urlString is actually a base64 encoded ggb file
			} else if (processedUrlString.startsWith("UEs")) {
				success = loadURL_base64(processedUrlString.replace("\\/", "/"));

				// special case: urlString is actually a GeoGebra XML file
			} else if (processedUrlString.startsWith("<?xml ")
					&& processedUrlString.endsWith("</geogebra>")) {
				success = getApp().loadXML(processedUrlString);

				// 'standard' case: url with GeoGebra applet (Java or HTML5)
			} else {
				// try to load from GeoGebra applet
				success = loadFromApplet(processedUrlString);
				isMacroFile = processedUrlString.contains(".ggt");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (!success && !suppressErrorMsg) {
			getApp().showError(
					getApp().getLocalization().getError("LoadFileFailed")
							+ "\n" + processedUrlString);
		}

		updateGUIafterLoadFile(success, isMacroFile);
		return success;
	}

	protected abstract App getApp();

	protected abstract boolean loadURL_GGB(String url) throws Exception;

	protected abstract boolean loadURL_base64(String url) throws Exception;

	protected abstract boolean loadFromApplet(String url) throws Exception;

	/**
	 * Attach a view which by using the view ID.
	 * 
	 * @author Florian Sonner
	 * @version 2008-10-21
	 * 
	 * @param viewId
	 */
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
		case App.VIEW_ASSIGNMENT:
			attachAssignmentView();
			break;
		case App.VIEW_PROPERTIES:
			attachPropertiesView();
			break;
		case App.VIEW_EUCLIDIAN:
		case App.VIEW_EUCLIDIAN2:
			// handled elsewhere
			break;
		default:
			if (App.isView3D(viewId)) {
				// handled elsewhere
			} else {
				Log.error("Error attaching VIEW: " + viewId);
			}
		}
	}

	public void showAxesCmd() {
		// get ev with focus
		EuclidianViewInterfaceCommon ev = getActiveEuclidianView();

		boolean bothAxesShown = ev.getShowXaxis() && ev.getShowYaxis();
		App app = getApp();
		if (app.getEuclidianView1() == ev) {
			app.getSettings().getEuclidian(1)
					.setShowAxes(!bothAxesShown, !bothAxesShown);

		} else if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == ev) {
			app.getSettings().getEuclidian(2)
					.setShowAxes(!bothAxesShown, !bothAxesShown);
		} else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == ev) {
			app.getSettings().getEuclidian(3).setShowAxes(!bothAxesShown);

		} else
			ev.setShowAxes(!bothAxesShown, true);

		ev.repaint();
		app.storeUndoInfo();
		app.updateMenubar();
	}

	public void showGridCmd() {
		// get ev with focus
		EuclidianView ev = getActiveEuclidianView();
		App app = getApp();
		if (app.getEuclidianView1() == ev) {
			app.getSettings().getEuclidian(1).showGrid(!ev.getShowGrid());

		} else if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == ev) {
			app.getSettings().getEuclidian(2).showGrid(!ev.getShowGrid());
		} else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == ev) {
			app.getSettings().getEuclidian(3).showGrid(!ev.getShowGrid());

		} else
			ev.showGrid(!ev.getShowGrid());

		ev.repaint();
		app.storeUndoInfo();
		app.updateMenubar();
	}

	public void doAfterRedefine(GeoElement geo) {

		// G.Sturr 2010-6-28
		// if a tracing geo has been redefined, then put it back into the
		// traceGeoCollection
		if (geo.getSpreadsheetTrace()) {
			getApp().getTraceManager().addSpreadsheetTraceGeo(geo);
		}
	}

	/**
	 * Detach a view which by using the view ID.
	 * 
	 * @author Florian Sonner
	 * @version 2008-10-21
	 * 
	 * @param viewId
	 */
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
		case App.VIEW_ASSIGNMENT:
			detachAssignmentView();
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

	public void openCommandHelp(String command) {
		String internalCmd = null;
		if (command != null)
			try { // convert eg uppersum to UpperSum
				internalCmd = getApp().getReverseCommand(command);
			} catch (Exception e) {
				Log.warn("Command not found in dictionary:" + command);
			}

		openHelp(internalCmd, Help.COMMAND);
	}

	protected abstract void openHelp(String internalCmd, Help command);

	public void openHelp(String page) {
		openHelp(page, Help.GENERIC);
	}

	public void setShowConstructionProtocolNavigation(boolean show, int id) {
		getConstructionProtocolNavigation(id).setVisible(show);

		if (show) {
			if (getApp().getActiveEuclidianView() != null)
				getApp().getActiveEuclidianView().resetMode();
			getConstructionProtocolView();
		}
	}

	public void setShowConstructionProtocolNavigation(boolean show, int id,
			boolean playButton, double playDelay, boolean showProtButton) {
		setShowConstructionProtocolNavigation(show, id);

		getConstructionProtocolNavigation().setPlayButtonVisible(playButton);
		getConstructionProtocolNavigation().setPlayDelay(playDelay);
		getConstructionProtocolNavigation().setConsProtButtonVisible(
					showProtButton);
	}

	protected HashMap<Integer, ConstructionProtocolNavigation> constProtocolNavigationMap;

	public void updateNavBars() {
		if (constProtocolNavigationMap != null) {
			Iterator<ConstructionProtocolNavigation> it = constProtocolNavigationMap
					.values().iterator();

			while (it.hasNext()) {
				ConstructionProtocolNavigation navBar = it.next();
				navBar.update();
			}

		}
	}

	/**
	 * Returns the construction protocol navigation bar instance.
	 * 
	 * @param id
	 *            view id
	 * @return construction protocol for the view id
	 */
	final public ConstructionProtocolNavigation getConstructionProtocolNavigation(
			int id) {

		if (constProtocolNavigationMap == null) {
			constProtocolNavigationMap = new HashMap<Integer, ConstructionProtocolNavigation>();
		}

		ConstructionProtocolNavigation constProtocolNavigation = constProtocolNavigationMap
				.get(id);
		if (constProtocolNavigation == null) {
			constProtocolNavigation = newConstructionProtocolNavigation(id);
			constProtocolNavigationMap.put(id, constProtocolNavigation);
		}

		return constProtocolNavigation;
	}


	final public Collection<ConstructionProtocolNavigation> getAllConstructionProtocolNavigations() {
		if (constProtocolNavigationMap == null) {
			return null;
		}

		return constProtocolNavigationMap.values();
	}

	final public void setNavBarButtonPause() {
		if (constProtocolNavigationMap != null) {
			for (ConstructionProtocolNavigation cpn : constProtocolNavigationMap
					.values()) {
				cpn.setButtonPause();
			}
		}
	}

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

	/**
	 * Returns the default construction protocol navigation bar instance.
	 * 
	 * @return construction protocol for the view id
	 */
	public ConstructionProtocolNavigation getConstructionProtocolNavigation() {
		return getConstructionProtocolNavigation(App.VIEW_EUCLIDIAN);
	}

	/**
	 * Returns the construction protocol navigation bar instance or null, if it
	 * not exists.
	 */
	public ConstructionProtocolNavigation getConstructionProtocolNavigationIfExists() {
		if (constProtocolNavigationMap == null) {
			return null;
		}
		return constProtocolNavigationMap.get(App.VIEW_EUCLIDIAN);
	}

	public abstract void updateCheckBoxesForShowConstructinProtocolNavigation(
			int id);

	public void applyCPsettings(ConstructionProtocolSettings cps) {
		if (constProtocolNavigationMap == null) {
			return;
		}

		for (ConstructionProtocolNavigation constProtocolNavigation : constProtocolNavigationMap
				.values()) {
			constProtocolNavigation.setConsProtButtonVisible(cps
					.showConsProtButton());
			constProtocolNavigation.setPlayDelay(cps.getPlayDelay());
			constProtocolNavigation.setPlayButtonVisible(cps.showPlayButton());
		}
	}

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

	private HashMap<Integer, PlotPanelEuclidianViewInterface> plotPanelIDMap;
	private int lastUsedPlotPanelID = -App.VIEW_PLOT_PANEL;

	protected HashMap<Integer, PlotPanelEuclidianViewInterface> getPlotPanelIDMap() {
		if (plotPanelIDMap == null)
			plotPanelIDMap = new HashMap<Integer, PlotPanelEuclidianViewInterface>();
		return plotPanelIDMap;
	}

	/**
	 * Adds the given PlotPanelEuclidianView instance to the plotPanelIDMap and
	 * returns a unique viewID
	 * 
	 * @param plotPanel
	 * @return
	 */
	public int assignPlotPanelID(PlotPanelEuclidianViewInterface plotPanel) {
		lastUsedPlotPanelID--;
		int viewID = lastUsedPlotPanelID;
		getPlotPanelIDMap().put(viewID, plotPanel);
		Log.debug(viewID);
		return viewID;
	}

	public PlotPanelEuclidianViewInterface getPlotPanelView(int viewID) {
		return getPlotPanelIDMap().get(viewID);
	}

	private boolean setModeFinished;
	protected ProbabilityCalculatorView probCalculator;
	protected int caller_APP;

	public void setMode(int mode, ModeSetter m) {

		setModeFinished = false;

		// can't move this after otherwise Object Properties doesn't work
		kernel.notifyModeChanged(mode, m);
		// notifyModeChanged called another setMode => nothing to do here
		if (setModeFinished)
			return;
		// select toolbar button, returns *actual* mode selected - only for
		// desktop
		// if (caller_APP == DESKTOP) {
		int newMode = setToolbarMode(mode, m);

		if (mode != EuclidianConstants.MODE_SELECTION_LISTENER
				&& newMode != mode) {
			mode = newMode;
			kernel.notifyModeChanged(mode, m);
		}
		// }
		if (mode == EuclidianConstants.MODE_PROBABILITY_CALCULATOR) {

			// show or focus the probability calculator
			if (showView(App.VIEW_PROBABILITY_CALCULATOR)) {
				this.getLayout().getDockManager()
						.setFocusedPanel(App.VIEW_PROBABILITY_CALCULATOR);
			} else {
				setShowView(true, App.VIEW_PROBABILITY_CALCULATOR);
				probCalculator.setProbabilityCalculator(DIST.NORMAL, null,
						false);
			}

			// nothing more to do, so reset to move mode
			app.setMoveMode();
		}

		if ((mode == EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS
				|| mode == EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS
				|| mode == EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS) && m == ModeSetter.TOOLBAR) {
			// save the selected geos so they can be re-selected later
			ArrayList<GeoElement> temp = new ArrayList<GeoElement>();
			if (app.getSelectionManager().getSelectedGeos() != null) {
				for (GeoElement geo : app.getSelectionManager()
						.getSelectedGeos()) {
					temp.add(geo);
				}
			}

			if (app.getGuiManager() != null) {

				app.getDialogManager().showDataSourceDialog(mode, true);
				app.setMoveMode();
			}

			// reselect the geos
			app.getSelectionManager().setSelectedGeos(temp);
		}

		setModeFinished = true;
	}

	/**
	 * @param mode
	 * @return sets the toolbar's mode
	 */
	protected int setToolbarMode(int mode, ModeSetter m) {
		return 0;
		// should be implemented in subclasses if needed
	}

	/**
	 * sets the caller app to the prober value
	 */
	protected abstract void setCallerApp();

	final public String getHelpURL(final Help type, String pageName) {
		// try to get help for given language
		// eg http://www.geogebra.org/help/en_GB/cmd/FitLogistic

		final StringBuilder urlSB = new StringBuilder();

		urlSB.append(GeoGebraConstants.GEOGEBRA_WEBSITE);
		urlSB.append("help/");
		urlSB.append(app.getLocalization().getLanguage()); // eg en_GB

		switch (type) {
		case COMMAND:
			pageName = app.getEnglishCommand(pageName);
			urlSB.append("/cmd/");
			urlSB.append(pageName);
			break;
		case TOOL:
			urlSB.append("/tool/");
			urlSB.append(pageName);
			break;
		case GENERIC:
			// eg openHelp("Custom_Tools", Help.GENERIC)
			// returns http://www.geogebra.org/help/hu/article/Custom_Tools
			// wiki redirects to correct page
			// ie http://wiki.geogebra.org/hu/Egy%E9ni_eszk%F6z%F6k
			urlSB.append("/article/");
			urlSB.append(pageName);
			break;
		default:
			Log.error("Bad getHelpURL call");
		}

		return urlSB.toString();
	}

	public void redo() {
		app.setWaitCursor();
		kernel.redo();
		updateActions();
		app.resetPen();
		app.setDefaultCursor();
	}

	public void undo() {
		app.setWaitCursor();
		kernel.undo();
		updateActions();
		app.resetPen();
		app.setDefaultCursor();
	}

	public abstract String getToolbarDefinition();

	public abstract void setToolBarDefinition(String toolBarDefinition);

	public void refreshCustomToolsInToolBar() {
		int macroCount = kernel.getMacroNumber();

		// add the ones that have (showInToolbar == true) into the toolbar if
		// they are not already there.
		StringBuilder customToolBar = new StringBuilder("");
		String oldToolbar = getToolbarDefinition() == null ? ToolBar
				.getAllTools(app) : getToolbarDefinition();
		for (int i = 0; i < macroCount; i++) {
			Macro macro = kernel.getMacro(i);
			int macroMode = EuclidianConstants.MACRO_MODE_ID_OFFSET + i;
			if (macro.isShowInToolBar()
					&& !(oldToolbar.contains(String
							.valueOf(macroMode)))) {
				customToolBar.append(" " + macroMode);
			}
		}

		String toolbarDef = oldToolbar.trim();
		String last = "";
		try {
			// get the last tool mode number in the toolbar def string
			String[] tools = toolbarDef.split(" ");
			last = tools[tools.length - 1];
			int lastToolId = Integer.parseInt(last);

			if (lastToolId >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
				setToolBarDefinition(toolbarDef + customToolBar.toString());
			} else {
				setToolBarDefinition(toolbarDef + " ||"
						+ customToolBar.toString());
			}
		} catch (NumberFormatException e) {
			// could not identify the last tool so just add the custom tools
			// onto the end
			if (last.contains("|")) {
				setToolBarDefinition(toolbarDef + customToolBar.toString());
			} else {
				setToolBarDefinition(toolbarDef + " ||"
						+ customToolBar.toString());
			}
		}
	}

	public void replaceInputSelection(String string) {

	}

	public void setInputText(String string) {

	}

	private GeoPoint getImageCornerFromSelection(int index) {

		ArrayList<GeoElement> sel = app.getSelectionManager().getSelectedGeos();
		if (sel.size() > index) {
			GeoElement geo0 = sel.get(index);
			if (geo0.isGeoPoint()) {
				return (GeoPoint) geo0;
			}
		}
		return null;
	}

	public void ensure2ndCornerOnScreen(double x1, GeoPoint point) {
		double x2 = point.inhomX;
		EuclidianView ev = (EuclidianView) app.getActiveEuclidianView();
		double xmax = ev.toRealWorldCoordX((double) (ev.getWidth()) + 1);
		if (x2 > xmax) {
			point.setCoords((x1 + 9 * xmax) / 10, point.inhomY, 1);
			point.update();
		}

	}

	private void ensure1stCornerOnScreen(GeoPoint point) {
		EuclidianView ev = (EuclidianView) app.getActiveEuclidianView();
		double xmin = ev.toRealWorldCoordX(0.0);
		double xmax = ev.toRealWorldCoordX((double) (ev.getWidth()) + 1);
		double ymin = ev.toRealWorldCoordY(0.0);
		double ymax = ev.toRealWorldCoordY((double) (ev.getHeight()) + 1);
		point.setCoords(xmin + (xmax - xmin) / 5, ymax - (ymax - ymin) / 5,
				1.0);
		point.update();
	}

	/**
	 * Loads the image and sets its corners
	 * 
	 * @param fileName
	 *            The image file name.
	 */
	public void setImageCornersFromSelection(GeoImage geoImage) {

		ArrayList<GeoPoint> corners = new ArrayList<GeoPoint>();
		for (int i = 0; i < 3; i++) {
			GeoPoint p = getImageCornerFromSelection(i);
			if (p != null) {
				corners.add(p);
			}
		}

		GeoPoint point1 = null;

		if (corners.size() == 0) {
			point1 = new GeoPoint(app.getKernel().getConstruction());
			point1.setCoords(0, 0, 1.0);
			point1.setLabel(null);
			ensure1stCornerOnScreen(point1);
			corners.add(point1);
		} else if (corners.size() == 1) {
			point1 = corners.get(0);
		}

		for (int i = 0; i < corners.size(); i++) {
			geoImage.setCorner(corners.get(i), i);
		}

		if (corners.size() < 2) {
			GeoPoint point2 = new GeoPoint(app.getKernel().getConstruction());
			geoImage.calculateCornerPoint(point2, 2);
			geoImage.setCorner(point2, 1);
			point2.setLabel(null);

			// make sure 2nd corner is on screen
			ensure2ndCornerOnScreen(point1.inhomX, point2);
		}
		geoImage.setLabel(null);
		//
		GeoImage.updateInstances(app);
		// }
		// // make sure only the last image will be selected
		GeoElement[] geos = { geoImage };
		app.getActiveEuclidianView().getEuclidianController().clearSelections();
		app.getActiveEuclidianView().getEuclidianController()
				.memorizeJustCreatedGeos(geos);
		app.setMoveMode();
		app.getActiveEuclidianView().resetMode();

	}

}
