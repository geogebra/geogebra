/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.common.gui.view.properties;

import geogebra.common.gui.dialog.options.OptionsObject;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.main.OptionType;

import java.util.ArrayList;
import java.util.HashMap;

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
	 * Set the option panel to be displayed
	 */
	public abstract void setOptionPanel(OptionType type);

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
		case CAS:
			return loc.getPlain("PreferencesOfA", loc.getPlain("CAS"));
		case ADVANCED:
			return loc.getPlain("PreferencesOfA", app.getMenu("Advanced"));
		case OBJECTS:
			// return app.getMenu("Objects");
			return objectPanel.getSelectionDescription();
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

}
