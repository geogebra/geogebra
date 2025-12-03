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

import javax.annotation.Nonnull;

import org.geogebra.common.exam.restrictions.PropertyRestriction;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.properties.GeoElementPropertyFilter;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.facade.BooleanPropertyListFacade;
import org.geogebra.common.properties.impl.facade.ColorPropertyListFacade;
import org.geogebra.common.properties.impl.facade.FilePropertyListFacade;
import org.geogebra.common.properties.impl.facade.FlagListPropertyListFacade;
import org.geogebra.common.properties.impl.facade.IconsEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.ObjectEventPropertyListFacade;
import org.geogebra.common.properties.impl.facade.RangePropertyListFacade;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;
import org.geogebra.common.properties.impl.facade.StringPropertyWithSuggestionsListFacade;
import org.geogebra.common.properties.impl.objects.AngleArcSizeProperty;
import org.geogebra.common.properties.impl.objects.AngleDecorationProperty;
import org.geogebra.common.properties.impl.objects.AnimatingProperty;
import org.geogebra.common.properties.impl.objects.AnimationStepProperty;
import org.geogebra.common.properties.impl.objects.AuxiliaryObjectProperty;
import org.geogebra.common.properties.impl.objects.BackgroundImageProperty;
import org.geogebra.common.properties.impl.objects.BoldProperty;
import org.geogebra.common.properties.impl.objects.BorderColorProperty;
import org.geogebra.common.properties.impl.objects.BorderThicknessProperty;
import org.geogebra.common.properties.impl.objects.ButtonFixedSizeProperty;
import org.geogebra.common.properties.impl.objects.ButtonHeightProperty;
import org.geogebra.common.properties.impl.objects.ButtonWidthProperty;
import org.geogebra.common.properties.impl.objects.CaptionProperty;
import org.geogebra.common.properties.impl.objects.CaptionStyleProperty;
import org.geogebra.common.properties.impl.objects.CellBorderProperty;
import org.geogebra.common.properties.impl.objects.CellBorderThicknessProperty;
import org.geogebra.common.properties.impl.objects.DefinitionProperty;
import org.geogebra.common.properties.impl.objects.DrawArrowsProperty;
import org.geogebra.common.properties.impl.objects.ElementColorProperty;
import org.geogebra.common.properties.impl.objects.ElementObjectEventProperty;
import org.geogebra.common.properties.impl.objects.FillImageProperty;
import org.geogebra.common.properties.impl.objects.FillSymbolProperty;
import org.geogebra.common.properties.impl.objects.FillingStyleProperty;
import org.geogebra.common.properties.impl.objects.FixCheckboxProperty;
import org.geogebra.common.properties.impl.objects.FixObjectProperty;
import org.geogebra.common.properties.impl.objects.FixSliderObjectProperty;
import org.geogebra.common.properties.impl.objects.HatchingAngleProperty;
import org.geogebra.common.properties.impl.objects.HatchingDistanceProperty;
import org.geogebra.common.properties.impl.objects.HorizontalAlignmentProperty;
import org.geogebra.common.properties.impl.objects.ImageInterpolationProperty;
import org.geogebra.common.properties.impl.objects.ImageOpacityProperty;
import org.geogebra.common.properties.impl.objects.InequalityOnAxisProperty;
import org.geogebra.common.properties.impl.objects.InputBoxAlignmentProperty;
import org.geogebra.common.properties.impl.objects.InputBoxSizeProperty;
import org.geogebra.common.properties.impl.objects.InverseFillProperty;
import org.geogebra.common.properties.impl.objects.IsFixedObjectProperty;
import org.geogebra.common.properties.impl.objects.ItalicProperty;
import org.geogebra.common.properties.impl.objects.LabelProperty;
import org.geogebra.common.properties.impl.objects.LabelStyleProperty;
import org.geogebra.common.properties.impl.objects.LevelOfDetailProperty;
import org.geogebra.common.properties.impl.objects.LineOpacityProperty;
import org.geogebra.common.properties.impl.objects.LineStyleProperty;
import org.geogebra.common.properties.impl.objects.LinearEquationFormProperty;
import org.geogebra.common.properties.impl.objects.ListAsComboBoxProperty;
import org.geogebra.common.properties.impl.objects.MaxProperty;
import org.geogebra.common.properties.impl.objects.MinProperty;
import org.geogebra.common.properties.impl.objects.NameCaptionProperty;
import org.geogebra.common.properties.impl.objects.NameProperty;
import org.geogebra.common.properties.impl.objects.NotesColorWithOpacityProperty;
import org.geogebra.common.properties.impl.objects.NotesOpacityColorProperty;
import org.geogebra.common.properties.impl.objects.NotesThicknessProperty;
import org.geogebra.common.properties.impl.objects.ObjectAllEventsProperty;
import org.geogebra.common.properties.impl.objects.ObjectColorProperty;
import org.geogebra.common.properties.impl.objects.ObjectEventProperty;
import org.geogebra.common.properties.impl.objects.OpacityProperty;
import org.geogebra.common.properties.impl.objects.OutlyingIntersectionsProperty;
import org.geogebra.common.properties.impl.objects.PointSizeProperty;
import org.geogebra.common.properties.impl.objects.PointStyleExtendedProperty;
import org.geogebra.common.properties.impl.objects.PointStyleProperty;
import org.geogebra.common.properties.impl.objects.QuadraticEquationFormProperty;
import org.geogebra.common.properties.impl.objects.SegmentDecorationProperty;
import org.geogebra.common.properties.impl.objects.SegmentEndProperty;
import org.geogebra.common.properties.impl.objects.SegmentStartProperty;
import org.geogebra.common.properties.impl.objects.ShowInAVProperty;
import org.geogebra.common.properties.impl.objects.ShowObjectProperty;
import org.geogebra.common.properties.impl.objects.ShowTraceProperty;
import org.geogebra.common.properties.impl.objects.SliderIntervalProperty;
import org.geogebra.common.properties.impl.objects.SlopeSizeProperty;
import org.geogebra.common.properties.impl.objects.TextBackgroundColorProperty;
import org.geogebra.common.properties.impl.objects.TextFontColorProperty;
import org.geogebra.common.properties.impl.objects.TextFontSizeProperty;
import org.geogebra.common.properties.impl.objects.ThicknessProperty;
import org.geogebra.common.properties.impl.objects.UnderlineProperty;
import org.geogebra.common.properties.impl.objects.VectorHeadProperty;
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
		return createPropertiesArray(localization, elements, Stream.of(
				createNameProperty(localization, elements),
				createMinProperty(processor, localization, elements),
				createMaxProperty(processor, localization, elements),
				createAnimationStepProperty(processor, localization, elements, true),
				createShowObjectProperty(localization, elements),
				createColorProperty(localization, elements),
				createPointStyleProperty(localization, elements),
				createPointSizeProperty(localization, elements),
				createOpacityProperty(localization, elements),
				createLineStyleProperty(localization, elements),
				createThicknessProperty(localization, elements),
				createSlopeSizeProperty(localization, elements),
				createLinearEquationProperty(localization, elements),
				createQuadraticEquationProperty(localization, elements),
				createCaptionStyleProperty(localization, elements),
				createShowTraceProperty(localization, elements),
				createIsFixedObjectProperty(localization, elements),
				createPropertyFacade(elements,
						element -> new ShowInAVProperty(localization, element),
						BooleanPropertyListFacade::new)
		).filter(Objects::nonNull).collect(Collectors.toList()));
	}

	private Property createCaptionStyleProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new CaptionStyleProperty(localization, element),
				NamedEnumeratedPropertyListFacade::new);
	}

	private Property createShowTraceProperty(Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new ShowTraceProperty(localization, element),
				BooleanPropertyListFacade::new);
	}

	private Property createMinProperty(AlgebraProcessor processor, Localization localization,
			List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new MinProperty(processor, localization, element),
				StringPropertyListFacade::new);
	}

	private Property createMaxProperty(AlgebraProcessor processor, Localization localization,
			List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new MaxProperty(processor, localization, element),
				StringPropertyListFacade::new);
	}

	private Property createFixObjectProperty(Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> {
					try {
						return new FixObjectProperty(localization, element);
					} catch (NotApplicablePropertyException e1) {
						try {
							return new FixSliderObjectProperty(localization, element);
						} catch (NotApplicablePropertyException e2) {
							return new FixCheckboxProperty(localization, element);
						}
					}
				},
				BooleanPropertyListFacade::new);
	}

	private Property createAnimationStepProperty(AlgebraProcessor processor,
			Localization localization, List<GeoElement> elements, boolean forSliders) {
		return createPropertyFacade(elements,
				element -> new AnimationStepProperty(processor, localization, element, forSliders),
				StringPropertyListFacade::new);
	}

	private Property createQuadraticEquationProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new QuadraticEquationFormProperty(localization, element),
				NamedEnumeratedPropertyListFacade::new);
	}

	private Property createLinearEquationProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new LinearEquationFormProperty(localization, element),
				NamedEnumeratedPropertyListFacade::new);
	}

	private Property createSlopeSizeProperty(Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new SlopeSizeProperty(localization, element),
				RangePropertyListFacade::new);
	}

	/**
	 * Properties for the tabbed properties view.
	 * @param processor algebra processor
	 * @param localization localization
	 * @param elements selected elements
	 * @return properties organized in tabs
	 */
	public List<PropertiesArray> createStructuredProperties(
			AlgebraProcessor processor, Localization localization, List<GeoElement> elements) {
		return Stream.of(
				createBasicProperties(localization, elements),
				createStyleProperties(processor, localization, elements),
				createAdvancedProperties(processor, localization, elements),
				createScriptProperties(localization, elements)
		).filter(propertiesArray ->
				propertiesArray.getProperties().length > 0).collect(Collectors.toList());
	}

	private @Nonnull PropertiesArray createBasicProperties(
			Localization localization, List<GeoElement> elements) {
		return createPropsArray("Basic", localization, Stream.of(
				createNameProperty(localization, elements),
				elements.size() == 1 ? new DefinitionProperty(localization, elements.get(0)) : null,
				createPropertyFacade(elements,
						element -> new CaptionProperty(localization, element),
						StringPropertyWithSuggestionsListFacade::new),
				createPropertyFacade(elements,
						element -> new LabelProperty(localization, element),
						NamedEnumeratedPropertyListFacade::new),
				createShowObjectProperty(localization, elements),
				createShowTraceProperty(localization, elements),
				createFixObjectProperty(localization, elements),
				createPropertyFacade(elements,
						element -> new AuxiliaryObjectProperty(localization, element),
						BooleanPropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new OutlyingIntersectionsProperty(localization, element),
						BooleanPropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new AnimatingProperty(localization, element),
						BooleanPropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new BackgroundImageProperty(localization, element),
						BooleanPropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new ListAsComboBoxProperty(localization, element),
						BooleanPropertyListFacade::new)));
	}

	private @Nonnull PropertiesArray createStyleProperties(
			AlgebraProcessor processor,
			Localization localization, List<GeoElement> elements) {
		return createPropsArray("Style", localization, Stream.of(
				createPointSizeProperty(localization, elements),
				createPointStyleProperty(localization, elements),
				createThicknessProperty(localization, elements),
				createLineOpacityProperty(localization, elements),
				createLineStyleProperty(localization, elements, false),
				createLineStyleProperty(localization, elements, true),
				createPropertyFacade(elements,
						element -> new DrawArrowsProperty(localization, element),
						BooleanPropertyListFacade::new),
				// arcsize
				createSlopeSizeProperty(localization, elements),
				createPropertyFacade(elements,
						element -> new AngleArcSizeProperty(localization, element),
						RangePropertyListFacade::new),

				createFillingStyleProperty(localization, elements, true),
				createPropertyFacade(elements,
						element -> new HatchingAngleProperty(localization, element),
						RangePropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new HatchingDistanceProperty(localization, element),
						RangePropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new FillSymbolProperty(localization, element),
						StringPropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new FillImageProperty(localization, element),
						FilePropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new InverseFillProperty(localization, element),
						BooleanPropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new InequalityOnAxisProperty(localization, element),
						BooleanPropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new InputBoxSizeProperty(processor, localization, element),
						StringPropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new InputBoxAlignmentProperty(localization, element),
						NamedEnumeratedPropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new ButtonWidthProperty(processor, localization, element),
						StringPropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new ButtonHeightProperty(processor, localization, element),
						StringPropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new ButtonFixedSizeProperty(localization, element),
						BooleanPropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new LevelOfDetailProperty(localization, element),
						NamedEnumeratedPropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new ImageInterpolationProperty(localization, element),
						BooleanPropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new AngleDecorationProperty(localization, element),
						IconsEnumeratedPropertyListFacade::new),
				createPropertyFacade(elements,
						element -> new SegmentDecorationProperty(localization, element),
						IconsEnumeratedPropertyListFacade::new),
				createSegmentStartProperty(localization, elements),
				createSegmentEndProperty(localization, elements),
				createPropertyFacade(elements,
						element -> new VectorHeadProperty(localization, element),
						IconsEnumeratedPropertyListFacade::new)
		));
	}

	private @Nonnull PropertiesArray createAdvancedProperties(AlgebraProcessor processor,
			Localization localization, List<GeoElement> elements) {
		return createPropsArray("Advanced", localization, Stream.of(
				createOptionalProperty(
						() -> new SliderIntervalProperty(this, processor, localization, elements))

				// show condition
				// color function
				// layer
				// selection allowed
				// show in views
		));
	}

	private @Nonnull PropertiesArray createScriptProperties(
			Localization localization, List<GeoElement> elements) {
		return createPropsArray("Scripting", localization, Stream.of(
				createObjectEventsProperty(localization, elements)
			)
		);
	}

	/**
	 * Creates script related properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public ObjectAllEventsProperty createObjectEventsProperty(
			Localization localization, List<GeoElement> elements) {
		ArrayList<ObjectEventProperty> props = new ArrayList<>();
		for (EventType type : ElementObjectEventProperty.eventNames.keySet()) {
			ObjectEventProperty op = createPropertyFacade(elements,
					element -> new ElementObjectEventProperty(localization, element, type),
					ObjectEventPropertyListFacade::new);
			if (op != null) {
				props.add(op);
			}
		}
		return new ObjectAllEventsProperty(localization, props);
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
		return createPropertyFacade(elements,
				element -> new PointStyleExtendedProperty(localization, element),
				IconsEnumeratedPropertyListFacade::new);
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
	public RangePropertyListFacade<?> createCellBorderThicknessProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new CellBorderThicknessProperty(localization,
						element),
				RangePropertyListFacade::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the cell border or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedProperty<?> createCellBorderStyleProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new CellBorderProperty(localization, element),
				IconsEnumeratedPropertyListFacade::new);
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
				createPropertyFacade(elements,
						element -> new NameCaptionProperty(localization, element),
						StringPropertyListFacade::new),
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
		return createPropertyFacade(elements,
				element -> new ElementColorProperty(localization, element),
				ColorPropertyListFacade::new);
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
		return createPropertyFacade(elements,
				element -> new ObjectColorProperty(localization, element),
				ColorPropertyListFacade::new);
	}

	/**
	 * Creates a color property for non-mask shapes
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public ColorProperty createColorWithOpacityProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new NotesColorWithOpacityProperty(localization, element),
				ColorPropertyListFacade::new);
	}

	/**
	 * Creates a font color property for inline object and text
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public ColorProperty createTextFontColorProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new TextFontColorProperty(localization, element),
				ColorPropertyListFacade::new);
	}

	/**
	 * Creates a background color property for inline object and text
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public ColorProperty createTextBackgroundColorProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new TextBackgroundColorProperty(localization, element),
				ColorPropertyListFacade::new);
	}

	/**
	 * Creates border color property for text and mind-map
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public ColorProperty createBorderColorProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new BorderColorProperty(localization, element),
				ColorPropertyListFacade::new);
	}

	/**
	 * Returns with a Boolean property that fixes the object, or null if not applicable
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public BooleanProperty createIsFixedObjectProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new IsFixedObjectProperty(localization, element),
				BooleanPropertyListFacade::new);
	}

	/**
	 * Returns with a Boolean property that formats bold the texts, or null if not applicable
	 * @param localization localization
	 * @param elements elements
	 * @return bold property or null
	 */
	public BooleanProperty createBoldProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new BoldProperty(localization, element),
				BooleanPropertyListFacade::new);
	}

	/**
	 * Returns with a Boolean property that formats italic the texts, or null if not applicable
	 * @param localization localization
	 * @param elements elements
	 * @return italic property or null
	 */
	public BooleanProperty createItalicProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new ItalicProperty(localization, element),
				BooleanPropertyListFacade::new);
	}

	/**
	 * Returns with a Boolean property that formats underlined the texts, or null if not applicable
	 * @param localization localization
	 * @param elements elements
	 * @return underline property or null
	 */
	public BooleanProperty createUnderlineProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new UnderlineProperty(localization, element),
				BooleanPropertyListFacade::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the point style or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedProperty<?> createPointStyleProperty(Localization localization,
			List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new PointStyleProperty(localization, element),
				IconsEnumeratedPropertyListFacade::new);
	}

	/**
	 * Returns an Integer RangeProperty controlling the line thickness in notes,
	 * null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public RangePropertyListFacade<?> createNotesThicknessProperty(Localization
			localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new NotesThicknessProperty(localization, element),
				RangePropertyListFacade::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the line style or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedProperty<?> createLineStyleProperty(
			Localization localization, List<GeoElement> elements) {
		return createLineStyleProperty(localization, elements, false);
	}

	private IconsEnumeratedProperty<?> createLineStyleProperty(
			Localization localization, List<GeoElement> elements, boolean hidden) {
		return createPropertyFacade(elements,
				element -> new LineStyleProperty(localization, element, hidden),
				IconsEnumeratedPropertyListFacade::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the filling type or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedPropertyListFacade<?, ?> createFillingStyleProperty(
			Localization localization, List<GeoElement> elements) {
		return createFillingStyleProperty(localization, elements, false);
	}

	private IconsEnumeratedPropertyListFacade<?, ?> createFillingStyleProperty(
			Localization localization, List<GeoElement> elements, boolean b) {
		return createPropertyFacade(elements,
				element -> new FillingStyleProperty(localization, element, b),
				IconsEnumeratedPropertyListFacade::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the horizontal alignment or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedPropertyListFacade<?, ?> createHorizontalAlignmentProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new HorizontalAlignmentProperty(localization,
						element),
				IconsEnumeratedPropertyListFacade::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the horizontal alignment or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedPropertyListFacade<?, ?> createVerticalAlignmentProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new VerticalAlignmentProperty(localization,
						element),
				IconsEnumeratedPropertyListFacade::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the segment start style or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedPropertyListFacade<?, ?> createSegmentStartProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new SegmentStartProperty(localization, element),
				IconsEnumeratedPropertyListFacade::new);
	}

	/**
	 * Returns property controlling the text font size or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public NamedEnumeratedPropertyListFacade<?, ?> createTextFontSizeProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new TextFontSizeProperty(localization, element),
				NamedEnumeratedPropertyListFacade::new);
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the segment end style or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedPropertyListFacade<?, ?> createSegmentEndProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new SegmentEndProperty(localization, element),
				IconsEnumeratedPropertyListFacade::new);
	}

	/**
	 * Returns an ValuedPropertyCollection controlling the label style or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public static FlagListPropertyListFacade<LabelStyleProperty> createLabelStyleProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<LabelStyleProperty> labelStyleProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				labelStyleProperties.add(new LabelStyleProperty(localization, element.getKernel(),
						element));
			}
			return new FlagListPropertyListFacade<>(labelStyleProperties.toArray(
					new LabelStyleProperty[0]));
		} catch (NotApplicablePropertyException e) {
			return null;
		}
	}

	/**
	 * Returns an StringPropertyCollection controlling the label of geo or null
	 * if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public StringPropertyListFacade<NameProperty> createNameProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new NameProperty(localization, element),
				StringPropertyListFacade::new);
	}

	/**
	 * Returns a RangePropertyCollection controlling the opacity or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public RangeProperty<Integer> createOpacityProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new OpacityProperty(localization, element),
				RangePropertyListFacade::new);
	}

	private RangeProperty<Integer> createLineOpacityProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new LineOpacityProperty(localization, element),
				RangePropertyListFacade::new);
	}

	/**
	 * Returns a RangePropertyCollection controlling the opacity or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public RangeProperty<Integer> createOpacityColorProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new NotesOpacityColorProperty(localization, element),
				RangePropertyListFacade::new);
	}

	/**
	 * Returns a RangePropertyCollection controlling the opacity or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public RangeProperty<Integer> createBorderThicknessProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new BorderThicknessProperty(localization, element),
				RangePropertyListFacade::new);
	}

	/**
	 * Returns a RangePropertyCollection controlling the opacity or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public RangeProperty<Integer> createImageOpacityProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new ImageOpacityProperty(localization, element),
				RangePropertyListFacade::new);
	}

	/**
	 * Creates a {@link BooleanPropertyListFacade} to control the visibility of the elements.
	 * @param localization localization for the property name
	 * @param elements elements for which the property should be created
	 * @return the property or {@code null} if it couldn't be created or is filtered
	 */
	public BooleanProperty createShowObjectProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new ShowObjectProperty(localization, element),
				BooleanPropertyListFacade::new);
	}

	/**
	 * Creates a {@link RangePropertyListFacade} to control the size of the points.
	 * @param localization localization for the property name
	 * @param elements elements for which the property should be created
	 * @return the property or {@code null} if it couldn't be created or is filtered
	 */
	public RangeProperty<Integer> createPointSizeProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new PointSizeProperty(localization, element),
				RangePropertyListFacade::new);
	}

	/**
	 * Creates a {@link RangePropertyListFacade} to control the thickness of lines.
	 * @param localization localization for the property name
	 * @param elements elements for which the property should be created
	 * @return the property or {@code null} if it couldn't be created or is filtered
	 */
	public RangeProperty<Integer> createThicknessProperty(
			Localization localization, List<GeoElement> elements) {
		return createPropertyFacade(elements,
				element -> new ThicknessProperty(localization, element),
				RangePropertyListFacade::new);
	}

	private PropertiesArray createPropsArray(String name, Localization localization,
			Stream<Property> properties) {
		return new PropertiesArray(name, localization,
				properties.filter(Objects::nonNull).toArray(Property[]::new));
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
	 * A factory interface for creating instances of properties.
	 * @param <P> the type of property that this factory produces
	 */
	private interface PropertyFactory<P extends Property> {

		/**
		 * Creates a property instance.
		 * If the property is not applicable a {@link NotApplicablePropertyException} is thrown.
		 *
		 * @return an instance of the specific property type
		 * @throws NotApplicablePropertyException if the property cannot be applied
		 * @throws IllegalArgumentException if the property can't be created
		 */
		P create() throws NotApplicablePropertyException;
	}

	/**
	 * A factory interface for creating instances of
	 * properties associated with a {@link GeoElement}.
	 * @param <P> the type of property that this factory produces
	 */
	public interface GeoElementPropertyFactory<P extends Property> {
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
		P create(GeoElement geoElement) throws NotApplicablePropertyException;
	}

	/**
	 * Collector interface for aggregating multiple properties
	 * of a specific type into a single facade.
	 *
	 * @param <P> the type of individual properties that will be collected
	 * @param <C> the type of the resulting collection of properties
	 */
	public interface PropertyCollector<
			P extends Property,
			C extends Property> {
		/**
		 * Collects a list of individual properties into a single {@link C}.
		 *
		 * @param properties the list of individual properties to collect
		 * @return a collection of properties that represents the aggregated result
		 * @throws IllegalArgumentException if the input list of properties is invalid
		 */
		C collect(List<P> properties) throws IllegalArgumentException;
	}

	/**
	 * Same as {@link GeoElementPropertiesFactory#createPropertyFacadeThrowing} but thrown
	 * exceptions are caught, and null is returned instead.
	 */
	private <P extends Property, C extends Property> C createPropertyFacade(
			List<GeoElement> geoElements,
			GeoElementPropertyFactory<P> propertyFactory,
			PropertyCollector<P, C> propertyCollector
	) {
		try {
			return createPropertyFacadeThrowing(geoElements, propertyFactory, propertyCollector);
		} catch (NotApplicablePropertyException | IllegalArgumentException ignored) {
			return null;
		}
	}

	/**
	 * Creates a facade {@link PropertyFactory} to a list of
	 * {@link GeoElement} s and then aggregating the resulting properties using a
	 * {@link PropertyCollector}. The method filters properties using the provided property filters
	 * before collecting them.
	 *
	 * @param <P> the type of individual properties to be created
	 * @param <C> the type of the property collection to be created
	 * @param geoElements the list of {@link GeoElement}s for which properties are to be created
	 * @param propertyFactory the factory used to create
	 * individual properties for each {@link GeoElement}
	 * @param propertyCollector the collector used to
	 * aggregate the individual properties into a collection
	 * @return a collection of properties of type {@link C}
	 * @throws NotApplicablePropertyException if the property cannot be applied
	 * @throws IllegalArgumentException if the property can't be created from
	 * to the given {@link GeoElement} */
	public <P extends Property, C extends Property> C createPropertyFacadeThrowing(
			List<GeoElement> geoElements,
			GeoElementPropertyFactory<P> propertyFactory,
			PropertyCollector<P, C> propertyCollector
	) throws NotApplicablePropertyException {
		ArrayList<P> properties = new ArrayList<>();
		for (GeoElement geoElement : geoElements) {
			P property = propertyFactory.create(geoElement);
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
		return propertyCollector.collect(properties);
	}

	private <P extends Property> P createOptionalProperty(PropertyFactory<P> propertyFactory) {
		try {
			return propertyFactory.create();
		} catch (NotApplicablePropertyException | IllegalArgumentException ignored) {
			return null;
		}
	}

	private boolean isAllowedByFilters(Property property, GeoElement geoElement) {
		return propertyFilters.stream().allMatch(filter -> filter.isAllowed(property, geoElement));
	}
}
