package org.geogebra.common.properties.impl.objects;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.objects.collection.BooleanPropertyCollection;
import org.geogebra.common.properties.impl.objects.collection.ColorPropertyCollection;
import org.geogebra.common.properties.impl.objects.collection.EnumerablePropertyCollection;
import org.geogebra.common.properties.impl.objects.collection.IconsEnumerablePropertyCollection;
import org.geogebra.common.properties.impl.objects.collection.NumericPropertyCollection;
import org.geogebra.common.properties.impl.objects.collection.RangePropertyCollection;

/**
 * Creates the list of properties for a GeoElement or for a list of GeoElements.
 */
public class GeoElementProperties {

	/**
	 * @return The list of properties for the GeoElement(s)
	 */
	public static List<Property> getProperties(List<GeoElement> elements) {
		List<Property> properties = new ArrayList<>();
		addPropertyIfNotNull(properties, createMinProperty(elements));
		addPropertyIfNotNull(properties, createMaxProperty(elements));
		addPropertyIfNotNull(properties, createStepProperty(elements));
		addPropertyIfNotNull(properties, createShowObjectProperty(elements));
		addPropertyIfNotNull(properties, createColorProperty(elements));
		addPropertyIfNotNull(properties, createPointStyleProperty(elements));
		addPropertyIfNotNull(properties, createPointSizeProperty(elements));
		addPropertyIfNotNull(properties, createOpacityProperty(elements));
		addPropertyIfNotNull(properties, createLineStyleProperty(elements));
		addPropertyIfNotNull(properties, createThicknessProperty(elements));
		addPropertyIfNotNull(properties, createSlopeSizeProperty(elements));
		addPropertyIfNotNull(properties, createEquationFormProperty(elements));
		addPropertyIfNotNull(properties, createCaptionStyleProperty(elements));
		addPropertyIfNotNull(properties, createShowTraceProperty(elements));
		addPropertyIfNotNull(properties, createFixObjectProperty(elements));
		addPropertyIfNotNull(properties, createShowInAvProperty(elements));
		return properties;
	}

	private static void addPropertyIfNotNull(List<Property> properties, Property property) {
		if (property != null) {
			properties.add(property);
		}
	}

