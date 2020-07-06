package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractProperty;

public class ARRatioProperty extends AbstractProperty implements BooleanProperty {

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
	public boolean getValue() {
		return view3D.isARRatioShown();
	}

	@Override
	public void setValue(boolean value) {
		view3D.setARRatioIsShown(value);
	}
}
