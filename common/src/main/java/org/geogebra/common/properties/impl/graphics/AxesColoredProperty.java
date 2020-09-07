package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractProperty;

/**
 * This property controls the color of axes.
 */
public class AxesColoredProperty extends AbstractProperty
		implements BooleanProperty {

	private EuclidianSettings3D euclidianSettings;

	/**
	 * Constructs an Axes colored property.
	 *
	 * @param localization
	 *            localization for the title
	 * @param euclidianSettings
	 *            euclidian settings
	 */
	public AxesColoredProperty(Localization localization,
                               EuclidianSettings3D euclidianSettings) {
		super(localization, "AxesColored");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	public boolean getValue() {
		return euclidianSettings.getHasColoredAxes();
	}

	@Override
	public void setValue(boolean value) {
		euclidianSettings.setHasColoredAxes(value);
	}
}
