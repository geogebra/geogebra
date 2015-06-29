/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package org.geogebra.common.gui.view.properties;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.common.gui.dialog.options.OptionsObject;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;

/**
 * Properties view
 * 
 */
public abstract class PropertiesView implements View {
	protected static HashMap<Integer, OptionType> viewMap = new HashMap<Integer, OptionType>();
	// map to match view ID with OptionType
	static {

		viewMap = new HashMap<Integer, OptionType>();
		viewMap.put(App.VIEW_CAS, OptionType.CAS);
		viewMap.put(App.VIEW_SPREADSHEET, OptionType.SPREADSHEET);
		viewMap.put(App.VIEW_EUCLIDIAN, OptionType.EUCLIDIAN);
		viewMap.put(App.VIEW_EUCLIDIAN2, OptionType.EUCLIDIAN2);
		viewMap.put(App.VIEW_EUCLIDIAN3D, OptionType.EUCLIDIAN3D);
		viewMap.put(App.VIEW_EUCLIDIAN_FOR_PLANE_START,
				OptionType.EUCLIDIAN_FOR_PLANE);
	}

	protected Kernel kernel;
	protected boolean attached;
	protected App app;
	protected final Localization loc;
	protected OptionType selectedOptionType = OptionType.EUCLIDIAN;
	protected OptionsObject objectPanel;

	public PropertiesView(App app2) {
		app = app2;
		kernel = app.getKernel();
		loc = app.getLocalization();
	}

	/**
	 * Update selection
	 */
	public abstract void updateSelection();

	/**
	 * update the properties view as if geos where selected
	 * 
	 * @param geos
	 *            geos
	 */
	public abstract void updateSelection(ArrayList<GeoElement> geos);

	/**
	 * Sets and shows the option panel for the given option type
	 * 
	 * @param type
	 *            type
	 */
	final public void setOptionPanel(OptionType type) {

		ArrayList<GeoElement> geos = removeAllConstants(app
				.getSelectionManager().getSelectedGeos());

		if (type == OptionType.OBJECTS) {// ensure that at least one geo is
											// selected
			if (geos.size() == 0) {
				GeoElement geo = app.getSelectionManager()
						.setFirstGeoSelectedForPropertiesView();
				if (geo == null) {
					// does nothing: stay in same panel
					return;
				}

				// add this first geo
				geos.add(geo);

			}
		}

		setOptionPanel(type, geos);
	}

	protected void setOptionPanel(OptionType type, ArrayList<GeoElement> geos) {

		// App.printStacktrace("\ntype="+type+"\nisIniting="+isIniting);
		// App.printStacktrace("\ntype="+type+"\nisIniting="+isIniting+"\nsize="+app.getSelectedGeos().size());
		// App.debug("\ntype="+type+"\nisIniting="+isIniting+"\nsize="+app.getSelectedGeos().size()+"\ngeos="+geos);

		if (type == null) {
			return;
		}

		// update selection
		if (type == OptionType.OBJECTS) {
			if (geos != null) {
				updateObjectPanelSelection(geos);
			}

			setObjectsToolTip();

		}

		setOptionPanelWithoutCheck(type);
	}

	abstract protected void setObjectsToolTip();

	abstract protected void updateObjectPanelSelection(
			ArrayList<GeoElement> geos);

	protected ArrayList<GeoElement> removeAllConstants(
			ArrayList<GeoElement> geosList) {

		Construction.Constants firstConstant = Construction.Constants.NOT;

		// check if there is constants, remove it and remember what type
		ArrayList<GeoElement> geos = new ArrayList<GeoElement>();

		for (GeoElement geo : geosList) {
			Construction.Constants constant = kernel.getConstruction()
					.isConstantElement(geo);
			if (constant == Construction.Constants.NOT) {
				// add if not constant
				geos.add(geo);
			} else {
				// remember type
				if (firstConstant == Construction.Constants.NOT)
					firstConstant = constant;
			}
		}

		if (firstConstant != Construction.Constants.NOT)
			updateSelectedTab(firstConstant);

		return geos;

	}

	public abstract void setOptionPanel(OptionType type, int subType);

	public abstract void mousePressedForPropertiesView();

	public abstract void updatePropertiesView();

	public abstract void detachView();

	public abstract void attachView();

