package org.geogebra.common.gui.dialog;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;

@SuppressWarnings("serial")
public class ToolInputOutputList extends ArrayList<GeoElement> {

	public GeoElement[] toGeoElements() {
		GeoElement[] geos = new GeoElement[size()];
		return toArray(geos);
	}

}
