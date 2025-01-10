package org.geogebra.common.properties.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.GeoElementPropertyFilter;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.collections.BooleanPropertyCollection;
import org.geogebra.common.properties.impl.collections.ColorPropertyCollection;
import org.geogebra.common.properties.impl.collections.IconsEnumeratedPropertyCollection;
import org.geogebra.common.properties.impl.collections.NamedEnumeratedPropertyCollection;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
import org.geogebra.common.properties.impl.collections.StringPropertyCollection;
import org.geogebra.common.properties.impl.objects.AnimationStepProperty;
import org.geogebra.common.properties.impl.objects.CaptionStyleProperty;
import org.geogebra.common.properties.impl.objects.ElementColorProperty;
import org.geogebra.common.properties.impl.objects.EquationFormProperty;
import org.geogebra.common.properties.impl.objects.IsFixedObjectProperty;
import org.geogebra.common.properties.impl.objects.LineStyleProperty;
import org.geogebra.common.properties.impl.objects.MaxProperty;
import org.geogebra.common.properties.impl.objects.MinProperty;
import org.geogebra.common.properties.impl.objects.NameProperty;
import org.geogebra.common.properties.impl.objects.OpacityProperty;
import org.geogebra.common.properties.impl.objects.PointSizeProperty;
import org.geogebra.common.properties.impl.objects.PointStyleProperty;
import org.geogebra.common.properties.impl.objects.ShowInAVProperty;
import org.geogebra.common.properties.impl.objects.ShowObjectProperty;
import org.geogebra.common.properties.impl.objects.ShowTraceProperty;
import org.geogebra.common.properties.impl.objects.SlopeSizeProperty;
import org.geogebra.common.properties.impl.objects.ThicknessProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Creates the list of properties for a GeoElement or for a list of GeoElements.
 */
public final class GeoElementPropertiesFactory {
	private final Set<GeoElementPropertyFilter> propertyFilters = new HashSet<>();

	/**
	 * Adds a {@link GeoElementPropertyFilter} which can modify the returned properties by
	 * the methods of the class.
	 *
	 * @param filter the {@link GeoElementPropertyFilter} to be added
	 */
	public void addFilter(GeoElementPropertyFilter filter) {
		propertyFilters.add(filter);
	}

	/**
	 * Removes the previously added {@link GeoElementPropertyFilter}, undoing the effect of
	 * {@link GeoElementPropertiesFactory#addFilter}.
	 * @param filter the {@link GeoElementPropertyFilter} to be removed
	 */
	public void removeFilter(GeoElementPropertyFilter filter) {
		propertyFilters.remove(filter);
	}

