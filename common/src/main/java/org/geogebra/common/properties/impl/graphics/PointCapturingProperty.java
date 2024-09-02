package org.geogebra.common.properties.impl.graphics;

import java.util.Map;

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
		setNamedValues(Map.of(EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC, "Labeling.automatic",
				EuclidianStyleConstants.POINT_CAPTURING_ON, "SnapToGrid",
				EuclidianStyleConstants.POINT_CAPTURING_ON_GRID, "FixedToGrid",
				EuclidianStyleConstants.POINT_CAPTURING_OFF, "Off"
		));
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
