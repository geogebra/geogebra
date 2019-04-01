package org.geogebra.common.properties;

import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.algebra.AlgebraDescriptionProperty;
import org.geogebra.common.properties.impl.algebra.ShowAuxiliaryProperty;
import org.geogebra.common.properties.impl.algebra.SortByProperty;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.geogebra.common.properties.impl.general.CoordinatesProperty;
import org.geogebra.common.properties.impl.general.FontSizeProperty;
import org.geogebra.common.properties.impl.general.LabelingProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.general.RoundingProperty;

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
    public static PropertiesList createGeneralProperties(App app, Localization localization) {
        return createGeneralProperties(app, localization, null);
    }

    /**
     * Creates general properties.
     *
     * @param app                   properties for app
     * @param localization          localization for properties
     * @param onLanguageSetCallback callback when language is set
     * @return an array of general properties
     */
    public static PropertiesList createGeneralProperties(
            App app, Localization localization,
            LanguageProperty.OnLanguageSetCallback onLanguageSetCallback) {
        Kernel kernel = app.getKernel();

        return new PropertiesList(new RoundingProperty(app, localization),
                new AngleUnitProperty(kernel, localization),
                new LabelingProperty(app, localization),
                new CoordinatesProperty(kernel, localization),
                new FontSizeProperty(app, localization),
                new LanguageProperty(app, localization, onLanguageSetCallback));
    }

    /**
     * Creates properties for scientific calculator.
     *
     * @param app                   properties for app
     * @param localization          localization for properties
     * @param onLanguageSetCallback callback when language is set
     * @return an array of properties for scientific calculator
     */
    public static PropertiesList createScientificCalculatorProperties(App app, Localization
            localization, LanguageProperty.OnLanguageSetCallback onLanguageSetCallback) {
        Kernel kernel = app.getKernel();

        return new PropertiesList(
                new AngleUnitProperty(kernel, localization),
                new RoundingProperty(app, localization),
                new FontSizeProperty(app, localization),
                new LanguageProperty(app, localization, onLanguageSetCallback));
    }

    /**
     * Creates algebra specific properties.
     *
     * @param app          properties for app
     * @param localization localization for properties
     * @return an array of algebra specific properties
     */
    public static PropertiesList createAlgebraProperties(App app, Localization localization) {
        AlgebraView algebraView = app.getAlgebraView();
        Kernel kernel = app.getKernel();
        if (app.has(Feature.MOB_PROPERTY_SORT_BY)) {
			return new PropertiesList(
					new AlgebraDescriptionProperty(kernel, localization),
					new SortByProperty(algebraView, localization),
					new ShowAuxiliaryProperty(app, localization)
			);
		} else {
			return new PropertiesList(
					new AlgebraDescriptionProperty(kernel, localization),
					new ShowAuxiliaryProperty(app, localization));
		}
    }

    /**
     * Creates graphics specific properties.
     *
     * @param app          properties for app
     * @param localization localization for properties
     * @return an array of graphics specific properties
     */
    public static PropertiesList createGraphicsProperties(App app, Localization localization) {
        return new GraphicsPropertiesList(app, localization);
    }
}