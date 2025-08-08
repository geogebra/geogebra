package org.geogebra.common.properties.impl.graphics;

import static org.geogebra.common.properties.PropertyResource.ICON_AXES_ITALIC;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.IconAssociatedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class AxesItalicProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty, IconAssociatedProperty {
	private EuclidianSettings euclidianSettings;

	/**
	 * Create an axes label italic property.
	 * @param localization localization
	 * @param euclidianSettings EV settings
	 */
	public AxesItalicProperty(Localization localization, EuclidianSettings euclidianSettings) {
		super(localization, "Italic");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	protected void doSetValue(Boolean value) {
		int style = euclidianSettings.getAxisFontStyle();
		if (value) {
			if (style == GFont.PLAIN || style == GFont.BOLD) {
				style += GFont.ITALIC;
				euclidianSettings.setAxisFontStyle(style);
			}
		} else {
			if (style == GFont.ITALIC || style == GFont.ITALIC + GFont.BOLD) {
				style -= GFont.ITALIC;
				euclidianSettings.setAxisFontStyle(style);
			}
		}
	}

	@Override
	public Boolean getValue() {
		int style = euclidianSettings.getAxisFontStyle();
		return style == GFont.ITALIC || style == GFont.BOLD + GFont.ITALIC;
	}

	@Override
	public PropertyResource getIcon() {
		return ICON_AXES_ITALIC;
	}
}
