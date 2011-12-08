package geogebra.common.kernel.geos;

import geogebra.common.kernel.kernelND.GeoPointND;

public interface GeoLineInterface extends GeoElementInterface {
	public double getX();

	public double getY();
	
	public double getZ();
	
	public void setCoords(double x, double y, double z);
	
	public void setUndefined();
	
	public void removePointOnLine(GeoPoint2 p);
	
	public void addPointOnLine(GeoPointND p);
}
