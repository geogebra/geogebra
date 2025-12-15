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
 * Object settings / Advanced tab: Interaction
 */
public class InteractionPropertyCollection extends AbstractPropertyCollection<Property> {

	private final BooleanPropertyListFacade selectionAllowedProperty;
	private final StringPropertyListFacade animationStepProperty;
	private final StringPropertyListFacade verticalStepProperty;

	/**
	 * Constructor
	 * @param propertiesFactory {@code GeoElement} properties factory
	 * @param processor algebra processor
	 * @param localization localization
	 * @param elements a list of elements
	 * @throws NotApplicablePropertyException if {@code elements} contains elements for which
	 * this property collection is not applicable
	 */
	public InteractionPropertyCollection(GeoElementPropertiesFactory propertiesFactory,
			AlgebraProcessor processor, Localization localization, List<GeoElement> elements)
			throws NotApplicablePropertyException {
		super(localization, "Interaction");

		selectionAllowedProperty = propertiesFactory.createOptionalPropertyFacade(elements,
				element -> new SelectionAllowedProperty(localization, element),
				BooleanPropertyListFacade::new);
		animationStepProperty = propertiesFactory.createOptionalPropertyFacade(elements,
				element -> new AnimationStepProperty(processor, localization, element, false),
				StringPropertyListFacade::new);
		verticalStepProperty = propertiesFactory.createOptionalPropertyFacade(elements,
				element -> new VerticalStepProperty(processor, localization,
						element),
				StringPropertyListFacade::new);
		Property[] properties = Stream.<Property>of(
				selectionAllowedProperty, animationStepProperty, verticalStepProperty)
				.filter(Objects::nonNull)
				.toArray(Property[]::new);
		if (properties.length == 0) {
			throw new NotApplicablePropertyException(elements.get(0));
		}
		setProperties(properties);

	}

	public BooleanPropertyListFacade getSelectionAllowedProperty() {
		return selectionAllowedProperty;
	}

	public StringPropertyListFacade getAnimationStepProperty() {
		return animationStepProperty;
	}

	public StringPropertyListFacade getVerticalStepProperty() {
		return verticalStepProperty;
	}
}