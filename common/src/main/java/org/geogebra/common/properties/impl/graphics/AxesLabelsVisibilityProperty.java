package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractProperty;

/**
 * This property controls the visibility of the axis labels.
 */
public class AxesLabelsVisibilityProperty extends AbstractProperty
		implements BooleanProperty {

	private EuclidianSettings euclidianSettings;

	/**
	 * Constructs an Axes visibility property.
	 *
	 * @param localization
	 *            localization for the title
	 * @param euclidianSettings
	 *            euclidian settings
	 */
	public AxesLabelsVisibilityProperty(Localization localization,
			EuclidianSettings euclidianSettings) {
		super(localization, "Show");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	public boolean getValue() {
		String[] axesLabels = euclidianSettings.getAxesLabels();
		boolean value = false;
		for (int i = 0; i < euclidianSettings.getDimension(); i++) {
			value |= axesLabels[i] != null;
		}
		return value;
	}

	@Override
	public void setValue(boolean value) {
		int length = euclidianSettings.getDimension();
		for (int i = 0; i < length; i++) {
			euclidianSettings.setAxisLabel(i,
					value ? EuclidianSettings.getDefaultAxisLabel(i) : null,
					i == length - 1);
		}
	}
}
