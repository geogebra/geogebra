package org.geogebra.common.properties.impl.general;

import java.util.Arrays;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.LabelSettings;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

/**
 * Property for setting the labeling for new objects.
 */
public class LabelingProperty extends AbstractNamedEnumeratedProperty<LabelVisibility> {

	private LabelSettings labelSettings;

	/**
	 * Labeling property for all apps except for Classic
	 * @param localization localization
	 * @param labelSettings labelSettings
	 */
	public LabelingProperty(Localization localization, LabelSettings labelSettings) {
		this(localization, labelSettings,
				LabelVisibility.AlwaysOn, LabelVisibility.AlwaysOff, LabelVisibility.PointsOnly);
	}

	/**
	 * Labeling property with custom values (needed for Classic)
	 * @param localization localization
	 * @param labelSettings labelSettings
	 * @param values available values
	 */
	public LabelingProperty(Localization localization, LabelSettings labelSettings,
			LabelVisibility... values) {
		super(localization, "Labeling");
		this.labelSettings = labelSettings;
		setValues(values);
		setValueNames(Arrays.stream(values)
				.map(LabelVisibility::getTransKey).toArray(String[]::new));
	}

	@Override
	protected void doSetValue(LabelVisibility value) {
		labelSettings.setLabelVisibility(value);
	}

	@Override
	public LabelVisibility getValue() {
		return labelSettings.getLabelVisibility();
	}
}
