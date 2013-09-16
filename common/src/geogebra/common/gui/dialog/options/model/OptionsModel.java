package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;

public abstract class OptionsModel {
	private Object[] geos; // currently selected geos
	
	public Object[] getGeos() {
		return geos;
	}
	
	public void setGeos(Object[] geos) {
		this.geos = geos;
	}
	
	public GeoElement getGeoAt(int i) {
		return (GeoElement)geos[i];
	}
	
	public int getGeosLength() {
		return geos.length;
	}

	public boolean hasGeos() {
		return geos != null;
	}
	public abstract void updateProperties();
	public abstract boolean checkGeos();
}
