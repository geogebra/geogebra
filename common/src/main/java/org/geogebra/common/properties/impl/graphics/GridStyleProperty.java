package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.IconsEnumerableProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;

/**
 * This property controls the style of the grid.
 */
public class GridStyleProperty extends AbstractEnumerableProperty
		implements IconsEnumerableProperty {

	private EuclidianSettings euclidianSettings;

	private PropertyResource[] icons = new PropertyResource[] {
			PropertyResource.ICON_CARTESIAN,
			PropertyResource.ICON_CARTESIAN_MINOR, PropertyResource.ICON_POLAR,
			PropertyResource.ICON_ISOMETRIC };

	private int[] gridTypes = new int[] { EuclidianView.GRID_CARTESIAN,
			EuclidianView.GRID_CARTESIAN_WITH_SUBGRID, EuclidianView.GRID_POLAR,
			EuclidianView.GRID_ISOMETRIC };

	/**
	 * Controls a grid style property.
	 *
	 * @param localization
	 *            localization for the title
	 * @param euclidianSettings
	 *            euclidian settings.
	 */
	public GridStyleProperty(Localization localization,
			EuclidianSettings euclidianSettings) {
		super(localization, "GridType");
		this.euclidianSettings = euclidianSettings;
		setValuesAndLocalize(new String[] { "Grid.Major", "Grid.MajorAndMinor",
				"Polar", "Isometric" });
	}

	@Override
	public int getIndex() {
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
	public PropertyResource[] getIcons() {
		return icons;
	}
}
