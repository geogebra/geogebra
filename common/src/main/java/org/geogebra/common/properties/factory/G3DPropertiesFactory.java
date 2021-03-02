package org.geogebra.common.properties.factory;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.graphics.ARRatioPropertyCollection;
import org.geogebra.common.properties.impl.graphics.AxesColoredProperty;
import org.geogebra.common.properties.impl.graphics.AxesVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.BackgroundProperty;
import org.geogebra.common.properties.impl.graphics.DistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GraphicsPositionProperty;
import org.geogebra.common.properties.impl.graphics.GridVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.LabelsPropertyCollection;
import org.geogebra.common.properties.impl.graphics.PlaneVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.PointCapturingProperty;
import org.geogebra.common.properties.impl.graphics.ProjectionsProperty;

public class G3DPropertiesFactory implements PropertiesFactory {

    private PropertiesFactory basePropertiesFactory = new BasePropertiesFactory();

    @Override
    public PropertiesArray createGeneralProperties(
            App app,
            Localization localization,
            LanguageProperty.OnLanguageSetCallback onLanguageSetCallback) {
        return basePropertiesFactory
                .createGeneralProperties(app, localization, onLanguageSetCallback);
    }

    @Override
    public PropertiesArray createAlgebraProperties(App app, Localization localization) {
        return basePropertiesFactory.createAlgebraProperties(app, localization);
    }

    @Override
    public PropertiesArray createGraphicsProperties(App app, Localization localization) {
        EuclidianSettings euclidianSettings = app.getActiveEuclidianView().getSettings();
        ArrayList<Property> propertyList = new ArrayList<>();

        propertyList.add(new GraphicsPositionProperty(app));
        if (app.getActiveEuclidianView().isXREnabled()) {
            propertyList.add(new ARRatioPropertyCollection(app, localization));
            propertyList.add(new BackgroundProperty(app, localization));
        }
        propertyList.add(new AxesVisibilityProperty(localization, euclidianSettings));
        propertyList.add(
                new PlaneVisibilityProperty(localization, (EuclidianSettings3D) euclidianSettings));
        propertyList.add(new GridVisibilityProperty(localization, euclidianSettings));
        propertyList.add(
                new ProjectionsProperty(
                        localization,
                        app.getActiveEuclidianView(),
                        (EuclidianSettings3D) euclidianSettings));
        propertyList.add(new PointCapturingProperty(app, localization));
        propertyList.add(new DistancePropertyCollection(app, localization, euclidianSettings));
        propertyList.add(new LabelsPropertyCollection(localization, euclidianSettings));
        propertyList.add(
                new AxesColoredProperty(localization, (EuclidianSettings3D) euclidianSettings));

        return new PropertiesArray(
                localization.getMenu("DrawingPad"), propertyList.toArray(new Property[0]));
    }
}
