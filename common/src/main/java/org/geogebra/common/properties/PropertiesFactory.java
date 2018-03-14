package org.geogebra.common.properties;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AngleUnitProperty;
import org.geogebra.common.properties.impl.RoundingProperty;

public class PropertiesFactory {

    public static Property[] createGeneralProperties(App app, Localization localization) {
        return new Property[] {
                new RoundingProperty(app, localization),
                new AngleUnitProperty(app.getKernel(), localization)
        };
    }
}
