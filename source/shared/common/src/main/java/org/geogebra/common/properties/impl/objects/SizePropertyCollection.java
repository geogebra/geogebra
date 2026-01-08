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
import java.util.Objects;
import java.util.stream.Stream;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.BooleanPropertyListFacade;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Collection of size properties for geo elements (button and input box sizes).
 */
public class SizePropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Creates a size property collection for the given elements.
	 *
	 * @param propertiesFactory factory for creating properties
	 * @param processor algebra processor
	 * @param localization localization
	 * @param elements elements to create properties for
	 * @throws NotApplicablePropertyException if no size properties are applicable
	 */
	public SizePropertyCollection(GeoElementPropertiesFactory propertiesFactory,
			AlgebraProcessor processor, Localization localization,
			List<GeoElement> elements) throws NotApplicablePropertyException {
		super(localization, "Size");
		Property[] properties = Stream.<Property>of(
				// Button Size
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new ButtonFixedSizeProperty(localization, element),
						BooleanPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new ButtonSizeProperty.Width(processor, localization, element),
						StringPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new ButtonSizeProperty.Height(processor, localization, element),
						StringPropertyListFacade::new),
				// Input box size
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new InputBoxSizeProperty(processor, localization, element),
						StringPropertyListFacade::new)
		).filter(Objects::nonNull).toArray(Property[]::new);
		if (properties.length == 0) {
			throw new NotApplicablePropertyException(elements.get(0));
		}
		setProperties(properties);
	}
}
