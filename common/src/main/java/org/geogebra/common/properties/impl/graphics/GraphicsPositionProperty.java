package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;
import org.geogebra.common.properties.ActionsEnumerableProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.util.debug.Log;

public class GraphicsPositionProperty extends AbstractEnumerableProperty
        implements ActionsEnumerableProperty {

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
                    Log.debug("Zoom to fit called!");
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    Log.debug("Zoom to fit called!");
                }
            }
    };

    public GraphicsPositionProperty(Localization localization) {
        super(localization, "GridPosition");
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
