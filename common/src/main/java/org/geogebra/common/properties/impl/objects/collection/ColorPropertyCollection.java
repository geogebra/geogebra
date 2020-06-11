package org.geogebra.common.properties.impl.objects.collection;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.impl.objects.ColorProperty;

/**
 * Handles a collection of ColorProperty objects as a single ColorProperty.
 */
public class ColorPropertyCollection<T extends ColorProperty>
		extends AbstractPropertyCollection<T, GColor> {

    /**
     * @param properties properties to handle
     */
    public ColorPropertyCollection(T[] properties) {
        super(properties);
    }

    @Override
    void setPropertyValue(T property, GColor value) {
        property.setColor(value);
    }

    public GColor getColor() {
        return getFirstProperty().getColor();
    }

    public void setColor(GColor color) {
        setProperties(color);
    }
}
