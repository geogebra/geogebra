package org.geogebra.common.properties.impl.graphics;

import static org.geogebra.common.properties.PropertyResource.ICON_AXES_SERIF;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.IconAssociatedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class AxesSerifProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty, IconAssociatedProperty {
	private EuclidianSettings euclidianSettings;

	/**
	 * Create an axes label serif property.
	 * @param localization localization
	 * @param euclidianSettings EV settings
	 */
	public AxesSerifProperty(Localization localization, EuclidianSettings euclidianSettings) {
		super(localization, "Serif");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianSettings.setAxesLabelsSerif(value);
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.getAxesLabelsSerif();
	}

	@Override
	public PropertyResource getIcon() {
		return ICON_AXES_SERIF;
	}
}
