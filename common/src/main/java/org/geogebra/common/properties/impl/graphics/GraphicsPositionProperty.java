package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.ActionsEnumerableProperty;
import org.geogebra.common.properties.PropertyResource;

/**
 * This property controls the current positioning of the grid
 */
public class GraphicsPositionProperty implements ActionsEnumerableProperty {

    private Localization localization;
    private EuclidianView euclidianView;
    private App app;

    private PropertyResource[] icons;
    private PropertyResource[] iconsAR;

    private String[] values;
    private String[] valuesAR;

    private Runnable[] callbacksAll = {
            new Runnable() {
                @Override
                public void run() {
                    euclidianView.setStandardView(true);
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    boolean keepRatio = app.getConfig().shouldKeepRatioEuclidian();
                    euclidianView.setViewShowAllObjects(true, keepRatio);
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    // restart AR session
                    EuclidianView3D euclidianView3D = (EuclidianView3D) euclidianView;
                    euclidianView3D.getRenderer().setARShouldRestart();
                }
            }
    };

    private Runnable[] callbacks;
    private Runnable[] callbacksAR;

    /**
     * Constructs a GraphicsPositionProperty
     *
     * @param app for access to localization, the EuclidianView, and the app configuration
     */
    public GraphicsPositionProperty(final App app) {
        this.app = app;
        this.localization = app.getLocalization();
        this.euclidianView = app.getActiveEuclidianView();
    }

    @Override
    public Runnable[] getActions() {
        if (euclidianView.isAREnabled()) {
            if (callbacksAR == null) {
                callbacksAR = new Runnable[]{
                        callbacksAll[2],
                        callbacksAll[0],
                        callbacksAll[1]
                };
            }
            return callbacksAR;
        } else {
            if (callbacks == null) {
                callbacks = new Runnable[]{
                        callbacksAll[0],
                        callbacksAll[1]
                };
            }
            return callbacks;
        }
    }

    @Override
    public PropertyResource[] getIcons() {
        if (euclidianView.isAREnabled()) {
            if (iconsAR == null) {
                iconsAR = new PropertyResource[]{
                        PropertyResource.ICON_RELOAD_AR,
                        PropertyResource.ICON_STANDARD_VIEW,
                        PropertyResource.ICON_ZOOM_TO_FIT
                };
            }
            return iconsAR;

        } else {
            if (icons == null) {
                icons = new PropertyResource[]{
                        PropertyResource.ICON_STANDARD_VIEW,
                        PropertyResource.ICON_ZOOM_TO_FIT
                };
            }
            return icons;
        }
    }

    @Override
    public String[] getValues() {
        if (euclidianView.isAREnabled()) {
            if (valuesAR == null) {
                valuesAR = new String[]{
                        "ar.restart",
                        "StandardView",
                        "ShowAllObjects"
                };
                localizeValues(valuesAR);
            }
            return valuesAR;
        } else {
            if (values == null) {
                values = new String[]{
                        "StandardView",
                        "ShowAllObjects"
                };
                localizeValues(values);
            }
            return values;
        }
    }

    private void localizeValues(String[] messages) {
        for (int i = 0; i < messages.length; i++) {
            messages[i] = localization.getMenu(messages[i]);
        }
    }

    public String getName() {
        return null; // no name needed
    }

    public boolean isEnabled() {
        return true;
    }
}