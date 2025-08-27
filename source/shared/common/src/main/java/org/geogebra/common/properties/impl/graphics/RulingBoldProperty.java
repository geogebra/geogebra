package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class RulingBoldProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private EuclidianSettings euclidianSettings;

	/**
	 * Creates bold property for ruling
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 */
	public RulingBoldProperty(Localization localization, EuclidianSettings euclidianSettings) {
		super(localization, "Bold");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianSettings.setRulerBold(value);
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.isRulerBold();
	}

	@Override
	public boolean isEnabled() {
		BackgroundType backgroundType = euclidianSettings.getBackgroundType();
		return backgroundType == BackgroundType.RULER || backgroundType
				== BackgroundType.SQUARE_SMALL || backgroundType == BackgroundType.SQUARE_BIG;
	}
}
