package org.geogebra.common.properties;

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

import java.util.ArrayList;

public class GraphicsPropertiesList extends PropertiesList {

    private App mApp;
    private Localization mLocalization;
    private EuclidianView mEuclidianView;
    private Property[] propertiesListARView;

    public GraphicsPropertiesList(App app, Localization localization) {
        super(null);
        mApp = app;
        mLocalization = localization;
        mEuclidianView = mApp.getActiveEuclidianView();
    }

    @Override
    public Property[] getPropertiesList() {
        if (mEuclidianView.isAREnabled()) {
            if (propertiesListARView == null) {
                EuclidianView activeView = mApp.getActiveEuclidianView();
                EuclidianSettings euclidianSettings = activeView.getSettings();
                ArrayList<Property> propertyList = new ArrayList<>();

                if (mApp.has(Feature.MOB_STANDARD_VIEW_ZOOM_BUTTONS)) {
                    propertyList.add(new GraphicsPositionProperty(mApp));
                }
                propertyList.add(new AxesVisibilityProperty(mLocalization, euclidianSettings));

                if (mApp.has(Feature.MOB_SHOW_HIDE_PLANE)) {
                    if (activeView.isEuclidianView3D()) {
                        propertyList.add(new PlaneVisibilityProperty(mLocalization,
                                (EuclidianSettings3D) euclidianSettings));
                    }
                }

                propertyList.add(new BackgroundProperty(mApp, mLocalization));

                propertyList.add(new GridVisibilityProperty(mLocalization, euclidianSettings));

                if (!"3D".equals(mApp.getVersion().getAppName())) {
                    propertyList.add(new GridStyleProperty(mLocalization, euclidianSettings));
                }

                propertyList.add(new DistancePropertyCollection(mApp, mLocalization, euclidianSettings));
                propertyList.add(new LabelsPropertyCollection(mApp, mLocalization, euclidianSettings));

                propertiesListARView = new Property[propertyList.size()];
                propertyList.toArray(propertiesListARView);
            }
            return propertiesListARView;
        } else {
            if (mProperties == null) {
                EuclidianView activeView = mApp.getActiveEuclidianView();
                EuclidianSettings euclidianSettings = activeView.getSettings();
                ArrayList<Property> propertyList = new ArrayList<>();

                if (mApp.has(Feature.MOB_STANDARD_VIEW_ZOOM_BUTTONS)) {
                    propertyList.add(new GraphicsPositionProperty(mApp));
                }
                propertyList.add(new AxesVisibilityProperty(mLocalization, euclidianSettings));

                if (mApp.has(Feature.MOB_SHOW_HIDE_PLANE)) {
                    if (activeView.isEuclidianView3D()) {
                        propertyList.add(new PlaneVisibilityProperty(mLocalization,
                                (EuclidianSettings3D) euclidianSettings));
                    }
                }

                propertyList.add(new GridVisibilityProperty(mLocalization, euclidianSettings));

                if (!"3D".equals(mApp.getVersion().getAppName())) {
                    propertyList.add(new GridStyleProperty(mLocalization, euclidianSettings));
                }

                propertyList.add(new DistancePropertyCollection(mApp, mLocalization, euclidianSettings));
                propertyList.add(new LabelsPropertyCollection(mApp, mLocalization, euclidianSettings));

                mProperties = new Property[propertyList.size()];
                propertyList.toArray(mProperties);
            }
            return mProperties;
        }
    }

}