	/**
	 * Creates properties for a list of GeoElements.
	 * @param processor algebra processor
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public PropertiesArray createGeoElementProperties(
			AlgebraProcessor processor, Localization localization, List<GeoElement> elements) {
		return createPropertiesArray(localization, elements, Stream.<Property>of(
				createPropertyCollection(elements,
						element -> new NameProperty(localization, element),
						properties -> new StringPropertyCollection<>(
								properties.toArray(new NameProperty[0]))),
				createPropertyCollection(elements,
						element -> new MinProperty(processor, localization, element),
						properties -> new StringPropertyCollection<>(
								properties.toArray(new MinProperty[0]))),
				createPropertyCollection(elements,
						element -> new MaxProperty(processor, localization, element),
						properties -> new StringPropertyCollection<>(
								properties.toArray(new MaxProperty[0]))),
				createPropertyCollection(elements,
						element -> new AnimationStepProperty(processor, localization, element),
						properties -> new StringPropertyCollection<>(
								properties.toArray(new AnimationStepProperty[0]))),
				createShowObjectProperty(localization, elements),
				createColorProperty(localization, elements),
				createPointStyleProperty(localization, elements),
				createPointSizeProperty(localization, elements),
				createOpacityProperty(localization, elements),
				createLineStyleProperty(localization, elements),
				createThicknessProperty(localization, elements),
				createPropertyCollection(elements,
						element -> new SlopeSizeProperty(localization, element),
						properties -> new RangePropertyCollection<>(
								properties.toArray(new SlopeSizeProperty[0]))),
				createPropertyCollection(elements,
						element -> new EquationFormProperty(localization, element),
						properties -> new NamedEnumeratedPropertyCollection<>(
								properties.toArray(new EquationFormProperty[0]))),
				createPropertyCollection(elements,
						element -> new CaptionStyleProperty(localization, element),
						properties -> new NamedEnumeratedPropertyCollection<>(
								properties.toArray(new CaptionStyleProperty[0]))),
				createPropertyCollection(elements,
						element -> new ShowTraceProperty(localization, element),
						properties -> new BooleanPropertyCollection<>(
								properties.toArray(new ShowTraceProperty[0]))),
				createFixObjectProperty(localization, elements),
				createPropertyCollection(elements,
						element -> new ShowInAVProperty(localization, element),
						properties -> new BooleanPropertyCollection<>(
								properties.toArray(new ShowInAVProperty[0])))
		).filter(Objects::nonNull).collect(Collectors.toList()));
	}

	/**
	 * Creates Point style properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public PropertiesArray createPointStyleProperties(
			Localization localization, List<GeoElement> elements) {
		return createPropertiesArray(localization, elements, Stream.<Property>of(
				createPointStyleProperty(localization, elements),
				createPointSizeProperty(localization, elements)
		).filter(Objects::nonNull).collect(Collectors.toList()));
	}

	/**
	 * Creates Lines style properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public PropertiesArray createLineStyleProperties(
			Localization localization, List<GeoElement> elements) {
		return createPropertiesArray(localization, elements, Stream.<Property>of(
				createLineStyleProperty(localization, elements),
				createThicknessProperty(localization, elements)
		).filter(Objects::nonNull).collect(Collectors.toList()));
	}

	/**
	 * Creates a color property for the elements
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public ColorProperty createColorProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new ElementColorProperty(localization, element),
				properties -> new ColorPropertyCollection<>(
						properties.toArray(new ElementColorProperty[0])));
	}

	/**
	 * Returns with a Boolean property that fixes the object, or null if not applicable
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public BooleanProperty createFixObjectProperty(Localization localization,
			List<GeoElement> elements) {
        return createPropertyCollection(elements,
				element -> new IsFixedObjectProperty(localization, element),
				properties -> new BooleanPropertyCollection<>(
						properties.toArray(new IsFixedObjectProperty[0])));
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the point style or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedProperty createPointStyleProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new PointStyleProperty(localization, element),
				properties -> new IconsEnumeratedPropertyCollection<>(
						properties.toArray(new PointStyleProperty[0])));
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the line style or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedProperty createLineStyleProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new LineStyleProperty(localization, element),
				properties -> new IconsEnumeratedPropertyCollection<>(
						properties.toArray(new LineStyleProperty[0])));
	}

	/**
	 * Returns a RangePropertyCollection controlling the opacity or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public RangeProperty<Integer> createOpacityProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new OpacityProperty(localization, element),
				properties -> new RangePropertyCollection<>(
						properties.toArray(new OpacityProperty[0])));
	}

	/**
	 * Creates a {@link BooleanPropertyCollection} to control the visibility of the elements.
	 * @param localization localization for the property name
	 * @param elements elements for which the property should be created
	 * @return the property or {@code null} if it couldn't be created or is filtered
	 */
	public BooleanProperty createShowObjectProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new ShowObjectProperty(localization, element),
				properties -> new BooleanPropertyCollection<>(
						properties.toArray(new ShowObjectProperty[0])));
	}

	/**
	 * Creates a {@link RangePropertyCollection} to control the size of the points.
	 * @param localization localization for the property name
	 * @param elements elements for which the property should be created
	 * @return the property or {@code null} if it couldn't be created or is filtered
	 */
	public RangeProperty<Integer> createPointSizeProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new PointSizeProperty(localization, element),
				properties -> new RangePropertyCollection<>(
						properties.toArray(new PointSizeProperty[0])));
	}

	/**
	 * Creates a {@link RangePropertyCollection} to control the thickness of lines.
	 * @param localization localization for the property name
	 * @param elements elements for which the property should be created
	 * @return the property or {@code null} if it couldn't be created or is filtered
	 */
	public RangeProperty<Integer> createThicknessProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new ThicknessProperty(localization, element),
				properties -> new RangePropertyCollection<>(
						properties.toArray(new ThicknessProperty[0])));
	}

	private static PropertiesArray createPropertiesArray(Localization localization,
			List<GeoElement> geoElements, List<Property> properties) {
		if (properties.isEmpty()) {
			return new PropertiesArray("");
		}

		String name;
		if (geoElements.size() > 1) {
			name = localization.getMenu("Selection");
		} else if (geoElements.size() == 1) {
			GeoElement element = geoElements.get(0);
			name = element.translatedTypeString();
		} else {
			name = "";
		}

		return new PropertiesArray(name, properties.toArray(new Property[0]));
	}

	/**
	 * A factory interface for creating instances of
	 * properties associated with a {@link GeoElement}.
	 * @param <PropertyType> the type of property that this factory produces
	 */
	private interface PropertyFactory<PropertyType extends Property> {
		/**
		 * Creates a property instance for the specified {@link GeoElement}.
		 * If the property is not applicable to the provided {@link GeoElement}, a
		 * {@link NotApplicablePropertyException} is thrown.
		 *
		 * @param geoElement {@link GeoElement} for which the property should be created
		 * @return an instance of the specific property type
		 * @throws NotApplicablePropertyException if the property cannot be applied
		 * @throws IllegalArgumentException if the property can't be created from
		 * to the given {@link GeoElement}
		 */
		PropertyType create(GeoElement geoElement) throws NotApplicablePropertyException;
	}

	/**
	 * Collector interface for aggregating multiple properties
	 * of a specific type into a single collection.
	 *
	 * @param <PropertyType> the type of individual properties that will be collected
	 * @param <PropertyCollection> the type of the resulting collection of properties
	 */
	private interface PropertyCollector<
			PropertyType extends Property,
			PropertyCollection extends Property> {
		/**
		 * Collects a list of individual properties into a single {@link PropertyCollection}.
		 *
		 * @param properties the list of individual properties to collect
		 * @return a collection of properties that represents the aggregated result
		 * @throws IllegalArgumentException if the input list of properties is invalid
		 */
		PropertyCollection collect(List<PropertyType> properties) throws IllegalArgumentException;
	}

	/**
	 * Creates a collection of properties by applying a {@link PropertyFactory} to a list of
	 * {@link GeoElement} s and then aggregating the resulting properties using a
	 * {@link PropertyCollector}. The method filters properties using the provided property filters
	 * before collecting them.
	 *
	 * @param <Prop> the type of individual properties to be created
	 * @param <PropCollection> the type of the property collection to be created
	 * @param geoElements the list of {@link GeoElement}s for which properties are to be created
	 * @param propertyFactory the factory used to create
	 * individual properties for each {@link GeoElement}
	 * @param propertyCollector the collector used to
	 * aggregate the individual properties into a collection
	 * @return a collection of properties of type {@link PropCollection}, or {@code null}
	 * if a property cannot be created for one of the {@link GeoElement}s.
	 */
	private <
			Prop extends Property,
			PropCollection extends Property
	> PropCollection createPropertyCollection(
			List<GeoElement> geoElements,
			PropertyFactory<Prop> propertyFactory,
			PropertyCollector<Prop, PropCollection> propertyCollector
	) {
		try {
			ArrayList<Prop> properties = new ArrayList<>();
			for (GeoElement geoElement : geoElements) {
				Prop property = propertyFactory.create(geoElement);
				if (property != null && isAllowedByFilters(property, geoElement)) {
					properties.add(property);
				}
			}
			if (properties.isEmpty()) {
				return null;
			}
			return propertyCollector.collect(properties);
		} catch (NotApplicablePropertyException ignored) {
			return null;
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

	private boolean isAllowedByFilters(Property property, GeoElement geoElement) {
		return propertyFilters.stream().allMatch(filter -> filter.isAllowed(property, geoElement));
	}
}
