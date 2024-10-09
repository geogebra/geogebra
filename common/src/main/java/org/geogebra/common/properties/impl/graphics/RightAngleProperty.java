package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

public class RightAngleProperty extends AbstractNamedEnumeratedProperty<Integer> {

	private final App app;

	/**
	 * right angle style property
	 * @param localization - localization
	 * @param app - application
	 */
	public RightAngleProperty(Localization localization, App app) {
		super(localization, "Labeling");
		this.app = app;
		setValues(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE,
				EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
				EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT,
				EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L);
		setValueNames("Off", "\u25a1", "\u25CF", "\u2335");
	}

	@Override
	protected void doSetValue(Integer value) {
		app.setRightAngleStyle(value);
		app.getEuclidianView1().updateAllDrawables(true);
	}

	@Override
	public Integer getValue() {
		return app.getActiveEuclidianView().getRightAngleStyle();
	}
}
