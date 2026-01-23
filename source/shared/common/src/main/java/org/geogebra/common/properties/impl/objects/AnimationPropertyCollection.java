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

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Property collection for animation-related properties including speed and mode.
 */
public class AnimationPropertyCollection extends AbstractPropertyCollection<Property> {
	/**
	 * Constructs an AnimationPropertyCollection.
	 * @param propertiesFactory the properties factory
	 * @param processor the algebra processor
	 * @param localization the localization
	 * @param elements the elements to control
	 * @throws NotApplicablePropertyException if animation properties are not applicable
	 */
	public AnimationPropertyCollection(GeoElementPropertiesFactory propertiesFactory,
			AlgebraProcessor processor, Localization localization, List<GeoElement> elements)
			throws NotApplicablePropertyException {
		super(localization, "Animation");
		setProperties(new Property[]{
				propertiesFactory.createPropertyFacadeThrowing(elements,
						(element) -> new AnimationSpeedProperty(processor, localization, element),
						StringPropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						(element) -> new AnimationModeProperty(localization, element),
						NamedEnumeratedPropertyListFacade::new),
		});
	}
}
