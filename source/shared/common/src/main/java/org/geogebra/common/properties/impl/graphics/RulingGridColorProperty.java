package org.geogebra.common.properties.impl.graphics;

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.color.NeutralColorValues;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

public class RulingGridColorProperty extends AbstractEnumeratedProperty<GColor>
		implements ColorProperty {
	private final EuclidianSettings euclidianSettings;
	private final boolean isRuling;

	/**
	 * Creates a color property for grid lines
	 * @param loc localization
	 * @param euclidianSettings view settings
	 */
	public RulingGridColorProperty(Localization loc, EuclidianSettings euclidianSettings) {
		this(loc, euclidianSettings, false);
	}

	/**
	 * Creates a color property for ruling in notes
	 * @param loc localization
	 * @param euclidianSettings view settings
	 * @param isRuling ruling for notes
	 */
	public RulingGridColorProperty(Localization loc, EuclidianSettings euclidianSettings,
			boolean isRuling) {
		super(loc, "Color");
		this.euclidianSettings = euclidianSettings;
		this.isRuling = isRuling;
	}

	@Override
	protected void doSetValue(GColor value) {
		if (isRuling) {
			euclidianSettings.setBgRulerColor(value);
		} else {
			euclidianSettings.setGridColor(value);
		}
	}

	@Override
	public GColor getValue() {
		return isRuling ? euclidianSettings.getBgRulerColor() : euclidianSettings.getGridColor();
	}

	@Override
	public boolean isEnabled() {
		BackgroundType backgroundType = euclidianSettings.getBackgroundType();
		return backgroundType == BackgroundType.RULER || backgroundType
				== BackgroundType.SQUARE_SMALL || backgroundType == BackgroundType.SQUARE_BIG;
	}

	@Override
	public @Nonnull List<GColor> getValues() {
		return NeutralColorValues.values();
	}
}
