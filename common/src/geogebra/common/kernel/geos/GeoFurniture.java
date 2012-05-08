package geogebra.common.kernel.geos;

public interface GeoFurniture {

	int getAbsoluteScreenLocX();

	int getAbsoluteScreenLocY();

	void setAbsoluteScreenLoc(int screenCoordX, int screenCoordY);

	boolean isFurniture();

	void updateCascade();

	void updateRepaint();

}
