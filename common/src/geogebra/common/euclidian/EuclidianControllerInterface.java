package geogebra.common.euclidian;

import java.util.ArrayList;

import geogebra.common.kernel.geos.GeoElement;

public interface EuclidianControllerInterface {

	void handleMovedElement(GeoElement selGeo, boolean b);

	void clearJustCreatedGeos();

	void clearSelections();

	void memorizeJustCreatedGeos(ArrayList<GeoElement> geos);

	void memorizeJustCreatedGeos(GeoElement[] geos);

}