	public String getTypeString(OptionType type) {
		switch (type) {
		case DEFAULTS:
			return loc.getPlain("PreferencesOfA", loc.getPlain("Defaults"));
		case SPREADSHEET:
			return loc.getPlain("PreferencesOfA", loc.getPlain("Spreadsheet"));
		case EUCLIDIAN:
			return loc.getPlain("PreferencesOfA", loc.getPlain("DrawingPad"));
		case EUCLIDIAN2:
			return loc.getPlain("PreferencesOfA", loc.getPlain("DrawingPad2"));
		case EUCLIDIAN_FOR_PLANE:
			return loc.getPlain("PreferencesOfA", loc.getPlain("ExtraViews"));
		case EUCLIDIAN3D:
			return loc.getPlain("PreferencesOfA", loc.getPlain("GraphicsView3D"));
		case CAS:
			return loc.getPlain("PreferencesOfA", loc.getPlain("CAS"));
		case ADVANCED:
			return loc.getPlain("PreferencesOfA", app.getMenu("Advanced"));
		case OBJECTS:
		// return app.getMenu("Objects");
			return objectPanel == null ? app.getMenu("Objects") : objectPanel.getSelectionDescription();
		case LAYOUT:
			return loc.getPlain("PreferencesOfA", app.getMenu("Layout"));
		}
		return null;
	}

	/**
	 * @param app
	 * @param type
	 * @return short version of Option type string
	 */
	final public static String getTypeStringSimple(Localization loc, OptionType type) {
		switch (type) {
		case DEFAULTS:
			return loc.getPlain("Defaults");
		case SPREADSHEET:
			return loc.getPlain("Spreadsheet");
		case EUCLIDIAN:
			return loc.getPlain("DrawingPad");
		case EUCLIDIAN2:
			return loc.getPlain("DrawingPad2");
		case CAS:
			return loc.getPlain("CAS");
		case ADVANCED:
			return loc.getMenu("Advanced");
		case OBJECTS:
			return loc.getMenu("Objects");
			// return objectPanel.getSelectionDescription();
		case LAYOUT:
			return loc.getMenu("Layout");
		case EUCLIDIAN3D:
			return loc.getPlain("GraphicsView3D");
		case EUCLIDIAN_FOR_PLANE:
			return loc.getPlain("ExtraViews");

		}
		return null;
	}

	/**
	 * Updates the Title Bar
	 */
	protected abstract void updateTitleBar();

	/**
	 * @return type of option panel currently displayed
	 */
	public OptionType getSelectedOptionType() {
		return selectedOptionType;
	}

	/**
	 * @param app 
	 * @param type
	 *            Option panel type
	 * @return true if given Option panel is showing (or is instantiated but
	 *         hidden)
	 */
	public static boolean isOptionPanelAvailable(App app, OptionType type) {

		boolean isAvailable = true;

		switch (type) {
		case EUCLIDIAN:
			isAvailable = app.getGuiManager().showView(App.VIEW_EUCLIDIAN);
			break;
		case EUCLIDIAN2:
			isAvailable = app.getGuiManager().showView(App.VIEW_EUCLIDIAN2);
			break;
		case EUCLIDIAN_FOR_PLANE:
			isAvailable = app.hasEuclidianViewForPlaneVisible();
			break;
		case EUCLIDIAN3D:
			isAvailable = app.getGuiManager().showView(App.VIEW_EUCLIDIAN3D);
			break;
		case SPREADSHEET:
			isAvailable = app.getGuiManager().showView(App.VIEW_SPREADSHEET);
			break;
		case CAS:
			isAvailable = app.getGuiManager().showView(App.VIEW_CAS);
			break;
		case OBJECTS:
			// always available
			break;
		}
		return isAvailable;
	}
	
