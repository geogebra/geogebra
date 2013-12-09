/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.gui;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.common.gui.view.data.PlotPanelEuclidianViewInterface;
import geogebra.common.gui.view.probcalculator.ProbabilityCalcualtorView;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.GuiManagerInterface;
import geogebra.common.main.settings.ConstructionProtocolSettings;
import geogebra.common.main.settings.ProbabilityCalculatorSettings.DIST;
import geogebra.common.util.debug.Log;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class GuiManager implements GuiManagerInterface {

	public enum Help {
		COMMAND, TOOL, GENERIC
	}

	private static final String ggbTube = "geogebratube.org/";
	private static final String ggbTubeShort = "ggbtu.be/";
	private static final String ggbTubeTest = "test.geogebratube.org";	
	private static final String material = "/material/show/id/";
	public static final int DESKTOP = 0;
	public static final int WEB = 1;
	public static final int TOUCH = 2;
	
	protected Kernel kernel;
	protected App app;
	
	/**
	 * Abstract constructor
	 */
	public GuiManager () {
		setCallerApp();
	}
	

	public void updateMenubar() { } // temporarily nothing

	public boolean hasAlgebraView() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isUsingConstructionProtocol() {
		// TODO Auto-generated method stub
		return false;
	}

	public void getConsProtocolXML(StringBuilder sb) {
		// TODO Auto-generated method stub
		
	}

	public boolean hasProbabilityCalculator() {
		// TODO Auto-generated method stub
		return false;
	}

	public void getProbabilityCalculatorXML(StringBuilder sb) {
		// TODO Auto-generated method stub
		
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
	 * in an iframe, src=
	 * http://www.geogebratube.org/material/iframe/id/111
	 * http://www.geogebratube.org/material/iframe/id/111/param1/val1/param2/val2/...
	 * http://ggbtu.be/e111
	 * http://ggbtu.be/e111?param1=&param2=..
	 * 
	 * 
	 * also can have ?mobile=true ?mobile=false on end
	 */
	public boolean loadURL(String urlString, boolean suppressErrorMsg) {
		String processedUrlString = urlString.trim();
	
		boolean success = false;
		boolean isMacroFile = false;
		getApp().setWaitCursor();
	
		try {
			// check first for ggb/ggt file
			if ((processedUrlString.endsWith(".ggb") || processedUrlString.endsWith(".ggt"))
					&& (processedUrlString.indexOf("?") == -1)) { // This isn't a ggb file,
														// however ends with ".ggb":
														// http://www.geogebra.org/web/test42/?f=_circles5.ggb
				loadURL_GGB(processedUrlString);
				
	
				// special case: urlString is from GeoGebraTube
				// eg http://www.geogebratube.org/student/105 changed to
				// http://www.geogebratube.org/files/material-105.ggb	
			} else if (processedUrlString.indexOf(ggbTube) > -1
					|| processedUrlString.indexOf(ggbTubeShort) > -1
					|| processedUrlString.indexOf(ggbTubeTest) > -1) {
	
				// remove eg http:// if it's there
				if (processedUrlString.indexOf("://") > -1) {
					processedUrlString = processedUrlString.substring(
							processedUrlString.indexOf("://") + 3, processedUrlString.length());
				}
				// remove hostname
				processedUrlString = processedUrlString.substring(processedUrlString.indexOf('/'),
						processedUrlString.length());
	
				// remove ?mobile=true or ?mobile=false on end
				if (processedUrlString.endsWith("?mobile=true") || processedUrlString.endsWith("?mobile=false") ) {
					int i = processedUrlString.lastIndexOf('?');
					processedUrlString = processedUrlString.substring(0, i);
				}
	
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
					App.debug("problem parsing: " + processedUrlString);
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
				
				if (urlString.indexOf(ggbTubeTest) > -1) {
					processedUrlString = "http://test.geogebratube.org:8080/files/material-";
				} else {
					processedUrlString = "http://www.geogebratube.org/files/material-";
				}
				
				// Add the login token to assure that private files of the loggen in user can be accessed
				processedUrlString += id + ".ggb";
				if (app.getLoginOperation().isLoggedIn()) {
					String token = app.getLoginOperation().getModel().getLoggedInUser().getLoginToken();
					if (token != null) {
						processedUrlString += "?lt=" + token;
					}
				}
	
				App.debug(processedUrlString);
				success = loadURL_GGB(processedUrlString);
	
				// special case: urlString is actually a base64 encoded ggb file
			} else if (processedUrlString.startsWith("UEs")) {
				success = loadURL_base64(processedUrlString);
	
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
			getApp().showError(getApp().getLocalization().getError("LoadFileFailed") + "\n" + processedUrlString);
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
		case App.VIEW_PYTHON:
			App.debug("TODO: how to attach Python view?");
			break;
		default: 
			App.error("Error attaching VIEW: "+viewId);
		}
	}
	
	public void showAxesCmd() {
		// get ev with focus
		EuclidianViewInterfaceCommon ev = getActiveEuclidianView();
	
		boolean bothAxesShown = ev.getShowXaxis() && ev.getShowYaxis();
		App app = getApp();
		if (app.getEuclidianView1() == ev)
			app.getSettings().getEuclidian(1)
					.setShowAxes(!bothAxesShown, !bothAxesShown);
		else if (!app.hasEuclidianView2EitherShowingOrNot())
			ev.setShowAxes(!bothAxesShown, true);
		else if (app.getEuclidianView2() == ev)
			app.getSettings().getEuclidian(2)
					.setShowAxes(!bothAxesShown, !bothAxesShown);
		else
			ev.setShowAxes(!bothAxesShown, true);
	
		ev.repaint();
		app.storeUndoInfo();
		app.updateMenubar();
	}

	public void showGridCmd() {
		// get ev with focus
		EuclidianView ev = getActiveEuclidianView();
		App app = getApp();
		if (app.getEuclidianView1() == ev)
			app.getSettings().getEuclidian(1)
					.showGrid(!ev.getShowGrid());
		else if (!app.hasEuclidianView2EitherShowingOrNot())
			ev.showGrid(!ev.getShowGrid());
		else if (app.getEuclidianView2() == ev)
			app.getSettings().getEuclidian(2)
					.showGrid(!ev.getShowGrid());
		else
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
			App.debug("TODO: should we detach EV1/2?");
			break;
		case App.VIEW_PYTHON:
			App.debug("TODO: how to detach Python view?");
			break;
		case App.VIEW_EUCLIDIAN3D:
			App.debug("TODO: should we detach EV3D?");
			break;
		default: 
			App.error("Error detaching VIEW: "+viewId);
		}
	}

	public void openCommandHelp(String command) {
		String internalCmd = null;
		if (command != null)
			try { // convert eg uppersum to UpperSum
				internalCmd = getApp().getReverseCommand(command);
			} catch (Exception e) {
			}
	
		openHelp(internalCmd, Help.COMMAND);
	}

	protected abstract void openHelp(String internalCmd, Help command);

	public void openHelp(String page) {
		openHelp(page, Help.GENERIC);
	}

	public void setShowConstructionProtocolNavigation(boolean show) {
		getConstructionProtocolNavigation().setVisible(show);
		
		if (show) {
			if (getApp().getActiveEuclidianView() != null)
				getApp().getActiveEuclidianView().resetMode();
			getConstructionProtocolView();
		}
	}

	public void setShowConstructionProtocolNavigation(boolean show,
			boolean playButton, double playDelay, boolean showProtButton) {
				setShowConstructionProtocolNavigation(show);
				
				getConstructionProtocolNavigation().setPlayButtonVisible(playButton);
				getConstructionProtocolNavigation().setPlayDelay(playDelay);
				if (getApp().isFullAppGui()){
					getConstructionProtocolNavigation().setConsProtButtonVisible(showProtButton);
				} else {
					getConstructionProtocolNavigation().setConsProtButtonVisible(false);
				}
			}
	protected ConstructionProtocolNavigation constProtocolNavigation;
	
	/**
	 * Returns the construction protocol navigation bar instance.
	 */
	public abstract ConstructionProtocolNavigation getConstructionProtocolNavigation();

	/**
	 * Returns the construction protocol navigation bar instance or null, if it not exists.
	 */
	public ConstructionProtocolNavigation getConstructionProtocolNavigationIfExists(){
		return constProtocolNavigation;
	}
	
	public void updateCheckBoxesForShowConstructinProtocolNavigation() {
		App.debug("GuiManager.updateCheckBoxesForShowConstructionProtocolNavigation - implementation needed");		
	}
	
	public void applyCPsettings(ConstructionProtocolSettings cps){
		if(constProtocolNavigation == null){
			return;
		}
		constProtocolNavigation.setConsProtButtonVisible(cps.showConsProtButton());
		constProtocolNavigation.setPlayDelay(cps.getPlayDelay());
		constProtocolNavigation.setPlayButtonVisible(cps.showPlayButton());
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
		protected ProbabilityCalcualtorView probCalculator;
		protected int caller_APP;
		
		public void setMode(int mode,ModeSetter m) {

			setModeFinished = false;
			
			// can't move this after otherwise Object Properties doesn't work
			kernel.notifyModeChanged(mode,m);
			//notifyModeChanged called another setMode => nothing to do here
			if(setModeFinished)
				return;
			// select toolbar button, returns *actual* mode selected - only for desktop
			if (caller_APP == DESKTOP) {
				int newMode = setToolbarMode(mode);

				if (mode != EuclidianConstants.MODE_SELECTION_LISTENER && newMode != mode) {
					mode = newMode;
					kernel.notifyModeChanged(mode,m);
				}
			}
			if (mode == EuclidianConstants.MODE_PROBABILITY_CALCULATOR) {

				// show or focus the probability calculator
					if (showView(App.VIEW_PROBABILITY_CALCULATOR)) {
						this
								.getLayout()
								.getDockManager()
								.setFocusedPanel(
										App.VIEW_PROBABILITY_CALCULATOR);
					} else {
						setShowView(true,
								App.VIEW_PROBABILITY_CALCULATOR);
						probCalculator.setProbabilityCalculator(
								DIST.NORMAL, null,
								false);
					}

				// nothing more to do, so reset to move mode
				app.setMoveMode();
			}
			
			if (mode == EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS
					|| mode == EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS
					|| mode == EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS) {
				// save the selected geos so they can be re-selected later
				ArrayList<GeoElement> temp = new ArrayList<GeoElement>();
				if(app.getSelectionManager().getSelectedGeos() != null){
					for(GeoElement geo : app.getSelectionManager().getSelectedGeos()){
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
		protected int setToolbarMode(int mode) {
			return 0;
			//should be implemented in subclasses if needed
		};
		
		/**
		 * setst the caller app to the prober value
		 */
		protected abstract void setCallerApp();
}
