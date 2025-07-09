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
