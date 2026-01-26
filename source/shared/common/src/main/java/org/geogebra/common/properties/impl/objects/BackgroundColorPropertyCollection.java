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
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.ActionableIconPropertyListFacade;
import org.geogebra.common.properties.impl.facade.ColorPropertyListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Collection of background color related properties.
 */
public class BackgroundColorPropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Creates a collection of background color properties.
	 * @param propertiesFactory the factory to create property facades
	 * @param localization the localization
	 * @param elements the elements to create properties for
	 * @throws NotApplicablePropertyException if none of the elements support background color
	 */
	public BackgroundColorPropertyCollection(GeoElementPropertiesFactory propertiesFactory,
			Localization localization, List<GeoElement> elements)
			throws NotApplicablePropertyException {
		super(localization, "Background");
		setProperties(new Property[]{
				propertiesFactory.createPropertyFacadeThrowing(elements,
						(element) -> new BackgroundColorProperty(localization, element),
						ColorPropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						(element) -> new BackgroundColorResetProperty(localization, element),
						ActionableIconPropertyListFacade::new),
		});
	}
}
