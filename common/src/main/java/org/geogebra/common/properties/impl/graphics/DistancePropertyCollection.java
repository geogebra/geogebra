package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;

import java.util.ArrayList;

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
    public DistancePropertyCollection(App app, Localization localization, EuclidianSettings
            euclidianSettings) {
        super(localization, "Distance");

        ArrayList<Property> properties = new ArrayList<>();
        properties.add(new AxesNumberingDistanceProperty(localization, euclidianSettings, app));
        properties.add(new AxisDistanceProperty(localization, euclidianSettings, app.getKernel(),
                "xAxis", 0));
        properties.add(new AxisDistanceProperty(localization, euclidianSettings, app.getKernel(),
                "yAxis", 1));
        if ("3D".equals(app.getVersion().getAppName())) {
            properties.add(
                    new AxisDistanceProperty(localization, euclidianSettings, app.getKernel(),
                            "zAxis", 2));
        }

        collection = new Property[properties.size()];
        collection = properties.toArray(collection);
    }

    @Override
    public Property[] getProperties() {
        return collection;
    }
}
