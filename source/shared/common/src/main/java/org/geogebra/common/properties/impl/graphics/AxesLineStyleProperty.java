package org.geogebra.common.properties.impl.graphics;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

public class AxesLineStyleProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {
	private EuclidianSettings euclidianSettings;
	private PropertyResource[] icons = new PropertyResource[]{
			PropertyResource.ICON_AXES_LINE_TYPE_ARROW,
			PropertyResource.ICON_AXES_LINE_TYPE_ARROW_FILLED,
			PropertyResource.ICON_AXES_LINE_TYPE_TWO_ARROWS,
			PropertyResource.ICON_AXES_LINE_TYPE_TWO_ARROWS_FILLED,
			PropertyResource.ICON_AXES_LINE_TYPE_FULL};

	/**
	 * Creates a property for axes line style
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 */
	public AxesLineStyleProperty(Localization localization, EuclidianSettings euclidianSettings) {
		super(localization, "LineStyle");
		this.euclidianSettings = euclidianSettings;
		setValues(List.of(EuclidianStyleConstants.AXES_LINE_TYPE_ARROW,
				EuclidianStyleConstants.AXES_LINE_TYPE_ARROW_FILLED,
				EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS,
				EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS_FILLED,
				EuclidianStyleConstants.AXES_LINE_TYPE_FULL));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	protected void doSetValue(Integer value) {
		euclidianSettings.setAxesLineStyle(value);
	}

	@Override
	public Integer getValue() {
		return euclidianSettings.getAxesLineStyle();
	}

	@Override
	public @CheckForNull String[] getLabels() {
		return null;
	}
}
