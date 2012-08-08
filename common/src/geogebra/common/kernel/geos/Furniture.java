package geogebra.common.kernel.geos;

import geogebra.common.kernel.kernelND.GeoElementND;
/**
 * Interface for action objects (buttons, textfields, comboboxes)
 *
 */
public interface Furniture extends GeoElementND {

	/**
	 * 
	 * @return screen x-coord in pixels
	 */
	int getAbsoluteScreenLocX();
	/**
	 * 
	 * @return screen y-coord in pixels
	 */
	int getAbsoluteScreenLocY();

	/**
	 * @param screenCoordX screen x-coord in pixels
	 * @param screenCoordY screen y-coord in pixels
	 */
	void setAbsoluteScreenLoc(int screenCoordX, int screenCoordY);

	/**
	 * @return whether this is really furniture (GeoList may not be combobox)
	 */
	boolean isFurniture();

}
