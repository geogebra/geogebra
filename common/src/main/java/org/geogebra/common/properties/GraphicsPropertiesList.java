package org.geogebra.common.properties;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.impl.graphics.AxesVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.BackgroundProperty;
import org.geogebra.common.properties.impl.graphics.DistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GraphicsPositionProperty;
import org.geogebra.common.properties.impl.graphics.GridStyleProperty;
import org.geogebra.common.properties.impl.graphics.GridVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.LabelsPropertyCollection;
import org.geogebra.common.properties.impl.graphics.PlaneVisibilityProperty;

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
        super(null);
        mApp = app;
        mLocalization = localization;

        EuclidianView activeView = mApp.getActiveEuclidianView();
        EuclidianSettings euclidianSettings = activeView.getSettings();
        ArrayList<Property> propertyList = new ArrayList<>();

        if (mApp.has(Feature.MOB_STANDARD_VIEW_ZOOM_BUTTONS)) {
            propertyList.add(new GraphicsPositionProperty(mApp));
        }
		propertyList.add(
				new AxesVisibilityProperty(mLocalization, euclidianSettings));

        if (mApp.has(Feature.MOB_SHOW_HIDE_PLANE)) {
            if (activeView.isEuclidianView3D()) {
                propertyList.add(new PlaneVisibilityProperty(mLocalization,
                        (EuclidianSettings3D) euclidianSettings));
            }
        }

		propertyList.add(
				new GridVisibilityProperty(mLocalization, euclidianSettings));

        if (!"3D".equals(mApp.getVersion().getAppName())) {
			propertyList.add(
					new GridStyleProperty(mLocalization, euclidianSettings));
        }

		propertyList.add(new DistancePropertyCollection(mApp, mLocalization,
				euclidianSettings));
		propertyList.add(new LabelsPropertyCollection(mApp, mLocalization,
				euclidianSettings));

        mProperties = new Property[propertyList.size()];
        propertyList.toArray(mProperties);
    }

    @Override
    public Property[] getPropertiesList() {
        if (mApp.getActiveEuclidianView().isAREnabled()) {
            if (propertiesListARView == null) {
                if (mApp.has(Feature.MOB_BACKGROUND_PROPERTY)) {
                    propertiesListARView = new Property[mProperties.length + 1];
                    for (int i = 0; i < propertiesListARView.length; i++) {
                        if (i > 1) {
                            propertiesListARView[i] = mProperties[i - 1];
                        } else {
                            if (i == 0) {
                                propertiesListARView[i] = mProperties[i];
                            } else {
								// i = 1 -> BackgroundProperty added below
								// GraphicsPositionProperty
								propertiesListARView[i] = new BackgroundProperty(
										mApp, mLocalization);
                            }
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
