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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.StringPropertyWithSuggestionsListFacade;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * {@code PropertyCollection} containing {@code Property}s related to {@code GeoElement}
 * positioning. Depending on the selected {@link PlacementProperty.Placement} in
 * {@link PlacementProperty}, the availability of the rest of the {@code Property}s will vary.
 */
public class PositionPropertyCollection extends AbstractPropertyCollection<Property> {
	private final NamedEnumeratedProperty<PlacementProperty.Placement> placementProperty;
	private final PropertyCollection<StringProperty> absoluteScreenPositionPropertyCollection;
	private final StringPropertyWithSuggestions startingPointPositionProperty;
	private final StringPropertyWithSuggestions cornerPositionProperty1;
	private final StringPropertyWithSuggestions cornerPositionProperty2;
	private final StringPropertyWithSuggestions cornerPositionProperty4;
	private final StringPropertyWithSuggestions centerImagePositionProperty;

	/**
	 * Constructs the property for the given elements.
	 * @param propertiesFactory properties factory for creating property facades for the given list
	 * of elements
	 * @param localization localization for translating property names
	 * @param elements the elements to create the property for
	 * @throws NotApplicablePropertyException if the property is not applicable for any given
	 * elements
	 */
	public PositionPropertyCollection(
			GeoElementPropertiesFactory propertiesFactory, Localization localization,
			List<GeoElement> elements) throws NotApplicablePropertyException {
		super(localization, "Position");
		this.placementProperty = propertiesFactory.createPropertyFacadeThrowing(elements,
				element -> new PlacementProperty(localization, element),
				NamedEnumeratedPropertyListFacade::new);
		this.absoluteScreenPositionPropertyCollection = tryOrNull(() ->
				new AbsoluteScreenPositionPropertyCollection(
						propertiesFactory, localization, elements));
		this.startingPointPositionProperty = propertiesFactory.createOptionalPropertyFacade(
				elements, element -> new StartingPointPositionProperty(localization, element),
				StringPropertyWithSuggestionsListFacade::new);
		this.cornerPositionProperty1 = propertiesFactory.createOptionalPropertyFacade(elements,
				element -> new CornerPositionProperty(localization, element, 0),
				StringPropertyWithSuggestionsListFacade::new);
		this.cornerPositionProperty2 = propertiesFactory.createOptionalPropertyFacade(elements,
				element -> new CornerPositionProperty(localization, element, 1),
				StringPropertyWithSuggestionsListFacade::new);
		this.cornerPositionProperty4 = propertiesFactory.createOptionalPropertyFacade(elements,
				element -> new CornerPositionProperty(localization, element, 2),
				StringPropertyWithSuggestionsListFacade::new);
		this.centerImagePositionProperty = propertiesFactory.createOptionalPropertyFacade(elements,
				element -> new CenterImagePositionProperty(localization, element),
				StringPropertyWithSuggestionsListFacade::new);
		setProperties(Stream.of(
				placementProperty,
				absoluteScreenPositionPropertyCollection,
				startingPointPositionProperty,
				cornerPositionProperty1,
				cornerPositionProperty2,
				cornerPositionProperty4,
				centerImagePositionProperty
		).filter(Objects::nonNull).toArray(Property[]::new));
	}

	/**
	 * @return the placement property
	 */
	public @Nonnull NamedEnumeratedProperty<PlacementProperty.Placement> getPlacementProperty() {
		return placementProperty;
	}

	/**
	 * @return the absolute screen positioning property if it can be applied to the given element,
	 * or {@code null} otherwise
	 */
	public @CheckForNull PropertyCollection<StringProperty>
		getAbsoluteScreenPositionPropertyCollection() {
		return absoluteScreenPositionPropertyCollection;
	}

	/**
	 * @return the starting point positioning property if it can be applied to the given element,
	 * or {@code null} otherwise
	 */
	public @CheckForNull StringPropertyWithSuggestions getStartingPointPositionProperty() {
		return startingPointPositionProperty;
	}

	/**
	 * @return the corner positioning properties if they can be applied to the given element,
	 * or {@code null} otherwise
	 */
	public @CheckForNull List<StringPropertyWithSuggestions> getCornerPositionProperties() {
		List<StringPropertyWithSuggestions> cornerPositionProperties = Arrays.asList(
				cornerPositionProperty1, cornerPositionProperty2, cornerPositionProperty4);
		return cornerPositionProperties.stream().noneMatch(Objects::isNull)
				? cornerPositionProperties : null;
	}

	/**
	 * @return the center positioning properties if they can be applied to the given element,
	 * or {@code null} otherwise
	 */
	public @CheckForNull StringPropertyWithSuggestions getCenterImagePositionProperty() {
		return centerImagePositionProperty;
	}

	/** Utility method for validating expression for point input. */
	static @CheckForNull String validatePointExpression(
			Parser parser, Localization localization, String expression) {
		if (expression == null || expression.isEmpty()) {
			return "";
		}
		try {
			ValidExpression validExpression = parser.parseGeoGebraExpression(expression);
			if (!validExpression.evaluatesToNonComplex2DVector()) {
				return localization.getError("VectorExpected");
			}
			return null;
		} catch (ParseException | MyError parseException) {
			return parseException.getLocalizedMessage();
		}
	}

	/** Utility method for getting the list of suggested points */
	static List<String> getSuggestedPointLabels(Construction construction) {
		return getSuggestedPoints(construction).stream().limit(50)
				.map(element -> element.getLabel(StringTemplate.editTemplate))
				.collect(Collectors.toList());
	}

	/** Utility method for getting the displayed value of a point */
	static String getPointValue(GeoPointND geoPointND) {
		if (geoPointND == null) {
			return null;
		}
		return geoPointND.getLabel(StringTemplate.editTemplate);
	}

	/** Utility method for setting point with validated expression */
	@SuppressFBWarnings("DE_MIGHT_IGNORE")
	static void setCornerPoint(GeoElement geoElement, int cornerIndex, String pointExpression) {
		try {
			AlgebraProcessor algebraProcessor = geoElement.getKernel().getAlgebraProcessor();
			ErrorHandler errorHandler = geoElement.getApp().getErrorHandler();
			GeoPointND geoPointND = algebraProcessor.evaluateToPoint(
					pointExpression, errorHandler, true);
			((Locateable) geoElement).setStartPoint(geoPointND, cornerIndex);
			geoElement.updateRepaint();
		} catch (CircularDefinitionException circularDefinitionException) { }
	}

	private static List<GeoPoint> getSuggestedPoints(Construction construction) {
		return construction.getGeoSetConstructionOrder().stream()
				.filter(element -> element instanceof GeoPoint)
				.map(element -> (GeoPoint) element)
				.collect(Collectors.toList());
	}
}
