package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.BooleanProperty;

/**
 * This property controls the distance of the axes numbering.
 */
public class AxesNumberingDistanceProperty extends AbstractProperty
		implements BooleanProperty {

	private EuclidianSettings euclidianSettings;

	/**
	 * Constructs an Axes numbering distance property.
	 *
	 * @param localization
	 *            localization for the title
	 */
	public AxesNumberingDistanceProperty(Localization localization,
			EuclidianSettings euclidianSettings) {
		super(localization, "Automatic");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	public boolean getValue() {
		boolean[] axesAutomaticDistances = euclidianSettings
				.getAutomaticAxesNumberingDistances();

		for (boolean axesAutomaticDistance : axesAutomaticDistances) {
			if (!axesAutomaticDistance) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void setValue(boolean value) {
		int length = euclidianSettings
				.getAutomaticAxesNumberingDistances().length;
		for (int i = 0; i < length; i++) {
			euclidianSettings.setAutomaticAxesNumberingDistance(!value, i,
					value);
		}
	}
}
