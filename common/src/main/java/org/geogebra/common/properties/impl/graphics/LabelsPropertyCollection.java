package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;

/**
 * This collection groups properties that are related to labeling the axes.
 */
public class LabelsPropertyCollection extends AbstractProperty implements PropertyCollection {

    private Property[] collection;

    /**
     * Constructs a lables property collection.
     *
     * @param localization localization for the title
     */
    public LabelsPropertyCollection(Localization localization, EuclidianSettings euclidianSettings) {
        super(localization, "Labels");
        collection = new Property[]{
                new AxesLabelsVisibilityProperty(localization, euclidianSettings)
        };
    }

    @Override
    public Property[] getProperties() {
        return collection;
    }
}
