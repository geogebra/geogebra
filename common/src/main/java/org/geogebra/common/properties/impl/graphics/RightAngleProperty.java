package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;

public class RightAngleProperty extends AbstractEnumerableProperty {

	private int[] rightAngleStyle = {
			EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE,
			EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
			EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT,
			EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L
	};
	private final App app;

	/**
	 * right angle style property
	 * @param localization - localization
	 * @param app - application
	 */
	public RightAngleProperty(Localization localization, App app) {
		super(localization, "Labeling");
		this.app = app;
		setValues("Off", "\u25a1", "\u25CF", "\u2335");
	}

	@Override
	protected void setValueSafe(String value, int index) {
		int mode = index;
		if (mode == 0) {
			mode = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE;
		} else if (mode == 1) {
			mode = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE;
		} else if (mode == 2) {
			mode = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT;
		} else if (mode == 3) {
			mode = EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L;
		}

		app.setRightAngleStyle(mode);
		app.getEuclidianView1().updateAllDrawables(true);
	}

	@Override
	public int getIndex() {
		int tooltipStyle = app.getActiveEuclidianView().getRightAngleStyle();
		for (int i = 0; i < rightAngleStyle.length; i++) {
			if (tooltipStyle == rightAngleStyle[i]) {
				return i;
			}
		}

		return -1;
	}

}
