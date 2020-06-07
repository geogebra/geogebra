package org.geogebra.common.properties.impl.objects;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.properties.BooleanProperty;
import org.geogebra.common.properties.EnumerableProperty;
import org.geogebra.common.properties.IconsEnumerableProperty;
import org.geogebra.common.properties.NumericProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.RangeProperty;
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

    private static BooleanPropertyCollection createShowObjectProperty(List<GeoElement> elements) {
        try {
            List<BooleanProperty> showObjectProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                showObjectProperties.add(new ShowObjectProperty(element));
            }
            return new BooleanPropertyCollection(showObjectProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }

    private static ColorPropertyCollection createColorProperty(List<GeoElement> elements) {
        try {
            List<ColorProperty> colorProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                colorProperties.add(new ColorProperty(element));
            }
            return new ColorPropertyCollection(colorProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }

    private static BooleanPropertyCollection createFixObjectProperty(List<GeoElement> elements) {
        try {
            List<BooleanProperty> fixObjectProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                fixObjectProperties.add(new FixObjectProperty(element));
            }
            return new BooleanPropertyCollection(fixObjectProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }

    private static EnumerablePropertyCollection createCaptionStyleProperty(
            List<GeoElement> elements) {
        try {
            List<EnumerableProperty> captionStyleProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                captionStyleProperties.add(new CaptionStyleProperty(element));
            }
            return new EnumerablePropertyCollection(captionStyleProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }

    private static RangePropertyCollection createOpacityProperty(List<GeoElement> elements) {
        try {
            List<RangeProperty<Integer>> opacityProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                opacityProperties.add(new OpacityProperty(element));
            }
            return new RangePropertyCollection<>(opacityProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }

    private static NumericPropertyCollection createMinProperty(List<GeoElement> elements) {
        try {
            List<NumericProperty<Double>> minProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                if (element instanceof GeoNumeric) {
                    minProperties.add(new MinProperty((GeoNumeric) element));
                } else {
                    return null;
                }
            }
            return new NumericPropertyCollection<>(minProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }

    private static NumericPropertyCollection createMaxProperty(List<GeoElement> elements) {
        try {
            List<NumericProperty<Double>> maxProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                if (element instanceof GeoNumeric) {
                    maxProperties.add(new MaxProperty((GeoNumeric) element));
                } else {
                    return null;
                }
            }
            return new NumericPropertyCollection<>(maxProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }

    private static NumericPropertyCollection createStepProperty(List<GeoElement> elements) {
        try {
            List<NumericProperty<Double>> stepProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                if (element instanceof GeoNumeric) {
                    stepProperties.add(new StepProperty((GeoNumeric) element));
                } else {
                    return null;
                }
            }
            return new NumericPropertyCollection<>(stepProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }

    private static IconsEnumerablePropertyCollection createPointStyleProperty(
            List<GeoElement> elements) {
        try {
            List<IconsEnumerableProperty> pointStyleProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                pointStyleProperties.add(new PointStyleProperty(element));
            }
            return new IconsEnumerablePropertyCollection(pointStyleProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }

    private static RangePropertyCollection createPointSizeProperty(List<GeoElement> elements) {
        try {
            List<RangeProperty<Integer>> pointSizeProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                pointSizeProperties.add(new PointSizeProperty(element));
            }
            return new RangePropertyCollection<>(pointSizeProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }

    private static RangePropertyCollection createThicknessProperty(List<GeoElement> elements) {
        try {
            List<RangeProperty<Integer>> thicknessProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                thicknessProperties.add(new ThicknessProperty(element));
            }
            return new RangePropertyCollection<>(thicknessProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }

    private static IconsEnumerablePropertyCollection createLineStyleProperty(
            List<GeoElement> elements) {
        try {
            List<IconsEnumerableProperty> lineStyleProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                lineStyleProperties.add(new LineStyleProperty(element));
            }
            return new IconsEnumerablePropertyCollection(lineStyleProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }

    private static NumericPropertyCollection createSlopeSizeProperty(List<GeoElement> elements) {
        try {
            List<RangeProperty<Integer>> slopeSizeProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                if (element instanceof GeoNumeric) {
                    slopeSizeProperties.add(new SlopeSizeProperty((GeoNumeric) element));
                } else {
                    return null;
                }
            }
            return new NumericPropertyCollection<>(slopeSizeProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }

    private static EnumerablePropertyCollection createEquationFormProperty(
            List<GeoElement> elements) {
        try {
            List<EnumerableProperty> equationFormProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                equationFormProperties.add(new EquationFormProperty(element));
            }
            return new EnumerablePropertyCollection(equationFormProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }

    private static BooleanPropertyCollection createShowTraceProperty(List<GeoElement> elements) {
        try {
            List<BooleanProperty> traceProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                traceProperties.add(new ShowTraceProperty(element));
            }
            return new BooleanPropertyCollection(traceProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }

    private static BooleanPropertyCollection createShowInAvProperty(List<GeoElement> elements) {
        try {
            List<BooleanProperty> showInAvProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                showInAvProperties.add(new ShowInAVProperty(element));
            }
            return new BooleanPropertyCollection(showInAvProperties);
        } catch (NotApplicablePropertyException ignored) {
            return null;
        }
    }
}