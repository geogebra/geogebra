package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class GrayScaleProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private EuclidianView3DInterface euclidianView;

	/**
	 * Creates a gray scale property used for
	 * {@link ProjectionsProperty} PROJECTION_GLASSES
	 * @param localization localization
	 * @param euclidianView euclidian view
	 */
	public GrayScaleProperty(Localization localization, EuclidianView3DInterface euclidianView) {
		super(localization, "GrayScale");
		this.euclidianView = euclidianView;
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianView.setGlassesGrayScaled(value);
		euclidianView.repaintView();
	}

	@Override
	public Boolean getValue() {
		return euclidianView.isGlassesGrayScaled();
	}

	@Override
	public boolean isEnabled() {
		return euclidianView.getProjection() == EuclidianView3DInterface.PROJECTION_GLASSES;
	}
}
