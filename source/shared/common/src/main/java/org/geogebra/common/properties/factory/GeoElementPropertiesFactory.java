package org.geogebra.common.properties.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.exam.restrictions.PropertyRestriction;
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
import org.geogebra.common.properties.impl.collections.FlagListPropertyCollection;
import org.geogebra.common.properties.impl.collections.IconsEnumeratedPropertyCollection;
import org.geogebra.common.properties.impl.collections.NamedEnumeratedPropertyCollection;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
import org.geogebra.common.properties.impl.collections.StringPropertyCollection;
import org.geogebra.common.properties.impl.objects.AnimationStepProperty;
import org.geogebra.common.properties.impl.objects.BoldProperty;
import org.geogebra.common.properties.impl.objects.BorderColorProperty;
import org.geogebra.common.properties.impl.objects.BorderThicknessProperty;
import org.geogebra.common.properties.impl.objects.CaptionStyleProperty;
import org.geogebra.common.properties.impl.objects.CellBorderProperty;
import org.geogebra.common.properties.impl.objects.CellBorderThicknessProperty;
import org.geogebra.common.properties.impl.objects.ElementColorProperty;
import org.geogebra.common.properties.impl.objects.FillingStyleProperty;
import org.geogebra.common.properties.impl.objects.HorizontalAlignmentProperty;
import org.geogebra.common.properties.impl.objects.ImageOpacityProperty;
import org.geogebra.common.properties.impl.objects.IsFixedObjectProperty;
import org.geogebra.common.properties.impl.objects.ItalicProperty;
import org.geogebra.common.properties.impl.objects.LabelStyleProperty;
import org.geogebra.common.properties.impl.objects.LineStyleProperty;
import org.geogebra.common.properties.impl.objects.LinearEquationFormProperty;
import org.geogebra.common.properties.impl.objects.MaxProperty;
import org.geogebra.common.properties.impl.objects.MinProperty;
import org.geogebra.common.properties.impl.objects.NameCaptionProperty;
import org.geogebra.common.properties.impl.objects.NameProperty;
import org.geogebra.common.properties.impl.objects.NotesColorWithOpacityProperty;
import org.geogebra.common.properties.impl.objects.NotesOpacityColorProperty;
import org.geogebra.common.properties.impl.objects.NotesThicknessProperty;
import org.geogebra.common.properties.impl.objects.ObjectColorProperty;
import org.geogebra.common.properties.impl.objects.OpacityProperty;
import org.geogebra.common.properties.impl.objects.PointSizeProperty;
import org.geogebra.common.properties.impl.objects.PointStyleExtendedProperty;
import org.geogebra.common.properties.impl.objects.PointStyleProperty;
import org.geogebra.common.properties.impl.objects.QuadraticEquationFormProperty;
import org.geogebra.common.properties.impl.objects.SegmentEndProperty;
import org.geogebra.common.properties.impl.objects.SegmentStartProperty;
import org.geogebra.common.properties.impl.objects.ShowInAVProperty;
import org.geogebra.common.properties.impl.objects.ShowObjectProperty;
import org.geogebra.common.properties.impl.objects.ShowTraceProperty;
import org.geogebra.common.properties.impl.objects.SlopeSizeProperty;
import org.geogebra.common.properties.impl.objects.TextBackgroundColorProperty;
import org.geogebra.common.properties.impl.objects.TextFontColorProperty;
import org.geogebra.common.properties.impl.objects.TextFontSizeProperty;
import org.geogebra.common.properties.impl.objects.ThicknessProperty;
import org.geogebra.common.properties.impl.objects.UnderlineProperty;
import org.geogebra.common.properties.impl.objects.VerticalAlignmentProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Creates the list of properties for a GeoElement or for a list of GeoElements.
 */
public final class GeoElementPropertiesFactory {

