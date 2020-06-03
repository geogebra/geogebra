package org.geogebra.common.properties.impl.objects.collection;

import java.util.Collection;

import org.geogebra.common.properties.IconsEnumerableProperty;
import org.geogebra.common.properties.PropertyResource;

/**
 * Handles a collection of IconsEnumerableProperty objects as a single IconsEnumerableProperty.
 */
public class IconsEnumerablePropertyCollection
        extends EnumerablePropertyCollection implements IconsEnumerableProperty {

    /**
     * @param propertyCollection properties to handle
     */
    public IconsEnumerablePropertyCollection(
            Collection<? extends IconsEnumerableProperty> propertyCollection) {
        super(propertyCollection);
    }

    @Override
    public PropertyResource[] getIcons() {
        IconsEnumerableProperty iconsEnumerableProperty = (IconsEnumerableProperty) property;
        return iconsEnumerableProperty.getIcons();
    }
}
