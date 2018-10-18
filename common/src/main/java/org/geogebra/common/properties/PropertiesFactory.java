package org.geogebra.common.properties;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
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
import org.geogebra.common.properties.impl.graphics.BackgroundProperty;
import org.geogebra.common.properties.impl.graphics.DistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GraphicsPositionProperty;
import org.geogebra.common.properties.impl.graphics.GridStyleProperty;
import org.geogebra.common.properties.impl.graphics.GridVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.LabelsPropertyCollection;
import org.geogebra.common.properties.impl.graphics.PlaneVisibilityProperty;

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
        return createGeneralProperties(app, localization, null);
    }

    /**
     * Creates general properties.
     *
     * @param app          properties for app
     * @param localization localization for properties
     * @param onLanguageSetCallback callback when language is set
     * @return an array of general properties
     */
    public static Property[] createGeneralProperties(
            App app, Localization localization,
            LanguageProperty.OnLanguageSetCallback onLanguageSetCallback) {
        Kernel kernel = app.getKernel();
        return new Property[]{
                new RoundingProperty(app, localization),
                new AngleUnitProperty(kernel, localization),
                new LabelingProperty(app, localization),
                new CoordinatesProperty(kernel, localization),
                new FontSizeProperty(app, localization),
                new LanguageProperty(app, localization, onLanguageSetCallback)
        };
    }

    /**
     * Creates properties for scientific calculator.
     *
     * @param app          properties for app
     * @param localization localization for properties
     * @param onLanguageSetCallback callback when language is set
     * @return an array of properties for scientific calculator
     */
    public static Property[] createScientificCalculatorProperties(App app, Localization
            localization, LanguageProperty.OnLanguageSetCallback onLanguageSetCallback) {
        Kernel kernel = app.getKernel();
        return new Property[]{
                new AngleUnitProperty(kernel, localization),
                new RoundingProperty(app, localization),
                new FontSizeProperty(app, localization),
                new LanguageProperty(app, localization, onLanguageSetCallback)
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
        if (app.has(Feature.MOB_PROPERTY_SORT_BY)) {
            return new Property[]{
                    new AlgebraDescriptionProperty(kernel, localization),
                    new SortByProperty(algebraView, localization),
                    new ShowAuxiliaryProperty(app, localization)
            };
        } else {
            return new Property[]{
                    new AlgebraDescriptionProperty(kernel, localization),
                    new ShowAuxiliaryProperty(app, localization)
            };
        }
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
        ArrayList<Property> propertyList = new ArrayList<>();

        if (app.has(Feature.MOB_STANDARD_VIEW_ZOOM_BUTTONS)) {
            propertyList
                    .add(new GraphicsPositionProperty(app));
        }
        propertyList.add(new AxesVisibilityProperty(localization, euclidianSettings));

        if (app.has(Feature.MOB_SHOW_HIDE_PLANE)) {
            if (activeView.isEuclidianView3D()) {
                propertyList.add(new PlaneVisibilityProperty(localization,
                        (EuclidianSettings3D) euclidianSettings));
            }
        }

        // propertyList.add(new BackgroundProperty(app, localization));

        propertyList.add(new GridVisibilityProperty(localization, euclidianSettings));

        if (!"3D".equals(app.getVersion().getAppName())) {
            propertyList.add(new GridStyleProperty(localization, euclidianSettings));
        }

        propertyList.add(new DistancePropertyCollection(app, localization, euclidianSettings));
        propertyList.add(new LabelsPropertyCollection(app, localization, euclidianSettings));
        Property[] properties = new Property[propertyList.size()];

        return propertyList.toArray(properties);
    }
}
