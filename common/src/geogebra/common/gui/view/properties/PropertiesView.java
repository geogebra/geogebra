/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.gui.view.properties;

import java.util.ArrayList;
import java.util.HashMap;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.GeoElementSelectionListener;

/**
 * Properties view
 *
 */
public abstract class PropertiesView implements View{
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
		viewMap.put(App.VIEW_SPREADSHEET,
				OptionType.SPREADSHEET);
		viewMap.put(App.VIEW_EUCLIDIAN, OptionType.EUCLIDIAN);
		viewMap.put(App.VIEW_EUCLIDIAN2, OptionType.EUCLIDIAN2);
	}



	protected Kernel kernel;
	protected boolean attached;
	protected App app;
	protected OptionType selectedOptionType = OptionType.OBJECTS;
	
	/**
	 * Update selection
	 */
	public abstract void updateSelection();
	
	
	/**
	 * update the properties view as if geos where selected
	 * @param geos geos
	 */
	public abstract void updateSelection(ArrayList<GeoElement> geos);
	
	
	
	/**
	 * Set the option panel to be displayed
	 */
	public abstract void setOptionPanel(OptionType type);


	public abstract void mousePressedForPropertiesView();
	
}
