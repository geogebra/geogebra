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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.DefaultColorValues;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.ActionableIconPropertyListFacade;
import org.geogebra.common.properties.impl.facade.ColorPropertyListFacade;
import org.geogebra.common.properties.impl.facade.IconsEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Collection of background color and border related properties.
 */
public class BackgroundAndBorderPropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Creates a collection of background and border properties.
	 * @param propertiesFactory the factory to create property facades
	 * @param localization the localization
	 * @param elements the elements to create properties for
	 * @throws NotApplicablePropertyException if none of the elements support background and border
	 */
	public BackgroundAndBorderPropertyCollection(GeoElementPropertiesFactory propertiesFactory,
			Localization localization, List<GeoElement> elements)
			throws NotApplicablePropertyException {
		super(localization, "ObjectProperties.BackgroundAndBorder");
		setProperties(Arrays.stream(new Property[]{
				propertiesFactory.createPropertyFacadeThrowing(elements,
						(element) -> new BackgroundColorProperty(localization, element),
						ColorPropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						(element) -> new BackgroundColorResetProperty(localization, element),
						ActionableIconPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new BorderColorProperty(localization, element,
								DefaultColorValues.BRIGHT, "ObjectProperties.BorderColor"),
						ColorPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new BorderWidthProperty(localization, element),
						IconsEnumeratedPropertyListFacade::new),
				tryOrNull(() -> new BorderStylePropertyCollection(propertiesFactory,
						localization, elements))
		}).filter(Objects::nonNull).toArray(Property[]::new));
	}
}
