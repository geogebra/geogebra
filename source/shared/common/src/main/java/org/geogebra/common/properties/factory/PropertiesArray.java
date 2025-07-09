package org.geogebra.common.properties.factory;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.util.StringUtil;

/**
 * Holds a reference to the array of the properties and to the name of this properties collection.
 */
public class PropertiesArray {

	private final String rawName;
	private final Localization localization;
	private final Property[] properties;

	/**
	 * @param name name
	 * @param properties properties
	 */
	public PropertiesArray(@CheckForNull String name, Localization localization,
			Property... properties) {
		this.rawName = name;
		this.localization = localization;
		this.properties = properties;
	}

	/**
	 * @param name The name of the array.
	 * @param properties The list of properties.
	 */
	public PropertiesArray(@CheckForNull String name, Localization localization,
			List<Property> properties) {
		this.rawName = name;
		this.localization = localization;
		this.properties = properties.toArray(new Property[0]);
	}

	/**
	 * @return localized name, may be empty
	 */
	public @Nonnull String getName() {
		if (StringUtil.empty(rawName)) {
			return "";
		}
		return localization.getMenu(rawName);
	}

	public Property[] getProperties() {
		return properties;
	}
}
