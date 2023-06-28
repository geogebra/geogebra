package org.geogebra.common.gui.dialog.options;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;

public class OptionsObject {

	protected static final int MIN_LIST_WIDTH = 120;
	public static final int TEXT_FIELD_FRACTION_DIGITS = 8;
	public static final int SLIDER_MAX_WIDTH = 170;
	protected static final int MIN_WIDTH = 500;
	protected static final int MIN_HEIGHT = 300;
	protected GeoElement geoAdded = null;
	private ArrayList<GeoElement> selection;
	private StringBuilder sb = new StringBuilder();

	protected OptionsObject() {
		// no instances
	}

	/**
	 * update geo just added
	 * 
	 * @param geo
	 *            geo
	 */
	public void add(GeoElement geo) {
		geoAdded = geo;
	}

	/**
	 * forget last added geo
	 */
	public void forgetGeoAdded() {
		geoAdded = null;
	}

	/**
	 * consume last added geo
	 * 
	 * @return last added geo
	 */
	public GeoElement consumeGeoAdded() {
		GeoElement ret = geoAdded;
		forgetGeoAdded();
		return ret;
	}

	/**
	 * 
	 * @param loc
	 *            localization
	 * @return description for selection
	 */
	public String getSelectionDescription(Localization loc) {
		if (getSelection() == null || getSelection().size() == 0) {
			return loc.getMenu("Properties");
		} else if (getSelection().size() == 1) {
			GeoElement geo = getSelection().get(0);
			sb.setLength(0);
			sb.append("<html>");
			sb.append(loc.getPlain("PropertiesOfA",
					geo.getNameDescriptionHTML(false, false)));
			sb.append("</html>");
			return sb.toString();
		} else {
			return loc.getPlain("PropertiesOfA",
					loc.getMenu("Selection"));
		}
	}

	protected ArrayList<GeoElement> getSelection() {
		return selection;
	}

	protected void setSelection(ArrayList<GeoElement> selection) {
		this.selection = selection;
	}

}