	private final Set<GeoElementPropertyFilter> propertyFilters = new HashSet<>();
	private final Map<String, Set<PropertyRestriction>> propertyRestrictions = new HashMap<>();

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
	 * Add a property restriction to be applied to properties created by this factory.
	 * @param propertyName A property (raw) name (i.e., this should match property.getRawName())
	 * @param restriction A property restriction.
	 */
	public void addRestriction(String propertyName, PropertyRestriction restriction) {
		propertyRestrictions
				.computeIfAbsent(propertyName, key -> new HashSet<>())
				.add(restriction);
	}

	/**
	 * Remove a previously added property restriction.
	 * @param propertyName A property (raw) name (i.e., this should match property.getRawName())
	 * @param restriction Property restriction, identified by raw name (i.e., this should
	 * match property.getRawName()).
	 */
	public void removeRestriction(String propertyName, PropertyRestriction restriction) {
		Set<PropertyRestriction> registeredRestrictions = propertyRestrictions.get(propertyName);
		if (registeredRestrictions != null) {
			registeredRestrictions.remove(restriction);
		}
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
						StringPropertyCollection::new),
				createPropertyCollection(elements,
						element -> new MinProperty(processor, localization, element),
						StringPropertyCollection::new),
				createPropertyCollection(elements,
						element -> new MaxProperty(processor, localization, element),
						StringPropertyCollection::new),
				createPropertyCollection(elements,
						element -> new AnimationStepProperty(processor, localization, element),
						StringPropertyCollection::new),
				createShowObjectProperty(localization, elements),
				createColorProperty(localization, elements),
				createPointStyleProperty(localization, elements),
				createPointSizeProperty(localization, elements),
				createOpacityProperty(localization, elements),
				createLineStyleProperty(localization, elements),
				createThicknessProperty(localization, elements),
				createPropertyCollection(elements,
						element -> new SlopeSizeProperty(localization, element),
						RangePropertyCollection::new),
				createPropertyCollection(elements,
						element -> new LinearEquationFormProperty(localization, element),
						NamedEnumeratedPropertyCollection::new),
				createPropertyCollection(elements,
						element -> new QuadraticEquationFormProperty(localization, element),
						NamedEnumeratedPropertyCollection::new),
				createPropertyCollection(elements,
						element -> new CaptionStyleProperty(localization, element),
						NamedEnumeratedPropertyCollection::new),
				createPropertyCollection(elements,
						element -> new ShowTraceProperty(localization, element),
						BooleanPropertyCollection::new),
				createFixObjectProperty(localization, elements),
				createPropertyCollection(elements,
						element -> new ShowInAVProperty(localization, element),
						BooleanPropertyCollection::new)
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
	 * Creates extended point style properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public PropertiesArray createPointStyleExtendedProperties(
			Localization localization, List<GeoElement> elements) {
		return createPropertiesArray(localization, elements, Stream.of(
				createPointStyleExtendedProperty(localization, elements),
				createPointSizeProperty(localization, elements)
		).filter(Objects::nonNull).collect(Collectors.toList()));
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the extended point style or null if
	 * not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedProperty<?> createPointStyleExtendedProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new PointStyleExtendedProperty(localization, element),
				IconsEnumeratedPropertyCollection::new);
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
	 * Creates Lines style properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public List<Property> createNotesLineStyleProperties(
			Localization localization, List<GeoElement> elements) {
		return Stream.of(
				createLineStyleProperty(localization, elements),
				createNotesThicknessProperty(localization, elements)
		).filter(Objects::nonNull).collect(Collectors.toList());
	}

