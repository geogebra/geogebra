package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.AbstractEnumerableProperty;
import org.geogebra.common.properties.IconsEnumerableProperty;

/**
 * This property controls the style of the grid.
 */
public class GridStyleProperty extends AbstractEnumerableProperty implements IconsEnumerableProperty {

    public static final int ICON_CARTESIAN = 0;
    public static final int ICON_CARTESIAN_MINOR = 1;
    public static final int ICON_POLAR = 2;
    public static final int ICON_ISOMETRIC = 3;

    private EuclidianSettings euclidianSettings;

    private int[] iconIds = new int[]{
            ICON_CARTESIAN,
            ICON_CARTESIAN_MINOR,
            ICON_POLAR, ICON_ISOMETRIC
    };

    private int[] gridTypes = new int[]{
            EuclidianView.GRID_CARTESIAN,
            EuclidianView.GRID_CARTESIAN_WITH_SUBGRID,
            EuclidianView.GRID_POLAR,
            EuclidianView.GRID_ISOMETRIC
    };

    /**
     * Controls a grid style property.
     *
     * @param localization      localization for the title
     * @param euclidianSettings euclidian settings.
     */
    public GridStyleProperty(Localization localization, EuclidianSettings euclidianSettings) {
        super(localization, "GridType");
        this.euclidianSettings = euclidianSettings;
        setValuesAndLocalize(new String[]{
                "Grid.Major",
                "Grid.MajorAndMinor",
                "Polar",
                "Isometric"
        });
    }

    @Override
    public int getCurrent() {
        switch (euclidianSettings.getGridType()) {
            case EuclidianView.GRID_CARTESIAN:
                return 0;
            case EuclidianView.GRID_CARTESIAN_WITH_SUBGRID:
                return 1;
            case EuclidianView.GRID_POLAR:
                return 2;
            case EuclidianView.GRID_ISOMETRIC:
                return 3;
            default:
                return -1;
        }
    }

    @Override
    protected void setValueSafe(String value, int index) {
        euclidianSettings.setGridType(gridTypes[index]);
    }

    @Override
    public boolean isEnabled() {
        return euclidianSettings.getShowGrid();
    }

    @Override
    public int[] getIconIds() {
        return iconIds;
    }
}
