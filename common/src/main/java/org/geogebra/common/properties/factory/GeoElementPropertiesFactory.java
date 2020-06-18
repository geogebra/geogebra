package org.geogebra.common.properties.factory;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.objects.AnimationStepProperty;
import org.geogebra.common.properties.impl.objects.CaptionStyleProperty;
import org.geogebra.common.properties.impl.objects.ElementColorProperty;
import org.geogebra.common.properties.impl.objects.EquationFormProperty;
import org.geogebra.common.properties.impl.objects.FixObjectProperty;
import org.geogebra.common.properties.impl.objects.LineStyleProperty;
import org.geogebra.common.properties.impl.objects.MaxProperty;
import org.geogebra.common.properties.impl.objects.MinProperty;
import org.geogebra.common.properties.impl.objects.OpacityProperty;
import org.geogebra.common.properties.impl.objects.PointSizeProperty;
import org.geogebra.common.properties.impl.objects.PointStyleProperty;
import org.geogebra.common.properties.impl.objects.ShowInAVProperty;
import org.geogebra.common.properties.impl.objects.ShowObjectProperty;
import org.geogebra.common.properties.impl.objects.ShowTraceProperty;
import org.geogebra.common.properties.impl.objects.SlopeSizeProperty;
import org.geogebra.common.properties.impl.objects.ThicknessProperty;
import org.geogebra.common.properties.impl.objects.collection.BooleanPropertyCollection;
import org.geogebra.common.properties.impl.objects.collection.ColorPropertyCollection;
import org.geogebra.common.properties.impl.objects.collection.EnumerablePropertyCollection;
import org.geogebra.common.properties.impl.objects.collection.IconsEnumerablePropertyCollection;
import org.geogebra.common.properties.impl.objects.collection.NumericPropertyCollection;
import org.geogebra.common.properties.impl.objects.collection.RangePropertyCollection;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Creates the list of properties for a GeoElement or for a list of GeoElements.
 */
public class GeoElementPropertiesFactory {

