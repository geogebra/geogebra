package geogebra.common.gui.dialog.options;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;

import java.util.ArrayList;

public abstract class OptionsObject {

	protected static final int MIN_LIST_WIDTH = 120;
	public static final int TEXT_FIELD_FRACTION_DIGITS = 8;
	protected Kernel kernel;
	public static final int SLIDER_MAX_WIDTH = 170;
	protected static final int MIN_WIDTH = 500;
	protected static final int MIN_HEIGHT = 300;
	protected GeoElement geoAdded = null;
	protected boolean firstTime = true;
	protected ArrayList<GeoElement> selection;
	private StringBuilder sb = new StringBuilder();
	public App app;

	/**
	 * update geo just added
	 * 
	 * @param geo
	 *            geo
	 */
	public void add(GeoElement geo) {
		// AbstractApplication.debug("\ngeo = "+geo);
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
	 * @return description for selection
	 */
	public String getSelectionDescription() {
		if (selection == null || selection.size() == 0) {
			return app.getPlain("Properties");
		} else if (selection.size() == 1) {
			GeoElement geo = selection.get(0);
			sb.setLength(0);
			sb.append("<html>");
			sb.append(app.getLocalization().getPlain("PropertiesOfA",
					geo.getNameDescriptionHTML(false, false)));
			sb.append("</html>");
			return sb.toString();
		} else {
			return app.getLocalization().getPlain("PropertiesOfA", app.getPlain("Selection"));
		}
	}

}
