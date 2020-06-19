package org.geogebra.common.properties.impl;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;

/**
 * Implements the PropertyCollection interface.
 */
public abstract class AbstractPropertyCollection extends AbstractProperty
		implements PropertyCollection {

	private Property[] properties;

	/**
	 * Constructs an AbstractPropertyCollection.
	 * {@link AbstractPropertyCollection#setProperties(Property[])}
	 * must be called inside the constructor.
	 * @param localization localization
	 * @param name name
	 */
	public AbstractPropertyCollection(Localization localization,
			String name) {
		super(localization, name);
	}

	@Override
	public Property[] getProperties() {
		return properties;
	}

	/**
	 * Set the properties of this collection.
	 * @param properties properties
	 */
	protected void setProperties(Property[] properties) {
		this.properties = properties;
	}
}
