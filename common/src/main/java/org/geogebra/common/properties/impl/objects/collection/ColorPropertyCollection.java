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
    void setPropertyValue(ColorProperty property, GColor value) {
        property.setColor(value);
    }

    public GColor getColor() {
        return getFirstProperty().getColor();
    }

    public void setColor(GColor color) {
        setProperties(color);
    }
}
