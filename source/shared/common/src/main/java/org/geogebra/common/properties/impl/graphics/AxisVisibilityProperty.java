package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/**
 * This property controls the visibility of a single axis.
 */
public class AxisVisibilityProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty, SettingsDependentProperty {

	private final int axisIndex;
	private final EuclidianSettings euclidianSettings;

	/**
	 * Constructs an AxesVisibility property.
	 * @param localization localization for the name
	 * @param euclidianSettings euclidian settings
	 * @param index The index of the associated axis
	 * @param label The label used for this property
	 */
	public AxisVisibilityProperty(Localization localization,
			EuclidianSettings euclidianSettings, int index, String label) {
		super(localization, label);
		this.axisIndex = index;
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.getShowAxes()[axisIndex];
	}

	@Override
	public void doSetValue(Boolean value) {
		euclidianSettings.setShowAxis(axisIndex, value);
	}

	@Override
	public EuclidianSettings getSettings() {
		return euclidianSettings;
	}
}
