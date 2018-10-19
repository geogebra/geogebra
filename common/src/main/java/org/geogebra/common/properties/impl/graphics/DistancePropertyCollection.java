package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.PropertiesList;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;

import java.util.ArrayList;

/**
 * This collection groups properties that are related to the distances of axes numbering.
 */
public class DistancePropertyCollection extends AbstractProperty implements PropertyCollection {

    private PropertiesList collection;

    /**
     * Constructs a numbering distances property collection.
     *
     * @param localization localization for the title
     */
    public DistancePropertyCollection(App app, Localization localization, EuclidianSettings
            euclidianSettings) {
        super(localization, "Distance");

        Kernel kernel = app.getKernel();
        EuclidianView euclidianView = app.getActiveEuclidianView();
        ArrayList<Property> properties = new ArrayList<>();

        properties.add(new AxesNumberingDistanceProperty(localization, euclidianSettings,
                euclidianView, kernel));
        properties.add(new AxisDistanceProperty(localization, euclidianSettings, euclidianView,
                kernel, "xAxis", 0));
        properties.add(new AxisDistanceProperty(localization, euclidianSettings, euclidianView,
                kernel, "yAxis", 1));
        if ("3D".equals(app.getVersion().getAppName())) {
            properties.add(
                    new AxisDistanceProperty(localization, euclidianSettings, euclidianView, kernel,
                            "zAxis", 2));
        }

        Property[] p = new Property[properties.size()];
        properties.toArray(p);
        collection = new PropertiesList(p);
    }

    @Override
    public PropertiesList getProperties() {
        return collection;
    }
}
