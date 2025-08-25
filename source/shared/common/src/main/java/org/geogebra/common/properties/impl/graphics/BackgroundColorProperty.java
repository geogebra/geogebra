package org.geogebra.common.properties.impl.graphics;

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.color.BackgroundColorValues;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

/**
 * Property for background color of a graphics view.
 */
public class BackgroundColorProperty extends AbstractEnumeratedProperty<GColor>
		implements ColorProperty  {
	private final EuclidianSettings settings;

	/**
	 * @param loc localization
	 * @param settings view settings
	 */
	public BackgroundColorProperty(Localization loc, EuclidianSettings settings) {
		super(loc, "BackgroundColor");
		this.settings = settings;
	}

	@Override
	protected void doSetValue(GColor value) {
		settings.setBackground(value);
	}

	@Override
	public GColor getValue() {
		return settings.getBackground();
	}

	@Override
	public @Nonnull List<GColor> getValues() {
		return BackgroundColorValues.values();
	}
}
