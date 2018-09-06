package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.ActionsEnumerableProperty;
import org.geogebra.common.properties.PropertyResource;

public class GraphicsPositionProperty extends AbstractProperty
        implements ActionsEnumerableProperty {

    private EuclidianView euclidianView;

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
                    euclidianView.setViewShowAllObjects(true, false);
                }
            }
    };

    public GraphicsPositionProperty(Localization localization, EuclidianView euclidianView) {
        super(localization, "GridPosition");
        this.euclidianView = euclidianView;
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
        Localization localization = getLocalization();
        for (int i = 0; i < values.length; i++) {
            values[i] = localization.getMenu(values[i]);
        }
    }
}
