package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class OmitGreenChannelProperty extends AbstractValuedProperty<Boolean>
	implements BooleanProperty {
	private EuclidianView3D euclidianView;

	/**
	 * Creates a  property for toggling green channel in
	 * {@link ProjectionsProperty} PROJECTION_GLASSES
	 * @param localization localization
	 * @param euclidianView euclidian view
	 */
	public OmitGreenChannelProperty(Localization localization,
			EuclidianView3D euclidianView) {
		super(localization, "OmitGreen");
		this.euclidianView = euclidianView;
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianView.setGlassesShutDownGreen(value);
		euclidianView.repaintView();
	}

	@Override
	public Boolean getValue() {
		return euclidianView.isGlassesShutDownGreen();
	}

	@Override
	public boolean isEnabled() {
		return euclidianView.getProjection() == EuclidianView3DInterface.PROJECTION_GLASSES;
	}
}
