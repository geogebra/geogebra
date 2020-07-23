package org.geogebra.common.properties.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.graphics.ARRatioPropertyCollection;
import org.geogebra.common.properties.impl.graphics.BackgroundProperty;

public class G3DPropertiesFactory implements PropertiesFactory {

    private PropertiesFactory basePropertiesFactory = new BasePropertiesFactory();

    @Override
    public PropertiesArray createGeneralProperties(
            App app,
            Localization localization,
            LanguageProperty.OnLanguageSetCallback onLanguageSetCallback) {
        PropertiesArray propertiesArray =
                basePropertiesFactory
                        .createGeneralProperties(app, localization, onLanguageSetCallback);
        if (app.getActiveEuclidianView().isAREnabled()) {
            List<Property> propertyList =
                    new ArrayList<>(Arrays.asList(propertiesArray.getProperties()));
            propertyList.add(1, new ARRatioPropertyCollection(app, localization));
            propertyList.add(2, new BackgroundProperty(app, localization));
            return new PropertiesArray(
                    propertiesArray.getName(), propertyList.toArray(new Property[0]));
        } else {
            return propertiesArray;
        }
    }

    @Override
    public PropertiesArray createAlgebraProperties(App app, Localization localization) {
        return basePropertiesFactory.createAlgebraProperties(app, localization);
    }

    @Override
    public PropertiesArray createGraphicsProperties(App app, Localization localization) {
        return basePropertiesFactory.createGraphicsProperties(app, localization);
    }
}