	/**
	 * Creates color with opacity properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public PropertiesArray createNotesColorWithOpacityProperties(
			Localization localization, List<GeoElement> elements) {
		return createPropertiesArray(localization, elements, Stream.of(
				createColorWithOpacityProperty(localization, elements),
				createOpacityColorProperty(localization, elements)
		).filter(Objects::nonNull).collect(Collectors.toList()));
	}

	/**
	 * Creates border color and thickness for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public PropertiesArray createObjectBorderProperties(
			Localization localization, List<GeoElement> elements) {
		return createPropertiesArray(localization, elements, Stream.of(
				createBorderColorProperty(localization, elements),
				createBorderThicknessProperty(localization, elements)
		).filter(Objects::nonNull).collect(Collectors.toList()));
	}

	/**
	 * Creates cell border style properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public PropertiesArray createCellBorderStyleProperties(
			Localization localization, List<GeoElement> elements) {
		return createPropertiesArray(localization, elements, Stream.of(
				createCellBorderStyleProperty(localization, elements),
				createCellBorderThicknessProperty(localization, elements)
		).filter(Objects::nonNull).collect(Collectors.toList()));
	}

	/**
	 * Returns an Integer RangeProperty controlling the border thickness null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public RangePropertyCollection<?> createCellBorderThicknessProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new CellBorderThicknessProperty(localization,
						element),
				RangePropertyCollection::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the cell border or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedProperty<?> createCellBorderStyleProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new CellBorderProperty(localization, element),
				IconsEnumeratedPropertyCollection::new);
	}

	/**
	 * Create label property array
	 * @param localization localization
	 * @param elements elements
	 * @return label property
	 */
	public PropertiesArray createLabelProperties(Localization localization,
			List<GeoElement> elements) {
		return createPropertiesArray(localization, elements,  Stream.<Property>of(
				createPropertyCollection(elements,
						element -> new NameCaptionProperty(localization, element),
						StringPropertyCollection::new),
				createLabelStyleProperty(localization, elements)
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
				ColorPropertyCollection::new);
	}

	/**
	 * Creates a color property for strokes, shapes, lines, point, rays, sliders, points with
	 * the new color palette, see {@link org.geogebra.common.main.color.GeoColorValues}
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public ColorProperty createObjectColorProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new ObjectColorProperty(localization, element),
				ColorPropertyCollection::new);
	}

	/**
	 * Creates a color property for non-mask shapes
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public ColorProperty createColorWithOpacityProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new NotesColorWithOpacityProperty(localization, element),
				ColorPropertyCollection::new);
	}

	/**
	 * Creates a font color property for inline object and text
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public ColorProperty createTextFontColorProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new TextFontColorProperty(localization, element),
				ColorPropertyCollection::new);
	}

	/**
	 * Creates a background color property for inline object and text
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public ColorProperty createTextBackgroundColorProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new TextBackgroundColorProperty(localization, element),
				ColorPropertyCollection::new);
	}

	/**
	 * Creates border color property for text and mind-map
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public ColorProperty createBorderColorProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new BorderColorProperty(localization, element),
				ColorPropertyCollection::new);
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
				BooleanPropertyCollection::new);
	}

	/**
	 * Returns with a Boolean property that formats bold the texts, or null if not applicable
	 * @param localization localization
	 * @param elements elements
	 * @return bold property or null
	 */
	public BooleanProperty createBoldProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new BoldProperty(localization, element),
				BooleanPropertyCollection::new);
	}

	/**
	 * Returns with a Boolean property that formats italic the texts, or null if not applicable
	 * @param localization localization
	 * @param elements elements
	 * @return italic property or null
	 */
	public BooleanProperty createItalicProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new ItalicProperty(localization, element),
				BooleanPropertyCollection::new);
	}

	/**
	 * Returns with a Boolean property that formats underlined the texts, or null if not applicable
	 * @param localization localization
	 * @param elements elements
	 * @return underline property or null
	 */
	public BooleanProperty createUnderlineProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new UnderlineProperty(localization, element),
				BooleanPropertyCollection::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the point style or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedProperty<?> createPointStyleProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new PointStyleProperty(localization, element),
				IconsEnumeratedPropertyCollection::new);
	}

	/**
	 * Returns an Integer RangeProperty controlling the line thickness in notes,
	 * null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public RangePropertyCollection<?> createNotesThicknessProperty(Localization
			localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new NotesThicknessProperty(localization, element),
				RangePropertyCollection::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the line style or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedProperty<?> createLineStyleProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new LineStyleProperty(localization, element),
				IconsEnumeratedPropertyCollection::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the filling type or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedPropertyCollection<?, ?> createFillingStyleProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new FillingStyleProperty(localization, element),
				IconsEnumeratedPropertyCollection::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the horizontal alignment or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedPropertyCollection<?, ?> createHorizontalAlignmentProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new HorizontalAlignmentProperty(localization,
						element),
				IconsEnumeratedPropertyCollection::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the horizontal alignment or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedPropertyCollection<?, ?> createVerticalAlignmentProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new VerticalAlignmentProperty(localization,
						element),
				IconsEnumeratedPropertyCollection::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the segment start style or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedPropertyCollection<?, ?> createSegmentStartProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new SegmentStartProperty(localization, element),
				IconsEnumeratedPropertyCollection::new);
	}

	/**
	 * Returns property controlling the text font size or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @param ev euclidian view
	 * @return property or null
	 */
	public NamedEnumeratedPropertyCollection<?, ?> createTextFontSizeProperty(
			Localization localization, List<GeoElement> elements, EuclidianView ev) {
		return createPropertyCollection(elements,
				element -> new TextFontSizeProperty(localization, element, ev),
				NamedEnumeratedPropertyCollection::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the segment end style or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedPropertyCollection<?, ?> createSegmentEndProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new SegmentEndProperty(localization, element),
				IconsEnumeratedPropertyCollection::new);
	}

	/**
	 * Returns an ValuedPropertyCollection controlling the label style or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static FlagListPropertyCollection<LabelStyleProperty> createLabelStyleProperty(
			Localization localization, List<GeoElement> elements) {
		List<LabelStyleProperty> labelStyleProperties = new ArrayList<>();
		for (GeoElement element : elements) {
			labelStyleProperties.add(new LabelStyleProperty(localization, element.getKernel(),
					element));
		}
		return new FlagListPropertyCollection<>(labelStyleProperties.toArray(
				new LabelStyleProperty[0]));
	}

	/**
	 * Returns an StringPropertyCollection controlling the label of geo or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public StringPropertyCollection<NameProperty> createNameProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new NameProperty(localization, element),
				StringPropertyCollection::new);
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
				RangePropertyCollection::new);
	}

	/**
	 * Returns a RangePropertyCollection controlling the opacity or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public RangeProperty<Integer> createOpacityColorProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new NotesOpacityColorProperty(localization, element),
				RangePropertyCollection::new);
	}

	/**
	 * Returns a RangePropertyCollection controlling the opacity or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public RangeProperty<Integer> createBorderThicknessProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new BorderThicknessProperty(localization, element),
				RangePropertyCollection::new);
	}

	/**
	 * Returns a RangePropertyCollection controlling the opacity or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public RangeProperty<Integer> createImageOpacityProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyCollection(elements,
				element -> new ImageOpacityProperty(localization, element),
				RangePropertyCollection::new);
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
				BooleanPropertyCollection::new);
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
				RangePropertyCollection::new);
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
				RangePropertyCollection::new);
	}

	private PropertiesArray createPropertiesArray(Localization localization,
			List<GeoElement> geoElements, List<Property> properties) {
		if (properties.isEmpty()) {
			return new PropertiesArray(null, localization);
		}

		String name;
		if (geoElements.size() > 1) {
			name = "Selection";
		} else if (geoElements.size() == 1) {
			GeoElement element = geoElements.get(0);
			name = element.getTypeString();
		} else {
			name = null;
		}

		return new PropertiesArray(name, localization, properties.toArray(new Property[0]));
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
					// apply restrictions
					Set<PropertyRestriction> restrictions = propertyRestrictions.get(
							property.getRawName());
					if (restrictions != null) {
						for (PropertyRestriction restriction : restrictions) {
							restriction.applyTo(property);
						}
					}
				}
			}
			if (properties.isEmpty()) {
				return null;
			}
			return propertyCollector.collect(properties);
		} catch (NotApplicablePropertyException | IllegalArgumentException ignored) {
			return null;
		}
	}

	private boolean isAllowedByFilters(Property property, GeoElement geoElement) {
		return propertyFilters.stream().allMatch(filter -> filter.isAllowed(property, geoElement));
	}
}
