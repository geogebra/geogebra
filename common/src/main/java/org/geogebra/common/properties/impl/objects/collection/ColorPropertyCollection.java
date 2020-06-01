package org.geogebra.common.properties.impl.objects.collection;

import java.util.Collection;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.GeoElementProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.objects.ColorProperty;

public class ColorPropertyCollection implements GeoElementProperty {

    private Collection<? extends ColorProperty> propertyCollection;
    ColorProperty property;

    public ColorPropertyCollection(
            Collection<? extends ColorProperty> propertyCollection) {
        this.propertyCollection = propertyCollection;
        property = propertyCollection.iterator().next();
    }

    public GColor[] getColorValues() {
        return property.getColorValues();
    }

    public GColor getColor() {
        GColor defaultColor = getColorValues()[0];
        GColor commonColor = property.getColor();
        for (ColorProperty colorProperty : propertyCollection) {
            if (!commonColor.equals(colorProperty.getColor())) {
                return defaultColor;
            }
        }
        return commonColor;
    }

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
