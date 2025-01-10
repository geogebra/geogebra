package org.geogebra.common.properties;

/**
 * A property which has an icon associated with it.
 */
public interface IconAssociatedProperty extends Property {

	/**
	 * Gets the icon that is associated with this property.
	 * @return an icon
	 */
	PropertyResource getIcon();
}
