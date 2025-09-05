package org.geogebra.common.properties.impl.graphics;

import static java.util.Map.entry;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

/**
 * Property for setting the point capturing.
 */
public class PointCapturingProperty extends AbstractNamedEnumeratedProperty<Integer> {

	private final EuclidianViewInterfaceCommon view;

	/**
	 * Constructs a point capturing property.
	 * @param view Euclidian view
	 * @param localization localization
	 */
	public PointCapturingProperty(Localization localization,
			EuclidianViewInterfaceCommon view) {
		super(localization, "PointCapturing");
		this.view = view;
		setNamedValues(List.of(
				entry(EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC, "Labeling.automatic"),
				entry(EuclidianStyleConstants.POINT_CAPTURING_ON, "SnapToGrid"),
				entry(EuclidianStyleConstants.POINT_CAPTURING_ON_GRID, "FixedToGrid"),
				entry(EuclidianStyleConstants.POINT_CAPTURING_OFF, "Off")
		));
	}

	@Override
	public Integer getValue() {
		return view.getPointCapturingMode();
	}

	@Override
	protected void doSetValue(Integer value) {
		view.setPointCapturing(value);
	}
}
