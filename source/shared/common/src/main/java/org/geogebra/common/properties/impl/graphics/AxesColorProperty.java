package org.geogebra.common.properties.impl.graphics;

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.color.NeutralColorValues;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

public class AxesColorProperty extends AbstractEnumeratedProperty<GColor>
		implements ColorProperty {
	private final EuclidianSettings settings;

	/**
	 * Creates a color property for axes
	 * @param loc localization
	 * @param settings view settings
	 */
	public AxesColorProperty(Localization loc, EuclidianSettings settings) {
		super(loc, "Color");
		this.settings = settings;
	}

	@Override
	protected void doSetValue(GColor value) {
		settings.setAxesColor(value);
	}

	@Override
	public GColor getValue() {
		return settings.getAxesColor();
	}

	@Override
	public @Nonnull List<GColor> getValues() {
		return NeutralColorValues.values();
	}
}
