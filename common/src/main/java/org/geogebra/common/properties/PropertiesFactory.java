package org.geogebra.common.properties;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.geogebra.common.properties.impl.general.CoordinatesProperty;
import org.geogebra.common.properties.impl.general.FontSizeProperty;
import org.geogebra.common.properties.impl.general.LabelingProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.general.RoundingProperty;

public class PropertiesFactory {

    public static Property[] createGeneralProperties(App app, Localization localization) {
        Kernel kernel = app.getKernel();
        return new Property[] {
                new RoundingProperty(app, localization),
                new AngleUnitProperty(kernel, localization),
                new LabelingProperty(app, localization),
                new CoordinatesProperty(kernel, localization),
                new FontSizeProperty(app, localization),
                new LanguageProperty(app, localization)
        };
    }
}
