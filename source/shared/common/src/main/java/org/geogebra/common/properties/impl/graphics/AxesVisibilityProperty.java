package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/**
 * This property controls the visibility of the axes.
 */
public class AxesVisibilityProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {

	private EuclidianSettings euclidianSettings;

	/**
	 * Constructs an AxesVisibility property.
	 * @param localization localization for the name
	 * @param euclidianSettings euclidian settings
	 */
	public AxesVisibilityProperty(Localization localization,
			EuclidianSettings euclidianSettings) {
		super(localization, "ShowAxes");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	public Boolean getValue() {
		boolean[] showAxes = euclidianSettings.getShowAxes();
		boolean value = false;
		for (int i = 0; i < euclidianSettings.getDimension(); i++) {
			value |= showAxes[i];
		}

		return value;
	}

	@Override
	public void doSetValue(Boolean value) {
		euclidianSettings.setShowAxes(value);
	}
}
