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

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * A property collection containing the data coordinate properties (x- and y-lists) for chart
 * elements such as `LineGraph`.
 */
public class ChartDataPropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Constructs the (chart) data property for the given elements.
	 * @param propertiesFactory GeoElement properties factory
	 * @param localization localization
	 * @param elements the list of elements to create properties for
	 * @throws NotApplicablePropertyException if the property is not applicable to the given
	 * elements
	 */
	public ChartDataPropertyCollection(
			GeoElementPropertiesFactory propertiesFactory,
			Localization localization,
			List<GeoElement> elements) throws NotApplicablePropertyException {
		super(localization, "Data");
		setProperties(Stream.<Property>of(
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new LineGraphCoordinatesProperty(localization, element,
								LineGraphCoordinatesProperty.Axis.X),
						StringPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new LineGraphCoordinatesProperty(localization, element,
								LineGraphCoordinatesProperty.Axis.Y),
						StringPropertyListFacade::new)
		).filter(Objects::nonNull).toArray(Property[]::new));
		if (getProperties().length == 0) {
			throw new NotApplicablePropertyException(elements.get(0));
		}
	}
}
