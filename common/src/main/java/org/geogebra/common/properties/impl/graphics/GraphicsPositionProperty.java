package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.ActionsEnumerableProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyResource;

/**
 * This property controls the current positioning of the grid
 */
@SuppressWarnings("unused")
public class GraphicsPositionProperty
		implements ActionsEnumerableProperty,
		Property /* redundancy needed for iOS, see IGR-1023 */ {

    private Localization localization;
    private EuclidianView euclidianView;
    private App app;

    private PropertyResource[] icons = {
            PropertyResource.ICON_STANDARD_VIEW,
            PropertyResource.ICON_ZOOM_TO_FIT
    };

    private String[] values = {
            "StandardView",
            "ShowAllObjects"
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
                    boolean keepRatio = app.getConfig().shouldKeepRatioEuclidian();
                    euclidianView.setViewShowAllObjects(true, keepRatio);
                }
            }
    };

    /**
     * Constructs a GraphicsPositionProperty
     *
     * @param app  for access to localization, the EuclidianView, and the app configuration
     */
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
