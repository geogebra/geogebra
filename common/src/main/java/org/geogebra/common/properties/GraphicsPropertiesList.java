package org.geogebra.common.properties;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.impl.graphics.AxesColoredProperty;
import org.geogebra.common.properties.impl.graphics.AxesVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.BackgroundProperty;
import org.geogebra.common.properties.impl.graphics.DistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GraphicsPositionProperty;
import org.geogebra.common.properties.impl.graphics.GridStyleProperty;
import org.geogebra.common.properties.impl.graphics.GridVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.LabelsPropertyCollection;
import org.geogebra.common.properties.impl.graphics.PlaneVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.ProjectionsProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * List of properties for graphics views
 *
 */
public class GraphicsPropertiesList extends PropertiesList {

    private App mApp;
    private Localization mLocalization;
    private Property[] propertiesListARView;

	/**
	 * @param app
	 *            app
	 * @param localization
	 *            localization
	 */
    public GraphicsPropertiesList(App app, Localization localization) {
		super(getProperties(app, localization));
        mApp = app;
        mLocalization = localization;
	}

	private static List<Property> getProperties(App app,
			Localization localization) {

		EuclidianView activeView = app.getActiveEuclidianView();
        EuclidianSettings euclidianSettings = activeView.getSettings();
        ArrayList<Property> propertyList = new ArrayList<>();

		if (app.has(Feature.MOB_STANDARD_VIEW_ZOOM_BUTTONS)) {
			propertyList.add(new GraphicsPositionProperty(app));
        }
		propertyList.add(
				new AxesVisibilityProperty(localization, euclidianSettings));

        if (activeView.isEuclidianView3D()) {
			propertyList.add(new PlaneVisibilityProperty(localization,
                    (EuclidianSettings3D) euclidianSettings));
        }

		propertyList.add(
				new GridVisibilityProperty(localization, euclidianSettings));

        if (activeView.isEuclidianView3D()) {
            propertyList.add(
                    new ProjectionsProperty(localization, activeView,
                            (EuclidianSettings3D) euclidianSettings));
        }

		if (!"3D".equals(app.getVersion().getAppName())) {
			propertyList.add(
					new GridStyleProperty(localization, euclidianSettings));
        }

		propertyList.add(new DistancePropertyCollection(app, localization,
				euclidianSettings));
		propertyList.add(new LabelsPropertyCollection(app, localization,
				euclidianSettings));

		if (activeView.isEuclidianView3D()) {
			propertyList.add(new AxesColoredProperty(localization,
                    (EuclidianSettings3D) euclidianSettings));
        }

		return propertyList;
    }

    @Override
    public Property[] getPropertiesList() {
        if (mApp.getActiveEuclidianView().isAREnabled()) {
            if (propertiesListARView == null) {
                if (mApp.has(Feature.MOB_STANDARD_VIEW_ZOOM_BUTTONS)) {
                    // MOB_STANDARD_VIEW_ZOOM_BUTTONS checked here just in case it would be
                    // temporarily removed
                    propertiesListARView = new Property[mProperties.length + 1];
                    for (int i = 0; i < propertiesListARView.length; i++) {
                        if (i > 1) {
                            propertiesListARView[i] = mProperties[i - 1];
						} else if (i == 0) {
                                propertiesListARView[i] = mProperties[i];
						} else {
							// i = 1 -> BackgroundProperty added below
							// GraphicsPositionProperty
							propertiesListARView[i] = new BackgroundProperty(
									mApp, mLocalization);
						}
                    }
                } else {
                    propertiesListARView = mProperties;
                }
            }
            return propertiesListARView;
        }
        return mProperties;
    }

}
