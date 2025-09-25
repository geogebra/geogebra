package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class ShowClippingBooleanProperty extends AbstractValuedProperty<Boolean>
	implements BooleanProperty {
	private final EuclidianView3D euclidianView;

	/**
	 * Creates a boolean property to configure whether clipping should be shown
	 * @param localization localization
	 * @param euclidianView euclidian view
	 */
	public ShowClippingBooleanProperty(Localization localization, EuclidianView3D euclidianView) {
		super(localization, "ShowClipping");
		this.euclidianView = euclidianView;
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianView.setShowClippingCube(value);
		euclidianView.repaintView();
	}

	@Override
	public Boolean getValue() {
		return euclidianView.showClippingCube();
	}
}
