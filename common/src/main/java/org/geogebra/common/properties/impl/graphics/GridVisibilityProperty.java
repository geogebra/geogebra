package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractProperty;

/**
 * This property controls the visibility of the grid.
 */
public class GridVisibilityProperty extends AbstractProperty
		implements BooleanProperty {

	private EuclidianSettings euclidianSettings;

	/**
	 * Constructs a GridVisibility property.
	 *
	 * @param localization
	 *            localization for the name
	 * @param euclidianSettings
	 *            euclidian settings
	 */
	public GridVisibilityProperty(Localization localization,
			EuclidianSettings euclidianSettings) {
		super(localization, "ShowGrid");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	public boolean getValue() {
		return euclidianSettings.getShowGrid();
	}

	@Override
	public void setValue(boolean value) {
		euclidianSettings.showGrid(value);
	}
}
