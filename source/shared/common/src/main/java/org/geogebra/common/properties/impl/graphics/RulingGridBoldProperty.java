package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class RulingGridBoldProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private final EuclidianSettings euclidianSettings;
	private final boolean isRuling;

	/**
	 * Creates bold property for grid
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 */
	public RulingGridBoldProperty(Localization localization, EuclidianSettings euclidianSettings) {
		this(localization, euclidianSettings, false);
	}

	/**
	 * Creates bold property for ruling in notes
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 * @param isRuling ruling only for notes
	 */
	public RulingGridBoldProperty(Localization localization, EuclidianSettings euclidianSettings,
			boolean isRuling) {
		super(localization, "Bold");
		this.euclidianSettings = euclidianSettings;
		this.isRuling = isRuling;
	}

	@Override
	protected void doSetValue(Boolean value) {
		if (isRuling) {
			euclidianSettings.setRulerBold(value);
		} else {
			euclidianSettings.setGridIsBold(value);
		}
	}

	@Override
	public Boolean getValue() {
		return isRuling ? euclidianSettings.isRulerBold() : euclidianSettings.getGridIsBold();
	}

	@Override
	public boolean isEnabled() {
		BackgroundType backgroundType = euclidianSettings.getBackgroundType();
		return backgroundType == BackgroundType.RULER || backgroundType
				== BackgroundType.SQUARE_SMALL || backgroundType == BackgroundType.SQUARE_BIG;
	}
}
