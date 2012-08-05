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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Properties view
 * 
 */
public abstract class PropertiesView implements View {
	/**
	 * Option panel types
	 */
	public enum OptionType {
		// Order matters for the selection menu. A separator is placed after
		// OBJECTS and SPREADSHEET to isolate the view options
		OBJECTS, EUCLIDIAN, EUCLIDIAN2, CAS, SPREADSHEET, LAYOUT, DEFAULTS, ADVANCED
	}

	protected static HashMap<Integer, OptionType> viewMap = new HashMap<Integer, OptionType>();
	// map to match view ID with OptionType
	static {

		viewMap = new HashMap<Integer, OptionType>();
		viewMap.put(App.VIEW_CAS, OptionType.CAS);
		viewMap.put(App.VIEW_SPREADSHEET, OptionType.SPREADSHEET);
		viewMap.put(App.VIEW_EUCLIDIAN, OptionType.EUCLIDIAN);
		viewMap.put(App.VIEW_EUCLIDIAN2, OptionType.EUCLIDIAN2);
	}

	protected Kernel kernel;
	protected boolean attached;
	protected App app;
	protected OptionType selectedOptionType = OptionType.EUCLIDIAN;
	protected OptionsObject objectPanel;

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
			return app.getPlain("PropertiesOfA", app.getPlain("Defaults"));
		case SPREADSHEET:
			return app.getPlain("PropertiesOfA", app.getPlain("Spreadsheet"));
		case EUCLIDIAN:
			return app.getPlain("PropertiesOfA", app.getPlain("DrawingPad"));
		case EUCLIDIAN2:
			return app.getPlain("PropertiesOfA", app.getPlain("DrawingPad2"));
		case CAS:
			return app.getPlain("PropertiesOfA", app.getPlain("CAS"));
		case ADVANCED:
			return app.getPlain("PropertiesOfA", app.getMenu("Advanced"));
		case OBJECTS:
			// return app.getMenu("Objects");
			return objectPanel.getSelectionDescription();
		case LAYOUT:
			return app.getPlain("PropertiesOfA", app.getMenu("Layout"));
		}
		return null;
	}

	/**
	 * @param app
	 * @param type
	 * @return short version of Option type string
	 */
	public static String getTypeStringSimple(App app, OptionType type) {
		switch (type) {
		case DEFAULTS:
			return app.getPlain("Defaults");
		case SPREADSHEET:
			return app.getPlain("Spreadsheet");
		case EUCLIDIAN:
			return app.getPlain("DrawingPad");
		case EUCLIDIAN2:
			return app.getPlain("DrawingPad2");
		case CAS:
			return app.getPlain("CAS");
		case ADVANCED:
			return app.getMenu("Advanced");
		case OBJECTS:
			return app.getMenu("Objects");
			// return objectPanel.getSelectionDescription();
		case LAYOUT:
			return app.getMenu("Layout");
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
	 * @param type
	 *            Option panel type
	 * @return true if given Option panel is showing (or is instantiated but
	 *         hidden)
	 */
	public boolean isOptionPanelAvailable(OptionType type) {

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
