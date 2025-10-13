package org.geogebra.common.properties.impl.graphics;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

public class RulingGridLineStyleProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer>, SettingsDependentProperty {
	private final EuclidianSettings euclidianSettings;
	private final boolean isRuling;
	private static final PropertyResource[] icons =
			EuclidianStyleConstants.lineStyleIcons.toArray(new PropertyResource[0]);

	/**
	 * Creates a line style property for grid lines
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 */
	public RulingGridLineStyleProperty(Localization localization,
			EuclidianSettings euclidianSettings) {
		this(localization, euclidianSettings, false);
	}

	/**
	 * Creates a line style property for ruling in notes
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 * @param isRuling ruling for notes
	 */
	public RulingGridLineStyleProperty(Localization localization,
			EuclidianSettings euclidianSettings, boolean isRuling) {
		super(localization, "LineStyle");
		this.euclidianSettings = euclidianSettings;
		this.isRuling = isRuling;
		setValues(EuclidianStyleConstants.lineStyleList);
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	public @CheckForNull String[] getLabels() {
		return null;
	}

	@Override
	protected void doSetValue(Integer value) {
		if (isRuling) {
			euclidianSettings.setRulerLineStyle(value);
		} else {
			euclidianSettings.setGridLineStyle(value);
		}
	}

	@Override
	public Integer getValue() {
		return isRuling ? euclidianSettings.getRulerLineStyle()
				: euclidianSettings.getGridLineStyle();
	}

	@Override
	public boolean isAvailable() {
		if (isRuling) {
			BackgroundType backgroundType = euclidianSettings.getBackgroundType();
			return backgroundType == BackgroundType.RULER || backgroundType
					== BackgroundType.SQUARE_SMALL || backgroundType == BackgroundType.SQUARE_BIG;
		} else {
			return euclidianSettings.getGridType() != EuclidianView.GRID_DOTS;
		}
	}

	@Override
	public AbstractSettings getSettings() {
		return euclidianSettings;
	}
}
