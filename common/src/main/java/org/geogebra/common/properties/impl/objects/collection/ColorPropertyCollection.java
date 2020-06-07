package org.geogebra.common.properties.impl.objects.collection;

import java.util.Collection;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.impl.objects.ColorProperty;

/**
 * Handles a collection of ColorProperty objects as a single ColorProperty.
 */
public class ColorPropertyCollection extends AbstractPropertyCollection<ColorProperty, GColor> {

    /**
     * @param propertyCollection properties to handle
     */
    public ColorPropertyCollection(Collection<? extends ColorProperty> propertyCollection) {
        super(propertyCollection.toArray(new ColorProperty[0]));
    }

    @Override
    GColor defaultValue() {
        return GColor.BLACK;
    }

    @Override
    void setPropertyValue(ColorProperty property, GColor value) {
        property.setColor(value);
    }

    @Override
    GColor getPropertyValue(ColorProperty property) {
        return property.getColor();
    }

    public GColor getColor() {
        return reduceValue();
    }

    public void setColor(GColor color) {
        setProperties(color);
    }
}
