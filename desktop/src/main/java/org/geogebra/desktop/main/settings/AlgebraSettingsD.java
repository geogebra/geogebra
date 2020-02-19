package org.geogebra.desktop.main.settings;

import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.AlgebraStyle;

/**
 * Algebra settings for desktop.
 */
public class AlgebraSettingsD extends AlgebraSettings {

	@Override
	public int getStyle() {
		int style = super.getStyle();
		return style == AlgebraStyle.DEFINITION_AND_VALUE ? AlgebraStyle.VALUE : style;
	}
}
