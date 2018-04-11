package org.geogebra.common.properties;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.impl.algebra.AlgebraDescriptionProperty;
import org.geogebra.common.properties.impl.algebra.ShowAuxiliaryProperty;
import org.geogebra.common.properties.impl.algebra.SortByProperty;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.geogebra.common.properties.impl.general.CoordinatesProperty;
import org.geogebra.common.properties.impl.general.FontSizeProperty;
import org.geogebra.common.properties.impl.general.LabelingProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.general.RoundingProperty;
import org.geogebra.common.properties.impl.graphics.AxesVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.DistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GridStyleProperty;
import org.geogebra.common.properties.impl.graphics.GridVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.LabelsPropertyCollection;

/**
 * Creates properties for the GeoGebra application.
 */
public class PropertiesFactory {

    /**
     * Creates general properties.
     *
     * @param app          properties for app
     * @param localization localization for properties
     * @return an array of general properties
     */
    public static Property[] createGeneralProperties(App app, Localization localization) {
        Kernel kernel = app.getKernel();
        return new Property[]{
                new RoundingProperty(app, localization),
                new AngleUnitProperty(kernel, localization),
                new LabelingProperty(app, localization),
                new CoordinatesProperty(kernel, localization),
                new FontSizeProperty(app, localization),
                new LanguageProperty(app, localization)
        };
    }

    /**
     * Creates algebra specific properties.
     *
     * @param app          properties for app
     * @param localization localization for properties
     * @return an array of algebra specific properties
     */
    public static Property[] createAlgebraProperties(App app, Localization localization) {
        AlgebraView algebraView = app.getAlgebraView();
        Kernel kernel = app.getKernel();
        return new Property[]{
                new AlgebraDescriptionProperty(kernel, localization),
                new SortByProperty(algebraView, localization),
                new ShowAuxiliaryProperty(app, localization)
        };
    }

    /**
     * Creates graphics specific properties.
     *
     * @param app          properties for app
     * @param localization localization for properties
     * @return an array of graphics specific properties
     */
    public static Property[] createGraphicsProperties(App app, Localization localization) {
        EuclidianView activeView = app.getActiveEuclidianView();
        EuclidianSettings euclidianSettings = activeView.getSettings();
        return new Property[]{
                new AxesVisibilityProperty(localization, euclidianSettings),
                new GridVisibilityProperty(localization, euclidianSettings),
                new GridStyleProperty(localization, euclidianSettings),
                new DistancePropertyCollection(localization, app.getKernel(), euclidianSettings),
                new LabelsPropertyCollection(localization, euclidianSettings)
        };
    }
}
