package org.geogebra.common.kernel.geos;

/**
 * Interface for action objects (buttons, textfields, comboboxes)
 *
 */
public interface Furniture extends AbsoluteScreenLocateable {

	/**
	 * @return whether this is really furniture (GeoList may not be combobox)
	 */
	boolean isFurniture();

}
