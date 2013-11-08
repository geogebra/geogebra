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
	
	public Object getObjectAt(int i) {
		return geos[i];
	}
	
	public GeoElement getGeoAt(int i) {
		return (GeoElement)geos[i];
	}
	
	public int getGeosLength() {
		return geos.length;
	}

	public boolean hasGeos() {
		return (geos != null && geos.length > 0);
	}
	
	protected boolean check(int index){
		return false;
		};
	
		
	public abstract void updateProperties();
	
	public boolean checkGeos() {
		boolean geosOK = true;
		for (int i = 0; i < getGeosLength(); i++) {
			if (!check(i)) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

}
	

