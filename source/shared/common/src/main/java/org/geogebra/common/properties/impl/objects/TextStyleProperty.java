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

package org.geogebra.common.properties.impl.objects;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.ToggleableIconProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.ToggleableIconPropertyListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for setting text style,
 * including bold, italic, and serif styles independently.
 */
public class TextStyleProperty extends AbstractPropertyCollection<ToggleableIconProperty> {
	/**
	 * Constructs the property for the given elements.
	 * @param propertiesFactory properties factory for creating property facades for the given list
	 * of elements
	 * @param localization localization for translating property names
	 * @param elements the elements to create the property for
	 * @throws NotApplicablePropertyException if the property is not applicable for any given
	 * elements
	 */
	public TextStyleProperty(
			GeoElementPropertiesFactory propertiesFactory, Localization localization,
			List<GeoElement> elements) throws NotApplicablePropertyException {
		super(localization, "Properties.TextStyle");
		setProperties(new ToggleableIconProperty[]{
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new BoldProperty(localization, element),
						ToggleableIconPropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new ItalicProperty(localization, element),
						ToggleableIconPropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new SerifProperty(localization, element),
						ToggleableIconPropertyListFacade::new)
		});
	}
}