	private static BooleanPropertyCollection<ShowObjectProperty> createShowObjectProperty(
			List<GeoElement> elements) {
		try {
			List<ShowObjectProperty> showObjectProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				showObjectProperties.add(new ShowObjectProperty(element));
			}
			return new BooleanPropertyCollection<>(
					showObjectProperties.toArray(new ShowObjectProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static ColorPropertyCollection<ColorProperty> createColorProperty(
			List<GeoElement> elements) {
		try {
			List<ColorProperty> colorProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				colorProperties.add(new ColorProperty(element));
			}
			return new ColorPropertyCollection<>(
					colorProperties.toArray(new ColorProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static BooleanPropertyCollection<FixObjectProperty> createFixObjectProperty(
			List<GeoElement> elements) {
		try {
			List<FixObjectProperty> fixObjectProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				fixObjectProperties.add(new FixObjectProperty(element));
			}
			return new BooleanPropertyCollection<>(
					fixObjectProperties.toArray(new FixObjectProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static EnumerablePropertyCollection<CaptionStyleProperty> createCaptionStyleProperty(
			List<GeoElement> elements) {
		try {
			List<CaptionStyleProperty> captionStyleProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				captionStyleProperties.add(new CaptionStyleProperty(element));
			}
			return new EnumerablePropertyCollection<>(
					captionStyleProperties.toArray(new CaptionStyleProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static RangePropertyCollection<OpacityProperty, Integer> createOpacityProperty(
			List<GeoElement> elements) {
		try {
			List<OpacityProperty> opacityProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				opacityProperties.add(new OpacityProperty(element));
			}
			return new RangePropertyCollection<>(
					opacityProperties.toArray(new OpacityProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static NumericPropertyCollection<MinProperty, Double> createMinProperty(
			List<GeoElement> elements) {
		try {
			List<MinProperty> minProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				if (element instanceof GeoNumeric) {
					minProperties.add(new MinProperty((GeoNumeric) element));
				} else {
					return null;
				}
			}
			return new NumericPropertyCollection<>(
					minProperties.toArray(new MinProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static NumericPropertyCollection<MaxProperty, Double> createMaxProperty(
			List<GeoElement> elements) {
		try {
			List<MaxProperty> maxProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				if (element instanceof GeoNumeric) {
					maxProperties.add(new MaxProperty((GeoNumeric) element));
				} else {
					return null;
				}
			}
			return new NumericPropertyCollection<>(
					maxProperties.toArray(new MaxProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static NumericPropertyCollection<StepProperty, Double> createStepProperty(
			List<GeoElement> elements) {
		try {
			List<StepProperty> stepProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				if (element instanceof GeoNumeric) {
					stepProperties.add(new StepProperty((GeoNumeric) element));
				} else {
					return null;
				}
			}
			return new NumericPropertyCollection<>(
					stepProperties.toArray(new StepProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static IconsEnumerablePropertyCollection<PointStyleProperty> createPointStyleProperty(
			List<GeoElement> elements) {
		try {
			List<PointStyleProperty> pointStyleProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				pointStyleProperties.add(new PointStyleProperty(element));
			}
			return new IconsEnumerablePropertyCollection<>(
					pointStyleProperties.toArray(new PointStyleProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static RangePropertyCollection<PointSizeProperty, Integer> createPointSizeProperty(
			List<GeoElement> elements) {
		try {
			List<PointSizeProperty> pointSizeProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				pointSizeProperties.add(new PointSizeProperty(element));
			}
			return new RangePropertyCollection<>(
					pointSizeProperties.toArray(new PointSizeProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static RangePropertyCollection<ThicknessProperty, Integer> createThicknessProperty(
			List<GeoElement> elements) {
		try {
			List<ThicknessProperty> thicknessProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				thicknessProperties.add(new ThicknessProperty(element));
			}
			return new RangePropertyCollection<>(
					thicknessProperties.toArray(new ThicknessProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static IconsEnumerablePropertyCollection<LineStyleProperty> createLineStyleProperty(
			List<GeoElement> elements) {
		try {
			List<LineStyleProperty> lineStyleProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				lineStyleProperties.add(new LineStyleProperty(element));
			}
			return new IconsEnumerablePropertyCollection<>(
					lineStyleProperties.toArray(new LineStyleProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static NumericPropertyCollection<SlopeSizeProperty, Integer> createSlopeSizeProperty(
			List<GeoElement> elements) {
		try {
			List<SlopeSizeProperty> slopeSizeProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				if (element instanceof GeoNumeric) {
					slopeSizeProperties.add(new SlopeSizeProperty((GeoNumeric) element));
				} else {
					return null;
				}
			}
			return new NumericPropertyCollection<>(
					slopeSizeProperties.toArray(new SlopeSizeProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static EnumerablePropertyCollection<EquationFormProperty> createEquationFormProperty(
			List<GeoElement> elements) {
		try {
			List<EquationFormProperty> equationFormProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				equationFormProperties.add(new EquationFormProperty(element));
			}
			return new EnumerablePropertyCollection<>(
					equationFormProperties.toArray(new EquationFormProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static BooleanPropertyCollection<ShowTraceProperty> createShowTraceProperty(
			List<GeoElement> elements) {
		try {
			List<ShowTraceProperty> traceProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				traceProperties.add(new ShowTraceProperty(element));
			}
			return new BooleanPropertyCollection<>(
					traceProperties.toArray(new ShowTraceProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}

	private static BooleanPropertyCollection<ShowInAVProperty> createShowInAvProperty(
			List<GeoElement> elements) {
		try {
			List<ShowInAVProperty> showInAvProperties = new ArrayList<>();
			for (GeoElement element : elements) {
				showInAvProperties.add(new ShowInAVProperty(element));
			}
			return new BooleanPropertyCollection<>(
					showInAvProperties.toArray(new ShowInAVProperty[0]));
		} catch (NotApplicablePropertyException ignored) {
			return null;
		}
	}
}