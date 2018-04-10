package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;

/**
 * This collection groups properties that are related to labeling the axes.
 */
public class LabelsPropertyCollection extends AbstractProperty implements PropertyCollection {

    private Property[] collection = new Property[]{};

    /**
     * Constructs a lables property collection.
     *
     * @param localization localization for the title
     */
    public LabelsPropertyCollection(Localization localization) {
        super(localization, "Labels");
    }

    @Override
    public Property[] getProperties() {
        return collection;
    }
}
