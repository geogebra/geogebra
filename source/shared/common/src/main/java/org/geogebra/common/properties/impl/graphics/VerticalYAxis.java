package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class VerticalYAxis extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private EuclidianSettings3D euclidianSettings;

	/**
	 * Constructs a property to set the y-Axis vertical.
	 * @param localization localization for the title
	 * @param euclidianSettings euclidian settings
	 */
	public VerticalYAxis(Localization localization,
			EuclidianSettings3D euclidianSettings) {
		super(localization, "YAxisVertical");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.getYAxisVertical();
	}

	@Override
	public void doSetValue(Boolean value) {
		euclidianSettings.setYAxisVertical(value);
	}
}
