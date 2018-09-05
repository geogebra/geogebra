package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;
import org.geogebra.common.properties.ActionsEnumerableProperty;
import org.geogebra.common.properties.PropertyResource;

public class GraphicsPositionProperty extends AbstractEnumerableProperty
        implements ActionsEnumerableProperty {

    private EuclidianView euclidianView;

    private PropertyResource[] icons = {
            PropertyResource.ICON_STANDARD_VIEW,
            PropertyResource.ICON_ZOOM_TO_FIT
    };

    private String[] values = {
            "Standard view",
            "Zoom to fit"
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
        setValuesAndLocalize(values);
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

    @Override
    public int getIndex() {
        // Method stub
        return 0;
    }

    @Override
    protected void setValueSafe(String value, int index) {
        // Method stub
    }
}
