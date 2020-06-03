package org.geogebra.common.properties.impl.objects;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.properties.BooleanProperty;
import org.geogebra.common.properties.EnumerableProperty;
import org.geogebra.common.properties.GeoElementProperty;
import org.geogebra.common.properties.IconsEnumerableProperty;
import org.geogebra.common.properties.NumericProperty;
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

    private List<GeoElement> elements;
    private List<GeoElementProperty> properties;

    /**
     * @param elements elements
     */
    public GeoElementProperties(List<GeoElement> elements) {
        this.elements = elements;
    }

    /**
     * @param element element
     */
    public GeoElementProperties(GeoElement element) {
        elements = new ArrayList<>();
        elements.add(element);
    }

    /**
     * @return The list of properties for the GeoElement(s)
     */
    public List<GeoElementProperty> getProperties() {
        properties = new ArrayList<>();
        addMinProperty();
        addMaxProperty();
        addStepProperty();
        addShowObjectProperty();
        addColorProperty();
        addPointStyleProperty();
        addPointSizeProperty();
        addOpacityProperty();
        addLineStyleProperty();
        addThicknessProperty();
        addSlopeSizeProperty();
        addEquationFormProperty();
        addCaptionStyleProperty();
        addShowTraceProperty();
        addFixObjectProperty();
        addShowInAvProperty();
        return properties;
    }

    private void addShowObjectProperty() {
        try {
            List<BooleanProperty> showObjectProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                showObjectProperties.add(new ShowObjectProperty(element));
            }
            properties.add(new BooleanPropertyCollection(showObjectProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }

    private void addColorProperty() {
        try {
            List<ColorProperty> colorProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                colorProperties.add(new ColorProperty(element));
            }
            properties.add(new ColorPropertyCollection(colorProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }

    private void addFixObjectProperty() {
        try {
            List<BooleanProperty> fixObjectProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                fixObjectProperties.add(new FixObjectProperty(element));
            }
            properties.add(new BooleanPropertyCollection(fixObjectProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }

    private void addCaptionStyleProperty() {
        try {
            List<EnumerableProperty> captionStyleProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                captionStyleProperties.add(new CaptionStyleProperty(element));
            }
            properties.add(new EnumerablePropertyCollection(captionStyleProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }

    private void addOpacityProperty() {
        try {
            List<RangeProperty<Integer>> opacityProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                opacityProperties.add(new OpacityProperty(element));
            }
            properties.add(new RangePropertyCollection<>(opacityProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }

    private void addMinProperty() {
        try {
            List<NumericProperty<Double>> minProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                if (element instanceof GeoNumeric) {
                    minProperties.add(new MinProperty((GeoNumeric) element));
                } else {
                    return;
                }
            }
            properties.add(new NumericPropertyCollection<>(minProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }

    private void addMaxProperty() {
        try {
            List<NumericProperty<Double>> maxProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                if (element instanceof GeoNumeric) {
                    maxProperties.add(new MaxProperty((GeoNumeric) element));
                } else {
                    return;
                }
            }
            properties.add(new NumericPropertyCollection<>(maxProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }

    private void addStepProperty() {
        try {
            List<NumericProperty<Double>> stepProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                if (element instanceof GeoNumeric) {
                    stepProperties.add(new StepProperty((GeoNumeric) element));
                } else {
                    return;
                }
            }
            properties.add(new NumericPropertyCollection<>(stepProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }

    private void addPointStyleProperty() {
        try {
            List<IconsEnumerableProperty> pointStyleProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                pointStyleProperties.add(new PointStyleProperty(element));
            }
            properties.add(new IconsEnumerablePropertyCollection(pointStyleProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }

    private void addPointSizeProperty() {
        try {
            List<RangeProperty<Integer>> pointSizeProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                pointSizeProperties.add(new PointSizeProperty(element));
            }
            properties.add(new RangePropertyCollection<>(pointSizeProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }

    private void addThicknessProperty() {
        try {
            List<RangeProperty<Integer>> thicknessProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                thicknessProperties.add(new ThicknessProperty(element));
            }
            properties.add(new RangePropertyCollection<>(thicknessProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }

    private void addLineStyleProperty() {
        try {
            List<IconsEnumerableProperty> lineStyleProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                lineStyleProperties.add(new LineStyleProperty(element));
            }
            properties.add(new IconsEnumerablePropertyCollection(lineStyleProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }

    private void addSlopeSizeProperty() {
        try {
            List<RangeProperty<Integer>> slopeSizeProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                if (element instanceof GeoNumeric) {
                    slopeSizeProperties.add(new SlopeSizeProperty((GeoNumeric) element));
                } else {
                    return;
                }
            }
            properties.add(new NumericPropertyCollection<>(slopeSizeProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }

    private void addEquationFormProperty() {
        try {
            List<EnumerableProperty> equationFormProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                equationFormProperties.add(new EquationFormProperty(element));
            }
            properties.add(new EnumerablePropertyCollection(equationFormProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }

    private void addShowTraceProperty() {
        try {
            List<BooleanProperty> traceProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                traceProperties.add(new ShowTraceProperty(element));
            }
            properties.add(new BooleanPropertyCollection(traceProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }

    private void addShowInAvProperty() {
        try {
            List<BooleanProperty> showInAvProperties = new ArrayList<>();
            for (GeoElement element : elements) {
                showInAvProperties.add(new ShowInAVProperty(element));
            }
            properties.add(new BooleanPropertyCollection(showInAvProperties));
        } catch (NotApplicablePropertyException ignored) {
        }
    }
}