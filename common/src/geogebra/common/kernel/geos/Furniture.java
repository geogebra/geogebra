package geogebra.common.kernel.geos;

import geogebra.common.kernel.kernelND.GeoElementND;

public interface Furniture extends GeoElementND {

	int getAbsoluteScreenLocX();

	int getAbsoluteScreenLocY();

	void setAbsoluteScreenLoc(int screenCoordX, int screenCoordY);

	boolean isFurniture();

}
