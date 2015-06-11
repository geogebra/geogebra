package org.geogebra.web.html5.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.AsyncOperation;

import com.google.gwt.dom.client.Element;

/**
 * Object providing the corresponding geo
 */
public interface GeoContainer {
	/**
	 * @return corresponding geo
	 */
	public GeoElement getGeo();

	public boolean hideSuggestions();

	public boolean stopNewFormulaCreation(String input, String latex,
	        AsyncOperation callback);

	public boolean popupSuggestions();

	public boolean stopEditing(String latex);

	public Element getElement();

	public void scrollIntoView();

	public boolean shuffleSuggestions(boolean down);

	public App getApplication();

	public void typing(boolean heuristic);
}