package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;

/**
 * This collection groups properties that are related to the distances of axes numbering.
 */
public class DistancePropertyCollection extends AbstractProperty implements PropertyCollection {

    private Property[] collection = new Property[]{};

    /**
     * Constructs a numbering distances property collection.
     *
     * @param localization localization for the title
     */
    public DistancePropertyCollection(Localization localization, EuclidianSettings euclidianSettings) {
        super(localization, "Distance");
        collection = new Property[]{
                new AxesNumberingDistanceProperty(localization, euclidianSettings)
        };
    }

    @Override
    public Property[] getProperties() {
        return collection;
    }
}
