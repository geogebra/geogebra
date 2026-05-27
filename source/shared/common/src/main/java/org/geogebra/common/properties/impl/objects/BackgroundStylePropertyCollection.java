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
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/** {@code PropertyCollection} of background style related {@code Property}s. */
public class BackgroundStylePropertyCollection extends AbstractPropertyCollection<Property> {
	/**
	 * Constructs the property collection.
	 * @param propertiesFactory the factory to create property facades
	 * @param localization localization for the label translations
	 * @param elements the elements to create the properties for
	 * @throws NotApplicablePropertyException if the property cannot be applied to the given elements
	 */
	public BackgroundStylePropertyCollection(
			GeoElementPropertiesFactory propertiesFactory, Localization localization,
			List<GeoElement> elements) throws NotApplicablePropertyException {
		super(localization, "Background");
		setProperties(new Property[] {
				new BackgroundColorPropertyCollection(propertiesFactory, localization, elements)
		});
	}
}
