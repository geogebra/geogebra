package org.geogebra.common.properties.impl.general;

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
	 * @param localization localization
	 * @param labelSettings labelSettings
	 */
	public LabelingProperty(Localization localization, LabelSettings labelSettings) {
		super(localization, "Labeling");
		this.labelSettings = labelSettings;
		setValues(LabelVisibility.AlwaysOn, LabelVisibility.AlwaysOff, LabelVisibility.PointsOnly);
		setValueNames("Labeling.on", "Labeling.off", "Labeling.pointsOnly");
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
