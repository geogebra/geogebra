package org.geogebra.common.properties.impl.collections;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;
import org.geogebra.common.properties.impl.AbstractProperty;

/**
 * Implements the PropertyCollection interface.
 */
public abstract class AbstractPropertyCollection<P extends Property> extends AbstractProperty
		implements PropertyCollection<P> {

	private P[] properties;

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
	public P[] getProperties() {
		return properties;
	}

	/**
	 * Set the properties of this collection.
	 * @param properties properties
	 */
	protected void setProperties(P[] properties) {
		this.properties = properties;
	}
}
