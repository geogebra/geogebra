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

package org.geogebra.common.properties;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

/**
 * Property collection with one extra property that decides if the other properties are relevant.
 * In the UI, this collection can be represented by expandable panel that collapses
 * if the lead property is false.
 */
public class PropertyCollectionWithLead extends AbstractPropertyCollection<Property> {
	public final BooleanProperty leadProperty;

	/**
	 * @param loc localization
	 * @param name name translation key
	 * @param leadProperty lead property
	 * @param children additional properties
	 */
	public PropertyCollectionWithLead(Localization loc, String name,
			BooleanProperty leadProperty, Property... children) {
		super(loc, name);
		this.leadProperty = leadProperty;
		setProperties(children);
	}
}
