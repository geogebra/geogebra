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

public class GridColorProperty extends AbstractEnumeratedProperty<GColor>
		implements ColorProperty {
	private final EuclidianSettings euclidianSettings;

	/**
	 * Creates a color property for grid lines
	 * @param loc localization
	 * @param euclidianSettings view settings
	 */
	public GridColorProperty(Localization loc, EuclidianSettings euclidianSettings) {
		super(loc, "Color");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	protected void doSetValue(GColor value) {
		euclidianSettings.setBgRulerColor(value);
	}

	@Override
	public GColor getValue() {
		return euclidianSettings.getBgRulerColor();
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
