package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.PropertiesArray;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;

public class ARRatioPropertyCollection extends AbstractProperty
        implements PropertyCollection {

    private PropertiesArray collection;

    /**
     * Constructs a ar ratio property collection.
     * @param app
     *            application
     *
     * @param localization
     *            localization for the title
     */
    public ARRatioPropertyCollection(App app, Localization localization) {
        super(localization, "AR Ratio");

        Kernel kernel = app.getKernel();
        Renderer renderer = ((EuclidianView3D) app.getActiveEuclidianView()).getRenderer();
        ArrayList<Property> properties = new ArrayList<>();
        properties.add(new ARRatioProperty(localization, renderer, kernel));
        properties.add(new RatioUnitProperty((EuclidianView3D) app.getActiveEuclidianView(),
                localization));

        collection = new PropertiesArray("AR Ratio", properties.toArray(new Property[0]));
    }

    @Override
    public Property[] getProperties() {
        return collection.getProperties();
    }
}
