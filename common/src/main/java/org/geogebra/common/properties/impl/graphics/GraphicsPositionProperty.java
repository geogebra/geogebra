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
        uploadIcons();
    }

    private void uploadIcons() {
        if (icons == null) {
            icons = new PropertyResource[]{
                    PropertyResource.ICON_STANDARD_VIEW,
                    PropertyResource.ICON_ZOOM_TO_FIT
            };
        }
        if (values == null) {
            values = new String[]{
                    "StandardView",
                    "ShowAllObjects"
            };
            localizeValues(values);
        }
        if (callbacks == null) {
            callbacks = new Runnable[]{
                    callbacksAll[0],
                    callbacksAll[1]
            };
        }
    }

    private void uploadIconsForAR() {
        if (iconsAR == null) {
            iconsAR = new PropertyResource[]{
                    PropertyResource.ICON_RELOAD_AR,
                    PropertyResource.ICON_STANDARD_VIEW,
                    PropertyResource.ICON_ZOOM_TO_FIT
            };
        }
        if (valuesAR == null) {
            valuesAR = new String[]{
                    "ReloadAR",
                    "StandardView",
                    "ShowAllObjects"
            };
            localizeValues(valuesAR);
        }
        if (callbacksAR == null) {
            callbacksAR = new Runnable[]{
                    callbacksAll[2],
                    callbacksAll[0],
                    callbacksAll[1]
            };
        }
    }

    @Override
    public Runnable[] getActions() {
        if (euclidianView.isAREnabled()) {
            uploadIconsForAR();
            return callbacksAR;
        } else {
            uploadIcons();
            return callbacks;
        }
    }

    @Override
    public PropertyResource[] getIcons() {
        if (euclidianView.isAREnabled()) {
            uploadIconsForAR();
            return iconsAR;

        } else {
            uploadIcons();
            return icons;
        }
    }

    @Override
    public String[] getValues() {
        if (euclidianView.isAREnabled()) {
            uploadIconsForAR();
            return valuesAR;
        } else {
            uploadIcons();
            return values;
        }
    }

    private void localizeValues(String[] values) {
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