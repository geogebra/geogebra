package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class ShowMouseCoordinatesProperty extends AbstractValuedProperty<Boolean>
	implements BooleanProperty {
	private final EuclidianSettings euclidianSettings;

	/** Creates a property to show/hide mouse location
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 */
	public ShowMouseCoordinatesProperty(Localization localization,
			EuclidianSettings euclidianSettings) {
		super(localization, "ShowMouseCoordinates");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianSettings.setAllowShowMouseCoords(value);
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.getAllowShowMouseCoords();
	}
}
