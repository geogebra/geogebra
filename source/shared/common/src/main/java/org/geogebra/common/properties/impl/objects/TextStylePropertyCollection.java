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

import static org.geogebra.common.util.Util.tryOrNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.ColorPropertyListFacade;
import org.geogebra.common.properties.impl.facade.IconsEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code PropertyCollection} containing properties related to the style of texts.
 */
public final class TextStylePropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Constructs the property for the given elements.
	 * @param propertiesFactory properties factory for creating property facades for the given list
	 * of elements
	 * @param localization localization for translating property names
	 * @param elements the elements to create the property for
	 * @throws NotApplicablePropertyException if the property is not applicable for any given
	 * elements
	 */
	public TextStylePropertyCollection(
			GeoElementPropertiesFactory propertiesFactory, Localization localization,
			List<GeoElement> elements) throws NotApplicablePropertyException {
		super(localization, "Text");
		setProperties(Stream.of(
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new TextColorProperty(localization, element),
						ColorPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new FontSizeProperty(localization, element),
						NamedEnumeratedPropertyListFacade::new),
				tryOrNull(() -> new TextStyleProperty(propertiesFactory, localization, elements)),
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new InputBoxAlignmentProperty(localization, element),
						IconsEnumeratedPropertyListFacade::new)
		).filter(Objects::nonNull).toArray(Property[]::new));
		if (getProperties().length == 0) {
			throw new NotApplicablePropertyException(elements.get(0));
		}
	}
}
