/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	public @Nonnull String getRawName() {
		return rawName;
	}

	public Property[] getProperties() {
		return properties;
	}
}
