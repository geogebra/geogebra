package geogebra.common.euclidian;


import java.util.ArrayList;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;

public abstract class EuclidianController {

	public abstract void handleMovedElement(GeoElement selGeo, boolean b);

	public abstract void clearJustCreatedGeos();

	public abstract void clearSelections();

	public abstract void memorizeJustCreatedGeos(ArrayList<GeoElement> geos);

	public abstract void memorizeJustCreatedGeos(GeoElement[] geos);

	public abstract boolean isAltDown();

	public abstract void setLineEndPoint(geogebra.common.awt.Point2D endPoint);

	public abstract GeoElement getRecordObject();

	public abstract void setMode(int mode);

}
