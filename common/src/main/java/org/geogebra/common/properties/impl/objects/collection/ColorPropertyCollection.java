package org.geogebra.common.properties.impl.objects.collection;

import java.util.Collection;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.GeoElementProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.objects.ColorProperty;

/**
 * Handles a collection of ColorProperty objects as a single ColorProperty.
 */
public class ColorPropertyCollection implements GeoElementProperty {

    private Collection<? extends ColorProperty> propertyCollection;
    private ColorProperty property;

    /**
     * @param propertyCollection properties to handle
     */
    public ColorPropertyCollection(
            Collection<? extends ColorProperty> propertyCollection) {
        this.propertyCollection = propertyCollection;
        property = propertyCollection.iterator().next();
    }

    public GColor[] getColorValues() {
        return property.getColorValues();
    }

    public GColor getColor() {
        return property.getColor();
    }

    /**
     * Sets the color of the GeoElement of all the properties.
     * @param color color
     */
    public void setColor(GColor color) {
        for (ColorProperty property : propertyCollection) {
            property.setColor(color);
        }
    }

    @Override
    public String getName() {
        return property.getName();
    }

    @Override
    public boolean isEnabled() {
        boolean isEnabled = true;
        for (Property property : propertyCollection) {
            isEnabled = isEnabled && property.isEnabled();
        }
        return isEnabled;
    }
}
