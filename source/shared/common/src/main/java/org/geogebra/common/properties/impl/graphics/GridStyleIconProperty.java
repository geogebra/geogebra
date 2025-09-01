package org.geogebra.common.properties.impl.graphics;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

/**
 * This property controls the style of the grid.
 */
public class GridStyleIconProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {

	private EuclidianSettings euclidianSettings;

	private PropertyResource[] icons = new PropertyResource[]{
			PropertyResource.ICON_CARTESIAN_MINOR, PropertyResource.ICON_CARTESIAN,
			PropertyResource.ICON_POLAR, PropertyResource.ICON_ISOMETRIC,
			PropertyResource.ICON_DOTS};

	/**
	 * Controls a grid style property.
	 * @param localization localization for the title
	 * @param euclidianSettings euclidian settings.
	 */
	public GridStyleIconProperty(Localization localization, EuclidianSettings euclidianSettings) {
		super(localization, "GridType");
		this.euclidianSettings = euclidianSettings;
		setValues(List.of(EuclidianView.GRID_CARTESIAN_WITH_SUBGRID, EuclidianView.GRID_CARTESIAN,
				EuclidianView.GRID_POLAR, EuclidianView.GRID_ISOMETRIC,
				EuclidianView.GRID_DOTS));
	}

	@Override
	public Integer getValue() {
		return euclidianSettings.getGridType();
	}

	@Override
	protected void doSetValue(Integer value) {
		euclidianSettings.setGridType(value);
	}

	@Override
	public boolean isEnabled() {
		return euclidianSettings.getShowGrid();
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}
}
