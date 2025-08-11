package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class GridFixedDistanceProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private EuclidianSettings euclidianSettings;

	/** Creates a grid fixed distance property.
	 * @param localization localization
	 * @param euclidianSettings EV settings
	 */
	public GridFixedDistanceProperty(Localization localization,
			EuclidianSettings euclidianSettings) {
		super(localization, "SettingsView.FixedDistance");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianSettings.setAutomaticGridDistance(!value, true);
	}

	@Override
	public Boolean getValue() {
		return !euclidianSettings.getAutomaticGridDistance();
	}
}
