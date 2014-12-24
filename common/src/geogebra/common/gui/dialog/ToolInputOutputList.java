package geogebra.common.gui.dialog;

import geogebra.common.kernel.geos.GeoElement;

import java.util.ArrayList;

public class ToolInputOutputList extends ArrayList<GeoElement> {

	public ToolInputOutputList() {
		super();
	}

	public GeoElement[] toGeoElements() {
		GeoElement[] geos = new GeoElement[size()];
		return toArray(geos);
	}

}
