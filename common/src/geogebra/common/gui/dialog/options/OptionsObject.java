package geogebra.common.gui.dialog.options;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;

public abstract class OptionsObject {

	private static final int MAX_GEOS_FOR_EXPAND_ALL = 15;
	private static final int MAX_COMBOBOX_ENTRIES = 200;
	protected static final int MIN_LIST_WIDTH = 120;
	public static final int TEXT_FIELD_FRACTION_DIGITS = 8;
	protected Kernel kernel;
	public static final int SLIDER_MAX_WIDTH = 170;
	protected static final int MIN_WIDTH = 500;
	protected static final int MIN_HEIGHT = 300;
	protected GeoElement geoAdded = null;
	protected boolean firstTime = true;

	/**
	  * update geo just added
	  * @param geo geo
	  */
	public void add(GeoElement geo) {
		 //AbstractApplication.debug("\ngeo = "+geo);
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
	  * @return last added geo
	  */
	public GeoElement consumeGeoAdded() {
		 GeoElement ret = geoAdded;
		 forgetGeoAdded();
		 return ret;
	 }

}