	@Override
	public void startBatchUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endBatchUpdate() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * acts when mouse has been released in euclidian controller
	 * 
	 * @param creatorMode
	 *            says if euclidian view is in creator mode (ie not move mode)
	 */
	public void mouseReleasedForPropertiesView(boolean creatorMode) {

		GeoElement geo;
		if (objectPanel == null) {
			geo = null;
		} else {
			geo = objectPanel.consumeGeoAdded();
		}

		if (app.getSelectionManager().selectedGeosSize() > 0) {
			// selected geo is the most important
			updatePropertiesViewCheckConstants(app.getSelectionManager()
					.getSelectedGeos());
		} else if (geo != null) { // last created geo
			if (creatorMode) { // if euclidian view is e.g. in move mode, then
				// geo was created by a script, so just show
				// object properties
				ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
				geos.add(geo);
				setOptionPanel(OptionType.OBJECTS, geos);
			} else {
				setOptionPanel(OptionType.OBJECTS, null);
			}
		} else { // focus
			updateSelectedTab(Construction.Constants.NOT);
			setOptionPanelRegardingFocus(true);
			// updatePropertiesView();
		}
	}
	
	/**
	 * Updates properties view panel. If geos are not empty then the Objects
	 * panel will be shown. If not, then an option pane for the current focused
	 * view is shown.
	 * 
	 * @param geosList
	 *            geos list
	 */
	protected void updatePropertiesViewCheckConstants(
			ArrayList<GeoElement> geosList) {

		// remove constant geos
		ArrayList<GeoElement> geos = removeAllConstants(geosList);

		updatePropertiesView(geos);
	}
	
	private void updatePropertiesView(ArrayList<GeoElement> geos) {

		if (geos.size() > 0) {
			if (!stayInCurrentPanel())
				setOptionPanel(OptionType.OBJECTS, geos);
		} else {

			setOptionPanelRegardingFocus(true);

		}
	}

	final protected void setOptionPanelRegardingFocus(boolean updateEuclidianTab) {

		if (stayInCurrentPanelWithObjects())
			return;

		int focusedViewId = app.getGuiManager().getLayout().getDockManager()
				.getFocusedViewId();

		OptionType type = getTypeFromFocusedViewId(focusedViewId);

		if (type != null) {
			if (type == OptionType.EUCLIDIAN || type == OptionType.EUCLIDIAN2) {

				if (app.getActiveEuclidianView().getEuclidianController()
						.checkBoxOrTextfieldOrButtonJustHitted()) {
					// hit check box or text field : does nothing
					return;
				}

				// ev clicked
				setOptionPanelWithoutCheck(type);
				if (updateEuclidianTab) {
					setSelectedTab(type);
				}

			} else
				setOptionPanel(type);

			// here necessary no object is selected
			updateObjectPanelSelection(app.getSelectionManager()
					.getSelectedGeos());
		}

	}

	protected int selectedTab = 0;
	private int subType;

	abstract protected void setSelectedTab(OptionType type);

	protected void updateSelectedTab(Construction.Constants constant) {
		switch (constant) {
		case X_AXIS:
			selectedTab = 1;
			break;
		case Y_AXIS:
			selectedTab = 2;
			break;
		default:
			selectedTab = 0;
			break;
		}
	}

	abstract protected void setOptionPanelWithoutCheck(OptionType type);


	protected OptionType getTypeFromFocusedViewId(int id) {
		switch (id) {
		case App.VIEW_CAS:
			return OptionType.CAS;
		case App.VIEW_SPREADSHEET:
			return OptionType.SPREADSHEET;
		case App.VIEW_EUCLIDIAN:
			return OptionType.EUCLIDIAN;
		case App.VIEW_EUCLIDIAN2:
			return OptionType.EUCLIDIAN2;
		case App.VIEW_EUCLIDIAN3D:
			return OptionType.EUCLIDIAN3D;
		}

		if (id >= App.VIEW_EUCLIDIAN_FOR_PLANE_START
				&& id <= App.VIEW_EUCLIDIAN_FOR_PLANE_END) {
			return OptionType.EUCLIDIAN_FOR_PLANE;
		}

		return null;

	}

	protected boolean stayInCurrentPanelWithObjects() {

		return stayInCurrentPanel()
				|| (selectedOptionType == OptionType.OBJECTS && app
						.getSelectionManager().getSelectedGeos().size() > 0);
	}

	/**
	 * say if it has to stay in current panel. Should disable any try to change
	 * panel, unless from stylebar buttons.
	 */
	protected boolean stayInCurrentPanel() {

		return selectedOptionType == OptionType.DEFAULTS
				|| selectedOptionType == OptionType.ADVANCED
				|| selectedOptionType == OptionType.LAYOUT;
	}

}
