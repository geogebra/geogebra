package org.geogebra.common.properties.factory;

import org.geogebra.common.properties.Property;

/**
 * Holds a reference to the array of the properties and to the name of this properties collection.
 */
public class PropertiesArray {

	private String name;
	private Property[] properties;

	/**
	 * @param name name
	 * @param properties properties
	 */
	public PropertiesArray(String name, Property... properties) {
		this.name = name;
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public Property[] getProperties() {
		return properties;
	}
}
