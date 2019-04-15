package org.geogebra.desktop.main.settings;

import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.AlgebraStyle;

/**
 * Algebra settings for desktop.
 */
public class AlgebraSettingsD extends AlgebraSettings {

	@Override
	public AlgebraStyle getStyle() {
		AlgebraStyle style = super.getStyle();
		return style == AlgebraStyle.DefinitionAndValue ? AlgebraStyle.Value : style;
	}
}
