package geogebra.common.euclidian;


import java.util.ArrayList;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;

public interface EuclidianControllerInterface {

	void handleMovedElement(GeoElement selGeo, boolean b);

	void clearJustCreatedGeos();

	void clearSelections();

	void memorizeJustCreatedGeos(ArrayList<GeoElement> geos);

	void memorizeJustCreatedGeos(GeoElement[] geos);

	boolean isAltDown();

	void setLineEndPoint(geogebra.common.awt.Point2D endPoint);

	GeoElement getRecordObject();

}
