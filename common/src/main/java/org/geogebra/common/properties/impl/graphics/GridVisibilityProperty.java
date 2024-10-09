package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/**
 * This property controls the visibility of the grid.
 */
public class GridVisibilityProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {

	private EuclidianSettings euclidianSettings;

	/**
	 * Constructs a GridVisibility property.
	 * @param localization localization for the name
	 * @param euclidianSettings euclidian settings
	 */
	public GridVisibilityProperty(Localization localization,
			EuclidianSettings euclidianSettings) {
		super(localization, "ShowGrid");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.getShowGrid();
	}

	@Override
	public void doSetValue(Boolean value) {
		euclidianSettings.showGrid(value);
	}
}
