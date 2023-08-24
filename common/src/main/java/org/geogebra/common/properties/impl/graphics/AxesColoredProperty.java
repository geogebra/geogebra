package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/**
 * This property controls the color of axes.
 */
public class AxesColoredProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {

	private EuclidianSettings3D euclidianSettings;

	/**
	 * Constructs an Axes colored property.
	 * @param localization localization for the title
	 * @param euclidianSettings euclidian settings
	 */
	public AxesColoredProperty(Localization localization,
			EuclidianSettings3D euclidianSettings) {
		super(localization, "AxesColored");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.getHasColoredAxes();
	}

	@Override
	public void doSetValue(Boolean value) {
		euclidianSettings.setHasColoredAxes(value);
	}
}
