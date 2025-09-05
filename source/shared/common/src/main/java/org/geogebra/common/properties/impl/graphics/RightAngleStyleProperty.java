package org.geogebra.common.properties.impl.graphics;

import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

public class RightAngleStyleProperty extends AbstractEnumeratedProperty<Integer>
	implements IconsEnumeratedProperty<Integer> {
	private final App app;

	private PropertyResource[] icons = new PropertyResource[]{
			PropertyResource.ICON_RIGHT_ANGLE_STYLE_NONE,
			PropertyResource.ICON_RIGHT_ANGLE_STYLE_SQUARE,
			PropertyResource.ICON_RIGHT_ANGLE_STYLE_DOT,
			PropertyResource.ICON_RIGHT_ANGLE_STYLE_L};

	/**
	 * Create a right angle style icon property
	 * @param localization localization
	 * @param app application
	 */
	public RightAngleStyleProperty(Localization localization, App app) {
		super(localization, "RightAngleStyle");
		this.app = app;
		setValues(List.of(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE,
				EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
				EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT,
				EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L));
	}

	@Override
	protected void doSetValue(Integer value) {
		app.setRightAngleStyle(value);
		app.getEuclidianView1().updateAllDrawables(true);
	}

	@Override
	public Integer getValue() {
		return app.rightAngleStyle;
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}
}
