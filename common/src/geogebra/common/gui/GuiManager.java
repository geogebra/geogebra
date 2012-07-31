/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.gui;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;

import java.util.ArrayList;

public abstract class GuiManager {

	private static final String ggbTube = "geogebratube.org/";
	private static final String ggbTubeShort = "ggbtu.be/";
	private static final String material = "/material/show/id/";
	protected String strCustomToolbarDefinition;
	public App app;

	public void updateMenubar() { } // temporarily nothing

	public abstract void updateMenubarSelection();

	public abstract DialogManager getDialogManager();

	public abstract void showPopupMenu(ArrayList<GeoElement> selectedGeos,
			EuclidianViewInterfaceCommon euclidianViewInterfaceCommon,
			GPoint mouseLoc);
	
	public abstract void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
			ArrayList<GeoElement> geos, EuclidianViewInterfaceCommon view,
			GPoint p);

	public abstract void setMode(int mode);

	public abstract void redo();
	public abstract void undo();

	public abstract void setFocusedPanel(AbstractEvent event, boolean updatePropertiesView);

	public abstract void loadImage(GeoPoint loc, Object object, boolean altDown);

	public boolean hasAlgebraView() {
		// TODO Auto-generated method stub
		return false;
	}

	public abstract void updateFonts();

	public boolean isUsingConstructionProtocol() {
		// TODO Auto-generated method stub
		return false;
	}

	public void getConsProtocolXML(StringBuilder sb) {
		// TODO Auto-generated method stub
		
	}

	public abstract boolean isInputFieldSelectionListener();

	public abstract geogebra.common.javax.swing.GTextComponent getAlgebraInputTextField();

	public abstract void showDrawingPadPopup(EuclidianViewInterfaceCommon view,
			GPoint mouseLoc);

	public abstract boolean hasSpreadsheetView();

	public abstract void attachSpreadsheetView();

	public abstract void setShowView(boolean b, int viewSpreadsheet);
	
	public abstract void setShowView(boolean b, int viewSpreadsheet, boolean isPermanent);

	public abstract boolean showView(int viewSpreadsheet);

	public abstract View getConstructionProtocolData();
	
	public abstract View getCasView();
	
	public abstract View getSpreadsheetView();
	
	public abstract View getProbabilityCalculator();
	
	public abstract View getDataAnalysisView();
	
	public abstract View getPlotPanelView(int id);
	
	public abstract View getPropertiesView();

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

	public abstract void updateSpreadsheetColumnWidths();

	public void updateConstructionProtocol() {
		// TODO Auto-generated method stub
		
	}

	public abstract void updateAlgebraInput();

	public abstract void setShowAuxiliaryObjects(boolean flag);

	public abstract void updatePropertiesView();
	
	/**
	 * tells the properties view that mouse has been pressed
	 */
	public abstract void mousePressedForPropertiesView();	
	
	/**
	 * tells the properties view that mouse has been released
	 * @param creatorMode tells if ev is in creator mode (ie not move mode)
	 */
	public abstract void mouseReleasedForPropertiesView(boolean creatorMode);
	
	public abstract boolean save();

	/**
	 * tells the properties view to show slider tab
	 */
	public abstract void showPropertiesViewSliderTab();
	
	public abstract void openURL();

	public boolean loadURL(String urlString) {
		return loadURL(urlString, true);
	}

	public void setToolBarDefinition(String toolBarDefinition) {
		strCustomToolbarDefinition = toolBarDefinition;
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
		urlString = urlString.trim();
	
		boolean success = false;
		boolean isMacroFile = false;
		app.setWaitCursor();
	
		try {
			// check first for ggb/ggt file
			if ((urlString.endsWith(".ggb") || urlString.endsWith(".ggt"))
					&& (urlString.indexOf("?") == -1)) { // This isn't a ggb file,
														// however ends with ".ggb":
														// http://www.geogebra.org/web/test42/?f=_circles5.ggb
				loadURL_GGB(urlString);
				
	
				// special case: urlString is from GeoGebraTube
				// eg http://www.geogebratube.org/student/105 changed to
				// http://www.geogebratube.org/files/material-105.ggb	
			} else if (urlString.indexOf(ggbTube) > -1
					|| urlString.indexOf(ggbTubeShort) > -1) {
	
				// remove eg http:// if it's there
				if (urlString.indexOf("://") > -1) {
					urlString = urlString.substring(
							urlString.indexOf("://") + 3, urlString.length());
				}
				// remove hostname
				urlString = urlString.substring(urlString.indexOf('/'),
						urlString.length());
	
				// remove ?mobile=true or ?mobile=false on end
				if (urlString.endsWith("?mobile=true") || urlString.endsWith("?mobile=false") ) {
					int i = urlString.lastIndexOf('?');
					urlString = urlString.substring(0, i);
				}
	
				String id;
	
				// determine the start position of ID in the URL
				int start = -1;
	
				if (urlString.startsWith(material)) {
					start = material.length();
				} else {
					start = urlString.lastIndexOf("/m") + 2;
				}
	
				// no valid URL?
				if (start == -1) {
					App.debug("problem parsing: " + urlString);
					return false;
				}
	
				// the end position is either before the next slash or at the
				// end of the string
				int end = -1;
				if (start > -1) {
					end = urlString.indexOf('/', start);
				}
	
				if (end == -1) {
					end = urlString.length();
				}
	
				// fetch ID
				id = urlString.substring(start, end);
	
				urlString = "http://www.geogebratube.org/files/material-" + id
						+ ".ggb";
	
				App.debug(urlString);
				success = loadURL_GGB(urlString);
	
				// special case: urlString is actually a base64 encoded ggb file
			} else if (urlString.startsWith("UEs")) {
				success = loadURL_base64(urlString);
	
				// special case: urlString is actually a GeoGebra XML file
			} else if (urlString.startsWith("<?xml ")
					&& urlString.endsWith("</geogebra>")) {
				success = app.loadXML(urlString);
	
				// 'standard' case: url with GeoGebra applet (Java or HTML5)
			} else {
				// try to load from GeoGebra applet
				loadFromApplet(urlString);
				isMacroFile = urlString.contains(".ggt");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	
		if (!success && !suppressErrorMsg) {
			app.showError(app.getError("LoadFileFailed") + "\n" + urlString);
		}
	
		updateGUIafterLoadFile(success, isMacroFile);
	
		app.setDefaultCursor();
		return success;
	}
	
	protected abstract boolean loadURL_GGB(String url) throws Exception;
	protected abstract boolean loadURL_base64(String url) throws Exception;
	protected abstract boolean loadFromApplet(String url) throws Exception;
	public abstract void updateGUIafterLoadFile(boolean success, boolean isMacroFile);

	public abstract void startEditing(GeoElement geoElement);

	public abstract boolean noMenusOpen();

	public abstract void openFile();

	public abstract Layout getLayout();

	public abstract void showGraphicExport();

	public abstract void showPSTricksExport();

	public abstract void showWebpageExport();

	public abstract void detachPropertiesView();

	public abstract boolean hasPropertiesView();

	public abstract void attachPropertiesView();

	public abstract void attachAlgebraView();

	public abstract void attachCasView();

	public abstract void attachConstructionProtocolView();

	public abstract void attachProbabilityCalculatorView();
	
	public abstract void attachAssignmentView();
	
	public abstract void attachDataAnalysisView();

	public abstract void detachDataAnalysisView();
	
	public abstract boolean hasDataAnalysisView();
	

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
			// TODO: understand what to do here
			break;
		default: 
			App.error("Error attaching VIEW: "+viewId);
		}
	}
	
	public abstract EuclidianView getActiveEuclidianView();

	public void showAxesCmd() {
		// get ev with focus
		EuclidianViewInterfaceCommon ev = getActiveEuclidianView();
	
		boolean bothAxesShown = ev.getShowXaxis() && ev.getShowYaxis();
	
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
			app.getTraceManager().addSpreadsheetTraceGeo(geo);
		}
	}

	



}
