package org.geogebra.common.properties.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.geogebra.common.properties.impl.collections.EnumeratedPropertyCollection;
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
public class GeoElementPropertiesFactory {
	private final Set<GeoElementPropertyFilter> propertyFilters = new HashSet<>();

	public void addFilter(GeoElementPropertyFilter filter) {
		propertyFilters.add(filter);
	}

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
		if (elements.isEmpty()) {
			return new PropertiesArray("");
		}
		List<Property> properties = new ArrayList<>();
		addPropertyIfNotNull(properties, createNameProperty(localization, elements));
		addPropertyIfNotNull(properties, createMinProperty(processor, localization, elements));
		addPropertyIfNotNull(properties, createMaxProperty(processor, localization, elements));
		addPropertyIfNotNull(properties, createStepProperty(processor, localization, elements));
		addPropertyIfNotNull(properties, createShowObjectProperty(localization, elements));
		addPropertyIfNotNull(properties, createColorProperty(localization, elements));
		addPropertyIfNotNull(properties, createPointStyleProperty(localization, elements));
		addPropertyIfNotNull(properties, createPointSizeProperty(localization, elements));
		addPropertyIfNotNull(properties, createOpacityProperty(localization, elements));
		addPropertyIfNotNull(properties, createLineStyleProperty(localization, elements));
		addPropertyIfNotNull(properties, createThicknessProperty(localization, elements));
		addPropertyIfNotNull(properties, createSlopeSizeProperty(localization, elements));
		addPropertyIfNotNull(properties, createEquationFormProperty(localization, elements));
		addPropertyIfNotNull(properties, createCaptionStyleProperty(localization, elements));
		addPropertyIfNotNull(properties, createShowTraceProperty(localization, elements));
		addPropertyIfNotNull(properties, createFixObjectProperty(localization, elements));
		addPropertyIfNotNull(properties, createShowInAvProperty(localization, elements));
		return createPropertiesArray(localization, properties, elements);
	}

	/**
	 * Creates Point style properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public PropertiesArray createPointStyleProperties(
			Localization localization, List<GeoElement> elements) {
		List<Property> properties = new ArrayList<>();
		addPropertyIfNotNull(properties, createPointStyleProperty(localization, elements));
		addPropertyIfNotNull(properties, createPointSizeProperty(localization, elements));
		return createPropertiesArray(localization, properties, elements);
	}

	/**
	 * Creates Lines style properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public PropertiesArray createLineStyleProperties(
			Localization localization, List<GeoElement> elements) {
		List<Property> properties = new ArrayList<>();
		addPropertyIfNotNull(properties, createLineStyleProperty(localization, elements));
		addPropertyIfNotNull(properties, createThicknessProperty(localization, elements));
		return createPropertiesArray(localization, properties, elements);
	}

	/**
	 * Creates a color property for the elements
	 * @param localization localization
	 * @param elements elements
	 * @return color property
	 */
	public ColorProperty createColorProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<ElementColorProperty> colorProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				addPropertyIfAllowed(colorProperties, element, new ElementColorProperty(localization, element));
			}
			return new ColorPropertyCollection<>(
					colorProperties.toArray(new ElementColorProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns with a Boolean property that fixes the object, or null if not applicable
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public BooleanProperty createFixObjectProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<IsFixedObjectProperty> fixObjectProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				addPropertyIfAllowed(fixObjectProperties, element, new IsFixedObjectProperty(localization, element));
			}
			return new BooleanPropertyCollection<>(
					fixObjectProperties.toArray(new IsFixedObjectProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the point style or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedProperty createPointStyleProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<PointStyleProperty> pointStyleProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				addPropertyIfAllowed(pointStyleProperties, element, new PointStyleProperty(localization, element));
			}
			return new IconsEnumeratedPropertyCollection<>(
					pointStyleProperties.toArray(new PointStyleProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns an Integer RangeProperty controlling the point size or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public RangeProperty<Integer> createPointSizeProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<PointSizeProperty> pointSizeProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				addPropertyIfAllowed(pointSizeProperties, element, new PointSizeProperty(localization, element));
			}
			return new RangePropertyCollection<>(
					pointSizeProperties.toArray(new PointSizeProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns an Integer RangeProperty controlling the line thickness null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public RangePropertyCollection<?, ?> createThicknessProperty(Localization localization,
			List<GeoElement> elements) {
		try {
			List<ThicknessProperty> thicknessProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				addPropertyIfAllowed(thicknessProperties, element, new ThicknessProperty(localization, element));
			}
			return new RangePropertyCollection<>(
					thicknessProperties.toArray(new ThicknessProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns an IconsEnumeratedProperty controlling the line style or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public IconsEnumeratedProperty createLineStyleProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<LineStyleProperty> lineStyleProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				addPropertyIfAllowed(lineStyleProperties, element, new LineStyleProperty(localization, element));
			}
			return new IconsEnumeratedPropertyCollection<>(
					lineStyleProperties.toArray(new LineStyleProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static void addPropertyIfNotNull(List<Property> properties,
			Property property) {
		if (property != null) {
			properties.add(property);
		}
	}

	private <T extends Property> void addPropertyIfAllowed(List<T> properties, GeoElement geoElement, T property) {
		if (isAllowed(property, geoElement)) {
			properties.add(property);
		}
	}

	private StringPropertyCollection<NameProperty> createNameProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<NameProperty> nameProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				addPropertyIfAllowed(nameProperties, element, new NameProperty(localization, element));
			}
			return new StringPropertyCollection<>(nameProperties.toArray(new NameProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private BooleanPropertyCollection<ShowObjectProperty> createShowObjectProperty(
			Localization localization, List<GeoElement> elements) {
		List<ShowObjectProperty> showObjectProperties = new ArrayList<>();
		for (GeoElement element : elements) {
			addPropertyIfAllowed(showObjectProperties, element, new ShowObjectProperty(localization, element));
		}
		return new BooleanPropertyCollection<>(
				showObjectProperties.toArray(new ShowObjectProperty[0]));
	}

	private EnumeratedPropertyCollection<CaptionStyleProperty, Integer>
	createCaptionStyleProperty(Localization localization, List<GeoElement> elements) {
		try {
			List<CaptionStyleProperty> captionStyleProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				addPropertyIfAllowed(captionStyleProperties, element, new CaptionStyleProperty(localization, element));
			}
			return new NamedEnumeratedPropertyCollection<>(
					captionStyleProperties.toArray(new CaptionStyleProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	/**
	 * Returns a RangePropertyCollection controlling the opacity or null if not applicable.
	 * @param localization localization
	 * @param elements elements
	 * @return property or null
	 */
	public RangeProperty<Integer> createOpacityProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<OpacityProperty> opacityProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				addPropertyIfAllowed(opacityProperties, element, new OpacityProperty(localization, element));
			}
			return new RangePropertyCollection<>(opacityProperties.toArray(new OpacityProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private StringPropertyCollection<MinProperty> createMinProperty(
			AlgebraProcessor processor, Localization localization, List<GeoElement> elements) {
		try {
			List<MinProperty> minProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				addPropertyIfAllowed(minProperties, element, new MinProperty(processor, localization, element));
			}
			return new StringPropertyCollection<>(
					minProperties.toArray(new MinProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private StringPropertyCollection<MaxProperty> createMaxProperty(
			AlgebraProcessor processor, Localization localization, List<GeoElement> elements) {
		try {
			List<MaxProperty> maxProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				addPropertyIfAllowed(maxProperties, element, new MaxProperty(processor, localization, element));
			}
			return new StringPropertyCollection<>(
					maxProperties.toArray(new MaxProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private StringPropertyCollection<AnimationStepProperty> createStepProperty(
			AlgebraProcessor processor, Localization localization, List<GeoElement> elements) {
		try {
			List<AnimationStepProperty> stepProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				addPropertyIfAllowed(stepProperties, element, new AnimationStepProperty(processor, localization, element));
			}
			return new StringPropertyCollection<>(
					stepProperties.toArray(new AnimationStepProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private RangePropertyCollection<SlopeSizeProperty, Integer> createSlopeSizeProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<SlopeSizeProperty> slopeSizeProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				addPropertyIfAllowed(slopeSizeProperties, element, new SlopeSizeProperty(localization, element));
			}
			return new RangePropertyCollection<>(
					slopeSizeProperties.toArray(new SlopeSizeProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private EnumeratedPropertyCollection<EquationFormProperty, Integer>
	createEquationFormProperty(Localization localization, List<GeoElement> elements) {
		try {
			List<EquationFormProperty> equationFormProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				addPropertyIfAllowed(equationFormProperties, element, new EquationFormProperty(localization, element));
			}
			return new NamedEnumeratedPropertyCollection<>(
					equationFormProperties.toArray(new EquationFormProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private BooleanPropertyCollection<ShowTraceProperty> createShowTraceProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<ShowTraceProperty> traceProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				addPropertyIfAllowed(traceProperties, element, new ShowTraceProperty(localization, element));
			}
			return new BooleanPropertyCollection<>(
					traceProperties.toArray(new ShowTraceProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private BooleanPropertyCollection<ShowInAVProperty> createShowInAvProperty(
			Localization localization, List<GeoElement> elements) {
		List<ShowInAVProperty> showInAvProperties = new ArrayList<>();
		for (GeoElement element : elements) {
			addPropertyIfAllowed(showInAvProperties, element, new ShowInAVProperty(localization, element));
		}
		return new BooleanPropertyCollection<>(
				showInAvProperties.toArray(new ShowInAVProperty[0]));

	}

	private static PropertiesArray createPropertiesArray(Localization localization,
			List<Property> properties, List<GeoElement> geoElements) {
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

	private boolean isAllowed(Property property, GeoElement geoElement) {
		return propertyFilters.stream().allMatch(filter -> filter.isAllowed(property, geoElement));
	}
}