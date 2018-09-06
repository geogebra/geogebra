package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.ActionsEnumerableProperty;
import org.geogebra.common.properties.PropertyResource;

public class GraphicsPositionProperty implements ActionsEnumerableProperty {

    private Localization localization;
    private EuclidianView euclidianView;
    private App app;

    private PropertyResource[] icons = {
            PropertyResource.ICON_STANDARD_VIEW,
            PropertyResource.ICON_ZOOM_TO_FIT
    };

    private String[] values = {
            "StandardView",
            "ZoomToFit"
    };

    private Runnable[] callbacks = {
            new Runnable() {
                @Override
                public void run() {
                    euclidianView.setStandardView(true);
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    boolean keepRatio = app.getConfig().shouldKeepRatioEuclidean();
                    euclidianView.setViewShowAllObjects(true, keepRatio);
                }
            }
    };

    public GraphicsPositionProperty(App app) {
        this.app = app;
        this.localization = app.getLocalization();
        this.euclidianView = app.getActiveEuclidianView();
        localizeValues();
    }

    @Override
    public Runnable[] getActions() {
        return callbacks;
    }

    @Override
    public PropertyResource[] getIcons() {
        return icons;
    }

    @Override
    public String[] getValues() {
        return values;
    }

    private void localizeValues() {
        for (int i = 0; i < values.length; i++) {
            values[i] = localization.getMenu(values[i]);
        }
    }

    public String getName() {
        return null; // no name needed
    }

    public boolean isEnabled() {
        return true;
    }
}
