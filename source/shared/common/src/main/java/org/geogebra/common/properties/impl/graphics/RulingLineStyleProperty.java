package org.geogebra.common.properties.impl.graphics;

import java.util.List;

import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

public class RulingLineStyleProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {
	private final EuclidianSettings euclidianSettings;

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_LINE_TYPE_FULL, PropertyResource.ICON_LINE_TYPE_DASHED_DOTTED,
			PropertyResource.ICON_LINE_TYPE_DASHED_LONG, PropertyResource.ICON_LINE_TYPE_DOTTED,
			PropertyResource.ICON_LINE_TYPE_DASHED_SHORT
	};

	/**
	 * Creates a line style property for ruling
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 */
	public RulingLineStyleProperty(Localization localization, EuclidianSettings euclidianSettings) {
		super(localization, "LineStyle");
		this.euclidianSettings = euclidianSettings;
		setValues(List.of(
				EuclidianStyleConstants.LINE_TYPE_FULL,
				EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED,
				EuclidianStyleConstants.LINE_TYPE_DASHED_LONG,
				EuclidianStyleConstants.LINE_TYPE_DOTTED,
				EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT
		));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	protected void doSetValue(Integer value) {
		euclidianSettings.setRulerLineStyle(value);
	}

	@Override
	public Integer getValue() {
		return euclidianSettings.getRulerLineStyle();
	}

	@Override
	public boolean isEnabled() {
		BackgroundType backgroundType = euclidianSettings.getBackgroundType();
		return backgroundType == BackgroundType.RULER || backgroundType
				== BackgroundType.SQUARE_SMALL || backgroundType == BackgroundType.SQUARE_BIG;
	}
}
