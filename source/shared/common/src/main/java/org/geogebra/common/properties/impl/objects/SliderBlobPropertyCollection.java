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
import org.geogebra.common.properties.impl.facade.RangePropertyListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class SliderBlobPropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Creates a property collection for slider blob properties.
	 *
	 * @param propertiesFactory factory for creating properties
	 * @param localization localization for property names
	 * @param elements list of GeoElements to create properties for
	 * @throws NotApplicablePropertyException if the property is not applicable to the elements
	 */
	public SliderBlobPropertyCollection(GeoElementPropertiesFactory propertiesFactory,
			Localization localization, List<GeoElement> elements)
			throws NotApplicablePropertyException {
		super(localization, "Point");
		Property property = propertiesFactory.createPropertyFacadeThrowing(elements,
				(element) -> new SliderBlobSizeProperty(localization, element),
				RangePropertyListFacade::new);
		setProperties(new Property[]{property});
	}
}
