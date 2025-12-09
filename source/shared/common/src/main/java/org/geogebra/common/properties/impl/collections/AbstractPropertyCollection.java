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
