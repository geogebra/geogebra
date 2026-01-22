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
 * See https://www.geogebra.org/license for full licensing details'
 */

package org.geogebra.common.properties.impl.objects;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyCollectionWithLead;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.facade.BooleanPropertyListFacade;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;

/**
 * {@code PropertyCollection} containing {@code Property}s related to advanced color settings
 * of a {@code GeoElement}, including advanced color mode activation, a color space,
 * and color components depending on the selected color space.
 */
public class DynamicColorPropertyCollection extends PropertyCollectionWithLead {
	/**
	 * Constructs the property for the given elements.
	 * @param propertiesFactory properties factory for creating property facades for the given list
	 * of elements
	 * @param localization localization for translating property names
	 * @param elements the elements to create the property for
	 */
	public DynamicColorPropertyCollection(GeoElementPropertiesFactory propertiesFactory,
			Localization localization, List<GeoElement> elements) {
		super(localization, "DynamicColors",
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new DynamicColorModeProperty(localization, element),
						BooleanPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						new DynamicColorSpaceProperty(localization, element),
						NamedEnumeratedPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						DynamicColorComponentProperty.forRed(localization, element),
						StringPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						DynamicColorComponentProperty.forGreen(localization, element),
						StringPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						DynamicColorComponentProperty.forBlue(localization, element),
						StringPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						DynamicColorComponentProperty.forHueHSB(localization, element),
						StringPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						DynamicColorComponentProperty.forHueHSL(localization, element),
						StringPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						DynamicColorComponentProperty.forSaturationHSB(localization, element),
						StringPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						DynamicColorComponentProperty.forSaturationHSL(localization, element),
						StringPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						DynamicColorComponentProperty.forBrightness(localization, element),
						StringPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						DynamicColorComponentProperty.forLightness(localization, element),
						StringPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements, element ->
						DynamicColorComponentProperty.forOpacity(localization, element),
						StringPropertyListFacade::new));
	}
}
