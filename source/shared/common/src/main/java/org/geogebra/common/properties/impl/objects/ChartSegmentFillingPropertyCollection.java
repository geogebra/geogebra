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
import org.geogebra.common.properties.ObjectSettingsSession;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.IconsEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.ImagePropertyListFacade;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.RangePropertyListFacade;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.util.ImageManager;

/**
 * Collection of filling-related properties (fill type group, pattern style, hatching,
 * symbol, image, and inverse fill) for pie and bar charts. Similar to
 * {@link FillingPropertyCollection}, containing the same filling properties,
 * but changing the selected segment instead of the whole element.
 */
public class ChartSegmentFillingPropertyCollection extends AbstractPropertyCollection<Property> {
	/**
	 * Constructs the property collection
	 * @param propertiesFactory factory for creating property facades
	 * @param localization localization for translating properties
	 * @param imageManager image manager for the image filling property
	 * @param elements the elements to create the property for
	 * @param context temporary data owned by the settings session
	 * @throws NotApplicablePropertyException if the property is not applicable to the given elements
	 */
	public ChartSegmentFillingPropertyCollection(
			GeoElementPropertiesFactory propertiesFactory,
			Localization localization,
			ImageManager imageManager,
			List<GeoElement> elements,
			ObjectSettingsSession.Context context)
			throws NotApplicablePropertyException {
		super(localization, "Filling");
		setProperties(Stream.<Property>of(
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								element -> new ChartSegmentSelectionProperty(
										localization, element, context.forElement(element).fillingSelection),
								NamedEnumeratedPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								element -> new ChartSegmentFillCategoryProperty(
										localization, element, context.forElement(element)),
								NamedEnumeratedPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								element -> new ChartSegmentFillSymbolProperty(
										localization, element, context.forElement(element).fillingSelection),
								StringPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								element -> new ChartSegmentFillImageProperty(
										localization,
										imageManager,
										element,
										context.forElement(element).fillingSelection),
								ImagePropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								element -> new ChartSegmentPatternFillStyleProperty(
										localization, element, context.forElement(element).fillingSelection),
								IconsEnumeratedPropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								element -> new ChartSegmentHatchingDistanceProperty(
										localization, element, context.forElement(element).fillingSelection),
								RangePropertyListFacade::new),
						propertiesFactory.createOptionalPropertyFacade(
								elements,
								element -> new ChartSegmentHatchingAngleProperty(
										localization, element, context.forElement(element).fillingSelection),
								RangePropertyListFacade::new))
				.filter(Objects::nonNull)
				.toArray(Property[]::new));
		if (getProperties().length == 0) {
			throw new NotApplicablePropertyException(elements.get(0));
		}
	}
}
