package org.geogebra.common.properties;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AngleUnitProperty;
import org.geogebra.common.properties.impl.CoordinatesProperty;
import org.geogebra.common.properties.impl.FontSizeProperty;
import org.geogebra.common.properties.impl.LabelingProperty;
import org.geogebra.common.properties.impl.RoundingProperty;

public class PropertiesFactory {

    public static Property[] createGeneralProperties(App app, Localization localization) {
        Kernel kernel = app.getKernel();
        return new Property[] {
                new RoundingProperty(app, localization),
                new AngleUnitProperty(kernel, localization),
                new LabelingProperty(app, localization),
                new CoordinatesProperty(kernel, localization),
                new FontSizeProperty(app, localization)
        };
    }
}
