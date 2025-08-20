package org.geogebra.common.properties.impl.graphics;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class ObliqueFactorProperty extends AbstractValuedProperty<String>
	implements StringProperty {
	private EuclidianView3D euclidianView;

	/**
	 * Creates a distance from sceen porperty used for
	 * {@link ProjectionsProperty} PROJECTION_OBLIQUE
	 * @param localization localization
	 * @param euclidianView euclidian view
	 */
	public ObliqueFactorProperty(Localization localization, EuclidianView3D euclidianView) {
		super(localization, "Dilate.Factor");
		this.euclidianView = euclidianView;
	}

	@Override
	protected void doSetValue(String value) {
		double val = Double.parseDouble(value);
		if (!Double.isNaN(val)) {
			euclidianView.getSettings().setProjectionObliqueFactor(val);
			euclidianView.repaintView();
		}
	}

	@Override
	public String getValue() {
		return String.valueOf(euclidianView.getProjectionObliqueFactor());
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		try {
			Double.parseDouble(value);
			return null;
		} catch (Exception e) {
			return getLocalization().getError("InputError.Enter_a_number");
		}
	}

	@Override
	public boolean isEnabled() {
		return euclidianView.getProjection() == EuclidianView3DInterface.PROJECTION_OBLIQUE;
	}
}
