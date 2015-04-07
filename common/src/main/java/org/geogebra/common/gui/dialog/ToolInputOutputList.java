package org.geogebra.common.gui.dialog;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;

public class ToolInputOutputList extends ArrayList<GeoElement> {

	public ToolInputOutputList() {
		super();
	}

	public GeoElement[] toGeoElements() {
		GeoElement[] geos = new GeoElement[size()];
		return toArray(geos);
	}

}
