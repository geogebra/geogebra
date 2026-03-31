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

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.RangePropertyListFacade;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class SliderTrackPropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Creates a property collection for slider track properties.
	 *
	 * @param propertiesFactory factory for creating properties
	 * @param processor algebra processor for numeric properties
	 * @param localization localization for property names
	 * @param elements list of GeoElements to create properties for
	 * @throws NotApplicablePropertyException if the property is not applicable to the elements
	 */
	public SliderTrackPropertyCollection(GeoElementPropertiesFactory propertiesFactory,
			AlgebraProcessor processor, Localization localization,
			List<GeoElement> elements) throws NotApplicablePropertyException {
		super(localization, "Line");
		Property[] properties = Stream.of(
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new SliderTrackWidthProperty(processor, localization, element),
						StringPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new SliderTrackThicknessProperty(localization, element),
						RangePropertyListFacade::new),
				tryOrNull(() -> new SliderTrackColorPropertyCollection(
						propertiesFactory, localization, elements))
		).filter(Objects::nonNull).toArray(Property[]::new);
		if (properties.length == 0) {
			throw new NotApplicablePropertyException(elements.get(0));
		}
		setProperties(properties);
	}
}
