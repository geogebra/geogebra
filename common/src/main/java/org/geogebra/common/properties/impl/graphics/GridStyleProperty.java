package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.AbstractEnumerableProperty;
import org.geogebra.common.properties.IconsEnumerableProperty;

public class GridStyleProperty extends AbstractEnumerableProperty implements IconsEnumerableProperty {

    private EuclidianSettings euclidianSettings;

    public GridStyleProperty(Localization localization, EuclidianSettings euclidianSettings) {
        super(localization, "GridType");
        this.euclidianSettings = euclidianSettings;
    }

    @Override
    public int getCurrent() {
        return 0;
    }

    @Override
    protected void setValueSafe(String value, int index) {

    }

    @Override
    public boolean isEnabled() {
        return euclidianSettings.getShowGrid();
    }
}