	/**
	 * Creates properties for a list of GeoElements.
	 * @param localization localization
	 * @param elements input elements
	 * @return the list of properties for the GeoElement(s)
	 */
	public static PropertiesArray createGeoElementProperties(
			Localization localization, List<GeoElement> elements) {
		List<Property> properties = new ArrayList<>();
		addPropertyIfNotNull(properties, createMinProperty(localization, elements));
		addPropertyIfNotNull(properties, createMaxProperty(localization, elements));
		addPropertyIfNotNull(properties, createStepProperty(localization, elements));
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

	private static void addPropertyIfNotNull(List<Property> properties, Property property) {
		if (property != null) {
			properties.add(property);
		}
	}

	private static BooleanPropertyCollection<ShowObjectProperty> createShowObjectProperty(
			Localization localization, List<GeoElement> elements) {
		List<ShowObjectProperty> showObjectProperties = new ArrayList<>();
		for (GeoElement element : elements) {
			showObjectProperties.add(new ShowObjectProperty(localization, element));
		}
		return new BooleanPropertyCollection<>(
				showObjectProperties.toArray(new ShowObjectProperty[0]));
	}

	private static ColorPropertyCollection<ElementColorProperty> createColorProperty(
			Localization localization, List<GeoElement> elements) {
		List<ElementColorProperty> colorProperties = new ArrayList<>();
		for (GeoElement element : elements) {
			colorProperties.add(new ElementColorProperty(localization, element));
		}
		return new ColorPropertyCollection<>(
				colorProperties.toArray(new ElementColorProperty[0]));
	}

	private static BooleanPropertyCollection<FixObjectProperty> createFixObjectProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<FixObjectProperty> fixObjectProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				fixObjectProperties.add(new FixObjectProperty(localization, element));
			}
			return new BooleanPropertyCollection<>(
					fixObjectProperties.toArray(new FixObjectProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static EnumerablePropertyCollection<CaptionStyleProperty> createCaptionStyleProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<CaptionStyleProperty> captionStyleProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				captionStyleProperties.add(new CaptionStyleProperty(localization, element));
			}
			return new EnumerablePropertyCollection<>(
					captionStyleProperties.toArray(new CaptionStyleProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static RangePropertyCollection<OpacityProperty, Integer> createOpacityProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<OpacityProperty> opacityProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				opacityProperties.add(new OpacityProperty(localization, element));
			}
			return new RangePropertyCollection<>(
					opacityProperties.toArray(new OpacityProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static NumericPropertyCollection<MinProperty, Double> createMinProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<MinProperty> minProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				minProperties.add(new MinProperty(localization, element));
			}
			return new NumericPropertyCollection<>(
					minProperties.toArray(new MinProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static NumericPropertyCollection<MaxProperty, Double> createMaxProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<MaxProperty> maxProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				maxProperties.add(new MaxProperty(localization, element));
			}
			return new NumericPropertyCollection<>(
					maxProperties.toArray(new MaxProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static NumericPropertyCollection<AnimationStepProperty, Double> createStepProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<AnimationStepProperty> stepProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				stepProperties.add(new AnimationStepProperty(localization, element));
			}
			return new NumericPropertyCollection<>(
					stepProperties.toArray(new AnimationStepProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static IconsEnumerablePropertyCollection<PointStyleProperty> createPointStyleProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<PointStyleProperty> pointStyleProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				pointStyleProperties.add(new PointStyleProperty(localization, element));
			}
			return new IconsEnumerablePropertyCollection<>(
					pointStyleProperties.toArray(new PointStyleProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static RangePropertyCollection<PointSizeProperty, Integer> createPointSizeProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<PointSizeProperty> pointSizeProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				pointSizeProperties.add(new PointSizeProperty(localization, element));
			}
			return new RangePropertyCollection<>(
					pointSizeProperties.toArray(new PointSizeProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static RangePropertyCollection<ThicknessProperty, Integer> createThicknessProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<ThicknessProperty> thicknessProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				thicknessProperties.add(new ThicknessProperty(localization, element));
			}
			return new RangePropertyCollection<>(
					thicknessProperties.toArray(new ThicknessProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static IconsEnumerablePropertyCollection<LineStyleProperty> createLineStyleProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<LineStyleProperty> lineStyleProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				lineStyleProperties.add(new LineStyleProperty(localization, element));
			}
			return new IconsEnumerablePropertyCollection<>(
					lineStyleProperties.toArray(new LineStyleProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static NumericPropertyCollection<SlopeSizeProperty, Integer> createSlopeSizeProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<SlopeSizeProperty> slopeSizeProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				slopeSizeProperties.add(new SlopeSizeProperty(localization, element));
			}
			return new NumericPropertyCollection<>(
					slopeSizeProperties.toArray(new SlopeSizeProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static EnumerablePropertyCollection<EquationFormProperty> createEquationFormProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<EquationFormProperty> equationFormProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				equationFormProperties.add(new EquationFormProperty(localization, element));
			}
			return new EnumerablePropertyCollection<>(
					equationFormProperties.toArray(new EquationFormProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static BooleanPropertyCollection<ShowTraceProperty> createShowTraceProperty(
			Localization localization, List<GeoElement> elements) {
		try {
			List<ShowTraceProperty> traceProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				traceProperties.add(new ShowTraceProperty(localization, element));
			}
			return new BooleanPropertyCollection<>(
					traceProperties.toArray(new ShowTraceProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static BooleanPropertyCollection<ShowInAVProperty> createShowInAvProperty(
			Localization localization, List<GeoElement> elements) {
		List<ShowInAVProperty> showInAvProperties = new ArrayList<>();
		for (GeoElement element : elements) {
			showInAvProperties.add(new ShowInAVProperty(localization, element));
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
}