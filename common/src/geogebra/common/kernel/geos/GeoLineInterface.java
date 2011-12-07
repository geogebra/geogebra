package geogebra.common.kernel.geos;

public interface GeoLineInterface extends GeoElementInterface {
	public double getX();

	public double getY();
	
	public double getZ();
	
	public void setCoords(double x, double y, double z);
	
	public void setUndefined();
}
