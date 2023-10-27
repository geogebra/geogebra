package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

/**
 * Property for setting the point capturing.
 */
public class PointCapturingProperty extends AbstractNamedEnumeratedProperty<Integer> {

	private App app;

	/**
	 * Constructs a point capturing property.
	 * @param app app
	 * @param localization localization
	 */
	public PointCapturingProperty(App app, Localization localization) {
		super(localization, "PointCapturing");
		this.app = app;
		setValues(EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC,
				EuclidianStyleConstants.POINT_CAPTURING_ON,
				EuclidianStyleConstants.POINT_CAPTURING_ON_GRID,
				EuclidianStyleConstants.POINT_CAPTURING_OFF);
		setValueNames("Labeling.automatic", "SnapToGrid", "FixedToGrid", "Off");
	}

	@Override
	public Integer getValue() {
		return app.getActiveEuclidianView().getPointCapturingMode();
	}

	@Override
	protected void doSetValue(Integer value) {
		app.getActiveEuclidianView().setPointCapturing(value);
	}
}
