package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class ARRatioProperty extends AbstractValuedProperty<Boolean> implements BooleanProperty {

	private final EuclidianView3D view3D;

	/**
	 * Constructs an AR Ratio property.
	 * @param localization localization for the title
	 * @param view3D EuclidianView3D
	 */
	ARRatioProperty(Localization localization, EuclidianView3D view3D) {
		super(localization, "Show");
		this.view3D = view3D;
	}

	@Override
	public Boolean getValue() {
		return view3D.isARRatioShown();
	}

	@Override
	public void doSetValue(Boolean value) {
		view3D.setARRatioIsShown(value);
	}
}
